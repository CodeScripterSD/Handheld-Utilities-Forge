package com.craftminerd.handheld_utilities.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;
import java.util.UUID;

public class HandheldFurnaceData {
    private final UUID uuid;
    private final HandheldFurnaceItemHandler inventory;
    private final LazyOptional<IItemHandler> optional;
    private final ContainerData data;
    private final NonNullList<Integer> storedData = NonNullList.withSize(4, 0);
    public final Metadata meta = new Metadata();

    public boolean isLit() {
        return storedData.get(0) + 1 > 1;
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
