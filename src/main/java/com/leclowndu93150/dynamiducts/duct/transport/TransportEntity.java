package com.leclowndu93150.dynamiducts.duct.transport;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.init.DDDataAttachments;
import com.leclowndu93150.dynamiducts.init.DDEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TransportEntity extends Entity {

    private static final int DUCT_LENGTH = 100;
    private static final int STEP = 50;

    private static final EntityDataAccessor<Integer> DATA_PROGRESS =
            SynchedEntityData.defineId(TransportEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_DIRECTION =
            SynchedEntityData.defineId(TransportEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_OLD_DIRECTION =
            SynchedEntityData.defineId(TransportEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<BlockPos> DATA_DUCT_POS =
            SynchedEntityData.defineId(TransportEntity.class, EntityDataSerializers.BLOCK_POS);

    private TransportRoute route;
    private int pathIndex;
    private int progress;
    private Direction direction;
    private Direction oldDirection;
    private BlockPos currentDuctPos;
    private boolean finished;
    private boolean allowDismount;

    public TransportEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.currentDuctPos = blockPosition();
        this.direction = Direction.NORTH;
        this.oldDirection = Direction.NORTH;
    }

    public TransportEntity(ServerLevel level, TransportDuctUnit origin, TransportRoute route) {
        super(DDEntityTypes.TRANSPORT.get(), level);
        this.noPhysics = true;
        this.route = route;
        this.pathIndex = 0;
        this.progress = 0;
        this.currentDuctPos = origin.getPos();
        this.finished = false;

        if (!route.path.isEmpty()) {
            this.direction = route.path.get(0);
        } else {
            this.direction = Direction.values()[origin.getEndpointSide()].getOpposite();
        }
        this.oldDirection = this.direction;

        setPos(currentDuctPos.getX() + 0.5, currentDuctPos.getY() + 0.5, currentDuctPos.getZ() + 0.5);
    }

    public void start(Player player) {
        level().addFreshEntity(this);
        player.startRiding(this, true);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            updateClientPosition();
            return;
        }

        if (finished || route == null || getPassengers().isEmpty()) {
            ejectAndRemove();
            return;
        }

        for (Entity passenger : getPassengers()) {
            if (passenger instanceof Player player) {
                player.setSwimming(true);
                player.setPose(net.minecraft.world.entity.Pose.SWIMMING);
                player.fallDistance = 0;
            }
        }

        progress += getSpeed();

        if (progress >= DUCT_LENGTH) {
            progress -= DUCT_LENGTH;
            advanceToNextDuct();
        }

        updatePosition();
        syncData();
    }

    private int getSpeed() {
        if (level().getBlockEntity(currentDuctPos) instanceof DuctBlockEntity ductBE) {
            var unit = ductBE.getDuctUnit(DuctToken.TRANSPORT);
            if (unit instanceof TransportDuctUnit tdu && tdu.isLongRange()) {
                return STEP * 2;
            }
        }
        return STEP;
    }

    private void advanceToNextDuct() {
        if (pathIndex < route.path.size()) {
            oldDirection = direction;
            direction = route.path.get(pathIndex);
            currentDuctPos = currentDuctPos.relative(direction);
            pathIndex++;

            if (level().getBlockEntity(currentDuctPos) instanceof DuctBlockEntity) {
                updatePosition();
            } else {
                ejectAndRemove();
            }
        } else {
            dropAtDestination();
        }
    }

    private void dropAtDestination() {
        finished = true;
        allowDismount = true;
        for (Entity passenger : getPassengers()) {
            if (passenger instanceof Player player) {
                player.setData(DDDataAttachments.TRANSPORT_TARGET.get(), java.util.Optional.empty());
            }
        }
        Direction exitDir = Direction.values()[route.exitSide];
        double x = route.destination.getX() + 0.5 + exitDir.getStepX() * 1.5;
        double y = route.destination.getY() + exitDir.getStepY() * 1.0;
        double z = route.destination.getZ() + 0.5 + exitDir.getStepZ() * 1.5;

        if (exitDir == Direction.DOWN) {
            y = route.destination.getY() - 1.0;
        } else if (exitDir == Direction.UP) {
            y = route.destination.getY() + 1.0;
        }

        for (Entity passenger : getPassengers()) {
            passenger.stopRiding();
            passenger.teleportTo(x, y, z);
            if (passenger instanceof Player player) {
                float yaw = switch (exitDir) {
                    case NORTH -> 180F;
                    case SOUTH -> 0F;
                    case WEST -> 90F;
                    case EAST -> -90F;
                    default -> player.getYRot();
                };
                player.setYRot(yaw);
                player.setXRot(0);
            }
        }
        discard();
    }

    private void ejectAndRemove() {
        allowDismount = true;
        for (Entity passenger : getPassengers()) {
            passenger.stopRiding();
            if (currentDuctPos != null) {
                passenger.teleportTo(
                        currentDuctPos.getX() + 0.5,
                        currentDuctPos.getY() + 1.0,
                        currentDuctPos.getZ() + 0.5
                );
            }
        }
        discard();
    }

    private void updatePosition() {
        float t = progress / (float) DUCT_LENGTH;
        double baseX = currentDuctPos.getX() + 0.5;
        double baseY = currentDuctPos.getY() + 0.5;
        double baseZ = currentDuctPos.getZ() + 0.5;

        Direction moveDir = t < 0.5F ? oldDirection : direction;
        float offset = t - 0.5F;

        setPos(
                baseX + moveDir.getStepX() * offset,
                baseY + moveDir.getStepY() * offset,
                baseZ + moveDir.getStepZ() * offset
        );
    }

    private void updateClientPosition() {
        progress = entityData.get(DATA_PROGRESS);
        direction = Direction.from3DDataValue(entityData.get(DATA_DIRECTION));
        oldDirection = Direction.from3DDataValue(entityData.get(DATA_OLD_DIRECTION));
        currentDuctPos = entityData.get(DATA_DUCT_POS);
        updatePosition();
    }

    private void syncData() {
        entityData.set(DATA_PROGRESS, progress);
        entityData.set(DATA_DIRECTION, (byte) direction.get3DDataValue());
        entityData.set(DATA_OLD_DIRECTION, (byte) oldDirection.get3DDataValue());
        entityData.set(DATA_DUCT_POS, currentDuctPos);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_PROGRESS, 0);
        builder.define(DATA_DIRECTION, (byte) 0);
        builder.define(DATA_OLD_DIRECTION, (byte) 0);
        builder.define(DATA_DUCT_POS, BlockPos.ZERO);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        return new Vec3(0, -0.15, 0);
    }

    @Override
    public boolean canRiderInteract() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

    public boolean isFinishedOrRemoving() {
        return allowDismount || finished || isRemoved();
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getOldDirection() {
        return oldDirection;
    }

    public int getProgress() {
        return progress;
    }

    public BlockPos getCurrentDuctPos() {
        return currentDuctPos;
    }

    public TransportRoute getRoute() {
        return route;
    }
}
