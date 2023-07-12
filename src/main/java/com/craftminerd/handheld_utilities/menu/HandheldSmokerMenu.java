package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.item.ModItems;
import com.craftminerd.handheld_utilities.item.custom.HandheldAnvil;
import com.craftminerd.handheld_utilities.item.custom.HandheldSmoker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.IItemHandler;

public class HandheldSmokerMenu extends AbstractHandheldFurnaceMenu {
    public HandheldSmokerMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        super(ModMenuTypes.HANDHELD_SMOKER.get(), RecipeType.SMOKING, pContainerId, pPlayerInventory);
    }

    public HandheldSmokerMenu(int pContainerId, Inventory pPlayerInventory, IItemHandler handler, ContainerData pData) {
        super(ModMenuTypes.HANDHELD_SMOKER.get(), RecipeType.SMOKING, pContainerId, pPlayerInventory, handler, pData);
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player pPlayer) {
        return getHeldItem(pPlayer) != ItemStack.EMPTY;
    }

    private static ItemStack getHeldItem(Player player) {
        // Determine which held item is a handheld storage (if either)
        if (isHandheldSmoker(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (isHandheldSmoker(player.getOffhandItem())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static boolean isHandheldSmoker(ItemStack stack) {
        return stack.getItem() instanceof HandheldSmoker;
    }
}
