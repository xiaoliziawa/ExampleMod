package com.prizowo.examplemod.custom;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomEgg extends ThrowableItemProjectile {
    public CustomEgg(EntityType<? extends ThrowableItemProjectile> type, Level world) {
        super(type, world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.EGG;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            Level level = this.level();
            BlockPos pos = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());

            List<EntityType<?>> allEntities = new ArrayList<>();

            for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                allEntities.add(entityType);
            }

            if (!allEntities.isEmpty()) {
                EntityType<?> randomEntityType = allEntities.get(level.random.nextInt(allEntities.size()));

                try {
                    Entity entity = randomEntityType.create(level);
                    if (entity != null) {
                        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        if (entity instanceof Mob) {
                            ((Mob) entity).finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWN_EGG, null);
                        }
                        level.addFreshEntity(entity);
                    }
                } catch (Exception e) {
                    System.out.println("Failed to spawn entity: " + randomEntityType.getDescriptionId());
                    e.printStackTrace();
                }
            }
            this.discard();
        }
    }
}
