package com.leclowndu93150.modular_networks.item;

import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentPlacementHelper;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentTier;
import com.leclowndu93150.modular_networks.mixin.UseOnContextAccessor;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.function.BiFunction;

public class AttachmentItem extends Item {

    private final TDTooltipHelper.AttachmentTooltipType tooltipType;
    private final AttachmentTier tier;
    private final BiFunction<DuctBlockEntity, Direction, Attachment> placer;
    private final boolean requiresExternalSide;

    public AttachmentItem(Properties properties, TDTooltipHelper.AttachmentTooltipType tooltipType, AttachmentTier tier, BiFunction<DuctBlockEntity, Direction, Attachment> placer, String... tooltipKeys) {
        this(properties, tooltipType, tier, true, placer, tooltipKeys);
    }

    public AttachmentItem(Properties properties, TDTooltipHelper.AttachmentTooltipType tooltipType, AttachmentTier tier, boolean requiresExternalSide, BiFunction<DuctBlockEntity, Direction, Attachment> placer, String... tooltipKeys) {
        super(properties);
        this.tooltipType = tooltipType;
        this.tier = tier;
        this.requiresExternalSide = requiresExternalSide;
        this.placer = placer;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var hitResult = ((UseOnContextAccessor) context).modular_networks$getHitResult();

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
            var side = hit.side();
            boolean canPlace = requiresExternalSide
                    ? AttachmentPlacementHelper.canPlaceTransferAttachment(ductBE, side)
                    : AttachmentPlacementHelper.canPlaceFilterAttachment(ductBE, side);
            if (!canPlace) {
                return InteractionResult.PASS;
            }

            Attachment attachment = placer.apply(ductBE, side);
            if (attachment != null) {
                ductBE.setAttachment(side, attachment);
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
        TDTooltipHelper.appendAttachmentTooltip(tooltipType, tier, tooltip);
    }
}
