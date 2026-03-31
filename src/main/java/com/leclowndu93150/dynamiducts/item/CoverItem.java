package com.leclowndu93150.dynamiducts.item;

import com.leclowndu93150.dynamiducts.attachment.cover.Cover;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.block.DuctHitHelper;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.init.DDDataComponents;
import com.leclowndu93150.dynamiducts.init.DDItems;
import com.leclowndu93150.dynamiducts.mixin.UseOnContextAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoverItem extends Item {

    public CoverItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createForState(BlockState state) {
        return createForState(state, 6);
    }

    public static ItemStack createForState(BlockState state, int count) {
        ItemStack stack = new ItemStack(DDItems.COVER.get(), count);
        stack.set(DDDataComponents.COVER_STATE.get(), state);
        return stack;
    }

    public static @Nullable BlockState getCoverState(ItemStack stack) {
        BlockState state = stack.get(DDDataComponents.COVER_STATE.get());
        return isValidCoverState(state) ? state : null;
    }

    public static boolean isValidCoverState(@Nullable BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        Block block = state.getBlock();
        return state.getRenderShape() == RenderShape.MODEL
                && !(block instanceof DuctBlock)
                && !(block instanceof EntityBlock);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var hitResult = ((UseOnContextAccessor) context).dynamiducts$getHitResult();
        BlockState coverState = getCoverState(context.getItemInHand());
        if (coverState == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
            var side = hit.side();
            if (ductBE.getAttachment(side) == null) {
                ductBE.setAttachment(side, new Cover(ductBE, side, coverState));
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        BlockState state = getCoverState(stack);
        if (state != null) {
            return Component.translatable("item.dynamiducts.cover.filled", state.getBlock().getName());
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (stack.has(DDDataComponents.COVER_STATE.get()) && getCoverState(stack) == null) {
            tooltip.add(Component.translatable("info.dynamiducts.info.invalidCover"));
        }
    }
}
