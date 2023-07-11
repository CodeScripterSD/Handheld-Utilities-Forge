package com.craftminerd.handheld_utilities.menu.slot;

import com.craftminerd.handheld_utilities.item.ModItems;
import com.craftminerd.handheld_utilities.menu.AbstractHandheldFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class HandheldFurnaceFuelSlot extends SlotItemHandler {
    private final AbstractHandheldFurnaceMenu menu;

    public HandheldFurnaceFuelSlot(AbstractHandheldFurnaceMenu menu, IItemHandler handler, int pSlot, int pX, int pY) {
        super(handler, pSlot, pX, pY);
        this.menu = menu;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack pStack) { return !(pStack.is(ModItems.HANDHELD_FURNACE.get())) && (this.menu.isFuel(pStack) || isBucket(pStack)); }

    public int getMaxStackSize(ItemStack pStack) {
        return isBucket(pStack) ? 1 : super.getMaxStackSize(pStack);
    }

    public static boolean isBucket(ItemStack pStack) {
        return pStack.is(Items.BUCKET);
    }
}
