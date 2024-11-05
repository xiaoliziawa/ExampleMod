package com.prizowo.examplemod.events;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.PacketDistributor;
import com.prizowo.examplemod.network.MountAttackPacket;

@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT)
public class MountAttackEvents {

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        
        if (player != null && player.getVehicle() instanceof Mob) {
            PacketDistributor.sendToServer(new MountAttackPacket());
        }
    }
} 