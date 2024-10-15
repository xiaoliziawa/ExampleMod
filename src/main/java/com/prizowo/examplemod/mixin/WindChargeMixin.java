package com.prizowo.examplemod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level.ExplosionInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WindCharge.class)
public class WindChargeMixin {

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void onExplode(Vec3 pos, CallbackInfo ci) {
        WindCharge thisAs = (WindCharge) (Object) this;
        Level level = thisAs.level();

        // 创建增强的爆炸效果
        level.explode(thisAs, null, null,
                pos.x(), pos.y(), pos.z(), 5.0F, false,
                ExplosionInteraction.NONE,
                ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.GENERIC_EXPLODE
        );

        // 取消原版方法的执行
        ci.cancel();

        // 应用额外的击退效果
        exampleMod$applyKnockback(thisAs, pos);
    }

    @Unique
    private void exampleMod$applyKnockback(WindCharge windCharge, Vec3 pos) {
        double radius = 5.0;
        List<Entity> affectedEntities = windCharge.level().getEntities(windCharge, windCharge.getBoundingBox().inflate(radius));

        for (Entity entity : affectedEntities) {
            if (entity != windCharge.getOwner()) {
                Vec3 direction = entity.position().subtract(pos).normalize();

                double verticalForce = 5.0; // 调整这个值来改变击飞高度
                double horizontalForce = 1.5;

                Vec3 currentMotion = entity.getDeltaMovement();
                Vec3 additionalMotion = new Vec3(
                        direction.x * horizontalForce,
                        verticalForce,
                        direction.z * horizontalForce
                );

                entity.setDeltaMovement(currentMotion.add(additionalMotion));
                entity.hasImpulse = true;
            }
        }
    }
}
