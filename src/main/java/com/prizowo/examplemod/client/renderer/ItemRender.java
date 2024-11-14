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
        
        BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        int blockLight = entity.level().getBrightness(LightLayer.BLOCK, blockpos);
        int skyLight = entity.level().getBrightness(LightLayer.SKY, blockpos);
        int combinedLight = LightTexture.pack(Math.max(blockLight, 14), skyLight);
        
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
        
        poseStack.scale(1.0F, 1.0F, 1.0F);
        
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