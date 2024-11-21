package com.prizowo.examplemod.render;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class EntityOutlineRenderer {

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        
        // 对每个玩家处理
        for (Player player : level.players()) {
            // 获取玩家周围50格内的所有生物
            AABB searchBox = player.getBoundingBox().inflate(50.0D);
            level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> !(entity instanceof Player))
                .forEach(entity -> {
                    CompoundTag tag = entity.getPersistentData();
                    if (!tag.getBoolean("Glowing")) {
                        tag.putBoolean("Glowing", true);
                        entity.setGlowingTag(true);
                    }
                });
            // 移除超出范围实体的发光效果
            level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(100.0D),
                Entity::isCurrentlyGlowing)
                .forEach(entity -> {
                    if (player.distanceTo(entity) > 50.0D) {
                        CompoundTag tag = entity.getPersistentData();
                        if (tag.getBoolean("Glowing")) {
                            tag.putBoolean("Glowing", false);
                            entity.setGlowingTag(false);
                        }
                    }
                });
        }
    }
}
