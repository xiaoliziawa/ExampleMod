package com.prizowo.examplemod.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerThornsEffectMixin {

    @Unique
    private int exampleMod$thornsDamageCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        if (player.level().isClientSide) {
            return;
        }

        exampleMod$thornsDamageCounter++;

        if (exampleMod$thornsDamageCounter >= 20) {
            exampleMod$thornsDamageCounter = 0;

            ItemStack mainHandItem = player.getMainHandItem();
            boolean hasThorns = false;

            for (Object2IntMap.Entry<Holder<Enchantment>> entry : mainHandItem.getTagEnchantments().entrySet()) {
                if (entry.getKey().getKey() == Enchantments.THORNS) {
                    hasThorns = true;
                    break;
                }
            }

            if (hasThorns) {
                player.hurt(player.damageSources().magic(), 1.0F);
            }
        }
    }
}
