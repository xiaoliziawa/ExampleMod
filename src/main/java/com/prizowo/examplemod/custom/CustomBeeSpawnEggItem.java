package com.prizowo.examplemod.custom;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class CustomBeeSpawnEggItem extends SpawnEggItem {
    public CustomBeeSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(EntityReg.CUSTOM_BEE.get(), backgroundColor, highlightColor, properties);
    }

    @Override
    public @NotNull EntityType<?> getType(ItemStack stack) {
        return EntityReg.CUSTOM_BEE.get();
    }
} 