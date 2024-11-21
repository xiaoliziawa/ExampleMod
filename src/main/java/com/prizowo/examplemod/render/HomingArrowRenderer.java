package com.prizowo.examplemod.render;

import com.prizowo.examplemod.items.HomingArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HomingArrowRenderer extends ArrowRenderer<HomingArrow> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("examplemod", "textures/entity/homing_arrow.png");

    public HomingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull HomingArrow arrow) {
        return TEXTURE;
    }
}
