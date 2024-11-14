package com.prizowo.examplemod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.entity.HoneyBombEntity;
import com.prizowo.examplemod.entity.SlimeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class HoneyBombRenderer extends EntityRenderer<HoneyBombEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public HoneyBombRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 1.0F;
    }

    @Override
    public void render(HoneyBombEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.pushPose();

        // 缩放为原始大小的一半
        poseStack.scale(0.5F, 0.5F, 0.5F);

        // 添加旋转动画
        float rotation = (entity.level().getGameTime() + partialTicks) * 20;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 渲染蜂蜜块
        blockRenderer.renderSingleBlock(
                Blocks.HONEY_BLOCK.defaultBlockState(),
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HoneyBombEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/block/honey_block.png");
    }
} 