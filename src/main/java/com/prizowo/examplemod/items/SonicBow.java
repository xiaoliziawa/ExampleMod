package com.prizowo.examplemod.items;

import com.prizowo.examplemod.network.MagicProjectilePacket;
import com.prizowo.examplemod.network.MagicCirclePacket;
import com.prizowo.examplemod.network.SonicBoomPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;

public class SonicBow extends BowItem {

    public SonicBow(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        // 继承弓但不是弓(
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            Vec3 viewVec = player.getViewVector(1.0F);
            Vec3 start = player.getEyePosition();
            Vec3 direction = viewVec.normalize();
            double attackRange = 100.0;
            
            shootMagicProjectile(level, player, start, direction, attackRange);
        }
        
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    private void shootMagicProjectile(Level level, Player player, Vec3 start, Vec3 direction, double range) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                // 只发送魔法弹数据包，后续效果由粒子到达目标时触发
                PacketDistributor.sendToAllPlayers(new MagicProjectilePacket(start, direction, range));
            }
        }
    }

}