package com.prizowo.examplemod.mixin;


import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void modifyAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.0);
        cir.setReturnValue(builder);
    }
}
