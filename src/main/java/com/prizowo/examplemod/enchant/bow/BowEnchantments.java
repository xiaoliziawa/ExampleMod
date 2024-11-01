package com.prizowo.examplemod.enchant.bow;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public class BowEnchantments {
    public static final ResourceKey<Enchantment> EXPLOSIVE = registerKey("explosive");

    public BowEnchantments() {
    }

    private static ResourceKey<Enchantment> registerKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Examplemod.prefix(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        register(context, EXPLOSIVE, (new Enchantment.Builder(Enchantment.definition(items.getOrThrow(ItemTags.BOW_ENCHANTABLE), 1, 1, Enchantment.dynamicCost(20, 50), Enchantment.dynamicCost(50, 50), 10, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))).withEffect(EnchantmentEffectComponents.HIT_BLOCK, new ApplyExplosionEffect(LevelBasedValue.constant(2.0F))).withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.DAMAGING_ENTITY, new ApplyExplosionEffect(LevelBasedValue.constant(2.0F))));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}
