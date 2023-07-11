package com.craftminerd.handheld_utilities.event;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.craftminerd.handheld_utilities.conditions.ModConfigCondition;
import com.craftminerd.handheld_utilities.menu.ModMenuTypes;
import com.craftminerd.handheld_utilities.screen.*;
import com.craftminerd.handheld_utilities.util.ModItemProperties;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HandheldUtilities.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ModItemProperties.addCustomProperties();

        MenuScreens.register(ModMenuTypes.HANDHELD_CHEST.get(), HandheldStorageScreen::new);
        MenuScreens.register(ModMenuTypes.HANDHELD_ANVIL.get(), HandheldAnvilScreen::new);
        MenuScreens.register(ModMenuTypes.HANDHELD_FURNACE.get(), HandheldFurnaceScreen::new);
        MenuScreens.register(ModMenuTypes.HANDHELD_BLAST_FURNACE.get(), HandheldBlastFurnaceScreen::new);
        MenuScreens.register(ModMenuTypes.HANDHELD_SMOKER.get(), HandheldSmokerScreen::new);
    }
    @SubscribeEvent
    public static void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CraftingHelper.register(ModConfigCondition.Serializer.INSTANCE);
    }
}
