package com.prizowo.examplemod.client;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.network.MountAttackPacket;
import com.prizowo.examplemod.network.MountFlyPacket;
import com.prizowo.examplemod.network.PlayerMountPacket;
import com.prizowo.examplemod.network.ToggleThrowPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean throwEnabled = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        if (KeyBindings.MOUNT_KEY.consumeClick()) {
            if (minecraft.player.isPassenger()) {
                PacketDistributor.sendToServer(
                    new PlayerMountPacket(minecraft.player.getId(), 
                        minecraft.player.getVehicle().getId(), false)
                );
            } else {
                HitResult hit = minecraft.hitResult;
                if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                    Entity target = ((EntityHitResult) hit).getEntity();
                    if ((target instanceof Mob || target instanceof Player) && !target.isPassenger()) {
                        PacketDistributor.sendToServer(
                            new PlayerMountPacket(minecraft.player.getId(), target.getId(), true)
                        );
                    }
                }
            }
        }

        if (KeyBindings.DESCEND_KEY.consumeClick()) {
            if (minecraft.player.getVehicle() instanceof Mob) {
                PacketDistributor.sendToServer(new MountFlyPacket(false));
            }
        }

        if (KeyBindings.TOGGLE_THROW.consumeClick()) {
            throwEnabled = !throwEnabled;
            
            PacketDistributor.sendToServer(new ToggleThrowPacket(throwEnabled));
            
            minecraft.player.displayClientMessage(
                Component.translatable("message.examplemod.throw_" + (throwEnabled ? "enabled" : "disabled")),
                true
            );
        }

        if (minecraft.options.keyAttack.isDown() || minecraft.options.keyUse.isDown()) {
            if (minecraft.player.getVehicle() instanceof Mob) {
                PacketDistributor.sendToServer(new MountAttackPacket(minecraft.options.keyUse.isDown()));
            }
        }
    }
}

