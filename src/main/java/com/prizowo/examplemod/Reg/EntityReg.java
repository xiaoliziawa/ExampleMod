//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.custom.CustomEgg;
import com.prizowo.examplemod.custom.CustomSnowball;
import com.prizowo.examplemod.custom.customentity.CustomSnowGolem;
import com.prizowo.examplemod.custom.customentity.MyCustomEntity;
import com.prizowo.examplemod.entity.ThrownAxeEntity;
import com.prizowo.examplemod.entity.ThrownItemEntity;
import com.prizowo.examplemod.items.ExplosiveArrow;
import com.prizowo.examplemod.items.HomingArrow;
import com.prizowo.examplemod.entity.SlimeProjectile;
import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class EntityReg {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Examplemod.MODID);
    
    public static final DeferredHolder<EntityType<?>, EntityType<MyCustomEntity>> MY_HUMANOID = ENTITIES.register("my_humanoid", 
        () -> Builder.<MyCustomEntity>of(MyCustomEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.8F)
            .build(ResourceLocation.tryBuild("examplemod", "my_humanoid").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CustomEgg>> CUSTOM_EGG = ENTITIES.register("custom_egg",
        () -> Builder.<CustomEgg>of(CustomEgg::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(ResourceLocation.tryBuild("examplemod", "custom_egg").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CustomSnowball>> CUSTOM_SNOWBALL = ENTITIES.register("custom_snowball",
        () -> Builder.<CustomSnowball>of(CustomSnowball::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(ResourceLocation.tryBuild("examplemod", "custom_snowball").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CustomSnowGolem>> CUSTOM_SNOW_GOLEM = ENTITIES.register("custom_snow_golem",
        () -> Builder.<CustomSnowGolem>of(CustomSnowGolem::new, MobCategory.MISC)
            .sized(0.7F, 1.9F)
            .clientTrackingRange(8)
            .build(ResourceLocation.fromNamespaceAndPath("examplemod", "custom_snow_golem").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HomingArrow>> HOMING_ARROW = ENTITIES.register("homing_arrow",
        () -> Builder.<HomingArrow>of((type, level) -> new HomingArrow((EntityType<? extends HomingArrow>) type, level), MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(ResourceLocation.tryBuild("examplemod", "homing_arrow").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveArrow>> EXPLOSIVE_ARROW = ENTITIES.register("explosive_arrow",
        () -> EntityType.Builder.<ExplosiveArrow>of(ExplosiveArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("explosive_arrow"));

    public static final DeferredHolder<EntityType<?>, EntityType<SlimeProjectile>> SLIME_PROJECTILE = ENTITIES.register("slime_projectile",
        () -> EntityType.Builder.<SlimeProjectile>of(SlimeProjectile::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("slime_projectile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownAxeEntity>> THROWN_AXE = ENTITIES.register("thrown_axe",
        () -> EntityType.Builder.<ThrownAxeEntity>of(ThrownAxeEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("thrown_axe"));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownItemEntity>> THROWN_ITEM = ENTITIES.register("thrown_item",
            () -> EntityType.Builder.<ThrownItemEntity>of(ThrownItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_item"));
    public EntityReg(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
