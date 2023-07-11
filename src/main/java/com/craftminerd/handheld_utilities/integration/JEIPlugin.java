package com.craftminerd.handheld_utilities.integration;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.craftminerd.handheld_utilities.item.ModItems;
import com.craftminerd.handheld_utilities.menu.HandheldBlastFurnaceMenu;
import com.craftminerd.handheld_utilities.menu.HandheldFurnaceMenu;
import com.craftminerd.handheld_utilities.menu.HandheldSmokerMenu;
import com.craftminerd.handheld_utilities.screen.HandheldBlastFurnaceScreen;
import com.craftminerd.handheld_utilities.screen.HandheldFurnaceScreen;
import com.craftminerd.handheld_utilities.screen.HandheldSmokerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(HandheldUtilities.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModItems.HANDHELD_CRAFTER.get().getDefaultInstance(), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(ModItems.HANDHELD_SMITHING.get().getDefaultInstance(), RecipeTypes.SMITHING);
        registration.addRecipeCatalyst(ModItems.HANDHELD_STONECUTTER.get().getDefaultInstance(), RecipeTypes.STONECUTTING);
        registration.addRecipeCatalyst(ModItems.HANDHELD_ANVIL.get().getDefaultInstance(), RecipeTypes.ANVIL);

        registration.addRecipeCatalyst(ModItems.HANDHELD_FURNACE.get().getDefaultInstance(), RecipeTypes.SMELTING, RecipeTypes.FUELING);
        registration.addRecipeCatalyst(ModItems.HANDHELD_BLAST_FURNACE.get().getDefaultInstance(), RecipeTypes.BLASTING, RecipeTypes.FUELING);
        registration.addRecipeCatalyst(ModItems.HANDHELD_SMOKER.get().getDefaultInstance(), RecipeTypes.SMOKING, RecipeTypes.FUELING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HandheldFurnaceScreen.class, 78, 32, 28, 23, RecipeTypes.SMELTING, RecipeTypes.FUELING);
        registration.addRecipeClickArea(HandheldBlastFurnaceScreen.class, 78, 32, 28, 23, RecipeTypes.BLASTING, RecipeTypes.FUELING);
        registration.addRecipeClickArea(HandheldSmokerScreen.class, 78, 32, 28, 23, RecipeTypes.SMOKING, RecipeTypes.FUELING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(HandheldFurnaceMenu.class, RecipeTypes.FUELING, 1, 1, 3, 36);
        registration.addRecipeTransferHandler(HandheldBlastFurnaceMenu.class, RecipeTypes.FUELING, 1, 1, 3, 36);
        registration.addRecipeTransferHandler(HandheldSmokerMenu.class, RecipeTypes.FUELING, 1, 1, 3, 36);

        registration.addRecipeTransferHandler(HandheldFurnaceMenu.class, RecipeTypes.SMELTING, 0, 1, 3, 36);
        registration.addRecipeTransferHandler(HandheldBlastFurnaceMenu.class, RecipeTypes.BLASTING, 0, 1, 3, 36);
        registration.addRecipeTransferHandler(HandheldSmokerMenu.class, RecipeTypes.SMOKING, 0, 1, 3, 36);
    }
}
