package com.prizowo.examplemod.init;

import com.mojang.serialization.MapCodec;
import com.prizowo.examplemod.Reg.BlockEntities;
import com.prizowo.examplemod.custom.MyCustomFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MyCustomFurnaceBlock extends AbstractFurnaceBlock {
    public static final MapCodec<MyCustomFurnaceBlock> CODEC = simpleCodec(MyCustomFurnaceBlock::new);

    public MyCustomFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<MyCustomFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MyCustomFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createFurnaceTicker(level, blockEntityType, BlockEntities.MY_CUSTOM_FURNACE.get());
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof MyCustomFurnaceBlockEntity) {
            player.openMenu((MenuProvider)blockentity);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(LIT)) {
            double d0 = pos.getX() + 0.5;
            double d1 = pos.getY();
            double d2 = pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double d4 = random.nextDouble() * 0.6 - 0.3;
            double d5 = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : d4;
            double d6 = random.nextDouble() * 6.0 / 16.0;
            double d7 = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : d4;
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
        }
    }
}
