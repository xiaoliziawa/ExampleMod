package com.prizowo.examplemod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import com.prizowo.examplemod.component.ModComponents;
import com.prizowo.examplemod.network.HammerRangePacket;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends DiggerItem {
    private static final int[] BREAK_RANGES = {1, 2, 3, 4, 6};
    
    public HammerItem(Tier tier, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, 
            properties.component(ModComponents.HAMMER_RANGE.get(), 0));
    }

    public void cycleRange(Player player, boolean forward) {
        if (!player.isShiftKeyDown()) return;

        ItemStack stack = player.getMainHandItem();
        int currentIndex = stack.getOrDefault(ModComponents.HAMMER_RANGE.get(), 0);
        
        if (forward) {
            currentIndex = (currentIndex + 1) % BREAK_RANGES.length;
        } else {
            currentIndex = (currentIndex - 1 + BREAK_RANGES.length) % BREAK_RANGES.length;
        }
        
        stack.set(ModComponents.HAMMER_RANGE.get(), currentIndex);
        
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(new HammerRangePacket(currentIndex));
        }
        
        int range = BREAK_RANGES[currentIndex] * 2 + 1;
        player.displayClientMessage(Component.literal("破坏范围设置为: " + range + "x" + range), true);
        
        System.out.println("Range index set to: " + currentIndex + ", actual range: " + range);
    }

    private int getCurrentRange(ItemStack stack) {
        int index = stack.getOrDefault(ModComponents.HAMMER_RANGE.get(), 0);
        return BREAK_RANGES[index];
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (!player.isShiftKeyDown()) {
                breakBlocks(stack, level, pos, player);
            }
            
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
            }
        }
        return true;
    }

    private void breakBlocks(ItemStack stack, Level level, BlockPos pos, Player player) {
        if (player.isShiftKeyDown()) {
            return;
        }

        Direction face = null;
        HitResult hitResult = player.pick(20.0D, 0.0F, false);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            face = blockHitResult.getDirection();
        } else {
            face = player.getDirection();
        }

        int range = getCurrentRange(stack);
        
        System.out.println("Current range: " + range);
        
        List<BlockPos> positions = getPositionsToBreak(pos, face, range);
        
        for (BlockPos blockPos : positions) {
            BlockState state = level.getBlockState(blockPos);
            if (player.mayBuild()) {
                level.destroyBlock(blockPos, !player.getAbilities().instabuild, player);
            }
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return getTier().getSpeed();
    }

    private List<BlockPos> getPositionsToBreak(BlockPos center, Direction face, int range) {
        List<BlockPos> positions = new ArrayList<>();
        
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                BlockPos newPos;
                switch (face.getAxis()) {
                    case X -> newPos = center.offset(0, i, j);
                    case Y -> newPos = center.offset(i, 0, j);
                    case Z -> newPos = center.offset(i, j, 0);
                    default -> newPos = center;
                }
                
                if (Math.abs(i) <= range && Math.abs(j) <= range) {
                    positions.add(newPos);
                }
            }
        }
        
        return positions;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && !player.getAbilities().instabuild) {
            stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }
} 