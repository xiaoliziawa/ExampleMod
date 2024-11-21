package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayerMountPacket(int riderId, int targetId, boolean mounting) implements CustomPacketPayload {
    public static final Type<PlayerMountPacket> TYPE = new Type<>(Examplemod.prefix("player_mount"));

    public static final StreamCodec<FriendlyByteBuf, PlayerMountPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> {
                buf.writeVarInt(packet.riderId());
                buf.writeVarInt(packet.targetId());
                buf.writeBoolean(packet.mounting());
            },
            buf -> new PlayerMountPacket(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean()
            )
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final PlayerMountPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                Entity target = serverPlayer.level().getEntity(data.targetId());
                
                if (target != null) {
                    if (data.mounting()) {
                        // 骑乘操作
                        if (!target.isPassenger() && !serverPlayer.isPassenger()) {
                            boolean success = serverPlayer.startRiding(target, true);
                            if (success) {
                                // 发送原版乘客数据包给所有玩家
                                broadcastMountState(serverPlayer, target);
                            }
                        }
                    } else {
                        // 下马操作
                        if (serverPlayer.isPassenger()) {
                            serverPlayer.stopRiding();
                            // 发送原版乘客数据包给所有玩家
                            broadcastMountState(serverPlayer, target);
                            // 同步玩家位置
                            serverPlayer.connection.teleport(
                                serverPlayer.getX(),
                                serverPlayer.getY(),
                                serverPlayer.getZ(),
                                serverPlayer.getYRot(),
                                serverPlayer.getXRot()
                            );
                        }
                    }
                }
            }
        });
    }

    private static void broadcastMountState(ServerPlayer rider, Entity target) {
        // 发送乘客数据包给所有玩家
        ClientboundSetPassengersPacket passengersPacket = new ClientboundSetPassengersPacket(target);
        for (ServerPlayer player : rider.serverLevel().players()) {
            player.connection.send(passengersPacket);
        }

        // 发送确认包
        PacketDistributor.sendToAllPlayers(
            new PlayerMountConfirmPacket(rider.getId(), target.getId(), rider.isPassenger())
        );
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 