package com.leclowndu93150.dynamiducts.block;

import com.leclowndu93150.dynamiducts.attachment.cover.Cover;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class DuctHitHelper {

    private static final double OUTLINE_EPSILON = 1.0 / 1024.0;
    private static final AABB CENTER = new AABB(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
    private static final AABB[] COLLARS = new AABB[6];
    private static final AABB[] CABLES = new AABB[6];
    private static final AABB[] COVERS = new AABB[6];
    private static final VoxelShape[] COVER_SHAPES = new VoxelShape[6];

    static {
        genBoxes(COLLARS, 0.25, 0.2, 0.8);
        genBoxes(CABLES, 0.3, 0.3, 0.7);
        genCoverBoxes(COVERS, 0.0625);
        for (Direction dir : Direction.values()) {
            COVER_SHAPES[dir.ordinal()] = Shapes.create(COVERS[dir.ordinal()]);
        }
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
            if (hasInteractiveAttachment(ductBE, dir) && attachmentBox(dir).inflate(0.01).contains(hit)) {
                return new DuctHit(HitPart.COLLAR, dir);
            }
        }

        for (Direction dir : Direction.values()) {
            if (hasCover(ductBE, dir) && COVERS[dir.ordinal()].contains(hit)) {
                return new DuctHit(HitPart.COVER, dir);
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
            if (hasInteractiveAttachment(ductBE, fallbackSide)) {
                return new DuctHit(HitPart.COLLAR, fallbackSide);
            }
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
            case COVER -> outlineCoverBox(hit.side());
            case CENTER -> CENTER;
        };
    }

    public static AABB coverBox(Direction side) {
        return COVERS[side.ordinal()];
    }

    public static VoxelShape coverShape(Direction side) {
        return COVER_SHAPES[side.ordinal()];
    }

    private static AABB outlineCoverBox(Direction side) {
        AABB box = COVERS[side.ordinal()];
        return switch (side) {
            case DOWN -> new AABB(-OUTLINE_EPSILON, -OUTLINE_EPSILON, -OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, box.maxY, 1.0 + OUTLINE_EPSILON);
            case UP -> new AABB(-OUTLINE_EPSILON, box.minY, -OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON);
            case NORTH -> new AABB(-OUTLINE_EPSILON, -OUTLINE_EPSILON, -OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, box.maxZ);
            case SOUTH -> new AABB(-OUTLINE_EPSILON, -OUTLINE_EPSILON, box.minZ, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON);
            case WEST -> new AABB(-OUTLINE_EPSILON, -OUTLINE_EPSILON, -OUTLINE_EPSILON, box.maxX, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON);
            case EAST -> new AABB(box.minX, -OUTLINE_EPSILON, -OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON, 1.0 + OUTLINE_EPSILON);
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

    private static void genCoverBoxes(AABB[] boxes, double thickness) {
        boxes[Direction.DOWN.ordinal()] = new AABB(0.0, 0.0, 0.0, 1.0, thickness, 1.0);
        boxes[Direction.UP.ordinal()] = new AABB(0.0, 1.0 - thickness, 0.0, 1.0, 1.0, 1.0);
        boxes[Direction.NORTH.ordinal()] = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, thickness);
        boxes[Direction.SOUTH.ordinal()] = new AABB(0.0, 0.0, 1.0 - thickness, 1.0, 1.0, 1.0);
        boxes[Direction.WEST.ordinal()] = new AABB(0.0, 0.0, 0.0, thickness, 1.0, 1.0);
        boxes[Direction.EAST.ordinal()] = new AABB(1.0 - thickness, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    private static boolean hasInteractiveAttachment(@Nullable DuctBlockEntity ductBE, Direction side) {
        if (ductBE == null) {
            return false;
        }
        Attachment attachment = ductBE.getAttachment(side);
        return attachment != null && !(attachment instanceof Cover);
    }

    private static boolean hasCover(@Nullable DuctBlockEntity ductBE, Direction side) {
        return ductBE != null && ductBE.getAttachment(side) instanceof Cover;
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
        COVER,
        CENTER
    }

    public record DuctHit(HitPart part, Direction side) {
    }
}
