package com.leclowndu93150.modular_networks.init;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.menu.AttachmentMenu;
import com.leclowndu93150.modular_networks.menu.RelayMenu;
import com.leclowndu93150.modular_networks.menu.TransportConfigMenu;
import com.leclowndu93150.modular_networks.menu.TransportMenu;
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

    public static final DeferredHolder<MenuType<?>, MenuType<TransportMenu>> TRANSPORT_MENU =
            MENU_TYPES.register("transport", () -> IMenuTypeExtension.create(TransportMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<TransportConfigMenu>> TRANSPORT_CONFIG_MENU =
            MENU_TYPES.register("transport_config", () -> IMenuTypeExtension.create(TransportConfigMenu::fromNetwork));
}
