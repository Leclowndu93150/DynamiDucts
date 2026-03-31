package com.leclowndu93150.dynamiducts.compat.jei;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.screen.AttachmentScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class DDJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AttachmentScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(AttachmentScreen screen) {
                List<Rect2i> areas = new ArrayList<>();
                if (screen.getInfoTab() != null) {
                    var tab = screen.getInfoTab();
                    areas.add(new Rect2i(tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight()));
                }
                if (screen.getRedstoneTab() != null) {
                    var tab = screen.getRedstoneTab();
                    areas.add(new Rect2i(tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight()));
                }
                return areas;
            }
        });
    }
}
