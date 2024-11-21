package com.prizowo.examplemod.client;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.network.ProjectileHitPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
@OnlyIn(Dist.CLIENT)
public class ParticleEffectHandler {
    private static final Map<Vec3, ParticleAnimation> activeAnimations = new HashMap<>();
    private static final Random random = new Random();
    
    private static final double PROJECTILE_SPEED = 1.0;
    private static final double SPIRAL_RADIUS = 0.5;
    private static final double SPIRAL_SPEED = 0.4;
    private static final int TOTAL_ANIMATION_TICKS = 80;
    private static final int SONIC_ANIMATION_DURATION = 40;
    private static final double SONIC_HEIGHT = 30.0D;

    private static class ParticleAnimation {
        final Vec3 position;
        int tick = 0;
        final int maxTicks;
        
        ParticleAnimation(Vec3 position, int maxTicks) {
            this.position = position;
            this.maxTicks = maxTicks;
        }
        
        boolean update(Level level) {
            if (tick >= maxTicks) {
                return false;
            }
            tick++;
            return true;
        }

        protected float nextFloat() {
            return random.nextFloat();
        }

        protected double nextDouble() {
            return random.nextDouble();
        }
    }

    private static void addParticle(Level level, ParticleOptions particle, 
            double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (Minecraft.getInstance().isSameThread()) {
            level.addParticle(particle, true, x, y, z, xSpeed, ySpeed, zSpeed);
        } else {
            Minecraft.getInstance().execute(() -> 
                level.addParticle(particle, true, x, y, z, xSpeed, ySpeed, zSpeed)
            );
        }
    }

    public static void handleMagicProjectile(Vec3 start, Vec3 direction, double range) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            activeAnimations.put(start, new ParticleAnimation(start, (int)(range/PROJECTILE_SPEED)) {
                @Override
                boolean update(Level level) {
                    if (!super.update(level)) return false;
                    
                    // 计算当前位置
                    Vec3 currentPos = position.add(direction.scale(tick * PROJECTILE_SPEED));
                    
                    // 进行射线检测
                    var hitResult = level.clip(new net.minecraft.world.level.ClipContext(
                        currentPos,
                        currentPos.add(direction.scale(PROJECTILE_SPEED)),
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        Minecraft.getInstance().player
                    ));

                    // 如果发生碰撞
                    if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.MISS) {
                        // 发送碰撞消息
                        PacketDistributor.sendToServer(new ProjectileHitPacket(hitResult.getLocation()));
                        return false; // 结束粒子效果
                    }
                    
                    // 检查实体碰撞
                    AABB boundingBox = new AABB(
                        currentPos.x - 0.5, currentPos.y - 0.5, currentPos.z - 0.5,
                        currentPos.x + 0.5, currentPos.y + 0.5, currentPos.z + 0.5
                    );
                    
                    List<Entity> entities = level.getEntities((Entity)null, boundingBox);
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity && entity != Minecraft.getInstance().player) {
                            // 发送碰撞消息
                            PacketDistributor.sendToServer(new ProjectileHitPacket(currentPos));
                            return false; // 结束粒子效果
                        }
                    }

                    // 计算主螺旋轨迹
                    double angle = tick * SPIRAL_SPEED;
                    Vec3 offset = new Vec3(
                        Math.cos(angle) * SPIRAL_RADIUS,
                        Math.sin(angle) * SPIRAL_RADIUS,
                        0
                    ).xRot((float)Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)))
                     .yRot((float)Math.atan2(-direction.x, direction.z));

                    currentPos = currentPos.add(offset);

                    // 主魔法弹粒子
                    addParticle(level, ParticleTypes.END_ROD,
                            currentPos.x, currentPos.y, currentPos.z,
                            0.05, 0.05, 0.05);
                            
                    // 能量核心效果
                    addParticle(level, ParticleTypes.SOUL_FIRE_FLAME,
                            currentPos.x, currentPos.y, currentPos.z,
                            0.02, 0.02, 0.02);
                            
                    // 魔法轨迹粒子 - 双螺旋效果
                    for (int i = 0; i < 2; i++) {
                        double trailAngle = angle + Math.PI * i;
                        Vec3 trailOffset = new Vec3(
                            Math.cos(trailAngle) * SPIRAL_RADIUS * 1.5,
                            Math.sin(trailAngle) * SPIRAL_RADIUS * 1.5,
                            0
                        ).xRot((float)Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)))
                         .yRot((float)Math.atan2(-direction.x, direction.z));
                        
                        Vec3 trailPos = currentPos.add(trailOffset);
                        
                        addParticle(level, ParticleTypes.SOUL_FIRE_FLAME,
                                trailPos.x, trailPos.y, trailPos.z,
                                0.02, 0.02, 0.02);
                                
                        addParticle(level, ParticleTypes.DRAGON_BREATH,
                                trailPos.x, trailPos.y, trailPos.z,
                                0.05, 0.05, 0.05);
                    }
                    
                    // 随机能量散射
                    if (java.util.concurrent.ThreadLocalRandom.current().nextFloat() < 0.3) {
                        double scatterAngle = java.util.concurrent.ThreadLocalRandom.current().nextDouble() * Math.PI * 2;
                        double scatterRadius = java.util.concurrent.ThreadLocalRandom.current().nextDouble() * 0.5;
                        Vec3 scatterOffset = new Vec3(
                            Math.cos(scatterAngle) * scatterRadius,
                            Math.sin(scatterAngle) * scatterRadius,
                            0
                        ).xRot((float)Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)))
                         .yRot((float)Math.atan2(-direction.x, direction.z));
                         
                        Vec3 scatterPos = currentPos.add(scatterOffset);
                        addParticle(level, ParticleTypes.ELECTRIC_SPARK,
                                scatterPos.x, scatterPos.y, scatterPos.z,
                                0.02, 0.02, 0.02);
                    }
                    
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                            serverLevel.getServer().getTickCount() + 1, () -> {
                                // 空的，因为我们使用tick系统
                            }
                        ));
                    }

                    return true;
                }
            });
        }
    }

    public static void handleMagicCircle(Vec3 targetPos) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            activeAnimations.put(targetPos, new ParticleAnimation(targetPos, TOTAL_ANIMATION_TICKS) {
                @Override
                boolean update(Level level) {
                    if (!super.update(level)) return false;
                    
                    float progress = (float) tick / maxTicks;
                    
                    // 多重魔法阵
                    for (int ring = 0; ring < 3; ring++) {
                        double ringHeight = SONIC_HEIGHT + ring * 0.5;
                        double ringScale = 1.0 + ring * 0.2;
                        double ringRotation = tick * 0.02 * (ring % 2 == 0 ? 1 : -1);
                        
                        // 五角星
                        for (int i = 0; i < 5; i++) {
                            double angle1 = ringRotation + (i * 2 * Math.PI / 5);
                            double angle2 = ringRotation + ((i + 2) % 5 * 2 * Math.PI / 5);
                            
                            // 绘制五角星的边
                            for (double t = 0; t <= 1; t += 0.02) {
                                double x = targetPos.x + (Math.cos(angle1) * 6.0 * (1 - t) + 
                                                        Math.cos(angle2) * 6.0 * t) * ringScale;
                                double z = targetPos.z + (Math.sin(angle1) * 6.0 * (1 - t) + 
                                                        Math.sin(angle2) * 6.0 * t) * ringScale;
                                
                                // 主要光线
                                addParticle(level, ParticleTypes.END_ROD,
                                        x, targetPos.y + ringHeight, z,
                                        0, 0, 0);
                                        
                                // 能量流动效果
                                if (nextFloat() < 0.2) {
                                    double flowOffset = Math.sin(t * Math.PI * 4 + tick * 0.2) * 0.3;
                                    addParticle(level, ParticleTypes.DRAGON_BREATH,
                                            x, targetPos.y + ringHeight + flowOffset, z,
                                            0, 0.05, 0);
                                }
                            }
                        }
                        
                        // 外圈光环
                        for (int i = 0; i < 60; i++) {
                            double angle = i * Math.PI * 2 / 60 + ringRotation;
                            double radius = 7.0 * ringScale;
                            double x = targetPos.x + Math.cos(angle) * radius;
                            double z = targetPos.z + Math.sin(angle) * radius;
                            
                            addParticle(level, ParticleTypes.END_ROD,
                                    x, targetPos.y + ringHeight, z,
                                    0, 0, 0);
                        }
                        
                        // 能量连接线
                        if (tick % 5 == 0) {
                            for (int i = 0; i < 5; i++) {
                                double startAngle = (i * 2 * Math.PI / 5) + ringRotation;
                                double x1 = targetPos.x + Math.cos(startAngle) * 6.0 * ringScale;
                                double z1 = targetPos.z + Math.sin(startAngle) * 6.0 * ringScale;
                                
                                for (int j = 0; j < 10; j++) {
                                    double t = j / 10.0;
                                    double y = targetPos.y + ringHeight + Math.sin(t * Math.PI) * 0.5;
                                    
                                    addParticle(level, ParticleTypes.ELECTRIC_SPARK,
                                            x1, y, z1,
                                            0, 0, 0);
                                }
                            }
                        }
                    }
                    
                    // 中心能量柱
                    for (int i = 0; i < 8; i++) {
                        double angle = tick * 0.1 + i * Math.PI / 4;
                        double x = targetPos.x + Math.cos(angle) * 0.3;
                        double z = targetPos.z + Math.sin(angle) * 0.3;
                        
                        addParticle(level, ParticleTypes.END_ROD,
                                x, targetPos.y + SONIC_HEIGHT, z,
                                0, 0.1, 0);
                    }
                    
                    // 能量汇聚效果
                    if (tick % 2 == 0) {
                        for (int i = 0; i < 5; i++) {
                            double angle = nextDouble() * Math.PI * 2;
                            double radius = nextDouble() * 8.0;
                            double height = SONIC_HEIGHT + nextDouble() * 2 - 1;
                            
                            addParticle(level, ParticleTypes.DRAGON_BREATH,
                                    targetPos.x + Math.cos(angle) * radius,
                                    targetPos.y + height,
                                    targetPos.z + Math.sin(angle) * radius,
                                    0, 0.05, 0);
                        }
                    }
                    
                    return true;
                }
            });
        }
    }

    public static void handleSonicBoom(Vec3 targetPos) {
        if (Minecraft.getInstance().level != null) {
            activeAnimations.put(targetPos, new ParticleAnimation(targetPos, SONIC_ANIMATION_DURATION) {
                @Override
                boolean update(Level level) {
                    if (!super.update(level)) return false;
                    
                    float progress = (float) tick / maxTicks;
                    Vec3 sonicPos = new Vec3(targetPos.x, targetPos.y + SONIC_HEIGHT, targetPos.z);
                    
                    // 在上方添加五角星法阵 - 调整高度
                    double upperStarHeight = SONIC_HEIGHT + 5.0; // 改为5个方块而不是15个方块
                    double starRotation = tick * 0.02;
                    
                    // 绘制上方的五角星
                    for (int i = 0; i < 5; i++) {
                        double angle1 = starRotation + (i * 2 * Math.PI / 5);
                        double angle2 = starRotation + ((i + 2) % 5 * 2 * Math.PI / 5);
                        
                        // 绘制五角星的边
                        for (double t = 0; t <= 1; t += 0.02) {
                            double x = targetPos.x + (Math.cos(angle1) * 8.0 * (1 - t) + 
                                                    Math.cos(angle2) * 8.0 * t);
                            double z = targetPos.z + (Math.sin(angle1) * 8.0 * (1 - t) + 
                                                    Math.sin(angle2) * 8.0 * t);
                            
                            // 主要光线
                            addParticle(level, ParticleTypes.END_ROD,
                                    x, targetPos.y + upperStarHeight, z,
                                    0, 0, 0);
                                    
                            // 能量流动效果
                            if (nextFloat() < 0.2) {
                                double flowOffset = Math.sin(t * Math.PI * 4 + tick * 0.2) * 0.3;
                                addParticle(level, ParticleTypes.DRAGON_BREATH,
                                        x, targetPos.y + upperStarHeight + flowOffset, z,
                                        0, 0.05, 0);
                            }
                        }
                    }
                    
                    // 外圈光环
                    for (int i = 0; i < 60; i++) {
                        double angle = i * Math.PI * 2 / 60 + starRotation;
                        double radius = 10.0;
                        double x = targetPos.x + Math.cos(angle) * radius;
                        double z = targetPos.z + Math.sin(angle) * radius;
                        
                        addParticle(level, ParticleTypes.END_ROD,
                                x, targetPos.y + upperStarHeight, z,
                                0, 0, 0);
                    }
                    
                    // 能量连接线
                    if (tick % 5 == 0) {
                        for (int i = 0; i < 5; i++) {
                            double startAngle = (i * 2 * Math.PI / 5) + starRotation;
                            double x1 = targetPos.x + Math.cos(startAngle) * 8.0;
                            double z1 = targetPos.z + Math.sin(startAngle) * 8.0;
                            
                            // 连接到中心的能量线
                            for (double t = 0; t <= 1; t += 0.1) {
                                addParticle(level, ParticleTypes.ELECTRIC_SPARK,
                                        x1 * (1-t) + targetPos.x * t, 
                                        targetPos.y + upperStarHeight + Math.sin(t * Math.PI) * 0.5,
                                        z1 * (1-t) + targetPos.z * t,
                                        0, 0, 0);
                            }
                        }
                    }

                    // 能量汇聚效果
                    for (int i = 0; i < 120; i++) {
                        double angle = nextDouble() * Math.PI * 2;
                        double radius = nextDouble() * 25;
                        double height = SONIC_HEIGHT + nextDouble() * 5; // 改为5而不是15
                        
                        double x = targetPos.x + Math.cos(angle) * radius;
                        double z = targetPos.z + Math.sin(angle) * radius;
                        
                        Vec3 particlePos = new Vec3(x, targetPos.y + height, z);
                        Vec3 toCenter = sonicPos.subtract(particlePos).normalize();
                        
                        // 主能量流
                        addParticle(level, ParticleTypes.SONIC_BOOM,
                                x, targetPos.y + height, z,
                                toCenter.x * 0.4, -0.4, toCenter.z * 0.4);
                        
                        // 能量尾迹
                        if (nextFloat() < 0.3) {
                            addParticle(level, ParticleTypes.DRAGON_BREATH,
                                    x, targetPos.y + height, z,
                                    toCenter.x * 0.2, -0.2, toCenter.z * 0.2);
                        }
                    }
                    
                    // 多重能量柱
                    for (double y = SONIC_HEIGHT; y > 0; y -= 0.2) {
                        double yProgress = y / SONIC_HEIGHT;
                        
                        // 中心光柱
                        double mainRadius = 2.0 * (1 - yProgress) * (1 - progress);
                        for (int i = 0; i < 12; i++) {
                            double angle = (i * Math.PI * 2 / 12) + (y * 0.2) + (tick * 0.1);
                            double x = sonicPos.x + Math.cos(angle) * mainRadius;
                            double z = sonicPos.z + Math.sin(angle) * mainRadius;
                            
                            addParticle(level, ParticleTypes.SONIC_BOOM,
                                    x, targetPos.y + y, z,
                                    0, 0.15, 0);
                        }
                        
                        // 外围螺旋能量
                        if (y % 0.8 < 0.2) {
                            for (int spiral = 0; spiral < 4; spiral++) {
                                double spiralAngle = (y * 0.5 + (spiral * Math.PI / 2) + tick * 0.2) % (Math.PI * 2);
                                double spiralRadius = 4.0 * (1 - yProgress) * (1 - progress * 0.8);
                                
                                for (int point = 0; point < 3; point++) {
                                    double pointAngle = spiralAngle + (point * Math.PI * 2 / 3);
                                    double x = sonicPos.x + Math.cos(pointAngle) * spiralRadius;
                                    double z = sonicPos.z + Math.sin(pointAngle) * spiralRadius;
                                    
                                    addParticle(level, ParticleTypes.SONIC_BOOM,
                                            x, targetPos.y + y, z,
                                            0, 0.2, 0);
                                }
                            }
                        }
                    }
                    
                    // 地面冲击波系统
                    double shockwaveRadius = 15.0 * (1 - Math.pow(1 - progress, 2));
                    
                    // 主冲击波
                    for (double radius = 0; radius <= shockwaveRadius; radius += 0.3) {
                        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 48) {
                            double x = targetPos.x + Math.cos(angle) * radius;
                            double z = targetPos.z + Math.sin(angle) * radius;
                            
                            addParticle(level, ParticleTypes.SONIC_BOOM,
                                    x, targetPos.y + 0.1, z,
                                    0, 0, 0);
                        }
                    }
                    
                    // 能量波纹
                    for (int wave = 0; wave < 3; wave++) {
                        double waveRadius = shockwaveRadius * (0.5 + wave * 0.25);
                        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 24) {
                            double x = targetPos.x + Math.cos(angle) * waveRadius;
                            double z = targetPos.z + Math.sin(angle) * waveRadius;
                            double waveHeight = Math.sin(waveRadius - tick * 0.4) * 0.8;
                            
                            addParticle(level, ParticleTypes.SONIC_BOOM,
                                    x, targetPos.y + waveHeight, z,
                                    0, 0.1, 0);
                        }
                    }
                    
                    // 垂直能量柱
                    if (tick % 2 == 0) {
                        for (int pillar = 0; pillar < 8; pillar++) {
                            double pillarAngle = pillar * Math.PI / 4 + tick * 0.1;
                            double pillarRadius = shockwaveRadius * 0.7;
                            double x = targetPos.x + Math.cos(pillarAngle) * pillarRadius;
                            double z = targetPos.z + Math.sin(pillarAngle) * pillarRadius;
                            
                            for (double y = 0; y < 8; y += 0.5) {
                                double pulseOffset = Math.sin(y * 0.5 + tick * 0.2) * 0.3;
                                addParticle(level, ParticleTypes.SONIC_BOOM,
                                        x + pulseOffset, targetPos.y + y, z + pulseOffset,
                                        0, 0.1, 0);
                            }
                        }
                    }
                    
                    // 音效系统
                    if (tick % 8 == 0) {
                        float randomPitch = level.getRandom().nextFloat() * 0.2F + 0.5F;
                        level.playLocalSound(targetPos.x, targetPos.y, targetPos.z,
                                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS,
                                3.0F * (1 - progress), randomPitch, false);
                    }
                    
                    return true;
                }
            });
        }
    }

    // 修改事件监听器
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!Minecraft.getInstance().isSameThread()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        Iterator<Map.Entry<Vec3, ParticleAnimation>> it = activeAnimations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vec3, ParticleAnimation> entry = it.next();
            if (!entry.getValue().update(mc.level)) {
                it.remove();
            }
        }
    }
} 