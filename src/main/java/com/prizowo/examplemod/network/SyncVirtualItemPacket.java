package com.prizowo.examplemod.network;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SyncVirtualItemPacket(BlockPos pos, ItemStack stack, boolean remove) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncVirtualItemPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "sync_virtual_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncVirtualItemPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull SyncVirtualItemPacket decode(@NotNull RegistryFriendlyByteBuf buf) {
            try {
                BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
                ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                boolean remove = buf.readBoolean();
                return new SyncVirtualItemPacket(pos, stack, remove);
            } catch (Exception e) {
                Examplemod.LOGGER.error("Error decoding SyncVirtualItemPacket: ", e);
                return new SyncVirtualItemPacket(BlockPos.ZERO, ItemStack.EMPTY, false);
            }
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull SyncVirtualItemPacket packet) {
            try {
                BlockPos.STREAM_CODEC.encode(buf, packet.pos);
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack);
                buf.writeBoolean(packet.remove);
            } catch (Exception e) {
                Examplemod.LOGGER.error("Error encoding SyncVirtualItemPacket: ", e);
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, ItemStack.EMPTY);
                buf.writeBoolean(packet.remove);
            }
        }
    };

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

