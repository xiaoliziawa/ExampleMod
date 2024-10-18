package com.prizowo.examplemod.custom;

import com.prizowo.examplemod.Reg.EntityReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class CustomSnowGolemSpawnEggItem extends SpawnEggItem {

    public CustomSnowGolemSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(EntityReg.CUSTOM_SNOW_GOLEM.get(), backgroundColor, highlightColor, properties);
    }

    @Override
    public @NotNull EntityType<?> getType(net.minecraft.world.item.ItemStack stack) {
        return EntityReg.CUSTOM_SNOW_GOLEM.get();
    }
}
