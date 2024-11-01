//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.prizowo.examplemod.Reg;

import com.prizowo.examplemod.custom.customentity.CustomSnowGolemSpawnEggItem;
import com.prizowo.examplemod.init.CustomEgg;
import com.prizowo.examplemod.init.MyCustomItem;
import com.prizowo.examplemod.items.ExplosiveBow;
import com.prizowo.examplemod.items.HomingBow;
import com.prizowo.examplemod.items.MultiShotBow;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ItemReg {
    public static final DeferredRegister<Item> ITEMS;
    public static final Supplier<Item> CUSTOM_ITEM;
    public static final Supplier<Item> CUSTOM_ITEM_1;
    public static final Supplier<Item> CUSTOM_BLOCK_ITEM;
    public static final Supplier<Item> CUSTOM_ITEM_2;
    public static final Supplier<Item> CRAFTING_TABLE_SLAB_ITEM;
    public static final Supplier<Item> CUSTOM_SNOW_GOLEM_SPAWN_EGG;
    public static final Supplier<Item> MY_HUMANOID_SPAWN_EGG;
    public static final Supplier<Item> CUSTOM_CHEST_ITEM;
    public static final Supplier<Item> MULTI_SHOT_BOW;
    public static final Supplier<Item> HOMING_BOW;
    public static final Supplier<Item> EXPLOSIVE_BOW;

    public ItemReg() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    static {
        ITEMS = DeferredRegister.create(Registries.ITEM, "examplemod");
        CUSTOM_ITEM = ITEMS.register("customitem", () -> {
            return new MyCustomItem(new Item.Properties());
        });
        CUSTOM_ITEM_1 = ITEMS.register("customitem1", () -> {
            return new Item(new Item.Properties());
        });
        CUSTOM_BLOCK_ITEM = ITEMS.register("customblock", () -> {
            return new BlockItem((Block)BlocksReg.CUSTOM_BLOCK.get(), new Item.Properties());
        });
        CUSTOM_ITEM_2 = ITEMS.register("customitem2", () -> {
            return new BlockItem((Block)BlocksReg.CUSTOM_BLOCK_2.get(), new Item.Properties());
        });
        CRAFTING_TABLE_SLAB_ITEM = ITEMS.register("crafting_table_slab", () -> {
            return new BlockItem((Block)BlocksReg.CRFTING_TABLE_SLAB.get(), new Item.Properties());
        });
        CUSTOM_SNOW_GOLEM_SPAWN_EGG = ITEMS.register("custom_snow_golem_spawn_egg", () -> {
            return new CustomSnowGolemSpawnEggItem(15790320, 13405749, new Item.Properties());
        });
        MY_HUMANOID_SPAWN_EGG = ITEMS.register("my_humanoid_spawn_egg", () -> {
            return new Item(new Item.Properties()) {
                public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
                    ItemStack itemstack = player.getItemInHand(hand);
                    level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
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
            };
        });
        CUSTOM_CHEST_ITEM = ITEMS.register("custom_chest", () -> {
            return new BlockItem((Block)BlocksReg.CUSTOM_CHEST.get(), new Item.Properties());
        });
        MULTI_SHOT_BOW = ITEMS.register("multi_shot_bow", () -> {
            return new MultiShotBow((new Item.Properties()).durability(384));
        });
        HOMING_BOW = ITEMS.register("homing_bow", () -> {
            return new HomingBow((new Item.Properties()).durability(384));
        });
        EXPLOSIVE_BOW = ITEMS.register("explosive_bow", () -> {
            return new ExplosiveBow((new Item.Properties()).durability(384));
        });
    }
}
