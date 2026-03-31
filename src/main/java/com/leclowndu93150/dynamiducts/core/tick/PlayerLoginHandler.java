package com.leclowndu93150.dynamiducts.core.tick;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import com.leclowndu93150.dynamiducts.duct.transport.TransportEntity;
import com.leclowndu93150.dynamiducts.duct.transport.TransportRoute;
import com.leclowndu93150.dynamiducts.init.DDDataAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = DynamiDucts.MODID)
public class PlayerLoginHandler {

    private static final List<PendingResume> pendingResumes = new ArrayList<>();
    private static final int MAX_RETRY_TICKS = 20;

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        Optional<DDDataAttachments.TransportTarget> target = player.getData(DDDataAttachments.TRANSPORT_TARGET.get());
        if (target.isPresent()) {
            pendingResumes.add(new PendingResume(serverPlayer, target.get(), 0));
        } else {
            BlockPos pos = player.blockPosition();
            if (player.level().getBlockState(pos).getBlock() instanceof DuctBlock
                    || player.level().getBlockState(pos.above()).getBlock() instanceof DuctBlock) {
                ejectFromDuct(player);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (pendingResumes.isEmpty()) return;

        var it = pendingResumes.iterator();
        while (it.hasNext()) {
            PendingResume pending = it.next();
            pending.ticksWaited++;

            if (pending.player.isRemoved()) {
                it.remove();
                continue;
            }

            if (tryResumeTransport(pending)) {
                it.remove();
            } else if (pending.ticksWaited >= MAX_RETRY_TICKS) {
                pending.player.setData(DDDataAttachments.TRANSPORT_TARGET.get(), Optional.empty());
                ejectFromDuct(pending.player);
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player.getVehicle() instanceof TransportEntity transport && !transport.isFinishedOrRemoving()) {
            if (transport.getRoute() != null) {
                player.setData(DDDataAttachments.TRANSPORT_TARGET.get(),
                        Optional.of(new DDDataAttachments.TransportTarget(
                                transport.getCurrentDuctPos(), transport.getRoute().destination)));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof TransportEntity transport) {
            if (!transport.isFinishedOrRemoving()) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean tryResumeTransport(PendingResume pending) {
        ServerLevel level = pending.player.serverLevel();
        BlockPos origin = pending.target.origin();
        BlockPos destination = pending.target.destination();

        if (!(level.getBlockEntity(origin) instanceof DuctBlockEntity ductBE)) return false;
        if (!(ductBE.getDuctUnit(DuctToken.TRANSPORT) instanceof TransportDuctUnit unit)) return false;
        if (unit.getGrid() == null) return false;

        TransportRoute route = unit.getRouteTo(destination);
        if (route == null) return false;

        pending.player.setData(DDDataAttachments.TRANSPORT_TARGET.get(), Optional.empty());
        TransportEntity entity = new TransportEntity(level, unit, route);
        entity.start(pending.player);
        return true;
    }

    private static void ejectFromDuct(Player player) {
        BlockPos pos = player.blockPosition();
        for (Direction dir : Direction.values()) {
            BlockPos candidate = pos.relative(dir);
            if (player.level().getBlockState(candidate).isAir()
                    && player.level().getBlockState(candidate.above()).isAir()) {
                player.teleportTo(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                return;
            }
        }
        player.teleportTo(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
    }

    private static class PendingResume {
        final ServerPlayer player;
        final DDDataAttachments.TransportTarget target;
        int ticksWaited;

        PendingResume(ServerPlayer player, DDDataAttachments.TransportTarget target, int ticksWaited) {
            this.player = player;
            this.target = target;
            this.ticksWaited = ticksWaited;
        }
    }
}
