package com.prizowo.examplemod.client;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.network.MountEntityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT)
public class ClientForgeEvents {
    
    private static boolean overlayEnabled = true;
    
    @SubscribeEvent
    public static void onClientTick(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        
        if (minecraft.player != null) {
            if (KeyBindings.MOUNT_KEY.consumeClick()) {
                Entity target = minecraft.crosshairPickEntity;
                if (target != null && minecraft.player.distanceTo(target) < 3.0) {
                    PacketDistributor.sendToServer(new MountEntityPacket(target.getId()));
                }
            }
            
            if (KeyBindings.TOGGLE_OVERLAY.consumeClick()) {
                overlayEnabled = !overlayEnabled;
            }
        }
    }
    
    public static boolean isOverlayEnabled() {
        return overlayEnabled;
    }
} 