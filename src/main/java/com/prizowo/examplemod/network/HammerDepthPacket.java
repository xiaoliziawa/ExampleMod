package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.component.ModComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HammerDepthPacket(int depth) implements CustomPacketPayload {
    public static final Type<HammerDepthPacket> TYPE = new Type<>(Examplemod.prefix("hammer_depth"));

    public static final StreamCodec<FriendlyByteBuf, HammerDepthPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        HammerDepthPacket::depth,
        HammerDepthPacket::new
    );

    public static void handle(final HammerDepthPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                ItemStack stack = serverPlayer.getMainHandItem();
                stack.set(ModComponents.HAMMER_DEPTH.get(), data.depth);
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 