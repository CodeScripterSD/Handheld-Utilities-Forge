package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.item.custom.HandheldFurnace;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.IItemHandler;

public class HandheldFurnaceMenu extends AbstractHandheldFurnaceMenu{
    public HandheldFurnaceMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        super(ModMenuTypes.HANDHELD_FURNACE.get(), RecipeType.SMELTING, pContainerId, pPlayerInventory);
    }

    public HandheldFurnaceMenu(int pContainerId, Inventory pPlayerInventory, IItemHandler itemHandler, ContainerData pBlastFurnaceData) {
        super(ModMenuTypes.HANDHELD_FURNACE.get(), RecipeType.SMELTING, pContainerId, pPlayerInventory, itemHandler, pBlastFurnaceData);
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player pPlayer) {
        return getHeldItem(pPlayer) != ItemStack.EMPTY;
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
        return stack.getItem() instanceof HandheldFurnace;
    }
}
