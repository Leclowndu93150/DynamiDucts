package com.leclowndu93150.modular_networks.core.attachment;

import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public final class AttachmentPlacementHelper {

    private AttachmentPlacementHelper() {
    }

    public static boolean hasRenderedHand(DuctBlockEntity ductBE, Direction side) {
        return ductBE.getBlockState().getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(side));
    }

    public static boolean canPlaceSideAttachment(DuctBlockEntity ductBE, Direction side) {
        return hasRenderedHand(ductBE, side) && ductBE.getAttachment(side) == null;
    }

    public static boolean isExternalSide(DuctBlockEntity ductBE, Direction side) {
        Level level = ductBE.getLevel();
        if (level == null) return false;
        BlockPos neighborPos = ductBE.getBlockPos().relative(side);
        return !(level.getBlockState(neighborPos).getBlock() instanceof DuctBlock);
    }

    public static boolean canPlaceTransferAttachment(DuctBlockEntity ductBE, Direction side) {
        return canPlaceSideAttachment(ductBE, side) && isExternalSide(ductBE, side);
    }

    public static boolean canPlaceFilterAttachment(DuctBlockEntity ductBE, Direction side) {
        return canPlaceSideAttachment(ductBE, side);
    }

    public static boolean supportsTransferAttachment(DuctBlockEntity ductBE) {
        return ductBE.getDuctUnit(DuctToken.FLUID) != null || ductBE.getDuctUnit(DuctToken.ITEM) != null;
    }

    public static @Nullable Attachment createTransferAttachment(
            DuctBlockEntity ductBE,
            Direction side,
            BiFunction<DuctBlockEntity, Direction, Attachment> fluidFactory,
            BiFunction<DuctBlockEntity, Direction, Attachment> itemFactory
    ) {
        if (ductBE.getDuctUnit(DuctToken.FLUID) != null) {
            return fluidFactory.apply(ductBE, side);
        }
        if (ductBE.getDuctUnit(DuctToken.ITEM) != null) {
            return itemFactory.apply(ductBE, side);
        }
        return null;
    }
}
