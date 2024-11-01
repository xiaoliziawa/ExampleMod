package com.prizowo.examplemod.enchant.bow;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BowEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_EFFECTS;
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<ApplyExplosionEffect>> APPLY_EXPLOSION;

    public BowEnchantmentEffects() {
    }

    static {
        ENTITY_EFFECTS = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, "examplemod");
        APPLY_EXPLOSION = ENTITY_EFFECTS.register("apply_explosion", () -> {
            return ApplyExplosionEffect.CODEC;
        });
    }
}
