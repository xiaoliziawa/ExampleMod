package com.prizowo.examplemod.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WindChargeItem;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WindChargeItem.class)

public class WindChargeItemMixin {
    @Redirect(method ="use",at = @At(value = "INVOKE", target ="Lnet/minecraft/world/entity/player/Player;getCooldowns()Lnet/minecraft/world/item/ItemCooldowns;"))
    private ItemCooldowns removeCooldown(Player player) {
        return new ItemCooldowns() {
            @Override
            public void addCooldown(@NotNull Item item, int duration) {
            }
        };
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);

        for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : stack.getTagEnchantments().entrySet()) {
            if (holderEntry.getKey().getKey() == Enchantments.INFINITY) {
                cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide()));
            }
        }
    }
}
