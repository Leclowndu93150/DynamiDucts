# v1.1.1

## Bug Fixes
- Fixed fluid filters doing nothing. Whitelists and blacklists on fluid filters are now applied when fluid leaves the network, the same way item filters already worked.
- Fixed fluid servos and retrievers ignoring their whitelist on machines with several tanks. They used to look only at the first fluid the machine offered and give up if it didn't match. Every tank is now checked against the filter.
- Fixed the "Match Components" toggle being ignored for fluids.
- Fixed fluid servos and retrievers being able to duplicate fluid when the source tank handed over less than it promised.
- Fixed servos, retrievers, filters and relays being deleted when the duct they were on got broken. They now drop as items.
- Fixed ducts stopping working when connected to multiblocks (Immersive Engineering coke ovens and blast furnaces, Create fluid tanks, etc.) until an attachment was broken and replaced. This happened because multiblocks reform when their chunk loads. Ducts now notice when a neighboring block's inventory, tank or energy storage appears, changes or goes away, and reconnect on their own.
- Fixed fluxducts not connecting to Immersive Engineering multiblocks.
- Fixed ducts at chunk borders continuing to push into machines in unloaded chunks, and not reconnecting when those chunks loaded again.
- Fixed a single fluxduct duplicating energy (issue #12). A fluxduct touching more than one receiver could hand out more energy than it held, outputting its full transfer rate from a generator producing a fraction of that.
- Fixed fluxducts pushing energy back into the generator that was feeding them.
- Fixed fluxduct networks losing stored energy when two networks merged, and duplicating energy when part of a network unloaded and reloaded.
- Fixed a single Cryo-Stabilized Fluxduct not transferring any energy (issue #11). Cryo-Stabilized Fluxducts also now accept energy pushed into them, so Mekanism cables and energy cubes set to push work with them.

## Balance
- Long-Range Viaducts now behave like in Thermal Dynamics 1.12 (issue #10):
  - They only connect to other Long-Range Viaducts or Linking Viaducts.
  - They can no longer be used as entrances or exits. Existing Long-Range entrances can be cleared with a wrench.
  - They can't branch. A Long-Range Viaduct with more than two possible connections connects to nothing, so T and X junctions need a Linking Viaduct in between.
