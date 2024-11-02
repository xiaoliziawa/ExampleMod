package com.prizowo.examplemod.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import com.mojang.serialization.Codec;
import com.prizowo.examplemod.Examplemod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.bus.api.IEventBus;

public class ModComponents {
    
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = 
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Examplemod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HAMMER_RANGE = 
        COMPONENTS.register("hammer_range", 
            () -> DataComponentType.<Integer>builder()
                .persistent(Codec.INT)
                .build());

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
} 