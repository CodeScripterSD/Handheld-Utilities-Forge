package com.craftminerd.handheld_utilities.event;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.craftminerd.handheld_utilities.item.custom.HandheldAnvil;
import com.craftminerd.handheld_utilities.menu.HandheldAnvilScreen;
import com.craftminerd.handheld_utilities.menu.HandheldStorageScreen;
import com.craftminerd.handheld_utilities.menu.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HandheldUtilities.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.HANDHELD_CHEST.get(), HandheldStorageScreen::new);
    }
}
