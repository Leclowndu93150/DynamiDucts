package com.leclowndu93150.modular_networks.screen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.menu.TransportConfigMenu;
import com.leclowndu93150.modular_networks.network.payload.TransportRenamePayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class TransportConfigScreen extends AbstractContainerScreen<TransportConfigMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/transport_config.png");

    private EditBox nameField;

    public TransportConfigScreen(TransportConfigMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 134;
        this.inventoryLabelY = 40;
    }

    @Override
    protected void init() {
        super.init();
        nameField = addRenderableWidget(new EditBox(font, leftPos + 32, topPos + 18, 135, 10, title));
        nameField.setBordered(false);
        nameField.setMaxLength(32);
        nameField.setValue(menu.getEndpointName());
        nameField.setResponder(value -> PacketDistributor.sendToServer(new TransportRenamePayload(menu.getDuctPos(), value)));
        nameField.setFocused(true);
        setInitialFocus(nameField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (nameField != null && nameField.isFocused()) {
            if (keyCode == InputConstants.KEY_ESCAPE) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            nameField.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (nameField != null && nameField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (nameField != null && nameField.mouseClicked(mouseX, mouseY, button)) {
            nameField.setFocused(true);
            setFocused(nameField);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
