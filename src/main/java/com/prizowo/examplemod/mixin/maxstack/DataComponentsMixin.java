package com.prizowo.examplemod.mixin.maxstack;

import net.minecraft.core.component.DataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DataComponents.class )
public abstract class DataComponentsMixin {
    @ModifyArg(method = {"lambda$static$1"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;"), index = 1, order = 1100)
    private static int injected(int value) {
        return 1073741823;
    }
}
