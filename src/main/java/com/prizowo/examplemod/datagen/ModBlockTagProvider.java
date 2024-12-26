package com.prizowo.examplemod.datagen;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.Reg.BlocksReg;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, 
                             CompletableFuture<HolderLookup.Provider> lookupProvider, 
                             @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Examplemod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 这里可以添加方块标签
        // 例如：
        //         this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
        //             .add(BlocksReg.CUSTOM_BLOCK.get());
    }

} 