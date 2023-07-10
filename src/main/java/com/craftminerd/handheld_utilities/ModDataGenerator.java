package com.craftminerd.handheld_utilities;

import com.craftminerd.handheld_utilities.conditions.ModConfigCondition;
import com.craftminerd.handheld_utilities.config.ModCommonConfigs;
import com.craftminerd.handheld_utilities.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = HandheldUtilities.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        if (event.includeServer()) {
            gen.addProvider(new Recipes(gen));
        }
    }

    public static class Recipes extends RecipeProvider implements IConditionBuilder {
        public Recipes(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {

            ConditionalRecipe.builder()
                .addCondition(
                        new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_CRAFTER_RECIPE)
                )
                .addRecipe(
                        ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_CRAFTER.get(), 1)
                                .requires(Items.CRAFTING_TABLE)
                                .unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))
                                ::save
                )
                .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_crafting_table"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_SMITHING_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_SMITHING.get(), 1)
                                    .requires(Items.SMITHING_TABLE)
                                    .unlockedBy("has_smithing_table", has(Items.SMITHING_TABLE))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_smithing_table"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_STONECUTTER_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_STONECUTTER.get(), 1)
                                    .requires(Items.STONECUTTER)
                                    .unlockedBy("has_stonecutter", has(Items.STONECUTTER))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_stonecutter"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_LOOM_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_LOOM.get(), 1)
                                    .requires(Items.LOOM)
                                    .unlockedBy("has_loom", has(Items.LOOM))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_loom"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_CHEST_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_CHEST.get(), 1)
                                    .requires(Items.CHEST)
                                    .unlockedBy("has_chest", has(Items.CHEST))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_chest"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_GRINDSTONE_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_GRINDSTONE.get(), 1)
                                    .requires(Items.GRINDSTONE)
                                    .unlockedBy("has_grindstone", has(Items.GRINDSTONE))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_grindstone"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_ETABLE_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_ETABLE.get(), 1)
                                    .requires(Items.ENCHANTING_TABLE)
                                    .unlockedBy("has_enchanting_table", has(Items.ENCHANTING_TABLE))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_etable"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_ECHEST_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_ECHEST.get(), 1)
                                    .requires(Items.ENDER_CHEST)
                                    .unlockedBy("has_enderchest", has(Items.ENDER_CHEST))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_echest"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_ANVIL_RECIPES)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_ANVIL.get(), 1)
                                    .requires(Items.ANVIL)
                                    .unlockedBy("has_anvil", has(Items.ANVIL))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_anvil"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_ANVIL_RECIPES)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_CHIPPED_ANVIL.get(), 1)
                                    .requires(Items.CHIPPED_ANVIL)
                                    .unlockedBy("has_chipped_anvil", has(Items.CHIPPED_ANVIL))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_chipped_anvil"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_ANVIL_RECIPES)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_DAMAGED_ANVIL.get(), 1)
                                    .requires(Items.DAMAGED_ANVIL)
                                    .unlockedBy("has_damaged_anvil", has(Items.CHIPPED_ANVIL))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_damaged_anvil"));

            ConditionalRecipe.builder()
                    .addCondition(
                            new ModConfigCondition(ModCommonConfigs.ENABLE_HANDHELD_FURNACE_RECIPE)
                    )
                    .addRecipe(
                            ShapelessRecipeBuilder.shapeless(ModItems.HANDHELD_FURNACE.get(), 1)
                                    .requires(Items.FURNACE.asItem())
                                    .unlockedBy("has_furnace", has(Items.FURNACE.asItem()))
                                    ::save
                    )
                    .build(pFinishedRecipeConsumer, new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_furnace"));
        }
    }
}
