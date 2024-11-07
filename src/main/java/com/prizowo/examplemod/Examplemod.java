package com.prizowo.examplemod;

import com.prizowo.examplemod.Reg.*;
import com.prizowo.examplemod.Reg.music.JukeboxSongsReg;
import com.prizowo.examplemod.client.renderer.ThrownAxeRenderer;
import com.prizowo.examplemod.component.ModComponents;
import com.prizowo.examplemod.custom.CustomEgg;
import com.prizowo.examplemod.custom.customentity.CustomSnowGolem;
import com.prizowo.examplemod.custom.customentity.MyCustomEntity;
import com.prizowo.examplemod.enchant.TFEnchantmentEffects;
import com.prizowo.examplemod.enchant.TFMobEffects;
import com.prizowo.examplemod.init.LightningStaff;
import com.prizowo.examplemod.network.NetworkHandler;
import com.prizowo.examplemod.render.EntityOutlineRenderer;
import com.prizowo.examplemod.render.EntityOverlayRenderer;
import com.prizowo.examplemod.util.HeadManager;
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
import net.minecraft.world.item.Items;
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
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import com.prizowo.examplemod.items.HammerItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.InputEvent;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.loading.FMLEnvironment;
import com.prizowo.examplemod.events.MountMovementEvents;
import com.prizowo.examplemod.events.MountAttackEvents;
import com.prizowo.examplemod.client.KeyBindings;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import com.prizowo.examplemod.events.MountEvents;

import java.util.Locale;
import java.util.Objects;

@Mod(Examplemod.MODID)
public class Examplemod {
    public static final String MODID = "examplemod";
    public static final int ABSOLUTE_MAXIMUM_STACK_SIZE = 1073741823;
    public static final Logger LOGGER = LogManager.getLogger(Examplemod.class);

    public Examplemod(ModContainer modContainer, IEventBus modEventBus) {

        ItemReg.register(modEventBus);
        BlocksReg.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EntityReg.ENTITIES.register(modEventBus);
        TFEnchantmentEffects.ENTITY_EFFECTS.register(modEventBus);
        TFMobEffects.MOB_EFFECTS.register(modEventBus);
        JukeboxSongsReg.SOUND_EVENTS.register(modEventBus);
        JukeboxSongsReg.JUKEBOX_SONGS.register(modEventBus);
        JukeboxSongsReg.ITEMS.register(modEventBus);
        CreativeTable.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::addEntityAttributes);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new LightningStaff());
        NeoForge.EVENT_BUS.register(new EntityOverlayRenderer());
        NeoForge.EVENT_BUS.register(new EntityOutlineRenderer());
        NeoForge.EVENT_BUS.register(MountEvents.class);
        modEventBus.register(NetworkHandler.class);
        ModComponents.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::registerKeyBinds);
            modEventBus.addListener(this::registerRenderers);
            NeoForge.EVENT_BUS.register(MountMovementEvents.class);
            NeoForge.EVENT_BUS.register(MountAttackEvents.class);
        }
    }

    private void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityReg.MY_HUMANOID.get(), MyCustomEntity.createAttributes().build());
        event.put(EntityReg.CUSTOM_SNOW_GOLEM.get(), CustomSnowGolem.createAttributes().build());
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
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
                player.sendSystemMessage(Component.literal("的剑的锋利等级为：" + holderEntry.getIntValue()));
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        
        if (stack.is(Items.DIAMOND_PICKAXE) && player.isShiftKeyDown()) {
            if (player.level().isClientSide) {
                HeadManager.toggleHead(player);
                LOGGER.info("Client: Head visibility toggled to: {}", HeadManager.isHeadHidden());
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.isShiftKeyDown()) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof HammerItem hammer) {
                if (Screen.hasControlDown()) {
                    hammer.cycleDepth(player, event.getScrollDeltaY() > 0);
                } else {
                    hammer.cycleRange(player, event.getScrollDeltaY() > 0);
                }
                event.setCanceled(true);
            }
        }
    }

    @EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onModifyDefaultComponentsEvent(ModifyDefaultComponentsEvent event) {
            event.getAllItems().filter(item -> item.getDefaultMaxStackSize() > 1).forEach(item2 -> {
                int newDefaultMaxStackSize;
                if (Config.respectSmallStackSizes) {
                    newDefaultMaxStackSize = Math.clamp(((long) item2.getDefaultMaxStackSize() * Config.defaultStackSize) / 64, 1, ABSOLUTE_MAXIMUM_STACK_SIZE);
                } else {
                    newDefaultMaxStackSize = Config.defaultStackSize;
                }
                int i = newDefaultMaxStackSize;
                event.modify(item2, builder -> builder.set(DataComponents.MAX_STACK_SIZE, i));
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void registerKeyBinds(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.MOUNT_KEY);
        event.register(KeyBindings.DESCEND_KEY);
        event.register(KeyBindings.TOGGLE_OVERLAY);
    }

    @OnlyIn(Dist.CLIENT)
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityReg.THROWN_AXE.get(), ThrownAxeRenderer::new);
    }

}
