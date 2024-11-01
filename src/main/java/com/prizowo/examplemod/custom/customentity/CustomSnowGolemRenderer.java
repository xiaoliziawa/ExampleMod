package com.prizowo.examplemod.custom.customentity;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CustomSnowGolemRenderer extends MobRenderer<CustomSnowGolem, SnowGolemModel<CustomSnowGolem>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "textures/entity/snow_golem.png");

    public CustomSnowGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new SnowGolemModel<>(context.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CustomSnowGolem entity) {
        return TEXTURE;
    }
}
