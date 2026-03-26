package com.leclowndu93150.modular_networks.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class DiscreteSlider extends AbstractSliderButton {

    private final int minValue;
    private final int maxValue;
    private final IntConsumer onValueChanged;
    private final IntConsumer onValueReleased;
    private final @Nullable IntFunction<Component> tooltipFactory;
    private final @Nullable IntFunction<Component> messageFactory;

    public DiscreteSlider(
            int x,
            int y,
            int width,
            int height,
            int minValue,
            int maxValue,
            int initialValue,
            IntConsumer onValueChanged,
            IntConsumer onValueReleased,
            @Nullable IntFunction<Component> tooltipFactory,
            @Nullable IntFunction<Component> messageFactory
    ) {
        super(x, y, width, height, Component.empty(), normalize(initialValue, minValue, maxValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.onValueChanged = onValueChanged;
        this.onValueReleased = onValueReleased;
        this.tooltipFactory = tooltipFactory;
        this.messageFactory = messageFactory;
        updateMessage();
    }

    public int getIntValue() {
        if (maxValue <= minValue) {
            return minValue;
        }
        return minValue + (int) Math.round(value * (maxValue - minValue));
    }

    public void setIntValue(int value) {
        this.value = normalize(value, minValue, maxValue);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        if (messageFactory != null) {
            setMessage(messageFactory.apply(getIntValue()));
        } else {
            setMessage(Component.empty());
        }
    }

    @Override
    protected void applyValue() {
        onValueChanged.accept(getIntValue());
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        onValueReleased.accept(getIntValue());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (tooltipFactory != null && isMouseOver(mouseX, mouseY)) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltipFactory.apply(getIntValue()), mouseX, mouseY);
        }
    }

    private static double normalize(int value, int minValue, int maxValue) {
        if (maxValue <= minValue) {
            return 0.0D;
        }
        int clamped = Math.max(minValue, Math.min(maxValue, value));
        return (double) (clamped - minValue) / (double) (maxValue - minValue);
    }
}
