# DynamiDucts

A Minecraft NeoForge mod for 1.21.1 that brings back the Thermal Dynamics legacy duct system. Build pipe networks to transport items, fluids, energy, and even players.

## Features

### Ducts
- **Energy Ducts (Fluxducts)** transfer RF/FE between machines. 6 tiers from Leadstone to Cryo-Stabilized.
- **Fluid Ducts (Fluiducts)** transfer fluids between tanks. Hardened variants handle extreme temperatures. Super Laminar variant has unlimited throughput.
- **Item Ducts (Itemducts)** move items with configurable routing. Impulse variant for faster travel. Flux-plated variants also carry energy.
- **Transport Ducts (Viaducts)** send players between endpoints. Wrench a face to create an endpoint, right-click to pick a destination.
- **Structural Ducts** provide non-transferring structure for relay networks and covers.
- **Lux Ducts** emit light when powered by redstone.

### Attachments
- **Servos** extract items/fluids from adjacent inventories into the network. 5 tiers with increasing speed, stack size, and multi-slot extraction.
- **Retrievers** pull items/fluids from remote inventories back to the retriever's location.
- **Filters** control what passes through duct-to-duct connections. Supports whitelist/blacklist, mod sorting, and component matching.
- **Relays** transmit redstone signals through structural duct networks using 16 color channels.
- **Covers** disguise ducts as any block. Craft a cover item with any solid block in a crafting grid.

### Network Features
- Items visually travel through transparent ducts and bounce back when destinations are full.
- Fluids are shared across the entire network as a single tank.
- Redstone control on all servos, retrievers, and filters (Disabled / Low / High).
- Route types: Nearest-First, Furthest-First, Random, Round Robin.
- Dense and Vacuum item ducts for path weight control.
- Long Range Viaducts for double-speed player transport. Linking Viaducts connect separate networks.

## Materials

The mod adds its own ingots and nuggets for metals used in recipes: Lead, Tin, Silver, Invar, Electrum, Bronze, Signalum, Enderium, and Lumium. All metals are tagged with the `c:ingots/<metal>` and `c:nuggets/<metal>` conventions, so any mod providing the same metals (Mekanism, Thermal Series, etc.) will work interchangeably.

Simple alloy recipes are included so the mod is fully playable standalone without other tech mods.

## Attribution

This mod is inspired by and based on the design of **Thermal Dynamics** by **Team CoFH** (Cult of the Full Hub).

- Original mod: [Thermal Dynamics](https://github.com/CoFH/ThermalDynamics-1.12-Legacy)
- Original textures: [Thermal Foundation](https://github.com/CoFH/ThermalFoundation-1.12-Legacy)

Some textures (ingots, nuggets, duct textures, GUI elements) are derived from Thermal Foundation and Thermal Dynamics, released under **Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)**.

The game logic is a clean-room reimplementation following the original mod's design and behavior. Per the CoFH "Don't Be a Jerk" license, this project maintains a visible public repository.

## License

Code is licensed under the **MIT License**. See [LICENSE](LICENSE).

Art and texture assets derived from CoFH projects remain under **CC BY-NC-SA 4.0**.

## Building

```
./gradlew build
```

## Dependencies

- Minecraft 1.21.1
- NeoForge 21.1+
- CodeChicken Lib
