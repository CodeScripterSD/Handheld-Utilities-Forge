package com.craftminerd.handheld_utilities.util;

import net.minecraftforge.items.ItemStackHandler;

public class HandheldFurnaceItemHandler extends ItemStackHandler {
    public HandheldFurnaceItemHandler(int size) {
        super(size);
    }

    @Override
    protected void onContentsChanged(int slot) {
        HandheldFurnaceManager.get().setDirty();
    }
}
