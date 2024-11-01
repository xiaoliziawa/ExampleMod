package com.prizowo.examplemod.enchant;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TFMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Examplemod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> FROSTY = MOB_EFFECTS.register("frosted", FrostedEffect::new);
}
