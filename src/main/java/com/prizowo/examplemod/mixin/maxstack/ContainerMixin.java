package com.prizowo.examplemod.mixin.maxstack;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Container.class)
public interface ContainerMixin {
    @ModifyReturnValue(method = {"getMaxStackSize()I"}, at = {@At("RETURN")})
    private int maximizeStackSize(int original) {
        return 1073741823;
    }
}
