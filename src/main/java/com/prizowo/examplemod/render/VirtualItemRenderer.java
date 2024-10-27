package com.prizowo.examplemod.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VirtualItemRenderer {
    private static final Map<BlockPos, ItemStack> virtualItems = new HashMap<>();

    public static void addVirtualItem(BlockPos pos, ItemStack stack) {
        virtualItems.put(pos, stack);
    }

    public static ItemStack removeVirtualItem(BlockPos pos) {
        return virtualItems.remove(pos);
    }

    public static ItemStack getVirtualItem(BlockPos pos) {
        return virtualItems.get(pos);
    }

    public static Set<BlockPos> getVirtualItemPositions() {
        return virtualItems.keySet();
    }

    public static boolean hasVirtualItem(BlockPos pos) {
        return virtualItems.containsKey(pos);
    }

    public static void clearVirtualItem(BlockPos pos) {
        virtualItems.remove(pos);
    }
}
