package com.prizowo.examplemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaceItem.class)
public abstract class MaceMixin {
    @Shadow public abstract float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource);

    @Inject(method ="canAttackBlock", at = @At("HEAD"), cancellable = true)
    private void canAttackBlock(BlockState state, Level level, BlockPos pos, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player.isCreative()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canSmashAttack", at = @At("HEAD"), cancellable = true)
    private static void canSmashAttack(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!entity.isFallFlying() && !entity.onGround() && entity.getDeltaMovement().y() >= 0) {
            cir.setReturnValue(true);

            cir.cancel();
        }
    }


    @Inject(method = "getAttackDamageBonus", at = @At("RETURN"), cancellable = true)
    private void getAttackDamageBonus(Entity target, float damage, DamageSource damageSource, CallbackInfoReturnable<Float> cir) {
        Entity attacker = damageSource.getDirectEntity();
        if (attacker instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player player && !player.onGround() && livingEntity.getDeltaMovement().y() >= 0) {
                float baseDamage =20.0F;
                float heightBonus = (float) (player.getY() - player.getOnPos().getY());
                float totalDamage = baseDamage + (heightBonus * 5.0F);

                if (player.level() instanceof ServerLevel serverLevel) {
                    totalDamage += EnchantmentHelper.modifyFallBasedDamage(serverLevel, player.getMainHandItem(), target, damageSource, 0.0F) * heightBonus;
                }

                cir.setReturnValue(totalDamage);
                cir.cancel();
            }
        }
    }
}

