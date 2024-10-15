package com.prizowo.examplemod.init;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MyCustomItem extends Item {
    public MyCustomItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                GameType currentGameMode = serverPlayer.gameMode.getGameModeForPlayer();
                GameType newGameMode;

                if (currentGameMode == GameType.CREATIVE) {
                    newGameMode = GameType.SURVIVAL;
                } else {
                    newGameMode = GameType.CREATIVE;
                }

                serverPlayer.setGameMode(newGameMode);
                String modeName = newGameMode == GameType.CREATIVE ? "创造模式" : "生存模式";
                serverPlayer.sendSystemMessage(Component.literal("您的游戏模式已更改为: " + modeName));
            }

            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 24000, 5, false, false));
            player.getCooldowns().addCooldown(this, 0);
        }

        level.addParticle(ParticleTypes.HEART, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);

        return InteractionResultHolder.success(stack);
    }
}
