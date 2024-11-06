package com.prizowo.examplemod.events;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;

@EventBusSubscriber(modid = Examplemod.MODID)
public class MountEvents {
    
    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        if (event.isDismounting()) {
            Entity mount = event.getEntityBeingMounted();
            if (mount instanceof Mob mob) {
                // 恢复AI
                mob.setNoAi(false);
                
                // 如果是潜影贝，恢复重力
                if (mount instanceof Shulker) {
                    mount.setNoGravity(false);
                }
            }
        }
    }
} 