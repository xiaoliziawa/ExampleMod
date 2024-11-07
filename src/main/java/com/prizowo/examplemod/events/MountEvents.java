package com.prizowo.examplemod.events;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.util.Unit;

import java.lang.reflect.Field;
import java.util.Set;

@EventBusSubscriber(modid = Examplemod.MODID)
public class MountEvents {
    
    private static Field targetTypeField;
    private static Field availableGoalsField;
    
    static {
        try {
            targetTypeField = NearestAttackableTargetGoal.class.getDeclaredField("targetType");
            targetTypeField.setAccessible(true);
            
            availableGoalsField = GoalSelector.class.getDeclaredField("availableGoals");
            availableGoalsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    
    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (!event.isDismounting()) { // 骑上时
            Entity mount = event.getEntityBeingMounted();
            if (mount instanceof Warden warden) {
                try {
                    // 使用反射获取并清除所有目标
                    Set<?> availableGoals = (Set<?>) availableGoalsField.get(warden.targetSelector);
                    availableGoals.clear();
                    
                    // 使用反射获取并清除所有攻击相关的AI
                    Set<?> goals = (Set<?>) availableGoalsField.get(warden.goalSelector);
                    goals.clear();
                    
                    // 清除所有相关的记忆
                    warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    warden.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                    warden.getBrain().eraseMemory(MemoryModuleType.SONIC_BOOM_COOLDOWN);
                    warden.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
                    warden.getBrain().eraseMemory(MemoryModuleType.DISTURBANCE_LOCATION);
                    warden.getBrain().eraseMemory(MemoryModuleType.SNIFF_COOLDOWN);
                    warden.getBrain().eraseMemory(MemoryModuleType.RECENT_PROJECTILE);
                    warden.getBrain().eraseMemory(MemoryModuleType.VIBRATION_COOLDOWN);
                    warden.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
                    warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
                    warden.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                    warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
                    warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
                    warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
                    warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                    warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
                    warden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                    warden.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
                    
                    // 重置监守者的状态
                    warden.getAngerManagement().clearAnger(null);
                    warden.setAggressive(false);
                    
                    // 禁用振动感知和嗅探
                    warden.getBrain().setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, Long.MAX_VALUE);
                    warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, Long.MAX_VALUE);
                    warden.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, Long.MAX_VALUE);
                    
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        if (event.isDismounting()) {
            Entity mount = event.getEntityBeingMounted();
            if (mount instanceof Mob mob) {
                if (mount instanceof Warden warden) {
                    // 重新添加所有AI
                    warden.targetSelector.addGoal(2, new HurtByTargetGoal(warden));
                    warden.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(warden, Player.class, true));
                    warden.goalSelector.addGoal(4, new MeleeAttackGoal(warden, 1.0D, true));
                }
                
                // 如果是潜影贝，恢复重力
                if (mount instanceof Shulker) {
                    mount.setNoGravity(false);
                }
            }
        }
    }
} 