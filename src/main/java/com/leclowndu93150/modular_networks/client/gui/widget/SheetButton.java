package com.leclowndu93150.modular_networks.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SheetButton extends AbstractWidget {

    private final ResourceLocation texture;
    private final int textureWidth;
    private final int textureHeight;
    private final PressAction onPress;
    private final Supplier<Frame> frameSupplier;
    private final Supplier<@Nullable Component> tooltipSupplier;

    public SheetButton(
            int x,
            int y,
            int width,
            int height,
            ResourceLocation texture,
            int textureWidth,
            int textureHeight,
            Supplier<Frame> frameSupplier,
            PressAction onPress,
            Supplier<@Nullable Component> tooltipSupplier
    ) {
        super(x, y, width, height, Component.empty());
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.frameSupplier = frameSupplier;
        this.onPress = onPress;
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Frame frame = frameSupplier.get();
        graphics.blit(texture, getX(), getY(), frame.u(), frame.v(), width, height, textureWidth, textureHeight);

        Component tooltip = tooltipSupplier.get();
        if (tooltip != null && isMouseOver(mouseX, mouseY)) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !clicked(mouseX, mouseY) || (button != 0 && button != 1)) {
            return false;
        }
        playDownSound(Minecraft.getInstance().getSoundManager());
        onPress.onPress(this, button);
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

    public record Frame(int u, int v) {
    }

    @FunctionalInterface
    public interface PressAction {
        void onPress(SheetButton button, int mouseButton);
    }
}
