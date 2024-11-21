package com.prizowo.examplemod;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue DEFAULT_STACK_SIZE = BUILDER.comment("The new default stack size for items. Vanilla default stack size is 64. Default is 1000.").defineInRange("defaultStackSize", 1000, 1, 1073741823);
    private static final ModConfigSpec.BooleanValue RESPECT_SMALL_STACK_SIZES = BUILDER.comment("Whether items with small stack sizes (ex: ender pearls) have a new stack size proportional to the new default stack size value. Setting this value to false will cause all items to have the same stack size. Default is true.").define("respectSmallStackSizes", false);
    static final ModConfigSpec SPEC = BUILDER.build();
    public static int defaultStackSize;
    public static boolean respectSmallStackSizes;

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        defaultStackSize = ((Integer) DEFAULT_STACK_SIZE.get()).intValue();
        respectSmallStackSizes = ((Boolean) RESPECT_SMALL_STACK_SIZES.get()).booleanValue();
    }
}