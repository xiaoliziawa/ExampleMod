package com.prizowo.examplemod.custom;

import com.prizowo.examplemod.Reg.BlocksReg;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.NotNull;

public class CustomCraftingMenu extends CraftingMenu {
    private final ContainerLevelAccess access;

    public CustomCraftingMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(id, inventory, access);
        this.access = access;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, BlocksReg.CRFTING_TABLE_SLAB.get());
    }
}
