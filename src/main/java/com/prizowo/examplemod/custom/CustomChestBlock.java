package com.prizowo.examplemod.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class CustomChestBlock extends BaseEntityBlock {
    public static final MapCodec<CustomChestBlock> CODEC = simpleCodec(CustomChestBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;

    public CustomChestBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, ChestType.SINGLE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CustomChestBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        MenuProvider menuProvider = getMenuProvider(state, level, pos);
        if (menuProvider != null) {
            player.openMenu(menuProvider);
            player.awardStat(Stats.OPEN_CHEST);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            // 先处理物品掉落
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container container) {
                for(int i = 0; i < container.getContainerSize(); ++i) {
                    ItemStack itemstack = container.getItem(i);
                    if (!itemstack.isEmpty()) {
                        net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                    }
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }

            // 重置相邻箱子的状态
            ChestType type = state.getValue(TYPE);
            if (type != ChestType.SINGLE) {
                Direction facing = state.getValue(FACING);
                BlockPos otherPos = pos.relative(type == ChestType.LEFT ? 
                    facing.getClockWise() : facing.getCounterClockWise());
                BlockState otherState = level.getBlockState(otherPos);
                
                if (otherState.is(this)) {
                    // 将相邻箱子重置为单个箱子状态
                    level.setBlock(otherPos, otherState.setValue(TYPE, ChestType.SINGLE), 3);
                }
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ChestType chesttype = ChestType.SINGLE;

        // 检查左边是否有箱子
        BlockState leftState = level.getBlockState(pos.relative(direction.getCounterClockWise()));
        if (leftState.is(this) && leftState.getValue(FACING) == direction) {
            if (leftState.getValue(TYPE) == ChestType.SINGLE) {
                // 如果左边是单个箱子，将其变为左箱子，新箱子为右箱子
                level.setBlock(pos.relative(direction.getCounterClockWise()),
                        leftState.setValue(TYPE, ChestType.LEFT), 3);
                chesttype = ChestType.RIGHT;
            }
        }

        // 检查右边是否有箱子
        BlockState rightState = level.getBlockState(pos.relative(direction.getClockWise()));
        if (rightState.is(this) && rightState.getValue(FACING) == direction) {
            if (rightState.getValue(TYPE) == ChestType.SINGLE && chesttype == ChestType.SINGLE) {
                // 如果右边是单个箱子，将其变为右箱子，新箱子为左箱子
                level.setBlock(pos.relative(direction.getClockWise()),
                        rightState.setValue(TYPE, ChestType.RIGHT), 3);
                chesttype = ChestType.LEFT;
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(TYPE, chesttype);
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!(blockentity instanceof CustomChestBlockEntity)) {
            return null;
        }

        CustomChestBlockEntity chest = (CustomChestBlockEntity) blockentity;
        ChestType chestType = state.getValue(TYPE);
        if (chestType == ChestType.SINGLE) {
            return chest;
        }

        BlockPos otherPos = pos.relative(getConnectedDirection(state));
        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.is(this)) {
            ChestType otherType = otherState.getValue(TYPE);
            if (otherType != ChestType.SINGLE && chestType != otherType 
                    && otherState.getValue(FACING) == state.getValue(FACING)) {
                BlockEntity other = level.getBlockEntity(otherPos);
                if (other instanceof CustomChestBlockEntity) {
                    CustomChestBlockEntity otherChest = (CustomChestBlockEntity) other;
                    Container left = chestType == ChestType.RIGHT ? otherChest : chest;
                    Container right = chestType == ChestType.RIGHT ? chest : otherChest;
                    Container combinedContainer = new CombinedContainer(left, right);
                    
                    return new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return Component.translatable("container.chestDouble");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                            return ChestMenu.sixRows(id, inventory, combinedContainer);
                        }
                    };
                }
            }
        }
        return chest;
    }

    private Direction getConnectedDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(TYPE) == ChestType.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }
} 