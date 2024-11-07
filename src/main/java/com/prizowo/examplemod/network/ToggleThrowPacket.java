package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ToggleThrowPacket(boolean enabled) implements CustomPacketPayload {
    public static final Type<ToggleThrowPacket> TYPE = new Type<>(Examplemod.prefix("toggle_throw"));

    public static final StreamCodec<FriendlyByteBuf, ToggleThrowPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ToggleThrowPacket::enabled,
        ToggleThrowPacket::new
    );

    public static void handle(final ToggleThrowPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                // 使用NBT数据存储投掷开关状态
                player.getPersistentData().putBoolean("throwEnabled", data.enabled);
                System.out.println("Set throwEnabled to: " + data.enabled + " for player: " + player.getName().getString()); // 调试输出
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 