package com.leclowndu93150.modular_networks.screen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.client.gui.widget.SheetButton;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.attachment.FilterLogic;
import com.leclowndu93150.modular_networks.menu.AttachmentMenu;
import com.leclowndu93150.modular_networks.network.payload.AttachmentConfigPayload;
import net.minecraft.client.gui.GuiGraphics;
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
        int buttonsY = topPos + menu.gridY0 + menu.gridHeight * 18 + 8;

        int flagCount = getFlagButtonCount(attachment);
        int levelCount = getLevelButtonCount(attachment);
        int totalButtons = flagCount + levelCount;

        if (totalButtons > 0) {
            int x0 = leftPos + imageWidth / 2 - totalButtons * (BUTTON_SPACING / 2) + 3;

            for (int i = 0; i < flagCount; i++) {
                int flagIndex = i;
                int bx = x0 + i * BUTTON_SPACING;
                final SheetButton[] buttonRef = new SheetButton[1];
                buttonRef[0] = addRenderableWidget(new SheetButton(
                        bx, buttonsY, BUTTON_SIZE, BUTTON_SIZE, TEXTURE, 256, 256,
                        () -> getFlagFrame(flagIndex, buttonRef[0]),
                        (button, mouseButton) -> onFlagClicked(flagIndex),
                        () -> getFlagTooltip(flagIndex)
                ));
            }

            for (int i = 0; i < levelCount; i++) {
                int levelIndex = i;
                int bx = x0 + (flagCount + i) * BUTTON_SPACING;
                final SheetButton[] buttonRef = new SheetButton[1];
                buttonRef[0] = addRenderableWidget(new SheetButton(
                        bx, buttonsY, BUTTON_SIZE, BUTTON_SIZE, TEXTURE, 256, 256,
                        () -> getLevelFrame(levelIndex, buttonRef[0]),
                        (button, mouseButton) -> onLevelClicked(levelIndex, mouseButton),
                        () -> getLevelTooltip(levelIndex)
                ));
            }
        }

        if (attachment.isServo()) {
            int decX = leftPos + 137;
            int incX = leftPos + 153;
            int stackY = topPos + 57;
            final SheetButton[] decRef = new SheetButton[1];
            decRef[0] = addRenderableWidget(new SheetButton(
                    decX, stackY, 14, 14, TEXTURE, 256, 256,
                    () -> new SheetButton.Frame(216, isHovered(decRef[0]) ? 134 : 120),
                    (button, mouseButton) -> onDecStackSize(mouseButton),
                    this::getDecStackTooltip
            ));
            final SheetButton[] incRef = new SheetButton[1];
            incRef[0] = addRenderableWidget(new SheetButton(
                    incX, stackY, 14, 14, TEXTURE, 256, 256,
                    () -> new SheetButton.Frame(230, isHovered(incRef[0]) ? 134 : 120),
                    (button, mouseButton) -> onIncStackSize(mouseButton),
                    this::getIncStackTooltip
            ));
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
            int stackSize = getDisplayedStackSize();
            String text = String.valueOf(stackSize);
            int xQty = 146;
            if (stackSize < 10) xQty += 6;
            graphics.drawString(font, text, xQty, 46, 0x404040, false);
        }

        graphics.drawString(font, Component.translatable("info.modular_networks.redstoneMode." + menu.getRedstoneMode()),
                8, 6, 0x404040, false);
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
        if (action < 0) {
            return;
        }

        FilterLogic filter = menu.getFilter();
        switch (flagIndex) {
            case 0 -> menu.setLocalWhitelist(!filter.isWhitelist());
            case 1 -> menu.setLocalMatchComponents(!filter.isMatchComponents());
            case 2 -> menu.setLocalMatchModId(!filter.isMatchModId());
            default -> {
                return;
            }
        }
        sendConfig(action, 0);
    }

    private void onLevelClicked(int levelIndex, int mouseButton) {
        if (levelIndex != 0) {
            return;
        }
        FilterLogic filter = menu.getFilter();
        int delta = mouseButton == 1 ? -1 : 1;
        int routeType = Math.floorMod(filter.getRouteType() + delta, FilterLogic.ROUTE_TYPE_COUNT);
        menu.setLocalRouteType(routeType);
        sendConfig(AttachmentConfigPayload.ACTION_SET_ROUTE_TYPE, routeType);
    }

    private void onDecStackSize(int mouseButton) {
        int current = getDisplayedStackSize();
        int updated = clampStackSize(current - getStackDelta(mouseButton));
        if (updated == current) {
            return;
        }
        menu.setLocalMaxStock(updated);
        sendConfig(AttachmentConfigPayload.ACTION_SET_MAX_STOCK, updated);
    }

    private void onIncStackSize(int mouseButton) {
        int current = getDisplayedStackSize();
        int updated = clampStackSize(current + getStackDelta(mouseButton));
        if (updated == current) {
            return;
        }
        menu.setLocalMaxStock(updated);
        sendConfig(AttachmentConfigPayload.ACTION_SET_MAX_STOCK, updated);
    }

    private SheetButton.Frame getFlagFrame(int flagIndex, SheetButton button) {
        boolean active = switch (flagIndex) {
            case 0 -> menu.isWhitelist();
            case 1 -> menu.isMatchComponents();
            case 2 -> menu.isMatchModId();
            default -> false;
        };
        int u = FLAG_BUTTON_TEX[flagIndex][0] + (active ? BUTTON_SIZE : 0);
        int v = FLAG_BUTTON_TEX[flagIndex][1] + (isHovered(button) ? BUTTON_SIZE : 0);
        return new SheetButton.Frame(u, v);
    }

    private SheetButton.Frame getLevelFrame(int levelIndex, SheetButton button) {
        int textureIndex = levelIndex == 0 ? 2 : Math.min(levelIndex + 1, LEVEL_BUTTON_TEX.length - 1);
        int u = LEVEL_BUTTON_TEX[textureIndex][0];
        int v = LEVEL_BUTTON_TEX[textureIndex][1];
        if (u < 0 || v < 0) {
            return new SheetButton.Frame(0, 0);
        }
        if (levelIndex == 0) {
            u += menu.getRouteType() * BUTTON_SIZE;
        }
        if (isHovered(button)) {
            v += BUTTON_SIZE;
        }
        return new SheetButton.Frame(u, v);
    }

    private Component getFlagTooltip(int flagIndex) {
        return switch (flagIndex) {
            case 0 -> Component.translatable("info.modular_networks.filter.whiteList." + (menu.isWhitelist() ? "on" : "off"));
            case 1 -> Component.translatable("info.modular_networks.filter.components." + (menu.isMatchComponents() ? "on" : "off"));
            case 2 -> Component.translatable("info.modular_networks.filter.modSorting." + (menu.isMatchModId() ? "on" : "off"));
            default -> Component.empty();
        };
    }

    private Component getLevelTooltip(int levelIndex) {
        if (levelIndex == 0) {
            return Component.translatable("info.modular_networks.filter.routeType." + menu.getRouteType());
        }
        return Component.empty();
    }

    private Component getDecStackTooltip() {
        return Component.translatable("info.modular_networks.servo.decStackSize", getStackDeltaLabel());
    }

    private Component getIncStackTooltip() {
        return Component.translatable("info.modular_networks.servo.incStackSize", getStackDeltaLabel());
    }

    private int getDisplayedStackSize() {
        int configured = menu.getMaxStock();
        return configured > 0 ? configured : menu.getAttachment().getTier().stackSize();
    }

    private int clampStackSize(int value) {
        return Math.max(1, Math.min(value, menu.getAttachment().getTier().stackSize()));
    }

    private int getStackDelta(int mouseButton) {
        if (hasShiftDown()) {
            return mouseButton == 1 ? 4 : 16;
        }
        return 1;
    }

    private String getStackDeltaLabel() {
        return Integer.toString(getStackDelta(0));
    }

    private static boolean isHovered(SheetButton button) {
        return button != null && button.isHoveredOrFocused();
    }
}
