package com.prizowo.examplemod.entity;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThrownAxeEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> STUCK = SynchedEntityData.defineId(ThrownAxeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUCK_FACE = SynchedEntityData.defineId(ThrownAxeEntity.class, EntityDataSerializers.INT);
    private BlockPos stuckPos;
    private Direction stuckFace;
    private ItemStack thrownStack = ItemStack.EMPTY;
    private float yRotOnHit;
    private Vec3 hitLocation;

    public ThrownAxeEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownAxeEntity(Level level, LivingEntity shooter, ItemStack axeStack) {
        super(EntityReg.THROWN_AXE.get(), shooter, level);
        this.thrownStack = axeStack.copy();
        this.setItem(axeStack.copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STUCK, false);
        builder.define(STUCK_FACE, Direction.NORTH.ordinal());
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.IRON_AXE;
    }

    public boolean isStuck() {
        return this.entityData.get(STUCK);
    }

    public Direction getStuckFace() {
        return Direction.values()[this.entityData.get(STUCK_FACE)];
    }

    public float getYRotOnHit() {
        return this.yRotOnHit;
    }

    @Override
    public void tick() {
        if (isStuck()) {
            if (stuckPos != null) {
                // 检查方块是否还存在
                BlockState state = level().getBlockState(stuckPos);
                if (state.isAir()) {
                    this.entityData.set(STUCK, false);
                    stuckPos = null;
                } else {
                    // 保持在击中位置
                    if (hitLocation != null) {
                        this.setPos(hitLocation.x, hitLocation.y, hitLocation.z);
                    }
                }
            }
        } else {
            Vec3 movement = this.getDeltaMovement();
            
            // 添加重力效果，但比普通重力小
            double gravity = -0.03D;
            
            // 保持一定的水平速度，但随时间略微减少
            double horizontalDrag = 0.99;
            double verticalDrag = 0.98;
            
            this.setDeltaMovement(
                movement.x * horizontalDrag,
                Math.max(movement.y * verticalDrag + gravity, -0.8), // 限制最大下落速度
                movement.z * horizontalDrag
            );
            
            super.tick();
            
            // 更新实体的旋转，使其朝向移动方向
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7) {
                this.setYRot((float)(Mth.atan2(movement.x, movement.z) * (180F / Math.PI)));
            }
        }
    }

    private void setStuck(BlockPos pos, Direction face, BlockHitResult hitResult) {
        this.stuckPos = pos;
        this.stuckFace = face;
        this.entityData.set(STUCK, true);
        this.entityData.set(STUCK_FACE, face.ordinal());
        
        // 立即设置到击中位置
        this.hitLocation = hitResult.getLocation();
        this.setPos(hitLocation.x, hitLocation.y, hitLocation.z);
        
        // 保存击中时的旋转角度
        Vec3 movement = this.getDeltaMovement();
        this.yRotOnHit = (float)(Math.atan2(movement.x, movement.z) * (180F / Math.PI));
        
        // 完全停止移动
        this.setDeltaMovement(Vec3.ZERO);
        
        // 播放插入音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ARROW_HIT, SoundSource.PLAYERS,
                1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide && !isStuck()) {
            setStuck(result.getBlockPos(), result.getDirection(), result);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 10.0F);
            
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS,
                    1.0F, 1.0F);

            this.spawnAtLocation(this.getItem());
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && (isStuck() || this.onGround())) {
            player.take(this, 1);
            if (!player.getAbilities().instabuild) {
                player.getInventory().add(this.getItem());
            }
            this.discard();
        }
    }

    @Override
    public boolean isPushable() {
        return !isStuck();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
} 