package com.prizowo.examplemod.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class ExplosiveBow extends BowItem {
    private static final int ARROWS_PER_LAYER = 8;
    private static final int LAYER_COUNT = 3;
    private static final int TOTAL_ARROWS = 24;

    public ExplosiveBow(Item.Properties properties) {
        super(properties);
    }

    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean isCreative = player.getAbilities().instabuild;
            ItemStack ammo = player.getProjectile(stack);
            int i = this.getUseDuration(stack, entityLiving) - timeLeft;
            i = EventHooks.onArrowLoose(stack, level, player, i, !ammo.isEmpty());
            if (i < 0) {
                return;
            }

            if (!ammo.isEmpty()) {
                if (!isCreative && ammo.getCount() < 24) {
                    return;
                }

                float power = getPowerForTime(i);
                if (!((double)power < 0.1)) {
                    if (!level.isClientSide) {
                        int layer = 0;

                        while(true) {
                            if (layer >= 3) {
                                if (!isCreative) {
                                    ammo.shrink(24);
                                    if (ammo.isEmpty()) {
                                        player.getInventory().removeItem(ammo);
                                    }
                                }
                                break;
                            }

                            float currentVerticalAngle = -10.0F + (float)layer * 10.0F;

                            for(int j = 0; j < 8; ++j) {
                                ExplosiveArrow explosiveArrow = new ExplosiveArrow(level, player);
                                explosiveArrow.setCritArrow(power >= 1.0F);
                                explosiveArrow.pickup = Pickup.DISALLOWED;
                                float currentHorizontalAngle = -52.5F + (float)j * 15.0F;
                                explosiveArrow.shootFromRotation(player, player.getXRot() + currentVerticalAngle, player.getYRot() + currentHorizontalAngle, 0.0F, power * 3.0F, 1.0F);
                                level.addFreshEntity(explosiveArrow);
                            }

                            ++layer;
                        }
                    }

                    level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }

    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }
}
