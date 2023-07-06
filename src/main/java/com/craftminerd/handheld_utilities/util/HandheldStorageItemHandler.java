package com.craftminerd.handheld_utilities.util;

import net.minecraftforge.items.ItemStackHandler;

public class HandheldStorageItemHandler extends ItemStackHandler {
    public HandheldStorageItemHandler(int size) {
        super(size);
    }

    @Override
    protected void onContentsChanged(int slot) {
        HandheldStorageManager.get().setDirty();
    }
}
