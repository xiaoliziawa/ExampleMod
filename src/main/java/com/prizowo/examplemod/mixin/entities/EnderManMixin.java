package com.prizowo.examplemod.mixin.entities;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends LivingEntity {

    @Unique
    private boolean exampleMod$isProcessingProjectileDamage = false;

    protected EnderManMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!exampleMod$isProcessingProjectileDamage && source.is(DamageTypeTags.IS_PROJECTILE)) {
            exampleMod$isProcessingProjectileDamage = true;
            try {
                boolean result = super.hurt(source, amount);
                cir.setReturnValue(result);
            } finally {
                exampleMod$isProcessingProjectileDamage = false;
            }
        }
    }

    @Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    public void isSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "hurtWithCleanWater", at = @At("HEAD"), cancellable = true)
    public void hurtWithCleanWater(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
