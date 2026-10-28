# v1.2.0

## Changes
- Added support for Minecraft 26.1.2 (NeoForge). Immersive Engineering has no 26.1.2 release, so the Diesel Generator fix only applies to 1.21.1.
- CodeChicken Lib is no longer required.

## Bug Fixes
- Fixed fluid retrievers filling the duct partway and then stalling without delivering anything. They could drain their own tank, and the fluid they pulled was pushed straight back into the tank it came from. Retrievers now skip tanks that have a retriever on them and send what they pull directly into their own tank.
- Fixed fluxducts not connecting to the top of the Immersive Engineering Diesel Generator. The generator pushes power into the three blocks above its back row but reports no energy storage on that face, so ducts there never showed a connection.
- Fixed round robin servos and retrievers sending far more items to some destinations than others. The rotation was shared by every servo on the network and advanced even when nothing was sent, and a destination that couldn't take the item passed its turn to the next one. Each servo and retriever now keeps its own rotation and continues from the destination it last used.
- Fixed item routes being listed twice when a network mixed duct types with different path weights, which gave those destinations extra turns.
- Fixed items being deleted when a machine pushed a stack into an itemduct that could only accept part of it.
- Fixed fluxduct and fluiduct networks losing most of their stored energy or fluid when the world or chunk reloaded.
- Fixed fluiduct networks losing fluid when two networks merged.
- Fixed item retrievers deleting items when there was no route back to the retriever.
- Fixed items traveling through itemducts being lost when the world closed or their chunk unloaded. They now continue on their route after loading. Items that were already in transit in a world saved with an older version are still lost.
- Fixed the Information and Redstone Control tabs on servo, retriever and filter screens being able to open at the same time and overlap. Tabs now open and close when clicked, opening one closes the other, and the tab below moves down instead of being covered.
- Fixed those tabs opening almost instantly at high frame rates. They now take the same time to open at any frame rate.
- Fixed the Viaduct destination list scrollbar not being draggable.
- Fixed Viaduct screens darkening the world behind them twice.
- Fixed Viaduct textures being wrong after reloading resource packs.
- Fixed duct models sometimes being built from two threads at the same time.
- Fixed a crash when another mod inserted items into an itemduct without giving a side.
- Added the missing name for the DynamiDucts entry in the Jade config.
