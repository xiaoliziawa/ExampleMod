package com.prizowo.examplemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {

    @Shadow private BlockState movedState;
    @Shadow
    private Direction direction;
    @Shadow private boolean extending;

    @Unique
    abstract Level exampleMod$getLevel();

    @Unique
    abstract BlockPos exampleMod$getBlockPos();

    @Inject(method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)V", at = @At("RETURN"))
    private void onConstructor(BlockPos pos, BlockState blockState, BlockState movedState, Direction direction, boolean extending, boolean isSourcePiston, CallbackInfo ci) {
        if (movedState.is(Blocks.BEDROCK)) {
            // 允许推动基岩
            this.movedState = movedState;
        }
    }

    @Inject(method = "finalTick", at = @At("HEAD"), cancellable = true)
    private void onFinalTick(CallbackInfo ci) {
        if (this.movedState.is(Blocks.BEDROCK)) {
            // 对于基岩，我们需要特殊处理
            Level level = this.exampleMod$getLevel();
            BlockPos pos = this.exampleMod$getBlockPos();
            if (level != null) {
                if (this.extending) {
                    level.setBlock(pos.relative(this.direction), Blocks.BEDROCK.defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                }
                level.removeBlockEntity(pos);
            }
            ci.cancel(); // 取消原有的finalTick逻辑
        }
    }
}
