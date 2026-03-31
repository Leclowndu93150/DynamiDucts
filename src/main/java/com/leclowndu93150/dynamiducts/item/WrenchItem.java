package com.leclowndu93150.dynamiducts.item;

import com.leclowndu93150.dynamiducts.attachment.cover.Cover;
import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.block.DuctHitHelper;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.network.ConnectionType;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import com.leclowndu93150.dynamiducts.init.DDItems;
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

            if (hit.part() == DuctHitHelper.HitPart.COVER && attachment instanceof Cover) {
                removeSideAttachment(level, pos, ductBE, side, attachment);
                return InteractionResult.SUCCESS;
            }

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
        ItemStack drop = getAttachmentDrop(attachment);
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

        for (Attachment attachment : ductBE.getAttachments()) {
            if (attachment == null) {
                continue;
            }
            ItemStack drop = getAttachmentDrop(attachment);
            if (!drop.isEmpty()) {
                Block.popResource(level, pos, drop);
            }
        }

        level.removeBlock(pos, false);
        playPopSound(level, pos);
    }

    private static ItemStack getAttachmentDrop(Attachment attachment) {
        if (attachment instanceof Cover cover) {
            return CoverItem.createForState(cover.getCoverState(), 1);
        }
        if (attachment instanceof Relay) {
            return new ItemStack(DDItems.RELAY.get());
        }
        if (attachment instanceof ConnectionBase connection) {
            return getConnectionDrop(connection);
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getConnectionDrop(ConnectionBase connection) {
        AttachmentTier tier = connection.getTier();
        if (connection.isServo()) {
            return new ItemStack(switch (tier.index()) {
                case 0 -> DDItems.SERVO_BASIC.get();
                case 1 -> DDItems.SERVO_HARDENED.get();
                case 2 -> DDItems.SERVO_REINFORCED.get();
                case 3 -> DDItems.SERVO_SIGNALUM.get();
                default -> DDItems.SERVO_RESONANT.get();
            });
        }
        if (connection.isFilter()) {
            return new ItemStack(switch (tier.index()) {
                case 0 -> DDItems.FILTER_BASIC.get();
                case 1 -> DDItems.FILTER_HARDENED.get();
                case 2 -> DDItems.FILTER_REINFORCED.get();
                case 3 -> DDItems.FILTER_SIGNALUM.get();
                default -> DDItems.FILTER_RESONANT.get();
            });
        }
        if (connection.isRetriever()) {
            return new ItemStack(switch (tier.index()) {
                case 0 -> DDItems.RETRIEVER_BASIC.get();
                case 1 -> DDItems.RETRIEVER_HARDENED.get();
                case 2 -> DDItems.RETRIEVER_REINFORCED.get();
                case 3 -> DDItems.RETRIEVER_SIGNALUM.get();
                default -> DDItems.RETRIEVER_RESONANT.get();
            });
        }
        return ItemStack.EMPTY;
    }

    private static void playPopSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
