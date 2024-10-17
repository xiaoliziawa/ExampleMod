package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.custom.CustomEgg;
import com.prizowo.examplemod.custom.MyCustomEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

public class EntityReg {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Examplemod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<MyCustomEntity>> MY_HUMANOID = ENTITIES.register("my_humanoid",
            () -> EntityType.Builder.of(MyCustomEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build(Objects.requireNonNull(ResourceLocation.tryBuild(Examplemod.MOD_ID, "my_humanoid")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CustomEgg>> CUSTOM_EGG = ENTITIES.register("custom_egg",
            () -> EntityType.Builder.<CustomEgg>of(CustomEgg::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(Objects.requireNonNull(ResourceLocation.tryBuild(Examplemod.MOD_ID, "custom_egg")).toString()));

    public EntityReg(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
