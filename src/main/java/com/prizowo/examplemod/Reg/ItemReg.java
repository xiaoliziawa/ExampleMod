package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.custom.customentity.CustomSnowGolemSpawnEggItem;
import com.prizowo.examplemod.init.CustomEgg;
import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.init.MyCustomItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ItemReg {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Examplemod.MOD_ID);
    public static final Supplier<Item> CUSTOM_ITEM = ITEMS.register("customitem",
            () -> new MyCustomItem(new Item.Properties()));
    public static final Supplier<Item> CUSTOM_ITEM_1 = ITEMS.register("customitem1",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CUSTOM_BLOCK_ITEM = ITEMS.register("customblock",
            () -> new BlockItem(BlocksReg.CUSTOM_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> CUSTOM_ITEM_2 = ITEMS.register("customitem2",
            () -> new BlockItem(BlocksReg.CUSTOM_BLOCK_2.get(), new Item.Properties()));
    public static final Supplier<Item> CRAFTING_TABLE_SLAB_ITEM = ITEMS.register("crafting_table_slab",
            () -> new BlockItem(BlocksReg.CRFTING_TABLE_SLAB.get(), new Item.Properties()));

    public static final Supplier<Item> CUSTOM_SNOW_GOLEM_SPAWN_EGG = ITEMS.register("custom_snow_golem_spawn_egg",
            () -> new CustomSnowGolemSpawnEggItem(0xF0F0F0, 0xCC8E35,
                    new Item.Properties()));
    public static final Supplier<Item> MY_HUMANOID_SPAWN_EGG = ITEMS.register("my_humanoid_spawn_egg",
            () -> new Item(new Item.Properties()) {
                @Override
                public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
                    ItemStack itemstack = player.getItemInHand(hand);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                    if (!level.isClientSide) {
                        CustomEgg customEgg = new CustomEgg(level, player);
                        customEgg.setItem(itemstack);
                        customEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                        level.addFreshEntity(customEgg);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            });
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}



