package com.prizowo.examplemod.render;

import com.prizowo.examplemod.Examplemod;
import com.prizowo.examplemod.Reg.BlocksReg;
import com.prizowo.examplemod.network.SyncVirtualItemPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Examplemod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ObsidianInteractionHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (level.getBlockState(pos).getBlock() == BlocksReg.CUSTOM_BLOCK.get()) {
            event.setCanceled(true);
            if (!level.isClientSide()) {
                if (VirtualItemRenderer.hasVirtualItem(pos)) {
                    removeVirtualItem(player, pos, level);
                } else if (!heldItem.isEmpty() && canAddVirtualItem(heldItem)) {
                    addVirtualItem(player, pos, heldItem, level);
                }
            }
        }
    }

    private static boolean canAddVirtualItem(ItemStack stack) {
        return !(stack.getItem() instanceof EnchantedBookItem);
    }

    private static void removeVirtualItem(Player player, BlockPos pos, Level level) {
        ItemStack removedItem = VirtualItemRenderer.removeVirtualItem(pos);
        if (!player.getAbilities().instabuild) {
            if (!player.getInventory().add(removedItem)) {
                player.drop(removedItem, false);
            }
        }
        sendSyncPacket(level, pos, ItemStack.EMPTY, true);
    }

    private static void addVirtualItem(Player player, BlockPos pos, ItemStack heldItem, Level level) {
        ItemStack virtualItem = player.getAbilities().instabuild ? heldItem.copy() : heldItem.split(1);
        VirtualItemRenderer.addVirtualItem(pos, virtualItem);
        sendSyncPacket(level, pos, virtualItem, false);
    }

    private static void sendSyncPacket(Level level, BlockPos pos, ItemStack stack, boolean remove) {
        if (level instanceof ServerLevel serverLevel) {
            LevelChunk chunk = serverLevel.getChunkAt(pos);
            PacketDistributor.sendToPlayersTrackingChunk(
                    serverLevel,
                    chunk.getPos(),
                    new SyncVirtualItemPacket(pos, stack, remove)
            );
        }
    }
}
