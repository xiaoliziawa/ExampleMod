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
            // 箭矢式插入效果
            poseStack.translate(0.0D, 0.0D, 0.0D);
            
            // 使用保存的旋转角度
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRotOnHit()));
            
            // 根据插入面调整角度
            switch(entity.getStuckFace()) {
                case UP -> {
                    poseStack.translate(0, 0.1, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90)); // 平躺在地上
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
            // 飞行中的渲染
            // 首先让斧头朝向飞行方向
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
            
            // 让斧头平放
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            
            // 平面旋转动画
            float spinSpeed = 60.0F; // 增加旋转速度
            float rotation = (entity.tickCount + partialTicks) * spinSpeed;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        }
        
        // 调整斧头大小
        poseStack.scale(2.0F, 2.0F, 2.0F);
        
        // 渲染物品
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