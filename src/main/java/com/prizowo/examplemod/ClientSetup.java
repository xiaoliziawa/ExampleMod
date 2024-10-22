package com.prizowo.examplemod;

import com.prizowo.examplemod.Reg.EntityReg;
import com.prizowo.examplemod.custom.customentity.CustomSnowGolemRenderer;
import com.prizowo.examplemod.custom.customentity.MyHumanoidEntityRenderer;
import com.prizowo.examplemod.custom.customentity.MyHumanoidModel;
import com.prizowo.examplemod.custom.customentity.MyModelLayers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Examplemod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
    }
}
