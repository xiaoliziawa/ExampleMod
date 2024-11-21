package com.prizowo.examplemod.custom;

import com.prizowo.examplemod.Reg.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MyCustomFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public MyCustomFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MY_CUSTOM_FURNACE.get(), pos, state, RecipeType.SMELTING);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.custom_furnace");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return createMenuInternal(id, inventory, null);
    }

    @Nullable
    public AbstractContainerMenu createMenuInternal(int i, Inventory inventory, Player player) {
        return new FurnaceMenu(i, inventory, this, this.dataAccess) {
            @Override
            public boolean stillValid(@NotNull Player player) {
                return MyCustomFurnaceBlockEntity.this.stillValid(player);
            }
        };
    }

    public boolean stillValid(@NotNull Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D,
                    (double)this.worldPosition.getY() + 0.5D,
                    (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }
}
