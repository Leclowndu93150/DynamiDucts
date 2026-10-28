package com.leclowndu93150.dynamiducts.block;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class DuctHitHelper {

    private static final AABB CENTER_SMALL     = new AABB(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
    private static final AABB CENTER_SUPER     = new AABB(0.26875, 0.26875, 0.26875, 0.73125, 0.73125, 0.73125);
    private static final AABB CENTER_LARGE     = new AABB(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
    private static final AABB CENTER_TRANSPORT = new AABB(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);
    private static final AABB[] COLLARS           = new AABB[6];
    private static final AABB[] CABLES            = new AABB[6];
    private static final AABB[] COLLARS_SUPER     = new AABB[6];
    private static final AABB[] CABLES_SUPER      = new AABB[6];
    private static final AABB[] COLLARS_LARGE     = new AABB[6];
    private static final AABB[] CABLES_LARGE      = new AABB[6];
    private static final AABB[] COLLARS_TRANSPORT = new AABB[6];
    private static final AABB[] CABLES_TRANSPORT  = new AABB[6];

    static {
        // SMALL: collar depth 0.25, inner 0.2-0.8 (slightly looser than geometry for usability)
        genBoxes(COLLARS, 0.25, 0.2, 0.8);
        genBoxes(CABLES,  0.3,  0.3, 0.7);
        // SUPER (super-laminar, 4.5-11.5px): SMALL boxes shifted out by 0.5px
        genBoxes(COLLARS_SUPER, 0.25, 0.16875, 0.83125);
        genBoxes(CABLES_SUPER,  0.3,  0.26875, 0.73125);
        // LARGE (bronze-framed): arm cross-section 2-14px (0.125-0.875), collar depth 0.25
        genBoxes(COLLARS_LARGE, 0.25,   0.125, 0.875);
        genBoxes(CABLES_LARGE,  0.125,  0.125, 0.875);
        // TRANSPORT (viaducts, 1-15px): collar depth 0.25, cross-section 1-15
        genBoxes(COLLARS_TRANSPORT, 0.25,   0.0625, 0.9375);
        genBoxes(CABLES_TRANSPORT,  0.0625, 0.0625, 0.9375);
    }

    private DuctHitHelper() {
    }

    private static VoxelShape[] shapeCacheOf(BlockState state) {
        return state.getBlock() instanceof DuctBlock duct ? duct.getShapeCache() : null;
    }

    private static AABB[] collarsFor(BlockState state) {
        VoxelShape[] cache = shapeCacheOf(state);
        if (cache == DuctBlock.SHAPE_LARGE) return COLLARS_LARGE;
        if (cache == DuctBlock.SHAPE_SUPER) return COLLARS_SUPER;
        if (cache == DuctBlock.SHAPE_TRANSPORT) return COLLARS_TRANSPORT;
        return COLLARS;
    }

    private static AABB[] cablesFor(BlockState state) {
        VoxelShape[] cache = shapeCacheOf(state);
        if (cache == DuctBlock.SHAPE_LARGE) return CABLES_LARGE;
        if (cache == DuctBlock.SHAPE_SUPER) return CABLES_SUPER;
        if (cache == DuctBlock.SHAPE_TRANSPORT) return CABLES_TRANSPORT;
        return CABLES;
    }

    private static AABB centerFor(BlockState state) {
        VoxelShape[] cache = shapeCacheOf(state);
        if (cache == DuctBlock.SHAPE_LARGE) return CENTER_LARGE;
        if (cache == DuctBlock.SHAPE_SUPER) return CENTER_SUPER;
        if (cache == DuctBlock.SHAPE_TRANSPORT) return CENTER_TRANSPORT;
        return CENTER_SMALL;
    }

    public static DuctHit resolve(BlockState state, BlockPos pos, BlockHitResult hitResult) {
        return resolve(state, null, pos, hitResult);
    }

    public static DuctHit resolve(BlockState state, @Nullable DuctBlockEntity ductBE, BlockPos pos, BlockHitResult hitResult) {
        Vec3 hit = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        Direction fallbackSide = hitResult.getDirection();
        AABB[] collars = collarsFor(state);
        AABB[] cables  = cablesFor(state);

        for (Direction dir : Direction.values()) {
            if (hasInteractiveAttachment(ductBE, dir) && attachmentBox(dir, collars, cables).inflate(0.01).contains(hit)) {
                return new DuctHit(HitPart.COLLAR, dir);
            }
        }

        Direction connected = pickConnectedSide(hit, state, collars, cables, fallbackSide);
        if (connected != null) {
            if (cables[connected.ordinal()].contains(hit)) {
                return new DuctHit(HitPart.CABLE, connected);
            }
            return new DuctHit(HitPart.COLLAR, connected);
        }

        Direction any = pickClosestSide(hit, collars, cables, fallbackSide);
        if (any != null) {
            return new DuctHit(HitPart.COLLAR, any);
        }

        AABB center = centerFor(state);
        if (center.contains(hit)) {
            if (hasInteractiveAttachment(ductBE, fallbackSide)) {
                return new DuctHit(HitPart.COLLAR, fallbackSide);
            }
            return new DuctHit(HitPart.CENTER, fallbackSide);
        }

        return new DuctHit(HitPart.COLLAR, fallbackSide);
    }

    private static Direction pickConnectedSide(Vec3 hit, BlockState state, AABB[] collars, AABB[] cables, Direction fallbackSide) {
        Direction best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Direction dir : Direction.values()) {
            if (!state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir))) continue;
            AABB box = unionBox(collars[dir.ordinal()], cables[dir.ordinal()]);
            if (!box.contains(hit)) continue;
            double dist = faceDistance(hit, dir);
            if (dir == fallbackSide) dist -= 1.0E-4;
            if (dist < bestDist) {
                bestDist = dist;
                best = dir;
            }
        }
        return best;
    }

    private static Direction pickClosestSide(Vec3 hit, AABB[] collars, AABB[] cables, Direction fallbackSide) {
        Direction best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Direction dir : Direction.values()) {
            AABB box = unionBox(collars[dir.ordinal()], cables[dir.ordinal()]);
            if (!box.contains(hit)) continue;
            double dist = faceDistance(hit, dir);
            if (dir == fallbackSide) dist -= 1.0E-4;
            if (dist < bestDist) {
                bestDist = dist;
                best = dir;
            }
        }
        return best;
    }

    private static AABB unionBox(AABB a, AABB b) {
        return new AABB(
                Math.min(a.minX, b.minX), Math.min(a.minY, b.minY), Math.min(a.minZ, b.minZ),
                Math.max(a.maxX, b.maxX), Math.max(a.maxY, b.maxY), Math.max(a.maxZ, b.maxZ)
        );
    }

    private static double faceDistance(Vec3 hit, Direction dir) {
        return switch (dir) {
            case DOWN -> hit.y;
            case UP -> 1.0 - hit.y;
            case NORTH -> hit.z;
            case SOUTH -> 1.0 - hit.z;
            case WEST -> hit.x;
            case EAST -> 1.0 - hit.x;
        };
    }

    public static AABB outlineBox(DuctHit hit) {
        return outlineBox(hit, null, null);
    }

    public static AABB outlineBox(DuctHit hit, @Nullable DuctBlockEntity ductBE) {
        return outlineBox(hit, ductBE, null);
    }

    public static AABB outlineBox(DuctHit hit, @Nullable DuctBlockEntity ductBE, @Nullable BlockState state) {
        AABB[] collars = state == null ? COLLARS : collarsFor(state);
        AABB[] cables  = state == null ? CABLES  : cablesFor(state);
        AABB center    = state == null ? CENTER_SMALL : centerFor(state);
        return switch (hit.part()) {
            case COLLAR -> hasInteractiveAttachment(ductBE, hit.side()) ? attachmentBox(hit.side(), collars, cables) : collars[hit.side().ordinal()];
            case CABLE -> cables[hit.side().ordinal()];
            case CENTER -> center;
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
        return ductBE != null && ductBE.getAttachment(side) != null;
    }

    private static AABB attachmentBox(Direction side, AABB[] collars, AABB[] cables) {
        AABB collar = collars[side.ordinal()];
        AABB cable = cables[side.ordinal()];
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
