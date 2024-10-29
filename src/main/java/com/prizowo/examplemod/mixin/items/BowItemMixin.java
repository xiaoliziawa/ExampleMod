package com.prizowo.examplemod.mixin.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public class BowItemMixin {
    
    @Unique
    private static final ThreadLocal<Boolean> IS_RELEASING = ThreadLocal.withInitial(() -> false);

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!player.getProjectile(itemstack).isEmpty() && !IS_RELEASING.get()) {
            try {
                IS_RELEASING.set(true);
                ((BowItem)(Object)this).releaseUsing(itemstack, level, player, 0);
            } finally {
                IS_RELEASING.set(false);
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide()));
        }
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void onReleaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft, CallbackInfo ci) {
        if (!IS_RELEASING.get()) {
            ci.cancel();
        }
    }
}
