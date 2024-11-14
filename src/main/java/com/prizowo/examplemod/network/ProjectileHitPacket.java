package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ProjectileHitPacket(Vec3 hitPos) implements CustomPacketPayload {
    public static final Type<ProjectileHitPacket> TYPE = new Type<>(Examplemod.prefix("projectile_hit"));

    public static final StreamCodec<FriendlyByteBuf, ProjectileHitPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> {
                buf.writeDouble(packet.hitPos().x);
                buf.writeDouble(packet.hitPos().y);
                buf.writeDouble(packet.hitPos().z);
            },
            buf -> new ProjectileHitPacket(
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            )
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final ProjectileHitPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() != null) {
                Level level = ctx.player().level();
                Vec3 hitPos = data.hitPos();
                
                // 延迟创建魔法阵
                level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + 5, () -> {
                        // 发送魔法阵数据包
                        PacketDistributor.sendToAllPlayers(new MagicCirclePacket(hitPos));
                        
                        // 延迟发动声波攻击
                        level.getServer().tell(new net.minecraft.server.TickTask(
                            level.getServer().getTickCount() + 60, () -> {
                                // 发送声波攻击数据包
                                PacketDistributor.sendToAllPlayers(new SonicBoomPacket(hitPos));
                                
                                // 处理伤害
                                AABB damageArea = new AABB(
                                    hitPos.x - 15.0, hitPos.y - 2, hitPos.z - 15.0,
                                    hitPos.x + 15.0, hitPos.y + 4, hitPos.z + 15.0
                                );

                                for (Entity target : level.getEntities((Entity)null, damageArea)) {
                                    if (target instanceof LivingEntity && target != ctx.player()) {
                                        double distance = target.position().distanceTo(hitPos);
                                        if (distance <= 15.0) {
                                            target.hurt(level.damageSources().sonicBoom(null), 50.0F);
                                            
                                            Vec3 knockback = target.position().subtract(hitPos).normalize();
                                            target.setDeltaMovement(target.getDeltaMovement().add(
                                                knockback.x * 2.0, 0.5, knockback.z * 2.0
                                            ));
                                        }
                                    }
                                }
                            }
                        ));
                    }
                ));
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 