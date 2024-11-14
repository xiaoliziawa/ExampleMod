package com.prizowo.examplemod.custom.customentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class CustomBeeEntity extends Bee {
    private static final String SIZE_KEY = "CustomBeeSize";
    private static final Random random = new Random();
    private static final float MIN_SIZE = 0.5f; // 蜜蜂最小大小
    private static final float SIZE_RANGE = 4.0f; //蜜蜂最大大小
    private float customSize = -1.0f;

    public CustomBeeEntity(EntityType<? extends Bee> entityType, Level level) {
        super(entityType, level);
        if (customSize == -1.0f) {
            setRandomSize();
        }
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Bee.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FLYING_SPEED, 0.6F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    private void setRandomSize() {
        customSize = MIN_SIZE + random.nextFloat() * SIZE_RANGE;
        this.reapplyPosition();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(SIZE_KEY, customSize);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(SIZE_KEY)) {
            customSize = compound.getFloat(SIZE_KEY);
        } else {
            setRandomSize();
        }
        this.reapplyPosition();
    }

    @Override
    public float getScale() {
        return customSize;
    }

    @Override
    public boolean doHurtTarget(@NotNull net.minecraft.world.entity.Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag) {
            float damageMultiplier = customSize;
            entity.hurt(this.damageSources().sting(this), damageMultiplier * 2.0F);
        }
        return flag;
    }
} 