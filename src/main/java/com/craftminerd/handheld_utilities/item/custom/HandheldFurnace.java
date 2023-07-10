package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.menu.HandheldFurnaceMenu;
import com.craftminerd.handheld_utilities.menu.HandheldStorageMenu;
import com.craftminerd.handheld_utilities.util.*;
import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

import java.util.UUID;

public class HandheldFurnace extends Item {
    public HandheldFurnace(Properties pProperties) {
        super(pProperties);
    }

    public int getInventorySize(ItemStack stack) {
        return 3;
    }

    public IItemHandler getInventory(ItemStack stack) {
        if(stack.isEmpty())
            return null;
        ItemStackHandler stackHandler = new ItemStackHandler(getInventorySize(stack));
        stackHandler.deserializeNBT(stack.getOrCreateTag().getCompound("inventory"));
        return stackHandler;
    }

    public static HandheldFurnaceData getData(ItemStack stack) {

        UUID uuid;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("UUID")) {
            uuid = UUID.randomUUID();
            tag.putUUID("UUID", uuid);
        } else
            uuid = tag.getUUID("UUID");
        return HandheldFurnaceManager.get().getOrCreateHandheldFurnace(uuid);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide() && pPlayer.getItemInHand(pUsedHand).getItem() instanceof HandheldFurnace) {
            Component component = new TranslatableComponent("container.furnace");
            if (!pPlayer.getItemInHand(pUsedHand).getDisplayName().equals(this.getDefaultInstance().getDisplayName())) {
                component = pPlayer.getItemInHand(pUsedHand).getDisplayName();
            }
            ItemStack handheld_furnace = pPlayer.getItemInHand(pUsedHand);
            HandheldFurnaceData data = HandheldFurnace.getData(handheld_furnace);
            NetworkHooks.openGui(((ServerPlayer) pPlayer), new SimpleMenuProvider( (windowId, playerInventory, playerEntity) ->
                    new HandheldFurnaceMenu(windowId, playerInventory, data.getHandler(), data.getData()), component));
            pPlayer.awardStat(Stats.INTERACT_WITH_FURNACE);
            return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide()) return;
        HandheldFurnaceData data = getData(pStack);
        HandheldFurnaceItemHandler itemHandler = data.getHandler();
        if (data.isLit()) {
            data.getStoredData().set(0, data.getStoredData().get(0) - 1);
            HandheldFurnaceManager.get().setDirty();
        }

        ItemStack itemstack = itemHandler.getStackInSlot(1);
        if (data.isLit() || (!itemstack.isEmpty() && !itemHandler.getStackInSlot(0).isEmpty())) {
            SimpleContainer inv = new SimpleContainer(3);
            for (int i = 0; i < inv.getContainerSize(); i++) {
                inv.setItem(i, itemHandler.getStackInSlot(i));
            }
            Recipe<?> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inv, pLevel).orElse(null);
            int i = 64;
            if (!data.isLit() && data.canBurn(recipe, itemHandler, i)) {
                data.getStoredData().set(0, data.getBurnDuration(itemstack));
                data.getStoredData().set(1, data.getStoredData().get(0));
                HandheldFurnaceManager.get().setDirty();
                if (data.isLit()) {
                    if (itemstack.hasContainerItem()) {
                        itemHandler.setStackInSlot(1, itemstack.getContainerItem());
                    } else if (!itemstack.isEmpty()) {
                        Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            itemHandler.setStackInSlot(1, itemstack.getContainerItem());
                        }
                    }
                }
            }

            if (data.isLit() && data.canBurn(recipe, itemHandler, i)) {
                data.getStoredData().set(2, data.getStoredData().get(2)+1);
                HandheldFurnaceManager.get().setDirty();
                if (data.getStoredData().get(3) != 0 && data.getStoredData().get(2)-1 == data.getStoredData().get(3)) {
                    data.getStoredData().set(2, 0);
                    data.getStoredData().set(3, data.getTotalCookTime(pLevel, RecipeType.SMELTING, itemHandler));
                    HandheldFurnaceManager.get().setDirty();
                    if (data.burn(recipe, itemHandler, i)) {

                    }
                }
            } else {
                data.getStoredData().set(2, 0);
                HandheldFurnaceManager.get().setDirty();
            }
        } else if (!data.isLit() && data.getStoredData().get(2) > 0) {
            data.getStoredData().set(2, Mth.clamp(data.getStoredData().get(2) - 2, 0, data.getStoredData().get(3)));
            HandheldFurnaceManager.get().setDirty();
        }

    }
}
