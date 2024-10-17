package com.prizowo.examplemod.init;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class LightningStaff {


    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.RightClickItem event) {
        handleLightningStaff(event.getEntity(), event.getEntity().level());
    }

    private static void handleLightningStaff(Player player, Level level) {
        if (!level.isClientSide) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() == Items.STICK) {
                level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50),
                                entity -> entity != player)
                        .forEach(entity -> {
                            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                            if (lightning != null) {
                                lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
                                lightning.setVisualOnly(false);
                                level.addFreshEntity(lightning);
                                entity.hurt(level.damageSources().lightningBolt(), 500f);
                                player.swing(InteractionHand.MAIN_HAND);
                            }
                        });
            }
        }
    }
}
