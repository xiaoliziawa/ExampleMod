package com.prizowo.examplemod.custom;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomSnowball extends Snowball {
    private static final Random RANDOM = new Random();
    private static final List<ParticleOptions> PARTICLES = new ArrayList<>();
    private static final float EXPLOSION_RADIUS = 4.0F;
    private static final float EXPLOSION_POWER = 2.0F;

    static {
        for (Field field : ParticleTypes.class.getDeclaredFields()) {
            try {
                Object obj = field.get(null);
                if (obj instanceof ParticleOptions && !field.getName().equals("ELDER_GUARDIAN")) {
                    PARTICLES.add((ParticleOptions) obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public CustomSnowball(EntityType<? extends Snowball> type, Level world) {
        super(type, world);
    }

    public CustomSnowball(Level world, LivingEntity shooter) {
        super(world, shooter);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.spawnRandomParticles();
        }
    }

    private void spawnRandomParticles() {
        for (int i = 0; i < 5; i++) {
            double offsetX = RANDOM.nextDouble() * 0.5 - 0.25;
            double offsetY = RANDOM.nextDouble() * 0.5 - 0.25;
            double offsetZ = RANDOM.nextDouble() * 0.5 - 0.25;

            ParticleOptions randomParticle = PARTICLES.get(RANDOM.nextInt(PARTICLES.size()));

            this.level().addParticle(randomParticle,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                    EXPLOSION_RADIUS, true, Level.ExplosionInteraction.TNT);

            this.level().getEntities(this, this.getBoundingBox().inflate(EXPLOSION_RADIUS),
                            entity -> !(entity instanceof CustomSnowball))
                    .forEach(entity -> entity.hurt(this.damageSources().explosion(this, this.getOwner()), EXPLOSION_POWER));

            this.discard();
        }
    }
}
