package com.leclowndu93150.dynamiducts.compat.immersiveengineering;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.DieselGeneratorLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class IECompat {

    private IECompat() {
    }

    public static boolean isEnergyOutput(Level level, BlockPos neighborPos, Direction direction) {
        if (direction != Direction.DOWN) return false;
        if (!(level.getBlockEntity(neighborPos) instanceof IMultiblockBE<?> multiblock)) return false;
        var helper = multiblock.getHelper();
        if (!(helper.getMultiblock().logic() instanceof DieselGeneratorLogic)) return false;
        BlockPos posInMultiblock = helper.getPositionInMB();
        return posInMultiblock.getY() == 1 && posInMultiblock.getZ() == 4;
    }
}
