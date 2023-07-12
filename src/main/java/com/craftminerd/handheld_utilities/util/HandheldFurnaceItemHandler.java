package com.craftminerd.handheld_utilities.util;

import com.craftminerd.handheld_utilities.item.custom.HandheldFurnaceItem;
import com.craftminerd.handheld_utilities.item.custom.HandheldStorageItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class HandheldFurnaceItemHandler extends ItemStackHandler {
    public HandheldFurnaceItemHandler(int size) {
        super(size);
    }

    @Override
    protected void onContentsChanged(int slot) {
        HandheldFurnaceManager.get().setDirty();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !(stack.getItem() instanceof HandheldStorageItem) && !(stack.getItem() instanceof HandheldFurnaceItem);
    }
}
