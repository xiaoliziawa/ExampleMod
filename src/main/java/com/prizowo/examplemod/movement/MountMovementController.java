package com.prizowo.examplemod.movement;

import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import com.prizowo.examplemod.client.KeyBindings;
import net.neoforged.neoforge.network.PacketDistributor;
import com.prizowo.examplemod.network.MountFlyPacket;

public class MountMovementController {

    public static void updateMountMovement(Mob mount, Player rider) {
        // 获取玩家的输入
        float forward = rider.zza;
        float strafe = rider.xxa;

        // 获取生物的基础移动速度
        double speed = mount.getAttributeValue(Attributes.MOVEMENT_SPEED);

        // 获取当前移动
        Vec3 currentMotion = mount.getDeltaMovement();
        double moveX = currentMotion.x;
        double moveY = currentMotion.y;
        double moveZ = currentMotion.z;

        // 更新生物的朝向
        mount.setYRot(rider.getYRot());
        mount.yRotO = mount.getYRot();
        mount.yBodyRot = mount.getYRot();
        mount.yHeadRot = mount.yBodyRot;

        // 根据生物类型处理移动
        if (mount instanceof Slime) {
            handleSlimeMovement(mount, rider, forward, strafe, speed);
            return;
        }

        // 前后移动
        if (forward != 0) {
            double yawRadians = Math.toRadians(rider.getYRot());
            moveX = -Math.sin(yawRadians) * forward * speed;
            moveZ = Math.cos(yawRadians) * forward * speed;
        }

        // 左右移动
        if (strafe != 0) {
            double yawRadians = Math.toRadians(rider.getYRot());
            moveX += Math.cos(yawRadians) * strafe * speed;
            moveZ += Math.sin(yawRadians) * strafe * speed;
        }

        if (canFly(mount)) {
            if (Minecraft.getInstance().options.keyJump.isDown()) {
                PacketDistributor.sendToServer(new MountFlyPacket(true));
            }
            else if (KeyBindings.DESCEND_KEY.isDown()) {
                PacketDistributor.sendToServer(new MountFlyPacket(false));
            }
        } else {
            if (Minecraft.getInstance().options.keyJump.isDown() && mount.onGround()) {
                double jumpStrength = mount.getAttributeValue(Attributes.JUMP_STRENGTH);
                moveY = jumpStrength > 0 ? jumpStrength : 0.5;
            }
        }

        if (forward == 0 && strafe == 0 && !canFly(mount)) {
            moveX *= 0.8;
            moveZ *= 0.8;
        }

        mount.setDeltaMovement(moveX, moveY, moveZ);

        mount.getNavigation().stop();
        mount.setTarget(null);
        mount.setAggressive(false);
    }

    private static void handleSlimeMovement(Mob mount, Player rider, float forward, float strafe, double speed) {
        if ((forward != 0 || strafe != 0) && mount.onGround()) {
            // 计算跳跃方向
            double yawRadians = Math.toRadians(rider.getYRot());
            double jumpX = -Math.sin(yawRadians) * forward * speed * 2.0;
            double jumpZ = Math.cos(yawRadians) * forward * speed * 2.0;

            jumpZ += Math.sin(yawRadians) * strafe * speed * 2.0;

            mount.setDeltaMovement(jumpX, 0.4, jumpZ);
        } else if (!mount.onGround()) {
            Vec3 motion = mount.getDeltaMovement();
            mount.setDeltaMovement(motion.x * 0.98, motion.y, motion.z * 0.98);
        } else {
            mount.setDeltaMovement(0, mount.getDeltaMovement().y, 0);
        }
    }

    // 检查生物是否可以飞行
    private static boolean canFly(Mob mount) {
        return mount instanceof FlyingMob || // 飞行怪物
                mount instanceof FlyingAnimal || // 飞行动物
                mount instanceof EnderDragon || // 末影龙
                mount instanceof Ghast || // 恶魂
                mount instanceof Blaze || // 烈焰人
                mount instanceof WitherBoss ||// 凋零
                mount instanceof Bat ||
                mount instanceof Allay;
    }
} 