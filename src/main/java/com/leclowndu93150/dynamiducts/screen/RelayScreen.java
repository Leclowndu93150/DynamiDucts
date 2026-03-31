package com.leclowndu93150.dynamiducts.screen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.client.gui.widget.DiscreteSlider;
import com.leclowndu93150.dynamiducts.client.gui.widget.SheetButton;
import com.leclowndu93150.dynamiducts.menu.RelayMenu;
import com.leclowndu93150.dynamiducts.network.payload.RelayConfigPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class RelayScreen extends AbstractContainerScreen<RelayMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/relay.png");

    private SheetButton typeButton;
    private SheetButton invertButton;
    private SheetButton colorButton;
    private DiscreteSlider thresholdSlider;

    public RelayScreen(RelayMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 202;
        this.imageHeight = 74;
    }

    @Override
    protected void init() {
        super.init();

        typeButton = addRenderableWidget(new SheetButton(
                leftPos + 8, topPos + 16, 20, 20, TEXTURE, 256, 256,
                () -> new SheetButton.Frame(menu.getRelayType() * 20, 204 + (isButtonHovered(typeButton) ? 20 : 0)),
                (button, mouseButton) -> cycleType(mouseButton == 0 ? 1 : -1),
                () -> Component.translatable("info.dynamiducts.relay.type." + menu.getRelayType())
        ));

        invertButton = addRenderableWidget(new SheetButton(
                leftPos + 34, topPos + 16, 20, 20, TEXTURE, 256, 256,
                () -> new SheetButton.Frame(60 + menu.getInvertMode() * 20, 204 + (isButtonHovered(invertButton) ? 20 : 0)),
                (button, mouseButton) -> cycleInvert(mouseButton == 0 ? 1 : -1),
                () -> Component.translatable("info.dynamiducts.relay.invert." + menu.getInvertMode())
        ));

        colorButton = addRenderableWidget(new SheetButton(
                leftPos + 60, topPos + 16, 20, 20, TEXTURE, 256, 256,
                this::getColorFrame,
                (button, mouseButton) -> cycleColor(mouseButton == 0 ? 1 : -1),
                () -> Component.translatable("info.dynamiducts.relay.color." + menu.getRelayColor())
        ));

        thresholdSlider = addRenderableWidget(new DiscreteSlider(
                leftPos + 88, topPos + 16, 100, 20,
                0, 15, menu.getThreshold(),
                this::setThreshold,
                this::setThreshold,
                value -> Component.translatable("info.dynamiducts.relay.threshold", value),
                null
        ));

        updateThresholdVisibility();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        thresholdSlider.setIntValue(menu.getThreshold());
        updateThresholdVisibility();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean isInput = menu.getRelayType() == 0 || menu.getRelayType() == 2;
        int relayY = isInput ? 45 : 58;
        int gridY = isInput ? 58 : 45;

        graphics.drawString(font, Component.translatable("info.dynamiducts.relay.type." + menu.getRelayType()), 8, 4, 0x404040, false);
        graphics.drawString(font, Component.translatable("info.dynamiducts.relay.relayRS", menu.getRelayPower()), 8, relayY, 0x404040, false);
        graphics.drawString(font, Component.translatable("info.dynamiducts.relay.gridRS", menu.getGridPower()), 8, gridY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private SheetButton.Frame getColorFrame() {
        int color = menu.getRelayColor();
        int colorX = (color % 8) * 20;
        int colorY = color < 8 ? 124 : 164;
        if (isButtonHovered(colorButton)) {
            colorY += 20;
        }
        return new SheetButton.Frame(colorX, colorY);
    }

    private void cycleType(int delta) {
        int next = Math.floorMod(menu.getRelayType() + delta, 3);
        menu.getRelay().setType(next);
        sendConfig(RelayConfigPayload.ACTION_SET_TYPE, next);
        updateThresholdVisibility();
    }

    private void cycleInvert(int delta) {
        int next = Math.floorMod(menu.getInvertMode() + delta, 4);
        menu.getRelay().setInvertMode(next);
        sendConfig(RelayConfigPayload.ACTION_SET_INVERT, next);
        updateThresholdVisibility();
    }

    private void cycleColor(int delta) {
        int next = Math.floorMod(menu.getRelayColor() + delta, 16);
        menu.getRelay().setColor(next);
        sendConfig(RelayConfigPayload.ACTION_SET_COLOR, next);
    }

    private void setThreshold(int value) {
        menu.getRelay().setThreshold(value);
        sendConfig(RelayConfigPayload.ACTION_SET_THRESHOLD, value);
    }

    private void updateThresholdVisibility() {
        boolean visible = menu.getInvertMode() >= 2;
        thresholdSlider.visible = visible;
        thresholdSlider.active = visible;
    }

    private void sendConfig(int action, int value) {
        PacketDistributor.sendToServer(new RelayConfigPayload(
                menu.getRelay().getParent().getBlockPos(),
                menu.getRelay().getSide().ordinal(),
                action,
                value
        ));
    }

    private static boolean isButtonHovered(SheetButton button) {
        return button != null && button.isHoveredOrFocused();
    }
}
