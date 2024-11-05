package com.prizowo.examplemod.client;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.Reg.EntityReg;
import com.prizowo.examplemod.client.render.SlimeProjectileRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityReg.SLIME_PROJECTILE.get(), SlimeProjectileRenderer::new);
    }
} 