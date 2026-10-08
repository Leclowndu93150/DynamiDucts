package com.leclowndu93150.dynamiducts.item;

import com.leclowndu93150.dynamiducts.block.DuctHitHelper;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.network.ConnectionType;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import com.leclowndu93150.dynamiducts.mixin.UseOnContextAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
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
        var hitResult = ((UseOnContextAccessor) context).dynamiducts$getHitResult();

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
            Direction side = hit.side();
            Attachment attachment = ductBE.getAttachment(side);

            if (attachment != null && hit.part() == DuctHitHelper.HitPart.COLLAR) {
                removeSideAttachment(level, pos, ductBE, side, attachment);
                return InteractionResult.SUCCESS;
            }

            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                dismantleDuct(level, pos, ductBE);
                return InteractionResult.SUCCESS;
            }

            if (ductBE.getDuctUnit(DuctToken.TRANSPORT) instanceof TransportDuctUnit transportUnit) {
                transportUnit.trySetEndpoint(side);
                playPopSound(level, pos);
                return InteractionResult.SUCCESS;
            }

            ConnectionType current = ductBE.getConnectionType(side);
            ConnectionType next = current.next();
            ductBE.setConnectionType(side, next);

            BlockEntity neighbor = level.getBlockEntity(pos.relative(side));
            if (neighbor instanceof DuctBlockEntity neighborDuct) {
                neighborDuct.setConnectionType(side.getOpposite(), next);
            }

            playPopSound(level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static void removeSideAttachment(Level level, BlockPos pos, DuctBlockEntity ductBE, Direction side, Attachment attachment) {
        if (attachment == null) {
            return;
        }
        ItemStack drop = attachment.getDrop();
        ductBE.removeAttachment(side);
        if (!drop.isEmpty()) {
            Block.popResource(level, pos, drop);
        }
        playPopSound(level, pos);
    }

    private static void dismantleDuct(Level level, BlockPos pos, DuctBlockEntity ductBE) {
        ItemStack ductDrop = new ItemStack(ductBE.getBlockState().getBlock());
        if (!ductDrop.isEmpty()) {
            Block.popResource(level, pos, ductDrop);
        }

        level.removeBlock(pos, false);
        playPopSound(level, pos);
    }

    private static void playPopSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
