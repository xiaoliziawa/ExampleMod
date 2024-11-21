package com.prizowo.examplemod.datagen;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.Reg.ItemReg;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Examplemod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ItemReg.CUSTOM_BLOCK_ITEM.get());
        basicItem(ItemReg.CUSTOM_ITEM_2.get());
        basicItem(ItemReg.MY_HUMANOID_SPAWN_EGG.get());
        basicItem(ItemReg.CUSTOM_ITEM_1.get());
        basicItem(ItemReg.CUSTOM_ITEM.get());
    }
}
