package com.leclowndu93150.dynamiducts.screen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.menu.TransportConfigMenu;
import com.leclowndu93150.dynamiducts.network.payload.TransportRenamePayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class TransportConfigScreen extends AbstractContainerScreen<TransportConfigMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/transport_config.png");

    private EditBox nameField;

    public TransportConfigScreen(TransportConfigMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, 176, 134);
        this.inventoryLabelY = 40;
    }

    @Override
    protected void init() {
        super.init();
        nameField = addRenderableWidget(new EditBox(font, leftPos + 32, topPos + 18, 135, 10, title));
        nameField.setBordered(false);
        nameField.setMaxLength(32);
        nameField.setValue(menu.getEndpointName());
        nameField.setResponder(value -> ClientPacketDistributor.sendToServer(new TransportRenamePayload(menu.getDuctPos(), value)));
        nameField.setFocused(true);
        setInitialFocus(nameField);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (nameField != null && nameField.isFocused()) {
            if (event.key() == InputConstants.KEY_ESCAPE) {
                return super.keyPressed(event);
            }
            nameField.keyPressed(event);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (nameField != null && nameField.charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (nameField != null && nameField.mouseClicked(event, doubleClick)) {
            nameField.setFocused(true);
            setFocused(nameField);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
