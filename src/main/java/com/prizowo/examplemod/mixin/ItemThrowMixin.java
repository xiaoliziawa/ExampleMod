package com.prizowo.examplemod.mixin;

import com.prizowo.examplemod.entity.ThrownItemEntity;
import net.minecraft.world.item.Item;
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

@Mixin(Item.class)
public class ItemThrowMixin {
    
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, 
                      CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {

        ItemStack stack = player.getItemInHand(hand);
        
        if (player.isShiftKeyDown()) {

            // 检查投掷功能是否启用
            boolean throwEnabled = player.getPersistentData().getBoolean("throwEnabled");

            if (throwEnabled) {

                // 创建并发射物品实体
                ThrownItemEntity thrownItem = new ThrownItemEntity(level, player, stack);
                thrownItem.setPos(
                    player.getX() - Math.sin(Math.toRadians(player.getYRot())) * 0.5,
                    player.getEyeY() - 0.1,
                    player.getZ() + Math.cos(Math.toRadians(player.getYRot())) * 0.5
                );

                // 调整投掷速度
                float throwSpeed = 1.5F;
                // 较重的物品投掷速度较慢
                if (stack.getMaxStackSize() == 1) {
                    throwSpeed = 1.2F;
                }

                // 使用玩家的视角来设置投掷方向
                double motionX = -Math.sin(Math.toRadians(player.getYRot())) * Math.cos(Math.toRadians(player.getXRot()));
                double motionY = -Math.sin(Math.toRadians(player.getXRot()));
                double motionZ = Math.cos(Math.toRadians(player.getYRot())) * Math.cos(Math.toRadians(player.getXRot()));

                if (!level.isClientSide) {
                    thrownItem.shoot(motionX, motionY, motionZ, throwSpeed, 1.0F);
                    level.addFreshEntity(thrownItem);

                    // 播放投掷音效
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                            0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

                    // 如果不是创造模式,消耗物品
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                
                cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide()));
            }
        }
    }
} 