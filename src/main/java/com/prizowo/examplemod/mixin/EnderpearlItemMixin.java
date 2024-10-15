package com.prizowo.examplemod.mixin;

import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderpearlItem.class)
public class EnderpearlItemMixin {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getCooldowns()Lnet/minecraft/world/item/ItemCooldowns;"))
    private ItemCooldowns removeCooldown(Player player) {
        return new ItemCooldowns() {
            @Override
            public void addCooldown(@NotNull Item item, int duration) {
            }
        };
    }
}
