package com.leclowndu93150.modular_networks.recipe;

import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNItems;
import com.leclowndu93150.modular_networks.init.MNRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CoverRecipe extends CustomRecipe {

    public CoverRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasStructural = false;
        boolean hasBlock = false;
        int count = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            count++;

            if (stack.is(MNBlocks.STRUCTURAL_DUCT.asItem())) {
                if (hasStructural) return false;
                hasStructural = true;
            } else if (stack.getItem() instanceof BlockItem) {
                if (hasBlock) return false;
                hasBlock = true;
            } else {
                return false;
            }
        }
        return hasStructural && hasBlock && count == 2;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(MNItems.COVER.get(), 6);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MNRecipeSerializers.COVER_RECIPE.get();
    }
}
