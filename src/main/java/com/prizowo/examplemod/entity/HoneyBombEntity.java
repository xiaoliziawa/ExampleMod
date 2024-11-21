package com.prizowo.examplemod.entity;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;

public class HoneyBombEntity extends ThrowableItemProjectile {
    public HoneyBombEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public HoneyBombEntity(Level level, LivingEntity shooter) {
        super(EntityReg.HONEY_BOMB.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.HONEY_BLOCK;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            // 爆炸效果
            serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                this.getX(), this.getY(), this.getZ(),
                3, 0.0, 0.0, 0.0, 0.0
            );
            
            // 蜂蜜飞溅效果
            for (int i = 0; i < 30; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double radius = this.random.nextDouble() * 6;
                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;
                
                serverLevel.sendParticles(
                    ParticleTypes.DRIPPING_HONEY,
                    this.getX() + offsetX, 
                    this.getY(), 
                    this.getZ() + offsetZ,
                    1, 0.0, 0.1, 0.0, 0.0
                );
            }
            
            // 播放爆炸音效
            this.level().playSound(null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.NEUTRAL,
                2.0F, 0.5F
            );
            
            // 影响范围内的实体
            double radius = 6.0;
            AABB effectArea = new AABB(
                this.getX() - radius, this.getY() - 1, this.getZ() - radius,
                this.getX() + radius, this.getY() + 2, this.getZ() + radius
            );
            
            for (Entity target : this.level().getEntities(this, effectArea)) {
                if (target instanceof LivingEntity livingTarget && target != this.getOwner()) {
                    livingTarget.hurt(this.damageSources().thrown(this, this.getOwner()), 10.0f);
                    livingTarget.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, 
                        200, // 10秒
                        4,   // 等级5
                        false, 
                        true
                    ));
                }
            }
            
            this.discard();
        }
    }
} 