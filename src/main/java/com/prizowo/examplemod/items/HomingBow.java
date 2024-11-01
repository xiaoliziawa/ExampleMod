
package com.prizowo.examplemod.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class HomingBow extends BowItem {
    public HomingBow(Item.Properties properties) {
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
                float f = 1.0F;
                if (!level.isClientSide) {
                    HomingArrow homingArrow = new HomingArrow(level, player);
                    homingArrow.setBaseDamage(homingArrow.getBaseDamage() * 2.0);
                    homingArrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                    homingArrow.setCritArrow(true);
                    homingArrow.pickup = Pickup.DISALLOWED;
                    level.addFreshEntity(homingArrow);
                    if (!isCreative) {
                        ammo.shrink(1);
                        if (ammo.isEmpty()) {
                            player.getInventory().removeItem(ammo);
                        }
                    }

                    level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }

    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(itemstack).isEmpty();
        if (!player.getAbilities().instabuild && !flag) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            this.releaseUsing(itemstack, level, player, 0);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }
}
