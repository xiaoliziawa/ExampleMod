package com.prizowo.examplemod.datagen;

import com.prizowo.examplemod.Reg.BlocksReg;
import com.prizowo.examplemod.Reg.ItemReg;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(BlocksReg.CUSTOM_BLOCK_2.get());

        add(BlocksReg.CUSTOM_BLOCK_2.get(),
                block -> createOreDrop(BlocksReg.CUSTOM_BLOCK_2.get(), ItemReg.CUSTOM_BLOCK_ITEM.get()));

        add(BlocksReg.CUSTOM_BLOCK.get(),
                block -> createMultifaceBlockDrops(BlocksReg.CUSTOM_BLOCK.get(), ItemReg.CUSTOM_ITEM.get(), 2, 3));

        dropSelf(BlocksReg.CRFTING_TABLE_SLAB.get());
    }

    private LootTable.Builder createMultifaceBlockDrops(Block block, Item item, float minDrop, float maxDrop) {
        HolderLookup.RegistryLookup<Enchantment>  registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block,LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop,maxDrop)))
                        .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))));
    }


    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BlocksReg.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
