package com.prizowo.examplemod.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Inject(method = "killedEntity", at = @At("HEAD"), cancellable = true)
    private void onKilledEntity(ServerLevel level, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if ((level.getDifficulty() == Difficulty.NORMAL || level.getDifficulty() == Difficulty.HARD) && entity instanceof Villager killedVillager) {
            IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);
            if (ironGolem != null) {
                ironGolem.moveTo(killedVillager.getX(), killedVillager.getY(), killedVillager.getZ(), killedVillager.getYRot(), killedVillager.getXRot());
                level.addFreshEntity(ironGolem);
            }

            Villager newVillager = EntityType.VILLAGER.create(level);
            if (newVillager != null) {
                newVillager.moveTo(killedVillager.getX(), killedVillager.getY(), killedVillager.getZ(), killedVillager.getYRot(), killedVillager.getXRot());
                newVillager.finalizeSpawn(level, level.getCurrentDifficultyAt(newVillager.blockPosition()), MobSpawnType.CONVERSION, null);

                newVillager.setVillagerData(killedVillager.getVillagerData());
                newVillager.setVillagerXp(killedVillager.getVillagerXp());

                level.addFreshEntity(newVillager);
            }

            level.levelEvent(null, 1026, killedVillager.blockPosition(), 0);

            cir.setReturnValue(false);
            cir.cancel();
        }
    }       

    @Inject(method ="isSunSensitive", at = @At("HEAD"), cancellable = true)
    public void isSunSensitive(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
