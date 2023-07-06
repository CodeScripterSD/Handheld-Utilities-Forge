package com.craftminerd.handheld_utilities.integration;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.craftminerd.handheld_utilities.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
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

//        IModPlugin.super.registerRecipeCatalysts(registration);
    }
}
