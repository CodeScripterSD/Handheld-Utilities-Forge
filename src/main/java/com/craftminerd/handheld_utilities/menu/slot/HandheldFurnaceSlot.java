package com.craftminerd.handheld_utilities.menu.slot;

import com.craftminerd.handheld_utilities.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class HandheldFurnaceSlot extends SlotItemHandler {
    public HandheldFurnaceSlot(IItemHandler handler, int pSlot, int pX, int pY) {
        super(handler, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return !pStack.is(ModItems.HANDHELD_FURNACE.get()) || !pStack.is(ModItems.HANDHELD_CHEST.get());
    }
}
