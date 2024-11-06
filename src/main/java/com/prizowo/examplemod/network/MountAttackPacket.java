package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.entity.SlimeProjectile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.projectile.ThrownPotion;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;

public record MountAttackPacket(boolean isRightClick) implements CustomPacketPayload {
    public static final Type<MountAttackPacket> TYPE = new Type<>(Examplemod.prefix("mount_attack"));
    
    public static final StreamCodec<FriendlyByteBuf, MountAttackPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
                FriendlyByteBuf::writeBoolean,
            FriendlyByteBuf::readBoolean
        ),
        MountAttackPacket::isRightClick,
        MountAttackPacket::new
    );

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

                        for (int i = 0; i < spitCount; i++) {
                            LlamaSpit spit = EntityType.LLAMA_SPIT.create(mount.level());
                            if (spit != null) {
                                float angleOffset = spreadAngle * ((float)i / (spitCount - 1) - 0.5F);
                                double rotatedX = viewVec.x * Math.cos(Math.toRadians(angleOffset)) - viewVec.z * Math.sin(Math.toRadians(angleOffset));
                                double rotatedZ = viewVec.x * Math.sin(Math.toRadians(angleOffset)) + viewVec.z * Math.cos(Math.toRadians(angleOffset));

                                spit.setPos(
                                    mount.getX() - (double)(mount.getBbWidth() + 1.0F) * 0.5D * Math.sin(mount.yBodyRot * 0.017453292F),
                                    mount.getEyeY() - 0.10000000149011612D,
                                    mount.getZ() + (double)(mount.getBbWidth() + 1.0F) * 0.5D * Math.cos(mount.yBodyRot * 0.017453292F)
                                );
                                spit.setOwner(mount);
                                spit.shoot(rotatedX, viewVec.y + 0.1, rotatedZ, 1.5F, 10.0F);
                                mount.level().addFreshEntity(spit);
                            }
                        }

                        mount.level().playSound(null, mount.getX(), mount.getY(), mount.getZ(),
                            SoundEvents.LLAMA_SPIT, SoundSource.NEUTRAL, 1.0F,
                            1.0F + (mount.getRandom().nextFloat() - mount.getRandom().nextFloat()) * 0.2F);
                    }
                    else if (mount instanceof Witch) {
                        // 创建投掷药水实体
                        ThrownPotion potion = new ThrownPotion(mount.level(), mount);
                        
                        // 随机选择一个药水效果
                        List<Holder.Reference<Potion>> potions = BuiltInRegistries.POTION.holders().toList();
                        Holder<Potion> randomPotion = potions.get(mount.getRandom().nextInt(potions.size()));
                        
                        // 创建药水物品
                        ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
                        potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(randomPotion));
                        
                        // 设置药水物品和发射参数
                        potion.setItem(potionStack);
                        Vec3 start = mount.getEyePosition();
                        potion.setPos(start.x, start.y, start.z);
                        potion.shoot(viewVec.x, viewVec.y + 0.2, viewVec.z, 0.75F, 8.0F);  // 使用抛物线轨迹
                        
                        // 添加到世界
                        mount.level().addFreshEntity(potion);
                        
                        // 播放投掷声音
                        mount.level().playSound(null, 
                            mount.getX(), mount.getY(), mount.getZ(),
                            SoundEvents.WITCH_THROW, 
                            SoundSource.HOSTILE, 
                            1.0F, 
                            0.8F + mount.getRandom().nextFloat() * 0.4F
                        );
                    }
                    else if (mount instanceof Villager) {
                        Vec3 start = mount.getEyePosition();
                        Vec3 baseViewVec = viewVec;
                        
                        // 霰弹效果参数
                        int pelletCount = 14;  // 弹丸数量
                        float spreadAngle = 15.0F;  // 扩散角度
                        
                        for (int i = 0; i < pelletCount; i++) {
                            // 计算每个弹丸的随机偏移角度
                            float horizontalAngle = (float) (Math.random() * spreadAngle - spreadAngle/2);
                            float verticalAngle = (float) (Math.random() * spreadAngle - spreadAngle/2);
                            
                            // 根据角度旋转向量
                            Vec3 rotatedVec = baseViewVec
                                .xRot((float) Math.toRadians(verticalAngle))
                                .yRot((float) Math.toRadians(horizontalAngle));
                                
                            Vec3 end = start.add(rotatedVec.multiply(50, 50, 50));
                            
                            AABB boundingBox = new AABB(start.x, start.y, start.z, end.x, end.y, end.z)
                                    .inflate(1.0);
                            
                            EntityHitResult entityHit = null;
                            double closestDistance = Double.MAX_VALUE;
                            
                            for (Entity target : serverPlayer.level().getEntities(serverPlayer, boundingBox)) {
                                if (target == serverPlayer || target == mount) continue;
                                
                                AABB targetBox = target.getBoundingBox();
                                Vec3 intersection = targetBox.clip(start, end).orElse(null);
                                
                                if (intersection != null) {
                                    double distance = start.distanceToSqr(intersection);
                                    if (distance < closestDistance) {
                                        closestDistance = distance;
                                        entityHit = new EntityHitResult(target, intersection);
                                    }
                                }
                            }

                            Vec3 particleEnd = entityHit != null ? entityHit.getLocation() : end;
                            double distance = start.distanceTo(particleEnd);
                            Vec3 step = rotatedVec.scale(0.5);
                            
                            // 生成粒子效果
                            for (double d = 0; d < distance; d += 0.5) {
                                Vec3 pos = start.add(step.scale(d));
                                ((ServerLevel)mount.level()).sendParticles(
                                    ParticleTypes.COMPOSTER,
                                    pos.x, pos.y, pos.z,
                                    1,  // 减少每个位置的粒子数量
                                    0.0, 0.0, 0.0,
                                    0.0
                                );
                            }

                            // 如果击中实体，造成伤害
                            if (entityHit != null) {
                                Entity target = entityHit.getEntity();
                                target.hurt(mount.level().damageSources().mobAttack(mount), 15.0f / pelletCount);  // 每个弹丸的伤害平分
                            }
                        }
                    }
                    else if (mount instanceof Shulker shulker) {
                        if (!data.isRightClick()) {  // 左键瞬移
                            Vec3 start = mount.position();
                            Vec3 end = start.add(viewVec.multiply(30, 30, 30));
                            
                            // 进行射线碰撞检测
                            BlockHitResult hitResult = mount.level().clip(
                                new ClipContext(
                                    start,
                                    end,
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    mount
                                )
                            );
                            
                            // 如果碰到方块，就移动到碰撞点前方一格
                            if (hitResult.getType() != BlockHitResult.Type.MISS) {
                                end = hitResult.getLocation().subtract(viewVec.scale(1.0));
                            }
                            
                            // 保存玩家的相对位置
                            Vec3 playerOffset = serverPlayer.position().subtract(mount.position());
                            
                            // 设置新位置，稍微提高一点以避免卡入地面
                            end = end.add(0, 0.1, 0);
                            mount.setPos(end.x, end.y, end.z);
                            mount.setDeltaMovement(Vec3.ZERO);
                            mount.setNoGravity(true);  // 禁用重力
                            
                            // 确保玩家跟随移动并保持骑乘状态
                            serverPlayer.setPos(end.x + playerOffset.x, end.y + playerOffset.y, end.z + playerOffset.z);
                            serverPlayer.startRiding(mount, true);
                            
                            // 禁用潜影贝的AI
                            shulker.setNoAi(true);
                            
                            // 播放音效和粒子效果
                            mount.level().playSound(null, 
                                end.x, end.y, end.z,
                                SoundEvents.ENDERMAN_TELEPORT, 
                                SoundSource.NEUTRAL, 
                                1.0F, 
                                1.0F
                            );
                            
                            ((ServerLevel)mount.level()).sendParticles(
                                ParticleTypes.PORTAL,
                                start.x, start.y, start.z,
                                20, 0.5, 0.5, 0.5,
                                0.0
                            );
                            
                            ((ServerLevel)mount.level()).sendParticles(
                                ParticleTypes.PORTAL,
                                end.x, end.y, end.z,
                                20, 0.5, 0.5, 0.5,
                                0.0
                            );
                        } else {  // 右键发射潜影贝弹
                            int bulletCount = 8;  // 发射的子弹数量
                            float spreadAngle = 15.0F;  // 扩散角度
                            
                            for (int i = 0; i < bulletCount; i++) {
                                ShulkerBullet bullet = EntityType.SHULKER_BULLET.create(mount.level());
                                if (bullet != null) {
                                    // 计算随机偏移角度
                                    float horizontalAngle = (float) (Math.random() * spreadAngle - spreadAngle/2);
                                    float verticalAngle = (float) (Math.random() * spreadAngle - spreadAngle/2);
                                    
                                    // 根据角度旋转向量
                                    Vec3 rotatedVec = viewVec
                                        .xRot((float) Math.toRadians(verticalAngle))
                                        .yRot((float) Math.toRadians(horizontalAngle));
                                    
                                    Vec3 start = mount.getEyePosition();
                                    bullet.setOwner(mount);
                                    bullet.setPos(start.x, start.y, start.z);
                                    bullet.shoot(rotatedVec.x, rotatedVec.y, rotatedVec.z, 1.5F, 0.0F);
                                    mount.level().addFreshEntity(bullet);
                                }
                            }
                            
                            // 播放音效
                            mount.level().playSound(null, 
                                mount.getX(), mount.getY(), mount.getZ(),
                                SoundEvents.SHULKER_SHOOT, 
                                SoundSource.HOSTILE, 
                                2.0F, 
                                (mount.getRandom().nextFloat() - mount.getRandom().nextFloat()) * 0.2F + 1.0F
                            );
                        }
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