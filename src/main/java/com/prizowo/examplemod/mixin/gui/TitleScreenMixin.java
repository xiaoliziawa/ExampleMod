package com.prizowo.examplemod.mixin.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import org.lwjgl.glfw.GLFW;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    // 按F快速进入上一次进入的世界
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_F) {
            try {
                LevelStorageSource.LevelCandidates levelList = null;
                if (this.minecraft != null) {
                    levelList = this.minecraft.getLevelSource().findLevelCandidates();
                }
                List<LevelStorageSource.LevelDirectory> levels = null;
                if (levelList != null) {
                    levels = levelList.levels();
                }

                if (levels != null && !levels.isEmpty()) {
                    String firstLevelName = levels.getFirst().directoryName();
                    if (this.minecraft != null) {
                        this.minecraft.createWorldOpenFlows().openWorld(
                                firstLevelName,
                                () -> this.minecraft.setScreen((TitleScreen) (Object) this)
                        );
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Inject(method = "createNormalMenuOptions", at = @At("TAIL"))
    private void addDemoButton(int y, int rowHeight, CallbackInfo ci) {
        // Demo按钮
        this.addRenderableWidget(Button.builder(Component.translatable("menu.playdemo"), (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.createWorldOpenFlows().createFreshLevel("Demo_World",
                                MinecraftServer.DEMO_SETTINGS,
                                WorldOptions.DEMO_OPTIONS,
                                WorldPresets::createNormalWorldDimensions,
                                (TitleScreen) (Object) this);
                    }
                })
            .bounds(this.width / 2 - 100, y + rowHeight * 5 + 24, 200, 20)
            .build());

        // 快速进入按钮
        this.addRenderableWidget(Button.builder(Component.translatable("mixin.example.join_world"), (button) -> {
            try {
                LevelStorageSource.LevelCandidates levelList = null;
                if (this.minecraft != null) {
                    levelList = this.minecraft.getLevelSource().findLevelCandidates();
                }
                List<LevelStorageSource.LevelDirectory> levels = null;
                if (levelList != null) {
                    levels = levelList.levels();
                }

                if (levels != null && !levels.isEmpty()) {
                    String firstLevelName = levels.getFirst().directoryName();
                    this.minecraft.createWorldOpenFlows().openWorld(
                            firstLevelName,
                            () -> this.minecraft.setScreen((TitleScreen) (Object) this)
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        .bounds(this.width - 100, this.height / 4, 80, 20)
        .build());
    }
}
