package com.prizowo.examplemod.items;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ExplosiveArrow extends AbstractArrow {
    private float explosionPower = 2.0F;

    public ExplosiveArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public ExplosiveArrow(Level level, LivingEntity shooter) {
        super(EntityReg.EXPLOSIVE_ARROW.get(), shooter, level, ItemStack.EMPTY, null);
    }

    public ExplosiveArrow(Level level, double x, double y, double z) {
        super(EntityReg.EXPLOSIVE_ARROW.get(), x, y, z, level, ItemStack.EMPTY, null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 
                               explosionPower, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                               explosionPower, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }
}

