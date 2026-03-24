package com.leclowndu93150.modular_networks.init;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.menu.AttachmentMenu;
import com.leclowndu93150.modular_networks.menu.RelayMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MNMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, ModularNetworks.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AttachmentMenu>> ATTACHMENT_MENU =
            MENU_TYPES.register("attachment", () -> IMenuTypeExtension.create(AttachmentMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<RelayMenu>> RELAY_MENU =
            MENU_TYPES.register("relay", () -> IMenuTypeExtension.create(RelayMenu::fromNetwork));
}
