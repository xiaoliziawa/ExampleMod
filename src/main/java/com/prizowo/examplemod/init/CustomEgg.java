package com.prizowo.examplemod.init;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CustomEgg extends ThrowableItemProjectile {
    public CustomEgg(Level level, LivingEntity shooter) {
        super(EntityType.SNOWBALL, shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) result;
                if (entityHit.getEntity() instanceof LivingEntity) {
                    // 生成您的自定义实体
                    EntityType<?> entityType = EntityReg.MY_HUMANOID.get();
                    entityType.spawn(this.level().getServer().getLevel(this.level().dimension()), null, null, this.blockPosition(), MobSpawnType.SPAWN_EGG, true, false);
                }
            } else if (result.getType() == HitResult.Type.BLOCK) {

                EntityType<?> entityType = EntityReg.MY_HUMANOID.get();
                entityType.spawn(this.level().getServer().getLevel(this.level().dimension()), null, null, this.blockPosition(), MobSpawnType.SPAWN_EGG, true, false);
            }
            this.discard();
        }
    }
}
