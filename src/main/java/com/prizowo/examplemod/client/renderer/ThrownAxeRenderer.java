package com.prizowo.examplemod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prizowo.examplemod.entity.ThrownAxeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;

public class ThrownAxeRenderer extends EntityRenderer<ThrownAxeEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownAxeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
    }

    @Override
    public void render(ThrownAxeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        if (entity.isStuck()) {
            poseStack.translate(0.0D, 0.0D, 0.0D);
            
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRotOnHit()));
            
            switch(entity.getStuckFace()) {
                case UP -> {
                    poseStack.translate(0, 0.1, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                }
                case DOWN -> {
                    poseStack.translate(0, -0.1, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                }
                case NORTH -> {
                    poseStack.translate(0, 0, -0.1);
                    poseStack.mulPose(Axis.XP.rotationDegrees(180));
                }
                case SOUTH -> {
                    poseStack.translate(0, 0, 0.1);
                }
                case EAST -> {
                    poseStack.translate(0.1, 0, 0);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                }
                case WEST -> {
                    poseStack.translate(-0.1, 0, 0);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                }
            }
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
            
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            
            float spinSpeed = 60.0F;
            float rotation = (entity.tickCount + partialTicks) * spinSpeed;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        }
        
        poseStack.scale(2.0F, 2.0F, 2.0F);
        
        itemRenderer.renderStatic(entity.getItem(),
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId());
        
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownAxeEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/" + entity.getItem().getItem().toString() + ".png");
    }
} 