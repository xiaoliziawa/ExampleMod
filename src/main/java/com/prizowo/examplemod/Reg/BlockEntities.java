package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.custom.MyCustomFurnaceBlockEntity;
import com.prizowo.examplemod.custom.CustomChestBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Examplemod.MODID);

    public static final Supplier<BlockEntityType<MyCustomFurnaceBlockEntity>> MY_CUSTOM_FURNACE = BLOCK_ENTITIES.register("my_custom_furnace",
            () -> BlockEntityType.Builder.of(MyCustomFurnaceBlockEntity::new, BlocksReg.CUSTOM_BLOCK_2.get()).build(null));

    public static final Supplier<BlockEntityType<CustomChestBlockEntity>> CUSTOM_CHEST = BLOCK_ENTITIES.register("custom_chest",
            () -> BlockEntityType.Builder.of(CustomChestBlockEntity::new, BlocksReg.CUSTOM_CHEST.get()).build(null));

}
