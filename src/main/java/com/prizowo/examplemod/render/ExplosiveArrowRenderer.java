package com.prizowo.examplemod.render;

import com.prizowo.examplemod.items.ExplosiveArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ExplosiveArrowRenderer extends ArrowRenderer<ExplosiveArrow> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("examplemod", "textures/entity/explosive_arrow.png");

    public ExplosiveArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull ExplosiveArrow arrow) {
        return TEXTURE;
    }
}
