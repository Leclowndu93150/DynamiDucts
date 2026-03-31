package com.leclowndu93150.dynamiducts.screen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.client.gui.widget.TransportDirectoryButton;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDirectoryEntry;
import com.leclowndu93150.dynamiducts.menu.TransportMenu;
import com.leclowndu93150.dynamiducts.network.payload.TransportRequestPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class TransportScreen extends AbstractContainerScreen<TransportMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/transport.png");

    private static final int NUM_ENTRIES = 7;
    private static final int BUTTON_WIDTH = 155;
    private static final int BUTTON_HEIGHT = 22;
    private static final int BUTTON_OFFSET = 1;
    private static final int SLIDER_WIDTH = 6;
    private static final int SLIDER_X = 164;
    private static final int TRACK_COLOR = 0x30FFFFFF;
    private static final int THUMB_COLOR = 0xA0FFFFFF;
    private static final int THUMB_HEIGHT = 10;

    private final TransportDirectoryButton[] directoryButtons = new TransportDirectoryButton[NUM_ENTRIES];

    private Button configButton;
    private int x0;
    private int y0;
    private int scrollOffset;
    private boolean scrolling;

    public TransportScreen(TransportMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    protected void init() {
        super.init();
        y0 = font.lineHeight + 28;

        for (int i = 0; i < directoryButtons.length; i++) {
            TransportDirectoryButton button = new TransportDirectoryButton(
                    leftPos,
                    topPos + y0 + i * (BUTTON_HEIGHT + BUTTON_OFFSET),
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    TEXTURE,
                    entry -> PacketDistributor.sendToServer(new TransportRequestPayload(menu.getDuctPos(), entry.pos()))
            );
            directoryButtons[i] = addRenderableWidget(button);
        }

        Component configText = Component.translatable("gui.dynamiducts.transport.config");
        int configWidth = Math.min(font.width(configText) + 8, 72);
        configButton = addRenderableWidget(Button.builder(configText, button -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        }).bounds(leftPos + imageWidth - 12 - configWidth, topPos + 16, configWidth, 16).build());

        updateButtons();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 && hasSlider() && isMouseOverSlider(mouseX, mouseY)) {
            scrolling = true;
            updateScrollFromMouse(mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!hasSlider()) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            return true;
        }
        scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(scrollY), 0, maxScroll);
        updateButtons();
        return true;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        if (hasSlider()) {
            int trackX = leftPos + SLIDER_X;
            int trackY = topPos + y0;
            int trackHeight = getTrackHeight();
            graphics.fill(trackX, trackY, trackX + SLIDER_WIDTH, trackY + trackHeight, TRACK_COLOR);
            graphics.fill(trackX, getThumbY(), trackX + SLIDER_WIDTH, getThumbY() + THUMB_HEIGHT, THUMB_COLOR);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);

        TransportDirectoryEntry currentEntry = menu.getCurrentEntry();
        if (currentEntry != null) {
            int dy = 15;
            int iconWidth = currentEntry.icon().isEmpty() ? 0 : BUTTON_HEIGHT;
            String displayName = getDisplayName(currentEntry);
            String text = font.plainSubstrByWidth(displayName, imageWidth - configButton.getWidth() - 16 - iconWidth);
            graphics.drawString(font, text, x0 - leftPos + iconWidth + 4, dy + (BUTTON_HEIGHT - 8) / 2, 0x404040, false);
            if (!currentEntry.icon().isEmpty()) {
                graphics.renderItem(currentEntry.icon(), x0 - leftPos + 3, dy + 3);
            }
        }

        if (menu.getDestinations().isEmpty()) {
            Component emptyText = Component.translatable("gui.dynamiducts.transport.noDestinations");
            graphics.drawString(font, emptyText, getCenteredTextX(emptyText), imageHeight / 2, 0x404040, false);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void updateButtons() {
        List<TransportDirectoryEntry> directory = menu.getDestinations();
        boolean needsSlider = hasSlider();
        x0 = leftPos + (imageWidth - BUTTON_WIDTH) / 2 - (needsSlider ? SLIDER_WIDTH : 0);

        for (int i = 0; i < directoryButtons.length; i++) {
            int index = scrollOffset + i;
            directoryButtons[i].setX(x0);
            directoryButtons[i].setEntry(index < directory.size() ? directory.get(index) : null);
        }
    }

    private boolean hasSlider() {
        return menu.getDestinations().size() > NUM_ENTRIES;
    }

    private int getMaxScroll() {
        return Math.max(0, menu.getDestinations().size() - NUM_ENTRIES);
    }

    private int getTrackHeight() {
        return NUM_ENTRIES * BUTTON_HEIGHT + (NUM_ENTRIES - 1) * BUTTON_OFFSET;
    }

    private int getThumbY() {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            return topPos + y0;
        }
        int travel = getTrackHeight() - THUMB_HEIGHT;
        return topPos + y0 + Mth.floor((float) scrollOffset * travel / maxScroll);
    }

    private boolean isMouseOverSlider(double mouseX, double mouseY) {
        int x = leftPos + SLIDER_X;
        int y = topPos + y0;
        return mouseX >= x && mouseX < x + SLIDER_WIDTH && mouseY >= y && mouseY < y + getTrackHeight();
    }

    private void updateScrollFromMouse(double mouseY) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            scrollOffset = 0;
            return;
        }
        int trackTop = topPos + y0;
        int travel = getTrackHeight() - THUMB_HEIGHT;
        int relative = Mth.clamp((int) mouseY - trackTop - THUMB_HEIGHT / 2, 0, travel);
        scrollOffset = Math.round((float) relative * maxScroll / Math.max(1, travel));
        updateButtons();
    }

    private int getCenteredTextX(Component text) {
        return imageWidth / 2 - font.width(text) / 2;
    }

    private String getDisplayName(TransportDirectoryEntry entry) {
        return entry.name().isEmpty()
                ? Component.translatable("gui.dynamiducts.transport.unnamed").getString()
                : entry.name();
    }
}
