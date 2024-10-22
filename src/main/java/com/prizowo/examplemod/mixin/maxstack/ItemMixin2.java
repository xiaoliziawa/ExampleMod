package com.prizowo.examplemod.mixin.maxstack;

import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class )
public abstract class ItemMixin2 implements FeatureElement, ItemLike, IItemExtension {

    @Shadow
    public static final int ABSOLUTE_MAX_STACK_SIZE = 1073741823;
}
