package com.prizowo.examplemod.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomingArrow extends AbstractArrow {
    private int ticksInAir = 0;
    private boolean hasSpawned = false;
    private int lifetimeCounter = 0;
    private static final int SPLIT_TIME = 100; // 5秒后分裂
    private static final int MAX_LIFETIME = 200; // 10秒后消失
    private static final double TRACKING_RANGE = 10.0; // 追踪范围
    private static final double HOMING_SPEED = 0.3; // 追踪速度
    
    public HomingArrow(Level level, LivingEntity shooter) {
        super(EntityType.ARROW, level);
        this.setOwner(shooter);
        this.pickup = Pickup.DISALLOWED;
    }

    public HomingArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.ARROW); // 返回一个箭的ItemStack，虽然实际上不会被拾取
    }

    @Override
    public void tick() {
        super.tick();
        ticksInAir++;
        lifetimeCounter++;

        // 检查是否应该消失
        if (lifetimeCounter >= MAX_LIFETIME) {
            this.discard();
            return;
        }

        // 5秒后分裂
        if (ticksInAir == SPLIT_TIME && !hasSpawned) {
            splitArrow();
            hasSpawned = true;
        }

        // 开始追踪最近的生物
        if (ticksInAir > SPLIT_TIME && !this.level().isClientSide) {
            LivingEntity target = findNearestTarget();
            if (target != null) {
                Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                Vec3 arrowPos = this.position();
                Vec3 motion = targetPos.subtract(arrowPos).normalize().scale(HOMING_SPEED);
                
                // 保持一定的原始速度
                Vec3 currentMotion = this.getDeltaMovement();
                this.setDeltaMovement(
                    currentMotion.x * 0.8 + motion.x * 0.2,
                    currentMotion.y * 0.8 + motion.y * 0.2,
                    currentMotion.z * 0.8 + motion.z * 0.2
                );
            }
        }
    }

    private void splitArrow() {
        if (!this.level().isClientSide) {
            for (int i = 0; i < 8; i++) {
                HomingArrow arrow = new HomingArrow(this.level(), (LivingEntity) this.getOwner());
                arrow.setPos(this.getX(), this.getY(), this.getZ());
                
                // 设置箭的初始速度和方向
                double angle = (Math.PI * 2 * i) / 8;
                double vx = Math.cos(angle) * 0.5;
                double vy = 0.2;
                double vz = Math.sin(angle) * 0.5;
                
                arrow.setDeltaMovement(vx, vy, vz);
                arrow.hasSpawned = true; // 防止新箭继续分裂
                arrow.ticksInAir = SPLIT_TIME; // 让新箭立即开始追踪
                
                this.level().addFreshEntity(arrow);
            }
            this.discard(); // 移除原始箭
        }
    }

    private LivingEntity findNearestTarget() {
        AABB searchBox = this.getBoundingBox().inflate(TRACKING_RANGE);
        List<Entity> entities = this.level().getEntities(this, searchBox);
        
        LivingEntity nearestTarget = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && !(entity instanceof Player) && entity != this.getOwner()) {
                double distanceSq = this.distanceToSqr(entity);
                if (distanceSq < nearestDistanceSq) {
                    nearestDistanceSq = distanceSq;
                    nearestTarget = (LivingEntity) entity;
                }
            }
        }

        return nearestTarget;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        this.discard(); // 击中方块后消失
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        this.discard(); // 击中实体后消失
    }
}
