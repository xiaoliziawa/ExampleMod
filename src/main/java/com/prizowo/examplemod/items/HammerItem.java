package com.prizowo.examplemod.items;

import com.prizowo.examplemod.network.HammerDepthPacket;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends DiggerItem {
    private static final int[] BREAK_RANGES = {1, 2, 3, 4, 6};
    private static final int[] BREAK_DEPTHS = {0, 1, 2, 3};
    
    public HammerItem(Tier tier, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, 
            properties.component(ModComponents.HAMMER_RANGE.get(), 0)
                     .component(ModComponents.HAMMER_DEPTH.get(), 0));
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
        
        player.level().playSound(
            player,                          // 播放给谁听
            player.getX(),                   // X坐标
            player.getY(),                   // Y坐标
            player.getZ(),                   // Z坐标
            SoundEvents.DISPENSER_DISPENSE,  // 音效
            SoundSource.PLAYERS,             // 音效类别
            0.5F,                           // 音量
            1.0F                            // 音调
        );
        
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(new HammerRangePacket(currentIndex));
        }
        
        int range = BREAK_RANGES[currentIndex] * 2 + 1;
        player.displayClientMessage(Component.literal("破坏范围设置为: " + range + "x" + range), true);
        
        System.out.println("Range index set to: " + currentIndex + ", actual range: " + range);
    }

    public void cycleDepth(Player player, boolean forward) {
        if (!player.isShiftKeyDown()) return;

        ItemStack stack = player.getMainHandItem();
        int currentIndex = stack.getOrDefault(ModComponents.HAMMER_DEPTH.get(), 0);
        
        if (forward) {
            currentIndex = (currentIndex + 1) % BREAK_DEPTHS.length;
        } else {
            currentIndex = (currentIndex - 1 + BREAK_DEPTHS.length) % BREAK_DEPTHS.length;
        }
        
        stack.set(ModComponents.HAMMER_DEPTH.get(), currentIndex);
        
        player.level().playSound(
            player,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.NOTE_BLOCK_CHIME,
            SoundSource.PLAYERS,
            0.5F,
            1.0F
        );
        
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(new HammerDepthPacket(currentIndex));
        }
        
        int depth = currentIndex == 0 ? 1 : (BREAK_DEPTHS[currentIndex] * 2 + 1);
        int range = BREAK_RANGES[stack.getOrDefault(ModComponents.HAMMER_RANGE.get(), 0)] * 2 + 1;
        player.displayClientMessage(Component.literal(
            String.format("挖掘范围设置为: %dx%dx%d", depth, range, range)), true);
    }

    private int getCurrentRange(ItemStack stack) {
        return BREAK_RANGES[stack.getOrDefault(ModComponents.HAMMER_RANGE.get(), 0)];
    }

    private int getCurrentDepth(ItemStack stack) {
        int depthIndex = stack.getOrDefault(ModComponents.HAMMER_DEPTH.get(), 0);
        return depthIndex == 0 ? 0 : BREAK_DEPTHS[depthIndex];
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
        int depth = getCurrentDepth(stack);
        
        List<BlockPos> positions = getPositionsToBreak(pos, face, range, depth);
        
        for (BlockPos blockPos : positions) {
            BlockState state = level.getBlockState(blockPos);
            if (state.isAir() || state.getDestroySpeed(level, blockPos) < 0) {
                continue;
            }
            if (player.mayBuild()) {
                level.destroyBlock(blockPos, !player.getAbilities().instabuild, player);
            }
        }
    }

    private List<BlockPos> getPositionsToBreak(BlockPos center, Direction face, int range, int depth) {
        List<BlockPos> positions = new ArrayList<>();
        
        int actualDepth = depth == 0 ? 0 : depth;
        
        for (int d = -actualDepth; d <= actualDepth; d++) {
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j <= range; j++) {
                    BlockPos newPos;
                    switch (face.getAxis()) {
                        case X -> newPos = center.offset(d, i, j);
                        case Y -> newPos = center.offset(i, d, j);
                        case Z -> newPos = center.offset(i, j, d);
                        default -> newPos = center;
                    }
                    
                    if (!newPos.equals(center)) {
                        positions.add(newPos);
                    }
                }
            }
        }
        
        return positions;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return getTier().getSpeed();
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