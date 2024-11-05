package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MountFlyPacket(boolean isAscending) implements CustomPacketPayload {
    public static final Type<MountFlyPacket> TYPE = new Type<>(Examplemod.prefix("mount_fly"));

    public static final StreamCodec<FriendlyByteBuf, MountFlyPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        MountFlyPacket::isAscending,
        MountFlyPacket::new
    );

    public static void handle(final MountFlyPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.getVehicle() instanceof Mob mount) {
                    float flySpeed = 0.4f;
                    double currentX = mount.getDeltaMovement().x;
                    double currentZ = mount.getDeltaMovement().z;
                    double newY = data.isAscending ? flySpeed : -flySpeed;
                    mount.setDeltaMovement(currentX, newY, currentZ);
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