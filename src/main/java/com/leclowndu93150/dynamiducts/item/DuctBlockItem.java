package com.leclowndu93150.dynamiducts.item;

import com.leclowndu93150.dynamiducts.block.DuctBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DuctBlockItem extends BlockItem {

    public DuctBlockItem(Block block, Properties properties, String... tooltipKeys) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (getBlock() instanceof DuctBlock duct && duct.isCraftingOnly()) {
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        DDTooltipHelper.appendDuctTooltip(stack, getBlock(), tooltip);
    }
}
