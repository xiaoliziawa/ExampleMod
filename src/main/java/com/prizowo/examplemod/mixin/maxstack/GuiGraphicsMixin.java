package com.prizowo.examplemod.mixin.maxstack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.IGuiGraphicsExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements IGuiGraphicsExtension {
    @ModifyVariable(method = {"renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"}, at = @At("STORE"), name = {"s"})
    private String injected(String s, Font pFont, ItemStack pStack, int pX, int pY, @Nullable String pText) {
        if (pStack.getCount() < 1000 || pText != null) {
            return s;
        }
        return (pStack.getCount() / 1000) + "K";
    }
}
