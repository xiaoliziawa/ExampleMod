package com.prizowo.examplemod.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Objects;

@OnlyIn(value = Dist.CLIENT)
public class EntityOverlayRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "textures/entity/overlay.png");
    private static final ResourceLocation HEART_TEXTURE = ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "textures/entity/heart.png");
    private static final double MAX_RENDER_DISTANCE_SQ = 50.0D * 50.0D;
    private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.#");

    @SubscribeEvent
    public void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null && isWithinRenderDistance(player, entity)) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            Component nameComponent = getDisplayName(entity);
            float partialTick = event.getPartialTick();

            // 处理实体的生命显示
            boolean isLowHealth = false;
            if (entity instanceof LivingEntity living) {
                float health = living.getHealth();
                float maxHealth = living.getMaxHealth();
                String healthString = HEALTH_FORMAT.format(health);

                // 名称后的 [ ]
                nameComponent = Component.literal(nameComponent.getString() + " [" + healthString + "]");
                isLowHealth = health / maxHealth <= 0.2f;
            }

            // 根据生命值状态应用RGB颜色效果
            MutableComponent displayComponent = isLowHealth ? applyRainbowEffect(nameComponent, partialTick) : nameComponent.copy();

            // 设置渲染位置和旋转
            poseStack.pushPose();
            double yOffset = entity instanceof ItemEntity ? 1.2D : entity.getBbHeight() + 0.5D;
            poseStack.translate(0, yOffset, 0);

            var camera = minecraft.gameRenderer.getMainCamera();
            poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));

            // 设置渲染比例
            float scale = 0.025f;
            poseStack.scale(-scale, -scale, scale);

            // 设置渲染状态
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // 渲染overlay.png，也就是那个绿宝石
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
            Matrix4f matrix = poseStack.last().pose();

            int r = 255, g = 255, b = 255, a = 255;
            buffer.addVertex(matrix, -8, -8, 0).setColor(r, g, b, a).setUv(0, 0).setOverlay(0).setUv2(240, 240).setNormal(0, 1, 0);
            buffer.addVertex(matrix, -8, 8, 0).setColor(r, g, b, a).setUv(0, 1).setOverlay(0).setUv2(240, 240).setNormal(0, 1, 0);
            buffer.addVertex(matrix, 8, 8, 0).setColor(r, g, b, a).setUv(1, 1).setOverlay(0).setUv2(240, 240).setNormal(0, 1, 0);
            buffer.addVertex(matrix, 8, -8, 0).setColor(r, g, b, a).setUv(1, 0).setOverlay(0).setUv2(240, 240).setNormal(0, 1, 0);

            // 渲染文本
            Font font = minecraft.font;
            float textX = -font.width(displayComponent) / 2f;
            float textY = 10;
            font.drawInBatch(displayComponent, textX, textY, 0xFFFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, event.getPackedLight());

            // 排除 ItemEntity 渲染 heart.png 到 LivingEntity
            if (entity instanceof LivingEntity) {
                float heartSize = 8;
                float heartX = textX + font.width(displayComponent);
                float heartY = textY - 1;

                // 渲染heart.png
                RenderSystem.enableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, HEART_TEXTURE);

                BufferBuilder heartBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                heartBuffer.addVertex(matrix, heartX, heartY + heartSize, 0).setUv(0, 1);
                heartBuffer.addVertex(matrix, heartX + heartSize, heartY + heartSize, 0).setUv(1, 1);
                heartBuffer.addVertex(matrix, heartX + heartSize, heartY, 0).setUv(1, 0);
                heartBuffer.addVertex(matrix, heartX, heartY, 0).setUv(0, 0);
                BufferUploader.drawWithShader(Objects.requireNonNull(heartBuffer.build()));

                RenderSystem.disableDepthTest();
            }

            // 恢复渲染状态
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();

            poseStack.popPose();

            // 清空原始名称标签内容
            event.setContent(Component.empty());
        }
    }

    // 应用RGB效果
    private MutableComponent applyRainbowEffect(Component component, float partialTick) {
        MutableComponent rainbowText = Component.empty();
        String text = component.getString();
        float baseHue = (float) ((System.currentTimeMillis() % 10000) / 10000.0);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float hue = (baseHue + (float) i / text.length()) % 1.0f;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            rainbowText.append(Component.literal(String.valueOf(c)).withStyle(style -> style.withColor(color)));
        }
        return rainbowText;
    }

    // 检查是否在渲染距离内
    private boolean isWithinRenderDistance(Player player, Entity entity) {
        Vector3d playerPos = new Vector3d(player.getX(), player.getY(), player.getZ());
        Vector3d entityPos = new Vector3d(entity.getX(), entity.getY(), entity.getZ());
        return playerPos.distanceSquared(entityPos) <= MAX_RENDER_DISTANCE_SQ;
    }

    // 获取显示名称
    private Component getDisplayName(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack itemStack = itemEntity.getItem();
            int count = itemStack.getCount();
            String itemName = itemStack.getHoverName().getString();
            return Component.literal(count + "x " + itemName);
        } else {
            return entity.getDisplayName();
        }
    }
}
