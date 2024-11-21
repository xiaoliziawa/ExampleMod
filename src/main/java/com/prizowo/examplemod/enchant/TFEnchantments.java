package com.prizowo.examplemod.enchant;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;


public class TFEnchantments {
    public static final ResourceKey<Enchantment> FIRE_REACT = registerKey("fire_react");
    public static final ResourceKey<Enchantment> CHILL_AURA = registerKey("chill_aura");

    private static ResourceKey<Enchantment> registerKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Examplemod.prefix(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, FIRE_REACT, new Enchantment.Builder(Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(5, 9),
                Enchantment.dynamicCost(20, 9),
                8,
                EquipmentSlotGroup.ARMOR)
        ).exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.THORNS), enchantments.getOrThrow(CHILL_AURA)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.VICTIM,
                        EnchantmentTarget.ATTACKER,
                        AllOf.entityEffects(
                                new Ignite(LevelBasedValue.perLevel(2.0F, 3.0F)),
                                new DamageItem(LevelBasedValue.constant(2.0F))),
                        LootItemRandomChanceCondition.randomChance(EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.15F)))));

        register(context, CHILL_AURA, new Enchantment.Builder(Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(5, 9),
                Enchantment.dynamicCost(20, 9),
                8,
                EquipmentSlotGroup.ARMOR)
        ).exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.THORNS), enchantments.getOrThrow(FIRE_REACT)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.VICTIM,
                        EnchantmentTarget.ATTACKER,
                        AllOf.entityEffects(
                                new ApplyFrostedEffect(LevelBasedValue.constant(200), LevelBasedValue.perLevel(0.0F, 1.0F)),
                                new DamageItem(LevelBasedValue.constant(2.0F))),
                        LootItemRandomChanceCondition.randomChance(EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.15F)))));

    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}
