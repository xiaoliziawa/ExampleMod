package com.prizowo.examplemod;

import com.prizowo.examplemod.Reg.*;
import com.prizowo.examplemod.Reg.music.JukeboxSongsReg;
import com.prizowo.examplemod.custom.CustomEgg;
import com.prizowo.examplemod.custom.customentity.CustomSnowGolem;
import com.prizowo.examplemod.custom.customentity.MyCustomEntity;
import com.prizowo.examplemod.enchant.TFEnchantmentEffects;
import com.prizowo.examplemod.enchant.TFMobEffects;
import com.prizowo.examplemod.init.LightningStaff;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Locale;
import java.util.Objects;

@Mod(Examplemod.MOD_ID)
public class Examplemod {
    public static final String MOD_ID = "examplemod";
    public static final int ABSOLUTE_MAXIMUM_STACK_SIZE = 1073741823;


    public Examplemod(IEventBus modEventBus, ModContainer modContainer) {
        ItemReg.register(modEventBus);
        BlocksReg.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EntityReg.ENTITIES.register(modEventBus);
        TFEnchantmentEffects.ENTITY_EFFECTS.register(modEventBus);
        TFMobEffects.MOB_EFFECTS.register(modEventBus);
        JukeboxSongsReg.ITEMS.register(modEventBus);
        JukeboxSongsReg.SOUND_EVENTS.register(modEventBus);
        JukeboxSongsReg.JUKEBOX_SONGS.register(modEventBus);
        CreativeTable.CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::addEntityAttributes);
        modContainer.registerConfig(ModConfig.Type.COMMON,Config.SPEC);
        NeoForge.EVENT_BUS.register(new LightningStaff());
        NeoForge.EVENT_BUS.register(this);
    }

    private void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityReg.MY_HUMANOID.get(), MyCustomEntity.createAttributes().build());
        event.put(EntityReg.CUSTOM_SNOW_GOLEM.get(), CustomSnowGolem.createAttributes().build());
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name.toLowerCase(Locale.ROOT));
    }
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof ThrownEgg) {
            event.setCanceled(true);
            Level level = event.getProjectile().level();
            if (!level.isClientSide()) {
                CustomEgg customEgg = new CustomEgg(EntityReg.CUSTOM_EGG.get(), level);
                customEgg.setPos(event.getProjectile().getX(), event.getProjectile().getY(), event.getProjectile().getZ());
                customEgg.setDeltaMovement(event.getProjectile().getDeltaMovement());
                level.addFreshEntity(customEgg);
            }
            event.getProjectile().discard();
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Zombie zombie) {
            zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(zombie, MyCustomEntity.class, true));
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.sendSystemMessage(Component.literal("欢迎" + Objects.requireNonNull(player.getDisplayName()).getString() + "进入世界"));
        player.sendSystemMessage(Component.literal(player.getDisplayName().getString() + "出生在了" + player.getBlockX() + " " + player.getBlockY() + " " + player.getBlockZ()));
    }

    @SubscribeEvent
    public void onPlayer(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : stack.getTagEnchantments().entrySet()) {
            if (holderEntry.getKey().getKey() == Enchantments.SHARPNESS && player.level().isClientSide()) {
                player.sendSystemMessage(Component.literal("你的剑的锋利等级为：" + holderEntry.getIntValue()));
            }
        }
    }
    @EventBusSubscriber(modid = Examplemod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onModifyDefaultComponentsEvent(ModifyDefaultComponentsEvent event) {
            event.getAllItems().filter(item -> {
                return item.getDefaultMaxStackSize() > 1;
            }).forEach(item2 -> {
                int newDefaultMaxStackSize;
                if (Config.respectSmallStackSizes) {
                    newDefaultMaxStackSize = Math.clamp(((long) item2.getDefaultMaxStackSize() * Config.defaultStackSize) / 64, 1, 1073741823);
                } else {
                    newDefaultMaxStackSize = Config.defaultStackSize;
                }
                int i = newDefaultMaxStackSize;
                event.modify(item2, builder -> {
                    builder.set(DataComponents.MAX_STACK_SIZE, Integer.valueOf(i));
                });
            });
        }
    }


}

