package com.prizowo.examplemod.entity;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ThrownItemEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> STUCK = SynchedEntityData.defineId(ThrownItemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUCK_FACE = SynchedEntityData.defineId(ThrownItemEntity.class, EntityDataSerializers.INT);
    private BlockPos stuckPos;
    private Direction stuckFace;
    private ItemStack thrownStack = ItemStack.EMPTY;
    private float yRotOnHit;
    private Vec3 hitLocation;

    public ThrownItemEntity(EntityType<? extends ThrownItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownItemEntity(Level level, LivingEntity shooter) {
        super(EntityReg.THROWN_ITEM.get(), shooter, level);
    }
    
    public ThrownItemEntity(Level level, LivingEntity shooter, ItemStack itemStack) {
        this(level, shooter);
        this.thrownStack = itemStack.split(1); // 只取一个物品
        this.setItem(this.thrownStack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STUCK, false);
        builder.define(STUCK_FACE, Direction.NORTH.ordinal());
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.STICK;
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
                if (level().getBlockState(stuckPos).isAir()) {
                    // 如果方块被破坏，掉落物品
                    if (!this.level().isClientSide) {
                        this.spawnAtLocation(this.thrownStack);
                    }
                    this.discard();
                } else {
                    // 保持在击中位置
                    if (hitLocation != null) {
                        this.setPos(hitLocation.x, hitLocation.y, hitLocation.z);
                    }
                    this.setDeltaMovement(Vec3.ZERO);
                }
            }
        } else {
            super.tick();
        }
    }

    // 添加玩家触碰时的处理
    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && isStuck()) {
            // 如果物品卡在方块上且玩家触碰，掉落物品
            this.spawnAtLocation(this.thrownStack);
            this.discard();
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
                SoundEvents.ANVIL_HIT, SoundSource.PLAYERS,
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
        if (!this.level().isClientSide) {
            // 基础伤害为2
            float damage = 2.0F;
            
            // 根据物品的材质增加伤害
            if (thrownStack.isDamageableItem()) {
                // 如果物品有耐久度,则根据最大耐久度来计算额外伤害
                damage += thrownStack.getMaxDamage() / 32.0F;
            }
            
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damage);
            
            // 掉落物品
            this.spawnAtLocation(this.thrownStack);
            this.discard();
        }
    }

    @Override
    public ItemStack getItem() {
        return !this.thrownStack.isEmpty() ? this.thrownStack : super.getItem();
    }
} 