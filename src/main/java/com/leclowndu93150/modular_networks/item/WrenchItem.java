package com.leclowndu93150.modular_networks.item;

import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.network.ConnectionType;
import com.leclowndu93150.modular_networks.mixin.UseOnContextAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        var hitResult = ((UseOnContextAccessor) context).modular_networks$getHitResult();

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
            Direction side = hit.side();

            if (hit.part() != DuctHitHelper.HitPart.COLLAR) {
                level.destroyBlock(pos, true);
                return InteractionResult.SUCCESS;
            }

            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                var att = ductBE.getAttachment(side);
                if (att != null) {
                    ductBE.removeAttachment(side);
                    return InteractionResult.SUCCESS;
                }
                level.destroyBlock(pos, true);
                return InteractionResult.SUCCESS;
            }

            ConnectionType current = ductBE.getConnectionType(side);
            ductBE.setConnectionType(side, current.next());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
