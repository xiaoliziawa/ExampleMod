package com.prizowo.examplemod.custom.customentity;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MyHumanoidEntityRenderer extends HumanoidMobRenderer<MyCustomEntity, MyHumanoidModel<MyCustomEntity>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.tryBuild(Examplemod.MODID, "textures/entity/my_humanoid.png");
    private static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.tryParse("textures/entity/steve.png");

    public MyHumanoidEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new MyHumanoidModel<>(context.bakeLayer(MyModelLayers.MY_HUMANOID)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MyCustomEntity entity) {
        return TEXTURE != null ? TEXTURE : FALLBACK_TEXTURE;
    }
}
