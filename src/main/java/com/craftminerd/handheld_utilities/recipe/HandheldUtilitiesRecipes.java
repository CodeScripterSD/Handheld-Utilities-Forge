package com.craftminerd.handheld_utilities.recipe;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HandheldUtilitiesRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HandheldUtilities.MOD_ID);

    public static final RegistryObject<RecipeSerializer<HandheldFurnaceUpgradeRecipe>> HANDHELD_FURNACE_UPGRADE_RECIPE_SERIALIZER = SERIALIZERS.register("handheld_furnace_upgrade",
            () -> HandheldFurnaceUpgradeRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<HandheldEnchantingTableAddBookshelfRecipe>> HANDHELD_ENCHANTING_TABLE_ADD_BOOKSHELF_RECIPE_SERIALIZER = SERIALIZERS.register("handheld_enchanting_table_add_bookshelf",
            () -> HandheldEnchantingTableAddBookshelfRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) { SERIALIZERS.register(eventBus); }
}
