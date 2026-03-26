package com.leclowndu93150.modular_networks.core.network;

import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class NetworkManager {

    private static final WeakHashMap<ServerLevel, NetworkManager> MANAGERS = new WeakHashMap<>();

    private final ServerLevel level;
    private final Set<NetworkGrid<?>> activeGrids = new LinkedHashSet<>();
    private final Set<NetworkGrid<?>> gridsToAdd = new LinkedHashSet<>();
    private final Set<NetworkGrid<?>> gridsToRemove = new LinkedHashSet<>();
    private final Set<DuctUnit<?, ?, ?>> pendingFormation = new LinkedHashSet<>();
    private final List<NetworkGrid<?>> toRecreate = new ArrayList<>();

    private NetworkManager(ServerLevel level) {
        this.level = level;
    }

    public static NetworkManager get(ServerLevel level) {
        return MANAGERS.computeIfAbsent(level, NetworkManager::new);
    }

    public static void remove(ServerLevel level) {
        NetworkManager manager = MANAGERS.remove(level);
        if (manager != null) {
            manager.activeGrids.forEach(NetworkGrid::destroy);
            manager.activeGrids.clear();
        }
    }

    public void addGrid(NetworkGrid<?> grid) {
        gridsToAdd.add(grid);
    }

    public void removeGrid(NetworkGrid<?> grid) {
        gridsToRemove.add(grid);
    }

    public void scheduleFormation(DuctUnit<?, ?, ?> unit) {
        pendingFormation.add(unit);
    }

    public void tickStart() {
        activeGrids.removeAll(gridsToRemove);
        gridsToRemove.clear();

        activeGrids.addAll(gridsToAdd);
        gridsToAdd.clear();
    }

    public void tickEnd() {
        processFormation();

        for (NetworkGrid<?> grid : activeGrids) {
            if (grid.needsRecreation()) {
                toRecreate.add(grid);
            } else if (grid.isValid()) {
                grid.tickGrid();
            }
        }

        if (!toRecreate.isEmpty()) {
            for (NetworkGrid<?> grid : toRecreate) {
                recreateGrid(grid);
            }
            toRecreate.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private void processFormation() {
        if (pendingFormation.isEmpty()) return;

        DuctUnit<?, ?, ?>[] units = pendingFormation.toArray(new DuctUnit<?, ?, ?>[0]);
        pendingFormation.clear();

        for (DuctUnit<?, ?, ?> unit : units) {
            if (unit.getGrid() != null) continue;
            if (unit.getParent().isRemoved()) continue;

            unit.updateCaches();
            formGridForUnit(unit);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void formGridForUnit(DuctUnit unit) {
        NetworkFormer.formGrid(unit, level);
    }

    @SuppressWarnings("unchecked")
    private <T extends DuctUnit<T, G, ?>, G extends NetworkGrid<T>> void recreateGrid(NetworkGrid<?> grid) {
        G typedGrid = (G) grid;
        activeGrids.remove(typedGrid);
        typedGrid.clearRecreationFlag();

        Set<T> allBlocks = new LinkedHashSet<>();
        allBlocks.addAll(typedGrid.getNodeSet());
        allBlocks.addAll(typedGrid.getIdleSet());

        for (T unit : allBlocks) {
            unit.setGrid(null);
        }
        typedGrid.getNodeSet().clear();
        typedGrid.getIdleSet().clear();

        for (T unit : allBlocks) {
            if (unit.getGrid() == null && !unit.getParent().isRemoved()) {
                unit.updateCaches();
                NetworkFormer.formGrid(unit, level);
            }
        }
    }
}
