package com.leclowndu93150.dynamiducts.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SheetButton extends AbstractWidget {

    private final Identifier texture;
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
            Identifier texture,
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Frame frame = frameSupplier.get();
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), frame.u(), frame.v(), width, height, textureWidth, textureHeight);

        Component tooltip = tooltipSupplier.get();
        if (tooltip != null && isMouseOver(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!active || !visible || !isMouseOver(event.x(), event.y()) || (event.button() != 0 && event.button() != 1)) {
            return false;
        }
        playDownSound(Minecraft.getInstance().getSoundManager());
        onPress.onPress(this, event.button());
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
