package com.prizowo.examplemod.mixin;

import com.prizowo.examplemod.entity.ThrownItemEntity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemThrowMixin {
    
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onSpawnEggUse(Level level, Player player, InteractionHand hand, 
                              CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player.isShiftKeyDown()) {
            boolean throwEnabled = player.getPersistentData().getBoolean("throwEnabled");
            if (throwEnabled) {
                ItemStack stack = player.getItemInHand(hand);
                ThrownItemEntity thrownItem = new ThrownItemEntity(level, player, stack);
                
                if (!level.isClientSide) {
                    thrownItem.setPos(
                        player.getX() - Math.sin(Math.toRadians(player.getYRot())) * 0.5,
                        player.getEyeY() - 0.1,
                        player.getZ() + Math.cos(Math.toRadians(player.getYRot())) * 0.5
                    );

                    double motionX = -Math.sin(Math.toRadians(player.getYRot())) * Math.cos(Math.toRadians(player.getXRot()));
                    double motionY = -Math.sin(Math.toRadians(player.getXRot()));
                    double motionZ = Math.cos(Math.toRadians(player.getYRot())) * Math.cos(Math.toRadians(player.getXRot()));
                    
                    thrownItem.shoot(motionX, motionY, motionZ, 1.5F, 1.0F);
                    level.addFreshEntity(thrownItem);

                    player.swing(InteractionHand.MAIN_HAND);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                            0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                
                cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide()));
            }
        }
    }
} 