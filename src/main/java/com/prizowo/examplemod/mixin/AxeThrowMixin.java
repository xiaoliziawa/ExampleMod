package com.prizowo.examplemod.mixin;

import com.prizowo.examplemod.entity.ThrownAxeEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.item.Item;

@Mixin(AxeItem.class)
public abstract class AxeThrowMixin extends Item {

    public AxeThrowMixin(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                // 创建并发射斧头实体
                ThrownAxeEntity thrownAxe = new ThrownAxeEntity(level, player, stack);
                thrownAxe.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                thrownAxe.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownAxe);

                // 播放投掷音效
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                        1.0F, 1.0F);

                // 如果不是创造模式，消耗物品
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            // 不要调用setShiftKeyDown，直接返回结果
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return super.use(level, player, hand);
    }
} 