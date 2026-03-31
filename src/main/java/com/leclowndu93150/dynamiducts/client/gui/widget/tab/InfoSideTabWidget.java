package com.leclowndu93150.dynamiducts.client.gui.widget.tab;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InfoSideTabWidget extends SideTabWidget {

    private static final ResourceLocation ICON_INFORMATION = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/icons/icon_information.png");
    private static final ResourceLocation ICON_ARROW_UP = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/icons/icon_arrow_up.png");
    private static final ResourceLocation ICON_ARROW_UP_INACTIVE = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/icons/icon_arrow_up_inactive.png");
    private static final ResourceLocation ICON_ARROW_DOWN = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/icons/icon_arrow_down.png");
    private static final ResourceLocation ICON_ARROW_DOWN_INACTIVE = ResourceLocation.fromNamespaceAndPath(
            DynamiDucts.MODID, "textures/gui/icons/icon_arrow_down_inactive.png");

    private final Supplier<List<Component>> linesSupplier;
    private int firstLine;

    public InfoSideTabWidget(int x, int y, Supplier<List<Component>> linesSupplier) {
        super(
                x, y,
                124, 92,
                Component.translatable("info.dynamiducts.information"),
                ICON_INFORMATION,
                0xE1C92F,
                0xAAAFB8,
                0xFFFFFF,
                0x555555
        );
        this.linesSupplier = linesSupplier;
    }

    @Override
    protected void renderTabContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        drawHeader(graphics, getMessage());

        List<FormattedCharSequence> lines = buildWrappedLines();
        int visibleLines = (92 - 24) / Minecraft.getInstance().font.lineHeight;
        int maxFirstLine = Math.max(0, lines.size() - visibleLines);
        if (firstLine > maxFirstLine) {
            firstLine = maxFirstLine;
        }

        if (maxFirstLine > 0) {
            ResourceLocation upIcon = firstLine > 0 ? ICON_ARROW_UP : ICON_ARROW_UP_INACTIVE;
            ResourceLocation downIcon = firstLine < maxFirstLine ? ICON_ARROW_DOWN : ICON_ARROW_DOWN_INACTIVE;
            graphics.blit(upIcon, getX() + 104, getY() + 16, 0, 0, 16, 16, 16, 16);
            graphics.blit(downIcon, getX() + 104, getY() + 76, 0, 0, 16, 16, 16, 16);
        }

        for (int i = 0; i < visibleLines && firstLine + i < lines.size(); i++) {
            graphics.drawString(Minecraft.getInstance().font, lines.get(firstLine + i), getX() + 2, getY() + 20 + i * Minecraft.getInstance().font.lineHeight, textColor());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            return false;
        }
        if (!isFullyOpen()) {
            return true;
        }

        List<FormattedCharSequence> lines = buildWrappedLines();
        int visibleLines = (92 - 24) / Minecraft.getInstance().font.lineHeight;
        int maxFirstLine = Math.max(0, lines.size() - visibleLines);
        if (maxFirstLine <= 0) {
            return true;
        }
        if (mouseX >= getX() + 104 && mouseX < getX() + 120) {
            if (mouseY >= getY() + 16 && mouseY < getY() + 52) {
                firstLine = Math.max(0, firstLine - 1);
            } else if (mouseY >= getY() + 52 && mouseY < getY() + 92) {
                firstLine = Math.min(maxFirstLine, firstLine + 1);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isMouseOver(mouseX, mouseY) || !isFullyOpen()) {
            return false;
        }
        List<FormattedCharSequence> lines = buildWrappedLines();
        int visibleLines = (92 - 24) / Minecraft.getInstance().font.lineHeight;
        int maxFirstLine = Math.max(0, lines.size() - visibleLines);
        if (maxFirstLine <= 0) {
            return false;
        }
        if (scrollY > 0.0D) {
            firstLine = Math.max(0, firstLine - 1);
            return true;
        }
        if (scrollY < 0.0D) {
            firstLine = Math.min(maxFirstLine, firstLine + 1);
            return true;
        }
        return false;
    }

    private List<FormattedCharSequence> buildWrappedLines() {
        List<FormattedCharSequence> wrapped = new ArrayList<>();
        for (Component line : linesSupplier.get()) {
            wrapped.addAll(Minecraft.getInstance().font.split(line, 108));
        }
        return wrapped;
    }
}
