package com.prizowo.examplemod.enchant.bow;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ApplyExplosionEffect implements EnchantmentEntityEffect {
    private final LevelBasedValue explosionPower;
    public static final MapCodec<ApplyExplosionEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(LevelBasedValue.CODEC.fieldOf("explosion_power").forGetter((effect) -> {
            return effect.explosionPower;
        })).apply(instance, ApplyExplosionEffect::new);
    });

    public ApplyExplosionEffect(LevelBasedValue explosionPower) {
        this.explosionPower = explosionPower;
    }

    public void apply(@NotNull ServerLevel level, int enchantLevel, @NotNull EnchantedItemInUse item, @NotNull Entity entity, @NotNull Vec3 position) {
        float power;
        if (entity instanceof AbstractArrow arrow) {
            power = this.explosionPower.calculate(enchantLevel);
            level.explode(arrow, arrow.getX(), arrow.getY(), arrow.getZ(), power, false, ExplosionInteraction.TNT);
            arrow.discard();
        } else {
            power = this.explosionPower.calculate(enchantLevel);
            level.explode(entity, position.x, position.y, position.z, power, false, ExplosionInteraction.TNT);
        }

    }

    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
