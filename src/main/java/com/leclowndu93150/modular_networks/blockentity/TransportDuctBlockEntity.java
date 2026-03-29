package com.leclowndu93150.modular_networks.blockentity;

import com.leclowndu93150.modular_networks.duct.transport.TransportDuctUnit;
import com.leclowndu93150.modular_networks.init.MNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TransportDuctBlockEntity extends DuctBlockEntity {

    private final Tier tier;

    public TransportDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier tier) {
        super(type, pos, state);
        this.tier = tier;
        initDuctUnits();
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void initDuctUnits() {
        if (tier == Tier.FRAME) return;
        addDuctUnit(new TransportDuctUnit(this, tier == Tier.LONG_RANGE));
    }

    public enum Tier {
        BASIC,
        LONG_RANGE,
        LINKING,
        FRAME;

        public TransportDuctBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
            BlockEntityType<?> type = switch (this) {
                case BASIC -> MNBlockEntities.TRANSPORT_DUCT_BASIC.get();
                case LONG_RANGE -> MNBlockEntities.TRANSPORT_DUCT_LONG_RANGE.get();
                case LINKING -> MNBlockEntities.TRANSPORT_DUCT_LINKING.get();
                case FRAME -> MNBlockEntities.TRANSPORT_DUCT_FRAME.get();
            };
            return new TransportDuctBlockEntity(type, pos, state, this);
        }
    }
}
