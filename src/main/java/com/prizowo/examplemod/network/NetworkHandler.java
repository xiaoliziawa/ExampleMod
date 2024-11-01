package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.render.VirtualItemRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Examplemod.MODID);
        registrar.playBidirectional(
                SyncVirtualItemPacket.TYPE,
                SyncVirtualItemPacket.STREAM_CODEC,
                (packet, context) -> context.enqueueWork(() -> {
                    if (packet.remove()) {
                        VirtualItemRenderer.removeVirtualItem(packet.pos());
                    } else {
                        VirtualItemRenderer.addVirtualItem(packet.pos(), packet.stack());
                    }
                })
        );
    }
}
