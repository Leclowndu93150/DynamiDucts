package com.leclowndu93150.modular_networks.menu;

import com.leclowndu93150.modular_networks.attachment.filter.FilterItem;
import com.leclowndu93150.modular_networks.attachment.retriever.RetrieverItem;
import com.leclowndu93150.modular_networks.attachment.servo.ServoItem;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentTier;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.attachment.FilterLogic;
import com.leclowndu93150.modular_networks.init.MNMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AttachmentMenu extends AbstractContainerMenu {

    private static final int[] GRID_WIDTHS = {3, 3, 3, 4, 5};

    private final DuctBlockEntity blockEntity;
    private final Direction side;
    private final ConnectionBase attachment;
    private final FilterLogic filter;
    private final ContainerData data;

    public final int gridWidth;
    public final int gridHeight;
    public final int gridX0;
    public final int gridY0;
    public final List<FilterSlot> filterSlots = new ArrayList<>();

    public AttachmentMenu(int containerId, Inventory playerInv, DuctBlockEntity blockEntity, Direction side, ConnectionBase attachment) {
        super(MNMenuTypes.ATTACHMENT_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.side = side;
        this.attachment = attachment;
        this.filter = attachment.getFilter();

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> filter.isWhitelist() ? 1 : 0;
                    case 1 -> filter.isMatchComponents() ? 1 : 0;
                    case 2 -> filter.isMatchModId() ? 1 : 0;
                    case 3 -> attachment.getRedstoneMode().ordinal();
                    case 4 -> filter.getMaxStock();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
        addDataSlots(this.data);

        int tierIndex = attachment.getTier().index();
        int n = filter.getSlotCount();
        gridWidth = tierIndex < GRID_WIDTHS.length ? GRID_WIDTHS[tierIndex] : Math.min(5, n);
        gridHeight = (n + gridWidth - 1) / gridWidth;

        gridX0 = 89 - gridWidth * 9;
        gridY0 = switch (gridHeight) {
            case 1 -> 38;
            case 2 -> 29;
            default -> 20;
        };

        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                int index = col + row * gridWidth;
                if (index < n) {
                    FilterSlot slot = new FilterSlot(filter, index, gridX0 + col * 18, gridY0 + row * 18);
                    filterSlots.add(slot);
                    addSlot(slot);
                }
            }
        }

        addPlayerInventory(playerInv);
    }

    public static AttachmentMenu fromNetwork(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int sideOrdinal = buf.readByte();
        Direction side = Direction.values()[sideOrdinal];
        int tierIndex = buf.readVarInt();
        boolean isServo = buf.readBoolean();
        boolean isFilter = buf.readBoolean();
        boolean isRetriever = buf.readBoolean();

        DuctBlockEntity ductBE = null;
        if (playerInv.player.level().getBlockEntity(pos) instanceof DuctBlockEntity be) {
            ductBE = be;
        }

        ConnectionBase conn = null;
        if (ductBE != null) {
            Attachment att = ductBE.getAttachment(side);
            if (att instanceof ConnectionBase c) {
                conn = c;
            }
        }

        if (conn == null && ductBE != null) {
            AttachmentTier tier = AttachmentTier.byIndex(tierIndex);
            if (isServo) conn = new ServoItem(ductBE, side, tier);
            else if (isFilter) conn = new FilterItem(ductBE, side, tier);
            else if (isRetriever) conn = new RetrieverItem(ductBE, side, tier);
            else conn = new ServoItem(ductBE, side, tier);
        }

        if (ductBE == null || conn == null) return null;
        return new AttachmentMenu(containerId, playerInv, ductBE, side, conn);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 123 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 181));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < filterSlots.size()) {
            FilterSlot filterSlot = filterSlots.get(slotId);
            ItemStack carried = getCarried();
            if (carried.isEmpty()) {
                filterSlot.set(ItemStack.EMPTY);
            } else {
                filterSlot.set(carried.copyWithCount(1));
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return !blockEntity.isRemoved() &&
                player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5,
                        blockEntity.getBlockPos().getY() + 0.5,
                        blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    public ConnectionBase getAttachment() {
        return attachment;
    }

    public FilterLogic getFilter() {
        return filter;
    }

    public Direction getSide() {
        return side;
    }

    public boolean isWhitelist() {
        return data.get(0) == 1;
    }

    public boolean isMatchComponents() {
        return data.get(1) == 1;
    }

    public boolean isMatchModId() {
        return data.get(2) == 1;
    }

    public int getRedstoneMode() {
        return data.get(3);
    }

    public int getMaxStock() {
        return data.get(4);
    }
}
