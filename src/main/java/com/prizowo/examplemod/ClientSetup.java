    package com.prizowo.examplemod;

    import com.prizowo.examplemod.Reg.EntityReg;
    import com.prizowo.examplemod.client.render.HoneyBombRenderer;
    import com.prizowo.examplemod.client.render.SlimeProjectileRenderer;
    import com.prizowo.examplemod.client.renderer.CustomBeeRenderer;
    import com.prizowo.examplemod.client.renderer.ItemRender;
    import com.prizowo.examplemod.client.renderer.ThrownAxeRenderer;
    import com.prizowo.examplemod.custom.customentity.CustomSnowGolemRenderer;
    import com.prizowo.examplemod.custom.customentity.MyHumanoidEntityRenderer;
    import com.prizowo.examplemod.custom.customentity.MyHumanoidModel;
    import com.prizowo.examplemod.custom.customentity.MyModelLayers;
    import com.prizowo.examplemod.render.ExplosiveArrowRenderer;
    import com.prizowo.examplemod.render.HomingArrowRenderer;
    import net.minecraft.client.renderer.entity.ThrownItemRenderer;
    import net.neoforged.api.distmarker.Dist;
    import net.neoforged.bus.api.SubscribeEvent;
    import net.neoforged.fml.common.EventBusSubscriber;
    import net.neoforged.neoforge.client.event.EntityRenderersEvent;
    import com.prizowo.examplemod.client.renderer.SuperFireworkRenderer;

    @EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientSetup {
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(MyModelLayers.MY_HUMANOID, MyHumanoidModel::createBodyLayer);
        }
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityReg.MY_HUMANOID.get(), MyHumanoidEntityRenderer::new);
            event.registerEntityRenderer(EntityReg.CUSTOM_EGG.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(EntityReg.CUSTOM_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(EntityReg.CUSTOM_SNOW_GOLEM.get(), CustomSnowGolemRenderer::new);
            event.registerEntityRenderer(EntityReg.EXPLOSIVE_ARROW.get(), ExplosiveArrowRenderer::new);
            event.registerEntityRenderer(EntityReg.HOMING_ARROW.get(), HomingArrowRenderer::new);
            event.registerEntityRenderer(EntityReg.THROWN_ITEM.get(), ItemRender::new);
            event.registerEntityRenderer(EntityReg.SLIME_PROJECTILE.get(), SlimeProjectileRenderer::new);
            event.registerEntityRenderer(EntityReg.THROWN_AXE.get(), ThrownAxeRenderer::new);
            event.registerEntityRenderer(EntityReg.CUSTOM_BEE.get(), CustomBeeRenderer::new);
            event.registerEntityRenderer(EntityReg.HONEY_BOMB.get(), HoneyBombRenderer::new);
            event.registerEntityRenderer(EntityReg.SUPER_FIREWORK.get(), SuperFireworkRenderer::new);
        }
    }
