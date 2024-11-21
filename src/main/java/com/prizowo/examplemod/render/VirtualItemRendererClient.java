package com.prizowo.examplemod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class VirtualItemRendererClient {
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;


        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        for (BlockPos pos : VirtualItemRenderer.getVirtualItemPositions()) {
            ItemStack stack = VirtualItemRenderer.getVirtualItem(pos);
            if (stack != null && !stack.isEmpty()) {
                Vec3 renderPos = Vec3.atCenterOf(pos).add(0, 1, 0);
                if (camera.getPosition().distanceToSqr(renderPos) > 64 * 64) continue; // 距离检查

                poseStack.pushPose();
                poseStack.translate(renderPos.x - cameraPos.x, renderPos.y - cameraPos.y, renderPos.z - cameraPos.z);

                long time = System.currentTimeMillis();
                float angle = ( time / 20) % 360;
                poseStack.mulPose(Axis.YP.rotationDegrees(angle));

                float scale = 2.0f;
                poseStack.scale(scale, scale, scale);

                itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, 15728880, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, Minecraft.getInstance().level, 0);

                poseStack.popPose();
            }
        }

        bufferSource.endBatch();
    }
}
