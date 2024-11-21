package com.prizowo.examplemod.events;

import com.prizowo.examplemod.movement.MountMovementController;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import com.prizowo.examplemod.Examplemod;

@EventBusSubscriber(modid = Examplemod.MODID)
public class MountMovementEvents {
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.hasTime()) {
            event.getLevel().players().forEach(player -> {
                if (player.getVehicle() instanceof Mob mount) {
                    MountMovementController.updateMountMovement(mount, player);
                }
            });
        }
    }
} 