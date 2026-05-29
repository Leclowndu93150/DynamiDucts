# v1.1.0

## Removed
- Removed Covers. The feature was incomplete and never quite worked right, so it's gone for now.

## Bug Fixes
- Fixed items extracted by servos/retrievers skipping the first pipe visually. The item now appears inside the pipe with the servo and travels through it like every other pipe in the network.
- Fixed a crash (ConcurrentModificationException) when fluiducts interact with certain modded tanks (e.g. Railcraft Water Tank). The crash occurred when toggling a servo on a fluiduct connected to a machine, or when connecting two duct segments while fluid was actively flowing.
- Fixed super-laminar fluiducts (opaque and clear) not pushing fluid into adjacent blocks. Fluid could enter the network from auto-ejecting machines but never came out the other side. Super-laminar fluiducts now behave like regular fluiducts with much higher throughput (16x base) instead of the broken infinite throughput.
- Fixed attachment placement on super-laminar fluiducts. They previously used the oversized "bronze-framed" hitbox shape meant for superconductor ducts. They now use a dedicated hitbox matched to the actual super-laminar model — same size as a regular duct, just expanded by half a pixel on each side to cover the bronze ring.

- Cleaned up connection visuals between ducts of the same family. Item ducts of any tier (basic, fast, dense, vacuum, energy, energy-fast) now connect to each other without showing the bronze connector band, matching Thermal Dynamics 1.12 behavior. Fluid ducts behave the same, except super-laminar ducts still display a band when joining a different tier. Fluxducts (energy ducts) intentionally keep their band on every join since they handle different transfer rates.
- Fixed wrenching, attachment placement and tooltips on viaducts and on the bronze-framed cryo/superconductor fluxducts. The interaction hitbox was previously the same as a normal duct's, which left huge dead zones around the wider arms. Each duct size now has its own matching hitbox, and the resolver prefers the connected side closest to where you clicked.

## Performance
- Replaced internal data structures across all grid types (fluid, item, energy, transport, structural) with faster alternatives, reducing iteration overhead during server ticks.
- Grid node snapshots are now cached and only rebuilt when the network changes, avoiding unnecessary allocations every tick.
- Mutations to duct networks that happen mid-tick (e.g. from external mod callbacks) are now safely deferred, preventing crashes without copying the entire node list each tick.
