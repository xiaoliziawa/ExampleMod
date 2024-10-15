package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.custom.MyCustomFurnaceBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Examplemod.MOD_ID);

    public static final Supplier<BlockEntityType<MyCustomFurnaceBlockEntity>> MY_CUSTOM_FURNACE = BLOCK_ENTITIES.register("my_custom_furnace",
            () -> BlockEntityType.Builder.of(MyCustomFurnaceBlockEntity::new, BlocksReg.CUSTOM_BLOCK_2.get()).build(null));
}
