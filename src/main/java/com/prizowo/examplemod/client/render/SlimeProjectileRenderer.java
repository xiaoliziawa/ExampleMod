package com.prizowo.examplemod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.entity.SlimeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class SlimeProjectileRenderer extends EntityRenderer<SlimeProjectile> {
    private final BlockRenderDispatcher blockRenderer;

    public SlimeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeProjectile entity) {
        return ResourceLocation.fromNamespaceAndPath(Examplemod.MODID,"textures/entity/slime_block.png");
    }

    @Override
    public void render(SlimeProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        poseStack.scale(0.5F, 0.5F, 0.5F);
        
        blockRenderer.renderSingleBlock(
            Blocks.SLIME_BLOCK.defaultBlockState(),
            poseStack,
            buffer,
            packedLight,
            OverlayTexture.NO_OVERLAY
        );
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
} 