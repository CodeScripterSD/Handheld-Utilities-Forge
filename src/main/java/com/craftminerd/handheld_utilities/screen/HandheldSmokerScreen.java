package com.craftminerd.handheld_utilities.screen;

import com.craftminerd.handheld_utilities.menu.AbstractHandheldFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HandheldSmokerScreen extends HandheldAbstractFurnaceScreen {
    public HandheldSmokerScreen(AbstractHandheldFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
