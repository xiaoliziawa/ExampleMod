package com.prizowo.examplemod.mixin.entities;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public class TridentEntityMixin {

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void onHitEntityMixin(EntityHitResult result, CallbackInfo ci) {
        ThrownTrident trident = (ThrownTrident)(Object)this;
        Level level = trident.level();

        if (!level.isClientSide) {
            Entity owner = trident.getOwner();

            level.explode(
                    trident,
                    trident.getX(),
                    trident.getY(),
                    trident.getZ(),
                    20.0F,
                    false,
                    Level.ExplosionInteraction.NONE
            );
            level.playSound(null, trident.getX(), trident.getY(), trident.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 5.0F, 1.0F);

            if (owner instanceof Player player) {
                ItemStack tridentItem = trident.getWeaponItem();
                if (tridentItem != null) {
                    for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry1: tridentItem.getTagEnchantments().entrySet()) {
                        if (holderEntry1.getKey().getKey() == Enchantments.CHANNELING) {
                            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                                            trident.getBoundingBox().inflate(50),
                                            entity -> entity != owner && !(entity instanceof Player))
                                    .forEach(entity -> {
                                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                                        level.playSound(null, trident.getX(), trident.getY(), trident.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 5.0F, 1.0F);
                                        if (lightning != null) {
                                            lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
                                            lightning.setVisualOnly(false);
                                            level.addFreshEntity(lightning);
                                        }
                                    });
                        }
                    }

                    for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : tridentItem.getTagEnchantments().entrySet()) {
                        if (holderEntry.getKey().getKey() == Enchantments.INFINITY) {
                            if (!player.hasInfiniteMaterials()) {
                                player.getInventory().add(tridentItem.copy());
                            }
                            break;
                        }
                    }
                }
            }
        }
        trident.setRemoved(Entity.RemovalReason.DISCARDED);
    }
}
