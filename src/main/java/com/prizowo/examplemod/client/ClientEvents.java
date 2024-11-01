package com.prizowo.examplemod.client;

import com.prizowo.examplemod.Reg.EntityReg;
import com.prizowo.examplemod.client.render.ExplosiveArrowRenderer;
import com.prizowo.examplemod.client.render.HomingArrowRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityReg.EXPLOSIVE_ARROW.get(), ExplosiveArrowRenderer::new);
        event.registerEntityRenderer(EntityReg.HOMING_ARROW.get(), HomingArrowRenderer::new);
    }
} 