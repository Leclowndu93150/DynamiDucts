package com.leclowndu93150.modular_networks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DuctBlockItem extends BlockItem {

    public DuctBlockItem(Block block, Properties properties, String... tooltipKeys) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TDTooltipHelper.appendDuctTooltip(stack, getBlock(), tooltip);
    }
}
