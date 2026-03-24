package com.leclowndu93150.modular_networks.block;

import com.leclowndu93150.modular_networks.attachment.cover.Cover;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class DuctHitHelper {

    private static final AABB CENTER = new AABB(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
    private static final AABB[] COLLARS = new AABB[6];
    private static final AABB[] CABLES = new AABB[6];

    static {
        genBoxes(COLLARS, 0.25, 0.2, 0.8);
        genBoxes(CABLES, 0.3, 0.3, 0.7);
    }

    private DuctHitHelper() {
    }

    public static DuctHit resolve(BlockState state, BlockPos pos, BlockHitResult hitResult) {
        return resolve(state, null, pos, hitResult);
    }

    public static DuctHit resolve(BlockState state, @Nullable DuctBlockEntity ductBE, BlockPos pos, BlockHitResult hitResult) {
        Vec3 hit = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        Direction fallbackSide = hitResult.getDirection();

        for (Direction dir : Direction.values()) {
            if (hasInteractiveAttachment(ductBE, dir) && attachmentBox(dir).contains(hit)) {
                return new DuctHit(HitPart.COLLAR, dir);
            }
        }

        for (Direction dir : Direction.values()) {
            if (state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir)) && CABLES[dir.ordinal()].contains(hit)) {
                return new DuctHit(HitPart.CABLE, dir);
            }
        }

        for (Direction dir : Direction.values()) {
            if (COLLARS[dir.ordinal()].contains(hit)) {
                return new DuctHit(HitPart.COLLAR, dir);
            }
        }

        if (CENTER.contains(hit)) {
            return new DuctHit(HitPart.CENTER, fallbackSide);
        }

        return new DuctHit(HitPart.COLLAR, fallbackSide);
    }

    public static AABB outlineBox(DuctHit hit) {
        return outlineBox(hit, null);
    }

    public static AABB outlineBox(DuctHit hit, @Nullable DuctBlockEntity ductBE) {
        return switch (hit.part()) {
            case COLLAR -> hasInteractiveAttachment(ductBE, hit.side()) ? attachmentBox(hit.side()) : COLLARS[hit.side().ordinal()];
            case CABLE -> CABLES[hit.side().ordinal()];
            case CENTER -> CENTER;
        };
    }

    private static void genBoxes(AABB[] boxes, double min, double innerMin, double innerMax) {
        boxes[Direction.DOWN.ordinal()] = new AABB(innerMin, 0.0, innerMin, innerMax, min, innerMax);
        boxes[Direction.UP.ordinal()] = new AABB(innerMin, 1.0 - min, innerMin, innerMax, 1.0, innerMax);
        boxes[Direction.NORTH.ordinal()] = new AABB(innerMin, innerMin, 0.0, innerMax, innerMax, min);
        boxes[Direction.SOUTH.ordinal()] = new AABB(innerMin, innerMin, 1.0 - min, innerMax, innerMax, 1.0);
        boxes[Direction.WEST.ordinal()] = new AABB(0.0, innerMin, innerMin, min, innerMax, innerMax);
        boxes[Direction.EAST.ordinal()] = new AABB(1.0 - min, innerMin, innerMin, 1.0, innerMax, innerMax);
    }

    private static boolean hasInteractiveAttachment(@Nullable DuctBlockEntity ductBE, Direction side) {
        if (ductBE == null) {
            return false;
        }
        Attachment attachment = ductBE.getAttachment(side);
        return attachment != null && !(attachment instanceof Cover);
    }

    private static AABB attachmentBox(Direction side) {
        AABB collar = COLLARS[side.ordinal()];
        AABB cable = CABLES[side.ordinal()];
        return new AABB(
                Math.min(collar.minX, cable.minX),
                Math.min(collar.minY, cable.minY),
                Math.min(collar.minZ, cable.minZ),
                Math.max(collar.maxX, cable.maxX),
                Math.max(collar.maxY, cable.maxY),
                Math.max(collar.maxZ, cable.maxZ)
        );
    }

    public enum HitPart {
        COLLAR,
        CABLE,
        CENTER
    }

    public record DuctHit(HitPart part, Direction side) {
    }
}
