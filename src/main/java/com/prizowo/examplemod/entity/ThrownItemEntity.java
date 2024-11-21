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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
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
        this.thrownStack = itemStack.split(1);
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
                BlockState state = level().getBlockState(stuckPos);
                if (state.isAir()) {
                    this.entityData.set(STUCK, false);
                    stuckPos = null;
                } else {
                    if (hitLocation != null) {
                        this.setPos(hitLocation.x, hitLocation.y, hitLocation.z);
                    }
                }
            }
        } else {
            Vec3 movement = this.getDeltaMovement();
            
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7) {
                this.setYRot((float)(Mth.atan2(movement.x, movement.z) * (180F / Math.PI)));
            }
            
            super.tick();
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && isStuck()) {
            this.spawnAtLocation(this.thrownStack);
            this.discard();
        }
    }

    private void setStuck(BlockPos pos, Direction face, BlockHitResult hitResult) {
        this.stuckPos = pos;
        this.stuckFace = face;
        this.entityData.set(STUCK, true);
        this.entityData.set(STUCK_FACE, face.ordinal());
        
        Vec3 hitLocation = hitResult.getLocation();
        
        double offset = 0.01;
        double x = hitLocation.x + face.getStepX() * offset;
        double y = hitLocation.y + face.getStepY() * offset;
        double z = hitLocation.z + face.getStepZ() * offset;
        
        this.hitLocation = new Vec3(x, y, z);
        this.setPos(x, y, z);
        
        Vec3 movement = this.getDeltaMovement();
        this.yRotOnHit = (float)(Mth.atan2(movement.x, movement.z) * (180F / Math.PI));
        
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);
        
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ANVIL_HIT, SoundSource.PLAYERS,
                1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide && !isStuck()) {
            // 保存击中时的朝向
            double dx = this.getDeltaMovement().x;
            double dz = this.getDeltaMovement().z;
            float hitYaw = (float) (Math.atan2(dx, dz) * (180F / Math.PI));
            this.setYRotOnHit(hitYaw);
            
            setStuck(result.getBlockPos(), result.getDirection(), result);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!this.level().isClientSide) {
            // 基础伤害为2
            float damage = 2.0F;
            
            if (thrownStack.isDamageableItem()) {
                damage += thrownStack.getMaxDamage() / 32.0F;
            }
            
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damage);
            
            this.spawnAtLocation(this.thrownStack);
            this.discard();
        }
    }

    @Override
    public ItemStack getItem() {
        return !this.thrownStack.isEmpty() ? this.thrownStack : super.getItem();
    }

    public void setYRotOnHit(float yRot) {
        this.yRotOnHit = yRot;
    }
} 