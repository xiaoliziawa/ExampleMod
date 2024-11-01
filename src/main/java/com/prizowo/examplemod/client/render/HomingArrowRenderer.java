package com.prizowo.examplemod.client.render;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.items.HomingArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HomingArrowRenderer extends ArrowRenderer<HomingArrow> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Examplemod.MOD_ID, "textures/entity/projectiles/arrow.png");

    public HomingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HomingArrow entity) {
        return TEXTURE;
    }
} 