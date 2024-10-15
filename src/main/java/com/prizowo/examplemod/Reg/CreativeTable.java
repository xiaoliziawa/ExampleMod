package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeTable {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Examplemod.MOD_ID);

    public static final Supplier<CreativeModeTab> CREATIVE_TABLE = CREATIVE_MODE_TABS.register("your_tab", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ItemReg.CUSTOM_ITEM.get()))
                    .title(Component.translatable("itemGroup." + Examplemod.MOD_ID + ".creative"))
                    .displayItems((parameters, output) -> {
                        // 添加所有注册的物品到创造模式物品栏
                        ItemReg.ITEMS.getEntries().forEach(item ->
                                output.accept(new ItemStack(item.get())));

                        // 添加所有注册的方块到创造模式物品栏
                        BlocksReg.BLOCKS.getEntries().forEach(block -> {
                            Item blockItem = block.get().asItem();
                            output.accept(new ItemStack(blockItem));
                        });
                    })
                    .build()
    );
}
