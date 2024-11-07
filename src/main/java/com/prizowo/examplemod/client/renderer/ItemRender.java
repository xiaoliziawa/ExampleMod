package com.prizowo.examplemod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prizowo.examplemod.entity.ThrownItemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;

public class ItemRender extends EntityRenderer<ThrownItemEntity> {
    private final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer;

    public ItemRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
    }

    @Override
    public void render(ThrownItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        // 计算实体位置的光照等级
        BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        int blockLight = entity.level().getBrightness(LightLayer.BLOCK, blockpos);
        int skyLight = entity.level().getBrightness(LightLayer.SKY, blockpos);
        int combinedLight = LightTexture.pack(Math.max(blockLight, 14), skyLight); // 增加最小光照到14
        
        if (entity.isStuck()) {
            // 箭矢式插入效果
            poseStack.translate(0.0D, 0.0D, 0.0D);
            
            // 使用保存的旋转角度
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRotOnHit()));
            
            // 根据插入面调整角度
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
            // 飞行中的渲染
            // 首先应用Y轴旋转以面向正确的方向
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() + 180.0F));
            
            // 然后应用X轴旋转使物品平躺
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            
            // 最后应用旋转动画
            float spinSpeed = 60.0F;
            float rotation = (entity.tickCount + partialTicks) * spinSpeed;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        }
        
        // 调整物品大小
        float scale = 0.8F; // 增大物品尺寸到0.8
        poseStack.scale(scale, scale, scale);
        
        // 渲染物品，使用计算出的光照值
        itemRenderer.renderStatic(entity.getItem(),
                ItemDisplayContext.FIXED,
                combinedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId());
        
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ThrownItemEntity entity) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(entity.getItem().getItem());
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "textures/item/" + itemId.getPath() + ".png");
    }
} 