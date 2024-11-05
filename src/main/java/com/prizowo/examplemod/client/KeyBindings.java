package com.prizowo.examplemod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping MOUNT_KEY = new KeyMapping(
        "key.examplemod.mount",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.categories.examplemod"
    );

    public static final KeyMapping DESCEND_KEY = new KeyMapping(
        "key.examplemod.descend",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "key.categories.examplemod"
    );
} 