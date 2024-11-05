package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.entity.EntityType;
import com.prizowo.examplemod.entity.SlimeProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.LlamaSpit;

public record MountAttackPacket() implements CustomPacketPayload {
    public static final Type<MountAttackPacket> TYPE = new Type<>(Examplemod.prefix("mount_attack"));

    private static final MountAttackPacket INSTANCE = new MountAttackPacket();

    public static final StreamCodec<FriendlyByteBuf, MountAttackPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    public static void handle(final MountAttackPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.getVehicle() instanceof Mob mount) {
                    Vec3 viewVec = serverPlayer.getViewVector(1.0F);

                    if (mount instanceof IronGolem ironGolem) {
                        ironGolem.setAggressive(true);
                    }
                    else if (mount instanceof Ghast) {
                        LargeFireball fireball = EntityType.FIREBALL.create(mount.level());
                        if (fireball != null) {
                            fireball.setOwner(mount);
                            fireball.setPos(
                                    mount.getX() + viewVec.x * 4.0D,
                                    mount.getY(0.5D) + 0.5D,
                                    mount.getZ() + viewVec.z * 4.0D
                            );
                            fireball.setDeltaMovement(viewVec.scale(1.5D));
                            mount.level().addFreshEntity(fireball);
                        }
                    }
                    else if (mount instanceof Blaze) {
                        SmallFireball fireball = EntityType.SMALL_FIREBALL.create(mount.level());
                        if (fireball != null) {
                            fireball.setOwner(mount);
                            fireball.setPos(
                                    mount.getX() + viewVec.x * 4.0D,
                                    mount.getY(0.5D) + 0.5D,
                                    mount.getZ() + viewVec.z * 4.0D
                            );
                            fireball.setDeltaMovement(viewVec.scale(1.5D));
                            mount.level().addFreshEntity(fireball);
                        }
                    }
                    else if (mount instanceof WitherBoss) {
                        WitherSkull skull = EntityType.WITHER_SKULL.create(mount.level());
                        if (skull != null) {
                            skull.setOwner(mount);
                            skull.setPos(
                                    mount.getX() + viewVec.x * 4.0D,
                                    mount.getY(0.5D) + 0.5D,
                                    mount.getZ() + viewVec.z * 4.0D
                            );
                            skull.setDeltaMovement(viewVec.scale(1.5D));
                            mount.level().addFreshEntity(skull);
                        }
                    }
                    else if (mount instanceof AbstractSkeleton) {
                        Arrow arrow = EntityType.ARROW.create(mount.level());
                        if (arrow != null) {
                            arrow.setOwner(mount);
                            arrow.setPos(
                                    mount.getX() + viewVec.x * 4.0D,
                                    mount.getY(0.5D) + 0.5D,
                                    mount.getZ() + viewVec.z * 4.0D
                            );
                            arrow.shoot(viewVec.x, viewVec.y, viewVec.z, 3.0F, 1.0F);
                            mount.level().addFreshEntity(arrow);
                        }
                    }
                    else if (mount instanceof Slime) {
                        SlimeProjectile projectile = new SlimeProjectile(mount.level(), mount);
                        Vec3 spawnPos = serverPlayer.getEyePosition().add(viewVec.scale(2.0));
                        projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                        projectile.setDeltaMovement(viewVec.scale(1.5));
                        mount.level().addFreshEntity(projectile);
                    }
                    else if (mount instanceof Creeper) {
                        PrimedTnt tnt = EntityType.TNT.create(mount.level());
                        if (tnt != null) {
                            Vec3 spawnPos = mount.position().add(viewVec.scale(2.0)).add(0, 1, 0);
                            tnt.setPos(spawnPos);
                            tnt.setDeltaMovement(viewVec.scale(1.0));
                            tnt.setFuse(40);
                            mount.level().addFreshEntity(tnt);
                        }
                    }
                    else if (mount instanceof Evoker) {
                        double angle = Math.toRadians(serverPlayer.getYRot());
                        double forwardX = -Math.sin(angle);
                        double forwardZ = Math.cos(angle);

                        for (int row = -1; row <= 1; row++) {
                            double offsetX = -forwardZ * row * 1.0;
                            double offsetZ = forwardX * row * 1.0;

                            for (int i = 0; i < 15; i++) {
                                double distance = i * 1.5;

                                double finalX = mount.getX() + (forwardX * distance) + offsetX;
                                double finalZ = mount.getZ() + (forwardZ * distance) + offsetZ;

                                EvokerFangs fangs = EntityType.EVOKER_FANGS.create(mount.level());
                                if (fangs != null) {
                                    fangs.setOwner(mount);
                                    fangs.setPos(
                                            finalX,
                                            mount.getY(),
                                            finalZ
                                    );
                                    mount.level().addFreshEntity(fangs);
                                }
                            }
                        }
                    }
                    else if (mount instanceof Llama llama) {
                        int spitCount = 5;
                        float spreadAngle = 15.0F;

                        Vec3 baseViewVec = serverPlayer.getViewVector(1.0F);

                        for (int i = 0; i < spitCount; i++) {
                            LlamaSpit spit = EntityType.LLAMA_SPIT.create(mount.level());
                            if (spit != null) {
                                spit.setPos(
                                        mount.getX() - (double)(mount.getBbWidth() + 1.0F) * 0.5D * Math.sin(mount.yBodyRot * 0.017453292F),
                                        mount.getEyeY() - 0.10000000149011612D,
                                        mount.getZ() + (double)(mount.getBbWidth() + 1.0F) * 0.5D * Math.cos(mount.yBodyRot * 0.017453292F)
                                );

                                spit.setOwner(mount);

                                float angleOffset = spreadAngle * ((float)i / (spitCount - 1) - 0.5F);

                                double rotatedX = baseViewVec.x * Math.cos(Math.toRadians(angleOffset)) - baseViewVec.z * Math.sin(Math.toRadians(angleOffset));
                                double rotatedZ = baseViewVec.x * Math.sin(Math.toRadians(angleOffset)) + baseViewVec.z * Math.cos(Math.toRadians(angleOffset));

                                double d0 = rotatedX;
                                double d1 = baseViewVec.y + 0.1;
                                double d2 = rotatedZ;
                                double d3 = Math.sqrt(d0 * d0 + d2 * d2);

                                spit.shoot(d0, d1 + d3 * 0.2D, d2, 1.5F, 1.0F);

                                mount.level().addFreshEntity(spit);
                            }
                        }

                        mount.level().playSound(null, mount.getX(), mount.getY(), mount.getZ(),
                                SoundEvents.LLAMA_SPIT, SoundSource.NEUTRAL, 1.0F,
                                1.0F + (mount.getRandom().nextFloat() - mount.getRandom().nextFloat()) * 0.2F);
                    }
                }
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 