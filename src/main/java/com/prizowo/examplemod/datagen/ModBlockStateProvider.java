package com.prizowo.examplemod.datagen;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.Reg.BlocksReg;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Examplemod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(BlocksReg.CUSTOM_BLOCK);
        blockWithItem(BlocksReg.CRFTING_TABLE_SLAB);
    }
    private void blockWithItem(Supplier<Block> deferredBlock ) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

}
