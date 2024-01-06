package com.craftminerd.handheld_utilities.recipe;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class HandheldEnchantingTableAddBookshelfRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    final String group;
    final ItemStack toUpgrade;
    final ItemStack toConsume;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public HandheldEnchantingTableAddBookshelfRecipe(ResourceLocation pId, String pGroup, ItemStack toUpgrade, ItemStack toConsume) {
        this.id = pId;
        this.group = pGroup;
        this.toUpgrade = toUpgrade;
        this.toConsume = toConsume;
        this.ingredients = NonNullList.of(Ingredient.EMPTY, Ingredient.of(toConsume), Ingredient.of(toUpgrade));
        this.isSimple = this.ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return new HandheldEnchantingTableAddBookshelfRecipe.Serializer();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        ItemStack result = this.toUpgrade.copy();
        result.getOrCreateTag().putInt("enchantPower" ,1);
        return result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    // Allow if the item toUpgrade does not have 15 bookshelves and the amount of bookshelves plus the amount of bookshelves on the item is not greater than 15
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        StackedContents stackedcontents = new StackedContents();
        int countHandheldEnchatingTable = 0;
        int countBookshelf = 0;
        ItemStack upgradeItem = ItemStack.EMPTY;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack = pInv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
            }
            if (itemstack.is(toConsume.getItem())) {
                countBookshelf++;
            }
            else if (itemstack.is(toUpgrade.getItem())) {
                countHandheldEnchatingTable++;
                upgradeItem = itemstack;
            } else if (!itemstack.isEmpty()) {
                return false;
            }
        }
        CompoundTag nbt = upgradeItem.getOrCreateTag();
        if (nbt.getInt("enchantPower") + countBookshelf <= 15) {
            return countHandheldEnchatingTable == 1 && countBookshelf >= 1;
        }

        return false;
    }

    public ItemStack assemble(CraftingContainer pInv) {
        int countBookshelf = 0;
        ItemStack upgradeItem = ItemStack.EMPTY;
        for (int i = 0; i < pInv.getContainerSize(); i++) {
            if (pInv.getItem(i).is(this.toUpgrade.getItem())) {
                upgradeItem = pInv.getItem(i).copy();
                upgradeItem.setCount(1);
            } else if (pInv.getItem(i).is(toConsume.getItem())) {
                countBookshelf++;
            }
        }
        CompoundTag nbt = upgradeItem.getOrCreateTag();
        int eP = nbt.getInt("enchantPower");
        nbt.putInt("enchantPower", eP+countBookshelf);

        return upgradeItem;
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= this.ingredients.size();
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<HandheldEnchantingTableAddBookshelfRecipe> {
        private static final ResourceLocation NAME = new ResourceLocation(HandheldUtilities.MOD_ID, "handheld_enchanting_table_add_bookshelf");
        public HandheldEnchantingTableAddBookshelfRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            ItemStack toUpgrade = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "upgrading"));
            ItemStack toConsume = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "consuming"));
            if (toConsume.isEmpty() || toUpgrade.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (toUpgrade.getCount() + toConsume.getCount() > 3*3 || toUpgrade.getCount() > 1) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is " + (3 * 3));
            } else {
//                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
                return new HandheldEnchantingTableAddBookshelfRecipe(pRecipeId, s, toUpgrade, toConsume);
            }
        }

        public HandheldEnchantingTableAddBookshelfRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String s = pBuffer.readUtf();

            ItemStack itemstack = pBuffer.readItem();
            ItemStack itemstack1 = pBuffer.readItem();
            return new HandheldEnchantingTableAddBookshelfRecipe(pRecipeId, s, itemstack, itemstack1);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, HandheldEnchantingTableAddBookshelfRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.group);

            pBuffer.writeItem(pRecipe.toUpgrade);
            pBuffer.writeItem(pRecipe.toConsume);
        }
    }
}
