package com.craftminerd.handheld_utilities.screen;

import com.craftminerd.handheld_utilities.menu.AbstractHandheldFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HandheldFurnaceScreen extends HandheldAbstractFurnaceScreen {
    public HandheldFurnaceScreen(AbstractHandheldFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
