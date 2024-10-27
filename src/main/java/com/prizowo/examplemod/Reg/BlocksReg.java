package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.custom.customblock.CustomCraftingTableBlock;
import com.prizowo.examplemod.init.MyCustomFurnaceBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlocksReg {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Examplemod.MOD_ID);
    public static final Supplier<Block> CUSTOM_BLOCK = BLOCKS.register("customblock",
            () -> new Block(Block.Properties.of().strength(3)));
    public static final Supplier<Block> CUSTOM_BLOCK_2 = BLOCKS.register("customblock2",
            () -> new MyCustomFurnaceBlock(Block.Properties.of()
                    .strength(5)
                    .sound(SoundType.ANVIL)
                    .lightLevel((state) -> state.getValue(AbstractFurnaceBlock.LIT) ? 10 : 0)));

    public static final Supplier<Block> CRFTING_TABLE_SLAB = BLOCKS.register("crafting_table_slab",
            ()-> new CustomCraftingTableBlock(Block.Properties.of()
                    .strength(3)
                    .sound(SoundType.WOOD)));
    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
