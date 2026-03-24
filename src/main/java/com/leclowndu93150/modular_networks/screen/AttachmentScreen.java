package com.leclowndu93150.modular_networks.screen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.attachment.FilterLogic;
import com.leclowndu93150.modular_networks.core.attachment.RedstoneMode;
import com.leclowndu93150.modular_networks.menu.AttachmentMenu;
import com.leclowndu93150.modular_networks.network.payload.AttachmentConfigPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class AttachmentScreen extends AbstractContainerScreen<AttachmentMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/connection.png");

    private static final int BUTTON_SIZE = 20;
    private static final int BUTTON_SPACING = 26;

    private static final int[][] FLAG_BUTTON_TEX = {
            {176, 0},
            {176, 60},
            {216, 0},
            {216, 60},
            {176, 120},
    };

    private static final int[][] LEVEL_BUTTON_TEX = {
            {-1, -1},
            {0, 204},
            {80, 204},
    };

    public AttachmentScreen(AttachmentMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 204;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        ConnectionBase attachment = menu.getAttachment();
        FilterLogic filter = menu.getFilter();

        int buttonsY = topPos + menu.gridY0 + menu.gridHeight * 18 + 8;

        int flagCount = getFlagButtonCount(attachment);
        int levelCount = getLevelButtonCount(attachment);
        int totalButtons = flagCount + levelCount;

        if (totalButtons > 0) {
            int x0 = leftPos + imageWidth / 2 - totalButtons * (BUTTON_SPACING / 2) + 3;

            for (int i = 0; i < flagCount; i++) {
                int flagIndex = i;
                int bx = x0 + i * BUTTON_SPACING;
                addRenderableWidget(Button.builder(Component.empty(), btn -> onFlagClicked(flagIndex))
                        .bounds(bx, buttonsY, BUTTON_SIZE, BUTTON_SIZE)
                        .build());
            }

            for (int i = 0; i < levelCount; i++) {
                int levelIndex = i;
                int bx = x0 + (flagCount + i) * BUTTON_SPACING;
                addRenderableWidget(Button.builder(Component.empty(), btn -> onLevelClicked(levelIndex))
                        .bounds(bx, buttonsY, BUTTON_SIZE, BUTTON_SIZE)
                        .build());
            }
        }

        if (attachment.isServo()) {
            int decX = leftPos + 137;
            int incX = leftPos + 153;
            int stackY = topPos + 57;
            addRenderableWidget(Button.builder(Component.literal("-"), btn -> onDecStackSize())
                    .bounds(decX, stackY, 14, 14)
                    .build());
            addRenderableWidget(Button.builder(Component.literal("+"), btn -> onIncStackSize())
                    .bounds(incX, stackY, 14, 14)
                    .build());
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        for (int row = 0; row < menu.gridHeight; row++) {
            for (int col = 0; col < menu.gridWidth; col++) {
                int x = leftPos + menu.gridX0 + col * 18 - 1;
                int y = topPos + menu.gridY0 + row * 18 - 1;
                graphics.blit(TEXTURE, x, y, 7, 122, 18, 18);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        ConnectionBase attachment = menu.getAttachment();

        if (attachment.isServo()) {
            int stackSize = attachment.getTier().stackSize();
            String text = String.valueOf(stackSize);
            int xQty = 146;
            if (stackSize < 10) xQty += 6;
            graphics.drawString(font, text, xQty, 46, 0x404040, false);
        }

        String modeName = switch (attachment.getRedstoneMode()) {
            case IGNORED -> "Always Active";
            case HIGH -> "High";
            case LOW -> "Low";
            case DISABLED -> "Disabled";
        };
        graphics.drawString(font, modeName, 8, 6, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private int getFlagButtonCount(ConnectionBase attachment) {
        int count = 1;
        if (attachment.getTier().index() >= 1) count++;
        if (attachment.getTier().index() >= 2) count++;
        return count;
    }

    private int getLevelButtonCount(ConnectionBase attachment) {
        if (attachment.getTier().index() >= 2) return 1;
        return 0;
    }

    private void sendConfig(int action, int value) {
        PacketDistributor.sendToServer(new AttachmentConfigPayload(
                menu.getAttachment().getParent().getBlockPos(),
                menu.getSide().ordinal(),
                action,
                value
        ));
    }

    private void onFlagClicked(int flagIndex) {
        int action = switch (flagIndex) {
            case 0 -> AttachmentConfigPayload.ACTION_TOGGLE_WHITELIST;
            case 1 -> AttachmentConfigPayload.ACTION_TOGGLE_MATCH_COMPONENTS;
            case 2 -> AttachmentConfigPayload.ACTION_TOGGLE_MATCH_MOD;
            default -> -1;
        };
        if (action >= 0) sendConfig(action, 0);
    }

    private void onLevelClicked(int levelIndex) {
    }

    private void onDecStackSize() {
    }

    private void onIncStackSize() {
    }
}
