package com.craftminerd.handheld_utilities.config;

import com.craftminerd.handheld_utilities.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ModCommonConfigs {

    public static @Nonnull Collection<ItemStack> getDisabledItems() {
        Collection<ItemStack> disabledItems = new java.util.ArrayList<>();
        if (!ENABLE_HANDHELD_CRAFTER_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_CRAFTER.get()));
        }
        if (!ENABLE_HANDHELD_SMITHING_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_SMITHING.get()));
        }
        if (!ENABLE_HANDHELD_STONECUTTER_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_STONECUTTER.get()));
        }
        if (!ENABLE_HANDHELD_LOOM_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_LOOM.get()));
        }
        if (!ENABLE_HANDHELD_CHEST_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_CHEST.get()));
        }
        if (!ENABLE_HANDHELD_GRINDSTONE_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_GRINDSTONE.get()));
        }
        if (!ENABLE_HANDHELD_ETABLE_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_ETABLE.get()));
        }
        if (!ENABLE_HANDHELD_ECHEST_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_ECHEST.get()));
        }
        if (!ENABLE_HANDHELD_ANVIL_RECIPES.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_ANVIL.get()));
            disabledItems.add(new ItemStack(ModItems.HANDHELD_CHIPPED_ANVIL.get()));
            disabledItems.add(new ItemStack(ModItems.HANDHELD_DAMAGED_ANVIL.get()));
        }
        if (!ENABLE_HANDHELD_FURNACE_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_FURNACE.get()));
        }
        if (!ENABLE_HANDHELD_BLAST_FURNACE_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_BLAST_FURNACE.get()));
        }
        if (!ENABLE_HANDHELD_SMOKER_RECIPE.get()) {
            disabledItems.add(new ItemStack(ModItems.HANDHELD_SMOKER.get()));
        }
        return disabledItems;
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_HARD_RECIPES;

    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_CRAFTER_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_SMITHING_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_STONECUTTER_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_LOOM_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_CHEST_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_GRINDSTONE_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_ETABLE_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_ECHEST_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_ANVIL_RECIPES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_FURNACE_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_BLAST_FURNACE_RECIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HANDHELD_SMOKER_RECIPE;

    static {
        BUILDER.push("Items");

        ENABLE_HARD_RECIPES = BUILDER.comment("Enable harder recipes for all items").define("Harder Recipes Enabled", false);

        ENABLE_HANDHELD_CRAFTER_RECIPE = BUILDER.comment("Enable the recipe for Handheld Crafting Table").define("Handheld Crafting Table recipe enabled", true);
        ENABLE_HANDHELD_SMITHING_RECIPE = BUILDER.comment("Enable the recipe for Handheld Smithing Table").define("Handheld Smithing Table recipe enabled", true);
        ENABLE_HANDHELD_STONECUTTER_RECIPE = BUILDER.comment("Enable the recipe for Handheld Stonecutter").define("Handheld Stonecutter recipe enabled", true);
        ENABLE_HANDHELD_LOOM_RECIPE = BUILDER.comment("Enable the recipe for Handheld Loom").define("Handheld Loom recipe enabled", true);
        ENABLE_HANDHELD_CHEST_RECIPE = BUILDER.comment("Enable the recipe for Handheld Chest").define("Handheld Chest recipe enabled", true);
        ENABLE_HANDHELD_GRINDSTONE_RECIPE = BUILDER.comment("Enable the recipe for Handheld Grindstone").define("Handheld Grindstone recipe enabled", true);
        ENABLE_HANDHELD_ETABLE_RECIPE = BUILDER.comment("Enable the recipe for Handheld Enchanting Table").define("Handheld Enchanting Table recipe enabled", true);
        ENABLE_HANDHELD_ECHEST_RECIPE = BUILDER.comment("Enable the recipe for Handheld Ender Chest").define("Handheld Ender Chest recipe enabled", true);
        ENABLE_HANDHELD_ANVIL_RECIPES = BUILDER.comment("Enable the recipe for Handheld Anvils").define("Handheld Anvil recipes enabled", true);
        ENABLE_HANDHELD_FURNACE_RECIPE = BUILDER.comment("Enable the recipe for Handheld Furnace").define("Handheld Furnace recipe enabled", true);
        ENABLE_HANDHELD_BLAST_FURNACE_RECIPE = BUILDER.comment("Enable the recipe for Handheld Blast Furnace").define("Handheld Blast Furnace recipe enabled", true);
        ENABLE_HANDHELD_SMOKER_RECIPE = BUILDER.comment("Enable the recipe for Handheld Smoker").define("Handheld Smoker recipe enabled", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
