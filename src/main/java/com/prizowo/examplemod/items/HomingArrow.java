package com.prizowo.examplemod.items;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomingArrow extends AbstractArrow {
    private static final double TRACKING_RANGE = 50.0; // 追踪范围
    private static final double TURN_SPEED = 0.15; // 调整转向速度
    private static final int TRACKING_DELAY = 5; // 飞行5tick后开始追踪
    private static final int SPLIT_DELAY = 10; // 飞行10tick后分裂
    private static final int SPLIT_COUNT = 8; // 分裂成8个子弹头
    private static final double MIN_HEIGHT = 1.0; // 最小飞行高度
    private static final int MAX_LIFETIME = 200; // 最大存活时间（10秒）
    private static final int GROUND_DESPAWN_TIME = 60; // 3秒 = 60tick
    private static final int FAR_DESPAWN_TIME = 300; // 15秒 = 300tick
    private static final double FAR_DISTANCE = 50.0; // 远离玩家的距离阈值
    private Entity target = null;
    private int ticksInAir = 0;
    private int ticksInGround = 0; // 添加地面计时器
    private int ticksFarFromPlayer = 0; // 记录远离玩家的时间
    private boolean hasSplit = false;
    private Vec3 originalDirection; // 保存初始方向

    public HomingArrow(EntityType<? extends HomingArrow> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.pickup = Pickup.DISALLOWED;
    }

    public HomingArrow(Level level) {
        this(EntityReg.HOMING_ARROW.get(), level);
    }

    public HomingArrow(Level level, LivingEntity owner) {
        super(EntityReg.HOMING_ARROW.get(), owner, level, new ItemStack(Items.ARROW), null);
        this.setNoGravity(true);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    private void split() {
        if (!this.level().isClientSide && !hasSplit && this.getOwner() instanceof Player shooter) {
            Vec3 currentMotion = this.getDeltaMovement();
            double speed = currentMotion.length();
            originalDirection = currentMotion.normalize(); // 保存分裂时的方向

            // 获取附近的目标
            List<Entity> potentialTargets = new ArrayList<>();
            for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(TRACKING_RANGE))) {
                if (entity instanceof LivingEntity living 
                        && entity != shooter 
                        && !entity.isSpectator()
                        && living.isAlive()) {
                    potentialTargets.add(entity);
                }
            }

            // 创建子弹头
            for (int i = 0; i < Math.min(SPLIT_COUNT - 1, potentialTargets.size()); i++) {
                HomingArrow splitArrow = new HomingArrow(this.level(), (LivingEntity)this.getOwner());
                splitArrow.setPos(this.position());
                splitArrow.setDeltaMovement(currentMotion.scale(0.8));
                splitArrow.target = potentialTargets.get(i);
                splitArrow.hasSplit = true;
                splitArrow.ticksInAir = TRACKING_DELAY;
                splitArrow.setBaseDamage(this.getBaseDamage() * 0.7);
                splitArrow.originalDirection = currentMotion.normalize();
                this.level().addFreshEntity(splitArrow);
            }

            if (!potentialTargets.isEmpty()) {
                this.target = potentialTargets.get(potentialTargets.size() - 1);
            }

            hasSplit = true;
        }
    }

    @Override
    public void tick() {
        if (this.inGround) {
            ticksInGround++;
            if (ticksInGround >= GROUND_DESPAWN_TIME) {
                this.discard();
                return;
            }
        } else {
            ticksInGround = 0;
        }

        super.tick();
        ticksInAir++;
        
        // 检查最大存活时间
        if (ticksInAir >= MAX_LIFETIME) {
            this.discard();
            return;
        }

        // 检查是否远离玩家
        if (this.getOwner() instanceof Player player) {
            double distanceToPlayer = this.distanceTo(player);
            if (distanceToPlayer > FAR_DISTANCE) {
                ticksFarFromPlayer++;
                if (ticksFarFromPlayer >= FAR_DESPAWN_TIME) {
                    this.discard();
                    return;
                }
            } else {
                ticksFarFromPlayer = 0; // 如果回到范围内，重置计时器
            }
        }

        // 检查是否应该分裂
        if (ticksInAir == SPLIT_DELAY && !hasSplit) {
            split();
            originalDirection = this.getDeltaMovement().normalize();
        }
        
        if (!this.level().isClientSide && this.getOwner() instanceof Player shooter && ticksInAir >= TRACKING_DELAY) {
            Vec3 currentMotion = this.getDeltaMovement();
            double speed = currentMotion.length();
            
            // 如果还没有目标且未分裂，寻找最近的目标
            if ((target == null || !target.isAlive()) && !hasSplit) {
                double closestDistance = TRACKING_RANGE;
                
                for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(TRACKING_RANGE))) {
                    if (entity instanceof LivingEntity living 
                            && entity != shooter 
                            && !entity.isSpectator()
                            && living.isAlive()) {
                        
                        Vec3 toTarget = entity.position().subtract(this.position()).normalize();
                        double dot = currentMotion.normalize().dot(toTarget);
                        
                        if (dot > -0.5) {
                            double distance = this.distanceTo(entity);
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                target = entity;
                            }
                        }
                    }
                }
            }

            Vec3 newDirection;
            if (target != null && target.isAlive()) {
                // 有目标时的追踪逻辑
                Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                Vec3 targetMotion = target.getDeltaMovement();
                targetPos = targetPos.add(targetMotion.scale(2.0));
                
                newDirection = targetPos.subtract(this.position()).normalize();
                
                if (this.getY() < target.getY() + MIN_HEIGHT) {
                    newDirection = newDirection.add(0, 0.1, 0).normalize();
                }
            } else {
                // 没有目标时保持原有方向
                newDirection = originalDirection != null ? originalDirection : currentMotion.normalize();
            }

            // 确保箭矢保持移动
            Vec3 newMotion = currentMotion.normalize().lerp(newDirection, TURN_SPEED).normalize().scale(speed);
            
            this.setDeltaMovement(newMotion);
            this.setYRot((float) (Math.atan2(newMotion.x, newMotion.z) * 180.0F / Math.PI));
            this.setXRot((float) (Math.atan2(newMotion.y, Math.sqrt(newMotion.x * newMotion.x + newMotion.z * newMotion.z)) * 180.0F / Math.PI));
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        // 击中方块时重置地面计时器
        ticksInGround = 0;
    }
} 