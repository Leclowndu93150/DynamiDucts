package com.leclowndu93150.modular_networks.item;

import com.leclowndu93150.modular_networks.attachment.relay.Relay;
import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.blockentity.StructuralDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentPlacementHelper;
import com.leclowndu93150.modular_networks.mixin.UseOnContextAccessor;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class RelayItem extends Item {

    public RelayItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var hitResult = ((UseOnContextAccessor) context).modular_networks$getHitResult();

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof StructuralDuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
            var side = hit.side();
            if (AttachmentPlacementHelper.canPlaceSideAttachment(ductBE, side)) {
                ductBE.setAttachment(side, new Relay(ductBE, side));
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TDTooltipHelper.appendRelayTooltip(tooltip);
    }
}
