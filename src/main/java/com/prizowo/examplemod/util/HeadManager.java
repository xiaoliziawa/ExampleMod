package com.prizowo.examplemod.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeadManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean hideHead = false;

    public static void toggleHead(Player player) {
        hideHead = !hideHead;
        if (hideHead) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, Integer.MAX_VALUE, 0, false, false));
        } else {
            player.removeEffect(MobEffects.DARKNESS);
        }
        LOGGER.info("Head visibility changed to: " + (hideHead ? "hidden" : "visible"));
    }

    public static boolean isHeadHidden() {
        return hideHead;
    }
} 