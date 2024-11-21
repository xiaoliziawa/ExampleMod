package com.prizowo.examplemod.mixin;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private static void onIsPushable(BlockState state, Level level, BlockPos pos, Direction direction, boolean destroyBlocks, Direction fromDirection, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.BEDROCK)) {
            cir.setReturnValue(true); // 允许推动基岩
        }
    }
}
