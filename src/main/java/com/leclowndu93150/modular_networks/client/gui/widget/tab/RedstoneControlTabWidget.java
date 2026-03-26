package com.leclowndu93150.modular_networks.client.gui.widget.tab;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.core.attachment.RedstoneMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RedstoneControlTabWidget extends SideTabWidget {

    private static final ResourceLocation ICON_REDSTONE_TAB = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/icons/icon_redstone_on.png");
    private static final ResourceLocation ICON_BUTTON = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/icons/icon_button.png");
    private static final ResourceLocation ICON_BUTTON_HIGHLIGHT = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/icons/icon_button_highlight.png");
    private static final ResourceLocation ICON_REDSTONE_OFF = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/icons/icon_redstone_off.png");
    private static final ResourceLocation ICON_RS_TORCH_OFF = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/icons/icon_rs_torch_off.png");
    private static final ResourceLocation ICON_RS_TORCH_ON = ResourceLocation.fromNamespaceAndPath(
            ModularNetworks.MODID, "textures/gui/icons/icon_rs_torch_on.png");

    private final Supplier<RedstoneMode> modeSupplier;
    private final Consumer<RedstoneMode> modeSetter;

    public RedstoneControlTabWidget(int x, int y, Supplier<RedstoneMode> modeSupplier, Consumer<RedstoneMode> modeSetter) {
        super(
                x, y,
                112, 92,
                Component.translatable("info.modular_networks.redstoneControl"),
                ICON_REDSTONE_TAB,
                0xE1C92F,
                0xAAAFB8,
                0x000000,
                0xD0230A
        );
        this.modeSupplier = modeSupplier;
        this.modeSetter = modeSetter;
    }

    @Override
    protected void renderTabContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RedstoneMode mode = modeSupplier.get();

        drawHeader(graphics, getMessage());

        graphics.setColor(0.49F, 0.08F, 0.02F, 1.0F);
        graphics.blit(TAB_TEXTURE, getX() + 24, getY() + 16, 16, 20, 64, 24, TAB_TEXTURE_SIZE, TAB_TEXTURE_SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        drawModeButton(graphics, getX() + 28, getY() + 20, ICON_REDSTONE_OFF, mode.isDisabled());
        drawModeButton(graphics, getX() + 48, getY() + 20, ICON_RS_TORCH_OFF, mode == RedstoneMode.LOW);
        drawModeButton(graphics, getX() + 68, getY() + 20, ICON_RS_TORCH_ON, mode == RedstoneMode.HIGH);

        drawBodyText(graphics, Component.translatable("info.modular_networks.controlStatus").append(":"), 6, 42, true);
        drawBodyText(graphics, mode.isDisabled()
                ? Component.translatable("info.modular_networks.disabled")
                : Component.translatable("info.modular_networks.enabled"), 14, 54, false);

        drawBodyText(graphics, Component.translatable("info.modular_networks.signalRequired").append(":"), 6, 66, true);
        Component signalText = switch (mode) {
            case LOW -> Component.translatable("info.modular_networks.low");
            case HIGH -> Component.translatable("info.modular_networks.high");
            default -> Component.translatable("info.modular_networks.ignored");
        };
        drawBodyText(graphics, signalText, 14, 78, false);

        Component tooltip = getButtonTooltip(mouseX, mouseY);
        if (tooltip != null) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !isMouseOver(mouseX, mouseY)) {
            return false;
        }
        if (!isFullyOpen()) {
            return true;
        }
        if (insideButton(mouseX, mouseY, 28)) {
            modeSetter.accept(RedstoneMode.DISABLED);
            playClickSound(0.4F);
        } else if (insideButton(mouseX, mouseY, 48)) {
            modeSetter.accept(RedstoneMode.LOW);
            playClickSound(0.6F);
        } else if (insideButton(mouseX, mouseY, 68)) {
            modeSetter.accept(RedstoneMode.HIGH);
            playClickSound(0.8F);
        }
        return true;
    }

    private boolean insideButton(double mouseX, double mouseY, int x) {
        return mouseX >= getX() + x && mouseX < getX() + x + 16 && mouseY >= getY() + 20 && mouseY < getY() + 36;
    }

    private Component getButtonTooltip(int mouseX, int mouseY) {
        if (!isFullyOpen()) {
            return null;
        }
        if (insideButton(mouseX, mouseY, 28)) {
            return Component.translatable("info.modular_networks.ignored");
        }
        if (insideButton(mouseX, mouseY, 48)) {
            return Component.translatable("info.modular_networks.low");
        }
        if (insideButton(mouseX, mouseY, 68)) {
            return Component.translatable("info.modular_networks.high");
        }
        return null;
    }

    private static void drawModeButton(GuiGraphics graphics, int x, int y, ResourceLocation icon, boolean active) {
        graphics.blit(active ? ICON_BUTTON_HIGHLIGHT : ICON_BUTTON, x, y, 0, 0, 16, 16, 16, 16);
        graphics.blit(icon, x, y, 0, 0, 16, 16, 16, 16);
    }
    private void playClickSound(float pitch) {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, pitch)
        );
    }
}
