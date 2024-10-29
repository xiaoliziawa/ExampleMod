package com.prizowo.examplemod.mixin.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerAttackMixin {

    @Unique
    private int exampleMod$attackCooldown = 0;

    @Inject(method = "getCurrentItemAttackStrengthDelay", at = @At("RETURN"), cancellable = true)
    private void modifyAttackDelay(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.0F);
    }

    @Inject(method = "getAttackStrengthScale", at = @At("RETURN"), cancellable = true) 
    private void modifyAttackStrength(float adjustTicks, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.0F);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        Player player = (Player)(Object)this;
        // 播放经验球拾取音效
        player.level().playSound(
            null, // null表示所有玩家都能听到
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.EXPERIENCE_ORB_PICKUP, // 经验球拾取音效
            SoundSource.PLAYERS, // 音效来源类型
            0.5F, // 音量
            1.0F  // 音调
        );
        target.invulnerableTime = 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        
        if (player.level().isClientSide) {
            // 检查是否按住左键
            if (Minecraft.getInstance().options.keyAttack.isDown()) {
                exampleMod$attackCooldown++;
                // 2tick攻击一次
                if (exampleMod$attackCooldown >= 2) {
                    exampleMod$attackCooldown = 0;
                    Entity target = Minecraft.getInstance().crosshairPickEntity;
                    if (target != null && player.distanceToSqr(target) < 36.0D) { // 6格距离内的进行连点攻击
                        // 发送攻击数据包
                        Minecraft.getInstance().gameMode.attack(player, target);
                        // 挥舞手臂动动画
                        player.swing(InteractionHand.MAIN_HAND);
                    }
                }
            } else {
                exampleMod$attackCooldown = 0;
            }
        }
    }
}
