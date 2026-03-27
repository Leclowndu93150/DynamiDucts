package com.leclowndu93150.modular_networks.client.gui.widget;

import com.leclowndu93150.modular_networks.duct.transport.TransportDirectoryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class TransportDirectoryButton extends AbstractWidget {

    private static final int BASE_U = 0;
    private static final int BASE_V = 204;
    private static final int HOVER_V = 226;

    private final ResourceLocation texture;
    private final Consumer<TransportDirectoryEntry> onPress;
    private TransportDirectoryEntry entry;

    public TransportDirectoryButton(int x, int y, int width, int height, ResourceLocation texture, Consumer<TransportDirectoryEntry> onPress) {
        super(x, y, width, height, Component.empty());
        this.texture = texture;
        this.onPress = onPress;
    }

    public void setEntry(TransportDirectoryEntry entry) {
        this.entry = entry;
        visible = entry != null;
        active = entry != null;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (entry == null) {
            return;
        }

        graphics.blit(texture, getX(), getY(), BASE_U, isHoveredOrFocused() ? HOVER_V : BASE_V, width, height, 256, 256);

        Font font = Minecraft.getInstance().font;
        if (!entry.icon().isEmpty()) {
            graphics.renderItem(entry.icon(), getX() + 3, getY() + 3);
        }

        String displayName = entry.name().isEmpty()
                ? Component.translatable("gui.modular_networks.transport.unnamed").getString()
                : entry.name();
        String text = font.plainSubstrByWidth(displayName, width - height - 4);
        graphics.drawString(font, text, getX() + height + 4, getY() + (height - 8) / 2, getTextColor(), true);

        if (isHoveredOrFocused()) {
            graphics.renderComponentTooltip(font, List.of(
                    Component.literal(displayName),
                    Component.literal("x: " + entry.pos().getX()),
                    Component.literal("y: " + entry.pos().getY()),
                    Component.literal("z: " + entry.pos().getZ())
            ), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (entry == null || button != 0 || !clicked(mouseX, mouseY)) {
            return false;
        }
        playDownSound(Minecraft.getInstance().getSoundManager());
        onPress.accept(entry);
        return true;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return wrapDefaultNarrationMessage(Component.empty());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage());
    }

    private int getTextColor() {
        if (!active) {
            return 0xA0A0A0;
        }
        return isHoveredOrFocused() ? 0xFFFFA0 : 0xE0E0E0;
    }
}
