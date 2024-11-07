package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Examplemod.MODID)
            .versioned("1.0.0");
        
        registrar.playToServer(
            HammerRangePacket.TYPE, 
            HammerRangePacket.STREAM_CODEC, 
            HammerRangePacket::handle
        );

        registrar.playToServer(
            HammerDepthPacket.TYPE, 
            HammerDepthPacket.STREAM_CODEC, 
            HammerDepthPacket::handle
        );

        registrar.playToServer(
            MountEntityPacket.TYPE,
            MountEntityPacket.STREAM_CODEC,
            MountEntityPacket::handle
        );

        registrar.playToServer(
            MountFlyPacket.TYPE,
            MountFlyPacket.STREAM_CODEC,
            MountFlyPacket::handle
        );

        registrar.playToServer(
            MountAttackPacket.TYPE,
            MountAttackPacket.STREAM_CODEC,
            MountAttackPacket::handle
        );

        registrar.playToServer(
            ToggleThrowPacket.TYPE,
            ToggleThrowPacket.STREAM_CODEC,
            ToggleThrowPacket::handle
        );
    }
}
