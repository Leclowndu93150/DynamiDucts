package com.leclowndu93150.modular_networks.item;

import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.mixin.UseOnContextAccessor;
import net.minecraft.ChatFormatting;
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

    private final BiFunction<DuctBlockEntity, Direction, Attachment> placer;
    private final String[] tooltipKeys;

    public AttachmentItem(Properties properties, BiFunction<DuctBlockEntity, Direction, Attachment> placer, String... tooltipKeys) {
        super(properties);
        this.placer = placer;
        this.tooltipKeys = tooltipKeys;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var hitResult = ((UseOnContextAccessor) context).modular_networks$getHitResult();

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
            var side = hit.side();
            if (ductBE.getAttachment(side) == null) {
                Attachment attachment = placer.apply(ductBE, side);
                if (attachment != null) {
                    ductBE.setAttachment(side, attachment);
                    if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        for (String key : tooltipKeys) {
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }
}
