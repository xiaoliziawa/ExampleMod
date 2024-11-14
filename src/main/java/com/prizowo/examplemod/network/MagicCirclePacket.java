package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.client.ParticleEffectHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MagicCirclePacket(Vec3 targetPos) implements CustomPacketPayload {
    public static final Type<MagicCirclePacket> TYPE = new Type<>(Examplemod.prefix("magic_circle"));

    public static final StreamCodec<FriendlyByteBuf, MagicCirclePacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> {
                buf.writeDouble(packet.targetPos().x);
                buf.writeDouble(packet.targetPos().y);
                buf.writeDouble(packet.targetPos().z);
            },
            buf -> new MagicCirclePacket(
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            )
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final MagicCirclePacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> ParticleEffectHandler.handleMagicCircle(data.targetPos()));
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 