package com.prizowo.examplemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RedstoneLampBlock.class)
public class MixinRedstoneLampBlock extends Block {

    public MixinRedstoneLampBlock(Properties properties) {
        super(properties);
    }

    /**
     * @author PrizOwO
     * @reason Change Redstone Lamp behavior and add TNT ignition sound
     */
    @Overwrite
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean flag = state.getValue(RedstoneLampBlock.LIT);
            if (flag != level.hasNeighborSignal(pos)) {
                if (flag) {
                    level.scheduleTick(pos, (Block) (Object) this, 4);
                } else {
                    level.setBlock(pos, state.cycle(RedstoneLampBlock.LIT), 2);
                    level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    // 安排5秒后的爆炸
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.scheduleTick(pos, (Block) (Object) this, 100); // 100 ticks = 5 seconds
                    }
                }
            }
        }
    }

    /**
     * @author PrizOwO
     * @reason Add explosion to Redstone Lamp
     */
    @Overwrite
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(RedstoneLampBlock.LIT) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(RedstoneLampBlock.LIT), 2);
        } else if (state.getValue(RedstoneLampBlock.LIT)) {
            // 如果灯还是亮着的，就爆炸
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    4.0F, Level.ExplosionInteraction.TNT);
        }
    }
}
