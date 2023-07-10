package com.craftminerd.handheld_utilities.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

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

    static {
        BUILDER.push("Items");

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

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
