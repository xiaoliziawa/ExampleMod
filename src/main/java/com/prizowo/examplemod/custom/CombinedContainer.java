package com.prizowo.examplemod.custom;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CombinedContainer implements Container {
    private final Container left;
    private final Container right;

    public CombinedContainer(Container left, Container right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int getContainerSize() {
        return left.getContainerSize() + right.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return left.isEmpty() && right.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (slot < left.getContainerSize()) {
            return left.getItem(slot);
        }
        return right.getItem(slot - left.getContainerSize());
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        if (slot < left.getContainerSize()) {
            return left.removeItem(slot, amount);
        }
        return right.removeItem(slot - left.getContainerSize(), amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        if (slot < left.getContainerSize()) {
            return left.removeItemNoUpdate(slot);
        }
        return right.removeItemNoUpdate(slot - left.getContainerSize());
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot < left.getContainerSize()) {
            left.setItem(slot, stack);
        } else {
            right.setItem(slot - left.getContainerSize(), stack);
        }
    }

    @Override
    public void startOpen(Player player) {
        left.startOpen(player);
        right.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        left.stopOpen(player);
        right.stopOpen(player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return left.stillValid(player) && right.stillValid(player);
    }

    @Override
    public void clearContent() {
        left.clearContent();
        right.clearContent();
    }

    @Override
    public void setChanged() {
        left.setChanged();
        right.setChanged();
    }
} 