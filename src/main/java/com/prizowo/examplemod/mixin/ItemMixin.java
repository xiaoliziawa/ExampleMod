package com.prizowo.examplemod.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getBreakingSound", at = @At("HEAD"), cancellable = true)
    private void onGetBreakingSound(CallbackInfoReturnable<SoundEvent> cir) {
        ResourceLocation location = ResourceLocation.tryParse("minecraft:entity.generic.explode");
        if (location != null) {
            SoundEvent newSound = SoundEvent.createVariableRangeEvent(location);
            cir.setReturnValue(newSound);
            cir.cancel();
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemstack = player.getItemInHand(usedHand);

        if (itemstack.getFoodProperties(player) == null) {
            FoodProperties defaultFood = (new FoodProperties.Builder()).nutrition(1).saturationModifier(3.0F).build();
            itemstack.set(DataComponents.FOOD, defaultFood);
        }

        player.startUsingItem(usedHand);
        cir.setReturnValue(InteractionResultHolder.consume(itemstack));
    }
}
