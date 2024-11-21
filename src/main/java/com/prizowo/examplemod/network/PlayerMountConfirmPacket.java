package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayerMountConfirmPacket(int riderId, int targetId, boolean mounting) implements CustomPacketPayload {
    public static final Type<PlayerMountConfirmPacket> TYPE = new Type<>(Examplemod.prefix("player_mount_confirm"));

    public static final StreamCodec<FriendlyByteBuf, PlayerMountConfirmPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> {
                buf.writeVarInt(packet.riderId());
                buf.writeVarInt(packet.targetId());
                buf.writeBoolean(packet.mounting());
            },
            buf -> new PlayerMountConfirmPacket(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean()
            )
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final PlayerMountConfirmPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Entity rider = mc.level.getEntity(data.riderId());
                Entity target = mc.level.getEntity(data.targetId());
                if (rider != null && target != null) {
                    if (data.mounting()) {
                        rider.startRiding(target, true);
                    } else {
                        rider.stopRiding();
                    }
                }
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 