package com.prizowo.examplemod.mixin.entities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Unique
    private boolean exampleMod$isAirSwimming = false;

    protected PlayerMixin(EntityType<? extends LivingEntity> living, Level level) {
        super(living, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.isSprinting() && !player.onGround() && player.getDeltaMovement().y < 0) {
            exampleMod$isAirSwimming = true;
        } else if (player.onGround() || player.isInWater()) {
            exampleMod$isAirSwimming = false;
        }

        if (exampleMod$isAirSwimming && player.isShiftKeyDown()) {
            exampleMod$isAirSwimming = false;
        }
    }

    @Inject(method = "isSwimming", at = @At("RETURN"), cancellable = true)
    private void onIsSwimming(CallbackInfoReturnable<Boolean> cir) {
        if (exampleMod$isAirSwimming) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (exampleMod$isAirSwimming && !player.isInWater() && !player.isPassenger()) {
            Vec3 lookAngle = player.getLookAngle();

            double speed = 0.02;

            Vec3 extraMovement = new Vec3(
                    lookAngle.x * speed,
                    lookAngle.y * speed,
                    lookAngle.z * speed
            );

            player.setDeltaMovement(player.getDeltaMovement().add(extraMovement));

            player.setDeltaMovement(player.getDeltaMovement().add(0, 0.08, 0));

            player.resetFallDistance();
        }
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void onUpdateSwimming(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (exampleMod$isAirSwimming) {
            player.setSwimming(true);
            ci.cancel();
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void onGetHurtSound(CallbackInfoReturnable<net.minecraft.sounds.SoundEvent> cir) {
        cir.setReturnValue(SoundEvents.PLAYER_LEVELUP);
        cir.cancel();
    }
}
