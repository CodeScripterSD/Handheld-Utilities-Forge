package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.menu.HandheldBlastFurnaceMenu;
import com.craftminerd.handheld_utilities.menu.HandheldSmokerMenu;
import com.craftminerd.handheld_utilities.util.HandheldFurnaceData;
import com.craftminerd.handheld_utilities.util.HandheldFurnaceItemHandler;
import com.craftminerd.handheld_utilities.util.HandheldFurnaceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class HandheldSmoker extends HandheldFurnaceItem {
    public HandheldSmoker(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide() && pPlayer.getItemInHand(pUsedHand).getItem() instanceof HandheldFurnaceItem) {
            ItemStack handheld_furnace = pPlayer.getItemInHand(pUsedHand);
            HandheldFurnaceData data = HandheldFurnaceItem.getData(handheld_furnace);

            NetworkHooks.openGui(((ServerPlayer) pPlayer), new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                    new HandheldSmokerMenu(windowId, playerInventory, data.getHandler(), data.getData()), handheld_furnace.getHoverName()));

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
            data.getStoredData().set(3, data.getTotalCookTime(pLevel, RecipeType.SMOKING, itemHandler));
            HandheldFurnaceManager.get().setDirty();
            SimpleContainer inv = new SimpleContainer(3);
            for (int i = 0; i < inv.getContainerSize(); i++) {
                inv.setItem(i, itemHandler.getStackInSlot(i));
            }
            Recipe<?> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeType.SMOKING, inv, pLevel).orElse(null);
            int i = 64;
            if (!data.isLit() && data.canBurn(recipe, itemHandler, i)) {
                data.getStoredData().set(0, data.getBurnDuration(itemstack) / 2);
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
                data.getStoredData().set(2, data.getStoredData().get(2) + 1);
                HandheldFurnaceManager.get().setDirty();
                if (data.getStoredData().get(3) != 0 && data.getStoredData().get(2).equals(data.getStoredData().get(3))) {
                    data.getStoredData().set(2, 0);
                    data.getStoredData().set(3, data.getTotalCookTime(pLevel, RecipeType.SMOKING, itemHandler));
                    HandheldFurnaceManager.get().setDirty();
                    if (data.burn(recipe, itemHandler, i)) {
                        data.setRecipeUsed(recipe);
                        HandheldFurnaceManager.get().setDirty();
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
