package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.client.ParticleEffectHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MagicProjectilePacket(Vec3 start, Vec3 direction, double range) implements CustomPacketPayload {
    public static final Type<MagicProjectilePacket> TYPE = new Type<>(Examplemod.prefix("magic_projectile"));

    public static final StreamCodec<FriendlyByteBuf, MagicProjectilePacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> {
                buf.writeDouble(packet.start().x);
                buf.writeDouble(packet.start().y);
                buf.writeDouble(packet.start().z);
                buf.writeDouble(packet.direction().x);
                buf.writeDouble(packet.direction().y);
                buf.writeDouble(packet.direction().z);
                buf.writeDouble(packet.range());
            },
            buf -> new MagicProjectilePacket(
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                buf.readDouble()
            )
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final MagicProjectilePacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ParticleEffectHandler.handleMagicProjectile(data.start(), data.direction(), data.range());
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 