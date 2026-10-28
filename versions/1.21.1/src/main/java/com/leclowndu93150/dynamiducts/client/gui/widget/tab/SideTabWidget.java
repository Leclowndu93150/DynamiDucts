package com.leclowndu93150.dynamiducts.client.gui.widget.tab;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public abstract class SideTabWidget extends AbstractWidget {

    protected static final ResourceLocation TAB_TEXTURE = ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "textures/gui/tab_right.png");
    protected static final int TAB_TEXTURE_SIZE = 256;
    protected static final int MIN_SIZE = 22;
    private static final float EXPAND_PIXELS_PER_MILLI = 0.4F;

    private final ResourceLocation iconTexture;
    private final int maxTabWidth;
    private final int maxTabHeight;
    private final int headerColor;
    private final int subheaderColor;
    private final int textColor;
    private final int backgroundColor;

    private Consumer<SideTabWidget> openListener = tab -> {};
    private boolean open;
    private long lastAnimationTime = Util.getMillis();
    private float animatedWidth = MIN_SIZE;
    private float animatedHeight = MIN_SIZE;
    private int currentWidth = MIN_SIZE;
    private int currentHeight = MIN_SIZE;

    protected SideTabWidget(
            int x,
            int y,
            int maxTabWidth,
            int maxTabHeight,
            Component title,
            ResourceLocation iconTexture,
            int headerColor,
            int subheaderColor,
            int textColor,
            int backgroundColor
    ) {
        super(x, y, MIN_SIZE, MIN_SIZE, title);
        this.iconTexture = iconTexture;
        this.maxTabWidth = maxTabWidth;
        this.maxTabHeight = maxTabHeight;
        this.headerColor = headerColor;
        this.subheaderColor = subheaderColor;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        animate();

        drawTabBackground(graphics, getX(), getY(), currentWidth, currentHeight, backgroundColor);
        drawTabIcon(graphics, getX() + 3, getY() + 3);

        if (isFullyOpen()) {
            renderTabContents(graphics, mouseX, mouseY, partialTick);
        }
    }

    protected void drawHeader(GuiGraphics graphics, Component title) {
        graphics.drawString(font(), title, getX() + 20, getY() + 6, headerColor, true);
    }

    protected abstract void renderTabContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    protected final void drawBodyText(GuiGraphics graphics, Component text, int x, int y, boolean subheader) {
        graphics.drawString(font(), text, getX() + x, getY() + y, subheader ? subheaderColor : textColor, subheader);
    }

    protected final boolean isFullyOpen() {
        return open && currentWidth == maxTabWidth && currentHeight == maxTabHeight;
    }

    protected final int tabRight() {
        return getX() + currentWidth;
    }

    protected final int tabBottom() {
        return getY() + currentHeight;
    }

    protected final int headerColor() {
        return headerColor;
    }

    protected final int subheaderColor() {
        return subheaderColor;
    }

    protected final int textColor() {
        return textColor;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && visible
                && mouseX >= getX()
                && mouseY >= getY()
                && mouseX < getX() + currentWidth
                && mouseY < getY() + currentHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !isMouseOver(mouseX, mouseY) || !isValidClickButton(button)) {
            return false;
        }
        if (isFullyOpen() && handleContentClick(mouseX, mouseY)) {
            return true;
        }
        return toggleOpen();
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return wrapDefaultNarrationMessage(getMessage());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }

    public void setOpenListener(Consumer<SideTabWidget> openListener) {
        this.openListener = openListener;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    protected boolean handleContentClick(double mouseX, double mouseY) {
        return false;
    }

    private boolean toggleOpen() {
        playDownSound(Minecraft.getInstance().getSoundManager());
        open = !open;
        if (open) {
            openListener.accept(this);
        }
        return true;
    }

    private void animate() {
        long now = Util.getMillis();
        float distance = (now - lastAnimationTime) * EXPAND_PIXELS_PER_MILLI;
        lastAnimationTime = now;
        animatedWidth = approach(animatedWidth, open ? maxTabWidth : MIN_SIZE, distance);
        animatedHeight = approach(animatedHeight, open ? maxTabHeight : MIN_SIZE, distance);
        currentWidth = Math.round(animatedWidth);
        currentHeight = Math.round(animatedHeight);
        setSize(currentWidth, currentHeight);
    }

    private static float approach(float current, float target, float distance) {
        return current < target ? Math.min(target, current + distance) : Math.max(target, current - distance);
    }

    private void drawTabIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(iconTexture, x, y, 0, 0, 16, 16, 16, 16);
    }

    private static void drawTabBackground(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        graphics.setColor(red, green, blue, 1.0F);
        graphics.blit(TAB_TEXTURE, x, y + 4, 0, TAB_TEXTURE_SIZE - height + 4, 4, height - 4, TAB_TEXTURE_SIZE, TAB_TEXTURE_SIZE);
        graphics.blit(TAB_TEXTURE, x + 4, y, TAB_TEXTURE_SIZE - width + 4, 0, width - 4, 4, TAB_TEXTURE_SIZE, TAB_TEXTURE_SIZE);
        graphics.blit(TAB_TEXTURE, x, y, 0, 0, 4, 4, TAB_TEXTURE_SIZE, TAB_TEXTURE_SIZE);
        graphics.blit(TAB_TEXTURE, x + 4, y + 4, TAB_TEXTURE_SIZE - width + 4, TAB_TEXTURE_SIZE - height + 4,
                width - 4, height - 4, TAB_TEXTURE_SIZE, TAB_TEXTURE_SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static Font font() {
        return Minecraft.getInstance().font;
    }
}
