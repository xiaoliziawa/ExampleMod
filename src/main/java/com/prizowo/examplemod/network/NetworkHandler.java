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

        // 添加新的数据包注册 - 从服务端到客户端的包
        registrar.playToClient(
            MagicProjectilePacket.TYPE,
            MagicProjectilePacket.STREAM_CODEC,
            MagicProjectilePacket::handle
        );

        registrar.playToClient(
            MagicCirclePacket.TYPE,
            MagicCirclePacket.STREAM_CODEC,
            MagicCirclePacket::handle
        );

        registrar.playToClient(
            SonicBoomPacket.TYPE,
            SonicBoomPacket.STREAM_CODEC,
            SonicBoomPacket::handle
        );

        // 原有的数据包注册
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

        registrar.playToServer(
            ProjectileHitPacket.TYPE,
            ProjectileHitPacket.STREAM_CODEC,
            ProjectileHitPacket::handle
        );

        // 添加这两个包的注册
        registrar.playToServer(
            PlayerMountPacket.TYPE,
            PlayerMountPacket.STREAM_CODEC,
            PlayerMountPacket::handle
        );

        registrar.playToClient(
            PlayerMountConfirmPacket.TYPE,
            PlayerMountConfirmPacket.STREAM_CODEC,
            PlayerMountConfirmPacket::handle
        );
    }
}
