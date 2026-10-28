package com.leclowndu93150.dynamiducts.block;

import com.leclowndu93150.dynamiducts.blockentity.StructuralDuctBlockEntity;
import com.leclowndu93150.dynamiducts.init.DDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LuxDuctBlock extends StructuralDuctBlock {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public LuxDuctBlock(Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            if (state.getValue(LIT) != powered) {
                level.setBlock(pos, state.setValue(LIT, powered), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StructuralDuctBlockEntity(DDBlockEntities.LUX_DUCT.get(), pos, state);
    }
}
