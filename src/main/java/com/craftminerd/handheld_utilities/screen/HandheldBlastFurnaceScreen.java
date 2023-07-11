package com.craftminerd.handheld_utilities.screen;

import com.craftminerd.handheld_utilities.menu.AbstractHandheldFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HandheldBlastFurnaceScreen extends HandheldAbstractFurnaceScreen {
    public HandheldBlastFurnaceScreen(AbstractHandheldFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
