package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MountEntityPacket(int targetEntityId) implements CustomPacketPayload {
    public static final Type<MountEntityPacket> TYPE = new Type<>(Examplemod.prefix("mount_entity"));

    public static final StreamCodec<FriendlyByteBuf, MountEntityPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        MountEntityPacket::targetEntityId,
        MountEntityPacket::new
    );

    public static void handle(final MountEntityPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                Entity target = serverPlayer.level().getEntity(data.targetEntityId);
                if (target != null && !target.isPassenger() && 
                    target.getPassengers().size() < 1) {
                    serverPlayer.startRiding(target, true);
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