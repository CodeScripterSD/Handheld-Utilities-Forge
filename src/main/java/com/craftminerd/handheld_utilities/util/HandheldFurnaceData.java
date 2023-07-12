package com.craftminerd.handheld_utilities.util;

import com.craftminerd.handheld_utilities.item.custom.HandheldFurnaceItem;
import com.craftminerd.handheld_utilities.item.custom.HandheldStorageItem;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HandheldFurnaceData {
    private final UUID uuid;
    private final HandheldFurnaceItemHandler inventory;
    private final LazyOptional<IItemHandler> optional;
    private final ContainerData data;
    private final NonNullList<Integer> storedData = NonNullList.withSize(4, 0);
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    public final Metadata meta = new Metadata();

    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {
        if (pRecipe != null) {
            ResourceLocation resourcelocation = pRecipe.getId();
            this.recipesUsed.addTo(resourcelocation, 1);
        }

    }

    private static ItemStack getHeldItem(Player player) {
        // Determine which held item is a handheld storage (if either)
        if (isHandheldFurnace(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (isHandheldFurnace(player.getOffhandItem())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static boolean isHandheldFurnace(ItemStack stack) {
        return stack.getItem() instanceof HandheldFurnaceItem;
    }

    public static void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        ItemStack heldItem = getHeldItem(player);
        if (heldItem.isEmpty()) return;
        HandheldFurnaceData data = HandheldFurnaceItem.getData(heldItem);
        List<Recipe<?>> list = data.getRecipesToAwardAndPopExperience(player.getLevel(), player.position(), data);
        player.awardRecipes(list);
        data.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec, HandheldFurnaceData data) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : data.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((p_155023_) -> {
                list.add(p_155023_);
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((AbstractCookingRecipe)p_155023_).getExperience());
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel pLevel, Vec3 pPopVec, int pRecipeIndex, float pExperience) {
        int i = Mth.floor((float)pRecipeIndex * pExperience);
        float f = Mth.frac((float)pRecipeIndex * pExperience);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(pLevel, pPopVec, i);
    }

    public boolean isLit() {
        return storedData.get(0) > 0;
    }

    public ContainerData getData() {
        return data;
    }

    public NonNullList<Integer> getStoredData() {
        return this.storedData;
    }

    public LazyOptional<IItemHandler> getOptional() {
        return this.optional;
    }

    public HandheldFurnaceItemHandler getHandler() {
        return this.inventory;
    }

    public void updateAccessRecords(String player, long time) {
        if (this.meta.firstAccessedTime == 0) {
            // new furnace, create access data
            this.meta.firstAccessedTime = time;
            this.meta.firstAccessedPlayer = player;
        }

        this.meta.setLastAccessedTime(time);
        this.meta.setLastAccessedPlayer(player);
    }

    public HandheldFurnaceData(UUID uuid) {
        this.uuid= uuid;
        this.inventory = new HandheldFurnaceItemHandler(3);
        this.optional = LazyOptional.of(() -> this.inventory);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return storedData.get(pIndex);
            }

            @Override
            public void set(int pIndex, int pValue) {
                storedData.set(pIndex, pValue);
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public HandheldFurnaceData(UUID uuid, CompoundTag incomingNBT) {
        this.uuid = uuid;

        this.inventory = new HandheldFurnaceItemHandler(9);

        // Fixes FTBTeam/FTB-Modpack-Issues #478 Part 2
        if (incomingNBT.getCompound("Inventory").contains("Size")) {
            if (incomingNBT.getCompound("Inventory").getInt("Size") != 3)
                incomingNBT.getCompound("Inventory").putInt("Size", 3);
        }
        this.inventory.deserializeNBT(incomingNBT.getCompound("Inventory"));
        this.optional = LazyOptional.of(() -> this.inventory);
        CompoundTag stored_data = incomingNBT.getCompound("stored_data");
        storedData.set(0, stored_data.getInt("litTime"));
        storedData.set(1, stored_data.getInt("litDuration"));
        storedData.set(2, stored_data.getInt("cookingProgress"));
        storedData.set(3, stored_data.getInt("cookingTotalTime"));
        CompoundTag recipesUsed = incomingNBT.getCompound("RecipesUsed");
        for (String s : recipesUsed.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(s), recipesUsed.getInt(s));
        }
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return storedData.get(pIndex);
            }

            @Override
            public void set(int pIndex, int pValue) {
                storedData.set(pIndex, pValue);
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        if (incomingNBT.contains("Metadata"))
            this.meta.deserializeNBT(incomingNBT.getCompound("Metadata"));
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static Optional<HandheldFurnaceData> fromNBT(CompoundTag nbt) {
        if (nbt.contains("UUID")) {
            UUID uuid = nbt.getUUID("UUID");
            return Optional.of(new HandheldFurnaceData(uuid, nbt));
        }
        return Optional.empty();
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putUUID("UUID", this.uuid);

        nbt.put("Inventory", this.inventory.serializeNBT());

        CompoundTag stored_data = new CompoundTag();
        stored_data.putInt("litTime", storedData.get(0));
        stored_data.putInt("litDuration", storedData.get(1));
        stored_data.putInt("cookingProgress", storedData.get(2));
        stored_data.putInt("cookingTotalTime", storedData.get(3));

        CompoundTag recipesUsed = new CompoundTag();
        this.recipesUsed.forEach((recipeLocation, amount) -> {
            recipesUsed.putInt(recipeLocation.toString(), amount);
        });
        nbt.put("RecipesUsed", recipesUsed);

        nbt.put("stored_data", stored_data);

        nbt.put("Metadata", this.meta.serializeNBT());

        return nbt;
    }

    public boolean canBurn(Recipe<?> recipe, HandheldFurnaceItemHandler itemHandler, int pStackSize) {
        if (!itemHandler.getStackInSlot(0).isEmpty() && recipe != null) {
            ItemStack itemstack = recipe.getResultItem();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = itemHandler.getStackInSlot(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.sameItem(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= pStackSize && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    public boolean burn(Recipe<?> recipe, HandheldFurnaceItemHandler itemHandler, int i) {
        if (recipe != null && this.canBurn(recipe, itemHandler, i)) {
            ItemStack itemstack = itemHandler.getStackInSlot(0);
            ItemStack itemstack1 = recipe.getResultItem();
            ItemStack itemstack2 = itemHandler.getStackInSlot(2);
            if (itemstack2.isEmpty()) {
                itemHandler.setStackInSlot(2, itemstack1.copy());
            } else if (itemstack2.is(itemstack1.getItem())) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (itemstack.is(Blocks.WET_SPONGE.asItem()) && !itemHandler.getStackInSlot(1).isEmpty() && itemHandler.getStackInSlot(1).is(Items.BUCKET)) {
                itemHandler.setStackInSlot(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    public Integer getTotalCookTime(Level pLevel, RecipeType<? extends AbstractCookingRecipe> smelting, HandheldFurnaceItemHandler itemHandler) {
        SimpleContainer simpleContainer = new SimpleContainer(3);
        for (int i = 0; i < simpleContainer.getContainerSize(); i++) {
            simpleContainer.setItem(i, itemHandler.getStackInSlot(i));
        }
        return pLevel.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>)smelting, simpleContainer, pLevel).map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }

    public Integer getBurnDuration(ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return 0;
        } else {
            Item item = itemstack.getItem();
            return ForgeHooks.getBurnTime(itemstack, RecipeType.SMELTING);
        }
    }



    public static class Metadata implements INBTSerializable<CompoundTag> {
        private String firstAccessedPlayer = "";

        private long firstAccessedTime = 0;
        private String lastAccessedPlayer = "";
        private long lastAccessedTime = 0;

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public void setLastAccessedPlayer(String lastAccessedPlayer) {
            this.lastAccessedPlayer = lastAccessedPlayer;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            nbt.putString("firstPlayer", this.firstAccessedPlayer);
            nbt.putLong("firstTime", this.firstAccessedTime);
            nbt.putString("lastPlayer", this.lastAccessedPlayer);
            nbt.putLong("lastTime", this.lastAccessedTime);

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.firstAccessedPlayer = nbt.getString("firstPlayer");
            this.firstAccessedTime = nbt.getLong("firstTime");
            this.lastAccessedPlayer = nbt.getString("lastPlayer");
            this.lastAccessedTime = nbt.getLong("lastTime");
        }
    }
}
