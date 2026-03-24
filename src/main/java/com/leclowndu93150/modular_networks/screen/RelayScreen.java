package com.leclowndu93150.modular_networks.screen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.menu.RelayMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RelayScreen extends AbstractContainerScreen<RelayMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/relay.png");

    private static final String[] TYPE_NAMES = {"Redstone Input", "Redstone Output", "Comparator Input"};
    private static final String[] INVERT_NAMES = {"Scaled", "Inverted Scaled", "Threshold", "Inverted Threshold"};
    private static final String[] COLOR_NAMES = {
            "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Light Gray", "Gray",
            "Pink", "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "White", "Black"
    };

    public RelayScreen(RelayMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 202;
        this.imageHeight = 74;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(Component.empty(), btn -> cycleType(1))
                .bounds(leftPos + 8, topPos + 16, 20, 20)
                .build());

        addRenderableWidget(Button.builder(Component.empty(), btn -> cycleInvert(1))
                .bounds(leftPos + 34, topPos + 16, 20, 20)
                .build());

        addRenderableWidget(Button.builder(Component.empty(), btn -> cycleColor(1))
                .bounds(leftPos + 60, topPos + 16, 20, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int type = menu.getRelayType();
        graphics.blit(TEXTURE, leftPos + 8, topPos + 16, type * 20, 204, 20, 20);

        int color = menu.getRelayColor();
        int colorTexY = color < 8 ? 124 : 164;
        int colorTexX = (color % 8) * 20;
        graphics.blit(TEXTURE, leftPos + 60, topPos + 16, colorTexX, colorTexY, 20, 20);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int type = menu.getRelayType();
        boolean isInput = type == 0 || type == 2;

        int relayY = isInput ? 45 : 58;
        int gridY = isInput ? 58 : 45;

        graphics.drawString(font, "Relay Power: " + menu.getRelayPower(), 8, relayY, 0x404040, false);
        graphics.drawString(font, "Duct Power: 0", 8, gridY, 0x404040, false);

        graphics.drawString(font, TYPE_NAMES[type], 8, 4, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void cycleType(int direction) {
        var relay = menu.getRelay();
        relay.setType((relay.getType() + 3 + direction) % 3);
    }

    private void cycleInvert(int direction) {
    }

    private void cycleColor(int direction) {
        var relay = menu.getRelay();
        relay.setColor((relay.getColor() + 16 + direction) % 16);
    }
}
