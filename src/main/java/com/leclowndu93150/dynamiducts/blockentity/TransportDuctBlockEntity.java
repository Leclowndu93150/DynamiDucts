package com.leclowndu93150.dynamiducts.blockentity;

import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import com.leclowndu93150.dynamiducts.init.DDBlockEntities;
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
                case BASIC -> DDBlockEntities.TRANSPORT_DUCT_BASIC.get();
                case LONG_RANGE -> DDBlockEntities.TRANSPORT_DUCT_LONG_RANGE.get();
                case LINKING -> DDBlockEntities.TRANSPORT_DUCT_LINKING.get();
                case FRAME -> DDBlockEntities.TRANSPORT_DUCT_FRAME.get();
            };
            return new TransportDuctBlockEntity(type, pos, state, this);
        }
    }
}
