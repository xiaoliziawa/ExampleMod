package com.prizowo.examplemod.datagen;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.Reg.ItemReg;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, 
                            CompletableFuture<HolderLookup.Provider> lookupProvider,
                            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Examplemod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // 添加弓的标签
        this.tag(ItemTags.BOW_ENCHANTABLE)
            .add(ItemReg.MULTI_SHOT_BOW.get())
            .add(ItemReg.HOMING_BOW.get())
            .add(ItemReg.EXPLOSIVE_BOW.get())
            .add(ItemReg.SONIC_BOW.get());

        // 添加工具标签
        this.tag(Tags.Items.TOOLS)
            .add(ItemReg.DIAMOND_HAMMER.get());
            
        // 添加工具附魔标签
        this.tag(ItemTags.MINING_ENCHANTABLE)
            .add(ItemReg.DIAMOND_HAMMER.get());

        // 添加生成蛋标签
        this.tag(Tags.Items.EGGS)
            .add(ItemReg.CUSTOM_SNOW_GOLEM_SPAWN_EGG.get())
            .add(ItemReg.MY_HUMANOID_SPAWN_EGG.get())
            .add(ItemReg.CUSTOM_BEE_SPAWN_EGG.get());
    }
}
