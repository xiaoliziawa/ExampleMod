package com.prizowo.examplemod.client.render;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.items.ExplosiveArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ExplosiveArrowRenderer extends ArrowRenderer<ExplosiveArrow> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Examplemod.MODID,"textures/entity/projectiles/arrow.png");

    public ExplosiveArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosiveArrow entity) {
        return TEXTURE;
    }
} 