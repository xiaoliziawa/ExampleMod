package com.prizowo.examplemod.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Unique
    private static final ResourceLocation CUSTOM_SKIN = ResourceLocation.fromNamespaceAndPath("examplemod", "textures/entity/custom_player.png");

    @Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
    private void getTexture(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> cir) {
        // 替换玩家的皮肤纹理
        cir.setReturnValue(CUSTOM_SKIN);
    }
} 