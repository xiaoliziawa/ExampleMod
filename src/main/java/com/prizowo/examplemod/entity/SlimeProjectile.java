package com.prizowo.examplemod.entity;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;

public class SlimeProjectile extends ThrowableProjectile {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/slime_block.png");
    
    public SlimeProjectile(EntityType<? extends SlimeProjectile> type, Level level) {
        super(type, level);
    }

    public SlimeProjectile(Level level, LivingEntity shooter) {
        super(EntityReg.SLIME_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (result instanceof EntityHitResult entityHit) {
                entityHit.getEntity().hurt(this.damageSources().mobProjectile(this, this.getOwner() instanceof LivingEntity living ? living : null), 5.0F);
            }
            this.discard();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }
} 