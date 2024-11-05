package com.prizowo.examplemod.mixin.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        
        if (!self.getPassengers().isEmpty() && self.getPassengers().get(0) instanceof Player player) {
            self.setYRot(player.getYRot());
            self.yRotO = self.getYRot();
            self.setXRot(player.getXRot() * 0.5F);
            
            self.yBodyRot = self.getYRot();
            self.yHeadRot = self.yBodyRot;
        }
    }
} 