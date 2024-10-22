package com.prizowo.examplemod.mixin.maxstack;


import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder, IItemStackExtension, MutableDataComponentHolder {
    @ModifyArg(method = {"lambda$static$3"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;"), index = 1, order = 1100)
    private static int injected(int value) {
        return 1073741823;
    }
}
