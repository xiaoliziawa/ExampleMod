package com.prizowo.examplemod.datagen;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.enchant.RegistryDataGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // 方块Tag
        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(output, lookupProvider, helper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        
        // 物品Tag
        generator.addProvider(event.includeServer(), 
            new ModItemTagProvider(output, lookupProvider, blockTagProvider.contentsGetter(), helper));

        // 其他提供者
//        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
//                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
//        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, helper));
//        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, helper));
//        generator.addProvider(event.includeServer(), new RegistryDataGenerator(output, lookupProvider));
    }
}

