package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.custom.customentity.CustomBeeEntity;
import com.prizowo.examplemod.entity.HoneyBombEntity;
import com.prizowo.examplemod.entity.SlimeProjectile;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    // 在类的开头添加一个静态Map来跟踪冷却时间
    private static final Map<UUID, Long> wardenSonicBoomCooldowns = new HashMap<>();
    private static final long WARDEN_SONIC_BOOM_COOLDOWN = 5000; // 5秒冷却时间

    // 在类的开头添加蜜蜂技能冷却Map

    public static void handle(final MountAttackPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.getVehicle() instanceof Mob mount) {
                    Vec3 viewVec = serverPlayer.getViewVector(1.0F);

                    switch (mount) {

                        case Ghast ignored -> {
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
                        case Blaze ignored -> {
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
                        case WitherBoss ignored -> {
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
                        case AbstractSkeleton ignored -> {
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
                        case Slime ignored -> {
                            SlimeProjectile projectile = new SlimeProjectile(mount.level(), mount);
                            Vec3 spawnPos = serverPlayer.getEyePosition().add(viewVec.scale(2.0));
                            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                            projectile.setDeltaMovement(viewVec.scale(1.5));
                            mount.level().addFreshEntity(projectile);
                        }
                        case Creeper ignored -> {
                            PrimedTnt tnt = EntityType.TNT.create(mount.level());
                            if (tnt != null) {
                                Vec3 spawnPos = mount.position().add(viewVec.scale(2.0)).add(0, 1, 0);
                                tnt.setPos(spawnPos);
                                tnt.setDeltaMovement(viewVec.scale(1.0));
                                tnt.setFuse(40);
                                mount.level().addFreshEntity(tnt);
                            }
                        }
                        case Evoker ignored -> {
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
                        case Llama ignored -> {
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
                        case IronGolem ironGolem -> {
                            if (!data.isRightClick()) {  // 左键 - 地面冲击波攻击
                                // 播放蓄力音效
                                mount.level().playSound(null,
                                        mount.getX(), mount.getY(), mount.getZ(),
                                        SoundEvents.IRON_GOLEM_REPAIR,
                                        SoundSource.HOSTILE,
                                        2.0F,
                                        0.5F
                                );

                                // 生成蓄力粒子效果
                                if (mount.level() instanceof ServerLevel serverLevel) {
                                    for (int i = 0; i < 20; i++) {
                                        double angle = i * Math.PI * 2 / 20;
                                        double radius = 2.0;
                                        serverLevel.sendParticles(
                                                ParticleTypes.CRIT,
                                                mount.getX() + Math.cos(angle) * radius,
                                                mount.getY(),
                                                mount.getZ() + Math.sin(angle) * radius,
                                                5, 0, 0.1, 0, 0.1
                                        );
                                    }
                                }

                                // 延迟执行冲击波效果
                                mount.level().getServer().tell(new TickTask(
                                        mount.level().getServer().getTickCount() + 10,
                                        () -> {
                                            // 冲击波范围
                                            double range = 8.0;
                                            AABB attackArea = new AABB(
                                                    mount.getX() - range, mount.getY() - 2, mount.getZ() - range,
                                                    mount.getX() + range, mount.getY() + 2, mount.getZ() + range
                                            );

                                            // 获取范围内的实体
                                            List<LivingEntity> targets = mount.level().getEntitiesOfClass(
                                                    LivingEntity.class,
                                                    attackArea,
                                                    entity -> entity != mount && 
                                                             entity != serverPlayer && 
                                                             !(entity instanceof Player)
                                            );

                                            // 播放冲击波音效
                                            mount.level().playSound(null,
                                                    mount.getX(), mount.getY(), mount.getZ(),
                                                    SoundEvents.GENERIC_EXPLODE,
                                                    SoundSource.HOSTILE,
                                                    2.0F,
                                                    0.8F
                                            );

                                            // 对每个目标造成伤害和击退
                                            for (LivingEntity target : targets) {
                                                target.hurt(mount.level().damageSources().mobAttack(mount), 10.0F);
                                                
                                                // 计算击退方向和强度
                                                Vec3 knockback = target.position().subtract(mount.position()).normalize();
                                                double verticalKnockback = 0.8;
                                                target.setDeltaMovement(
                                                        knockback.x * 1.5,
                                                        verticalKnockback,
                                                        knockback.z * 1.5
                                                );

                                                // 设置为目标
                                                mount.setTarget(target);


                                            }

                                            // 生成圆形冲击波粒子效果
                                            if (mount.level() instanceof ServerLevel serverLevel) {
                                                for (double r = 0; r < range; r += 0.5) {
                                                    for (int i = 0; i < 360; i += 15) {
                                                        double angle = Math.toRadians(i);
                                                        double x = mount.getX() + Math.cos(angle) * r;
                                                        double z = mount.getZ() + Math.sin(angle) * r;
                                                        serverLevel.sendParticles(
                                                                ParticleTypes.EXPLOSION,
                                                                x, mount.getY(), z,
                                                                1, 0, 0, 0, 0
                                                        );
                                                    }
                                                }
                                            }
                                        }
                                ));
                            }
                        }
                        case Witch ignored -> {
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
                        case Villager ignored -> {
                            Vec3 start = mount.getEyePosition();
                            Vec3 baseViewVec = viewVec;

                            // 在服务器端为玩家播放声音
                            mount.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                                    SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0F, 1.0F);

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
                                    target.hurt(mount.level().damageSources().mobAttack(mount), 15.0f);  // 每个弹丸的伤平分

                                }
                            }
                        }
                        case Shulker shulker -> {
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

                                // 播放音效粒子效果
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
                        case CustomBeeEntity customBeeEntity -> {
                            if (!data.isRightClick()) {
                                // 左键 - 投掷蜂蜜炸弹
                                HoneyBombEntity honeyBomb = new HoneyBombEntity(mount.level(), mount);
                                Vec3 spawnPos = mount.position().subtract(0, 0.5, 0);

                                honeyBomb.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                                honeyBomb.setDeltaMovement(0, -0.5, 0);

                                // 播放投弹音效
                                mount.level().playSound(null, mount.getX(), mount.getY(), mount.getZ(),
                                        SoundEvents.HONEY_BLOCK_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                                // 添加到世界
                                mount.level().addFreshEntity(honeyBomb);
                            } else if (mount.level() instanceof ServerLevel serverLevel) {
                                // 右键 - 召唤蜂群
                                // 播放召唤音效
                                serverLevel.playSound(null, mount.getX(), mount.getY(), mount.getZ(),
                                        SoundEvents.BEE_LOOP, SoundSource.NEUTRAL, 2.0F, 1.2F);

                                // 生成蜜蜂群
                                int beeCount = 8;
                                double spawnRadius = 2.0;

                                for (int i = 0; i < beeCount; i++) {
                                    Bee bee = new Bee(EntityType.BEE, serverLevel);

                                    // 随机生成位置
                                    double angle = mount.getRandom().nextDouble() * Math.PI * 2;
                                    double offsetX = Math.cos(angle) * spawnRadius;
                                    double offsetZ = Math.sin(angle) * spawnRadius;

                                    bee.setPos(
                                            mount.getX() + offsetX,
                                            mount.getY(),
                                            mount.getZ() + offsetZ
                                    );

                                    // 设置蜜蜂属性
                                    bee.setRemainingPersistentAngerTime(1200);
                                    bee.setPersistentAngerTarget(null);

                                    // 添加到世界
                                    serverLevel.addFreshEntity(bee);

                                    // 设置目标
                                    double targetRange = 16.0;
                                    AABB searchArea = new AABB(
                                            mount.getX() - targetRange, mount.getY() - targetRange, mount.getZ() - targetRange,
                                            mount.getX() + targetRange, mount.getY() + targetRange, mount.getZ() + targetRange
                                    );

                                    List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(
                                            LivingEntity.class,
                                            searchArea,
                                            entity -> entity != mount &&
                                                    !(entity instanceof Player) &&
                                                    !(entity instanceof Bee)
                                    );

                                    if (!nearbyEntities.isEmpty()) {
                                        LivingEntity target = nearbyEntities.get(
                                                mount.getRandom().nextInt(nearbyEntities.size())
                                        );
                                        bee.setTarget(target);
                                    }
                                }

                                // 额外的蜂群效果
                                for (int i = 0; i < 20; i++) {
                                    double angle = mount.getRandom().nextDouble() * Math.PI * 2;
                                    double radius = mount.getRandom().nextDouble() * 3;
                                    double offsetX = Math.cos(angle) * radius;
                                    double offsetY = mount.getRandom().nextDouble() * 2;
                                    double offsetZ = Math.sin(angle) * radius;

                                    serverLevel.sendParticles(
                                            ParticleTypes.WAX_ON,
                                            mount.getX() + offsetX,
                                            mount.getY() + offsetY,
                                            mount.getZ() + offsetZ,
                                            1, 0.0, 0.0, 0.0, 0.0
                                    );
                                }
                            }
                        }
                        case Warden warden -> {
                            if (!data.isRightClick()) {  // 只在左键时触发
                                UUID mountId = mount.getUUID();
                                long currentTime = System.currentTimeMillis();
                                Long lastUseTime = wardenSonicBoomCooldowns.get(mountId);

                                if (lastUseTime == null || currentTime - lastUseTime >= WARDEN_SONIC_BOOM_COOLDOWN) {
                                    // 声波攻击
                                    Vec3 start = mount.getEyePosition();
                                    Vec3 end = start.add(viewVec.multiply(20, 20, 20));

                                    // 播放声波攻击音效
                                    mount.level().playSound(null, mount.getX(), mount.getY(), mount.getZ(),
                                            SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, 1.0F);

                                    // 检测实体碰撞
                                    AABB boundingBox = new AABB(start.x, start.y, start.z, end.x, end.y, end.z).inflate(2.0);
                                    List<Entity> hitEntities = mount.level().getEntities(mount, boundingBox);

                                    // 添加声波粒子效果路径
                                    if (mount.level() instanceof ServerLevel serverLevel) {
                                        double distance = start.distanceTo(end);
                                        Vec3 direction = viewVec.normalize();
                                        for (double d = 0; d < distance; d += 0.5) {
                                            Vec3 pos = start.add(direction.scale(d));
                                            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                                                    pos.x, pos.y, pos.z,
                                                    1, 0, 0, 0, 0);
                                        }
                                    }

                                    for (Entity target : hitEntities) {
                                        if (target != mount && target != serverPlayer) {
                                            target.hurt(mount.level().damageSources().sonicBoom(mount), 50.0f);
                                        }
                                    }

                                    // 更新冷却时间
                                    wardenSonicBoomCooldowns.put(mountId, currentTime);

                                    // 安排5秒后的冷却完成音效
                                    mount.level().getServer().tell(new TickTask(
                                            mount.level().getServer().getTickCount() + 100, // 5秒 = 100 ticks
                                            () -> {
                                                // 播放冷却完成音效
                                                mount.level().playSound(null,
                                                    mount.getX(), mount.getY(), mount.getZ(),
                                                    SoundEvents.WARDEN_SONIC_CHARGE,
                                                    SoundSource.HOSTILE,
                                                    2.0F,
                                                    1.0F
                                                );
                                            }
                                    ));
                                }
                            }
                        }
                        default -> { } // 如果没有匹配的实体类型，不执行任何操作
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

