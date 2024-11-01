package com.prizowo.examplemod.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MultiShotBow extends BowItem {
    private static final int ARROWS_PER_LAYER = 8;
    private static final int LAYERS = 3;
    private static final int TOTAL_ARROWS = ARROWS_PER_LAYER * LAYERS;

    public MultiShotBow(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean isCreative = player.getAbilities().instabuild;
            ItemStack ammo = player.getProjectile(stack);
            
            if (!ammo.isEmpty()) {
                if (!isCreative && ammo.getCount() < TOTAL_ARROWS) {
                    return;
                }

                float power = 1.0F; // 固定最大力量，无需蓄力
                if (!level.isClientSide) {
                    int layer = 0;
                    while (layer < LAYERS) {
                        float currentVerticalAngle = -10.0F + (float)layer * 10.0F;

                        for (int j = 0; j < ARROWS_PER_LAYER; ++j) {
                            Arrow arrowEntity = createArrow(level, player);
                            arrowEntity.setCritArrow(true);
                            arrowEntity.pickup = isCreative ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
                            
                            float currentHorizontalAngle = -52.5F + (float)j * 15.0F;
                            arrowEntity.shootFromRotation(player, 
                                player.getXRot() + currentVerticalAngle,  // 垂直角度
                                player.getYRot() + currentHorizontalAngle, // 水平角度
                                0.0F, 
                                power * 3.0F,  // 速度
                                1.0F);  // 散布
                            
                            level.addFreshEntity(arrowEntity);
                        }
                        ++layer;
                    }

                    // 消耗箭矢
                    if (!isCreative) {
                        ammo.shrink(TOTAL_ARROWS);
                        if (ammo.isEmpty()) {
                            player.getInventory().removeItem(ammo);
                        }
                    }
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                        1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    private Arrow createArrow(Level level, Player player) {
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        return new Arrow(level, player, arrowStack, player.getMainHandItem());
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean hasAmmo = !player.getProjectile(itemstack).isEmpty();

        if (!player.getAbilities().instabuild && !hasAmmo) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            // 直接释放，无需蓄力
            this.releaseUsing(itemstack, level, player, 0);
            return InteractionResultHolder.consume(itemstack);
        }
    }
}
