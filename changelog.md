# v1.1.0

## Bug Fixes
- Fixed a crash (ConcurrentModificationException) when fluiducts interact with certain modded tanks (e.g. Railcraft Water Tank). The crash occurred when toggling a servo on a fluiduct connected to a machine, or when connecting two duct segments while fluid was actively flowing.
- Fixed super-laminar fluiducts (opaque and clear) not pushing fluid into adjacent blocks. Fluid could enter the network from auto-ejecting machines but never came out the other side. This was caused by the very high throughput value overflowing once two or more ducts were joined.
- Fixed attachment placement on super-laminar fluiducts. The clickable area for placing servos/retrievers/etc. now correctly aligns with the connected faces of the duct instead of inverting onto unconnected faces.

## Performance
- Replaced internal data structures across all grid types (fluid, item, energy, transport, structural) with faster alternatives, reducing iteration overhead during server ticks.
- Grid node snapshots are now cached and only rebuilt when the network changes, avoiding unnecessary allocations every tick.
- Mutations to duct networks that happen mid-tick (e.g. from external mod callbacks) are now safely deferred, preventing crashes without copying the entire node list each tick.
