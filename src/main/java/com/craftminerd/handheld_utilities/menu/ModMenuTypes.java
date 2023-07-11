package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, HandheldUtilities.MOD_ID);

    public static final RegistryObject<MenuType<HandheldStorageMenu>> HANDHELD_CHEST = MENUS.register("handheld_chest",
            () -> IForgeMenuType.create(HandheldStorageMenu::fromNetwork));

    public static final RegistryObject<MenuType<HandheldAnvilMenu>> HANDHELD_ANVIL = MENUS.register("handheld_anvil",
            () -> IForgeMenuType.create(HandheldAnvilMenu::new));

    public static final RegistryObject<MenuType<HandheldFurnaceMenu>> HANDHELD_FURNACE = MENUS.register("handheld_furnace",
            () -> IForgeMenuType.create(HandheldFurnaceMenu::new));

    public static final RegistryObject<MenuType<HandheldBlastFurnaceMenu>> HANDHELD_BLAST_FURNACE = MENUS.register("handheld_blast_furnace",
            () -> IForgeMenuType.create(HandheldBlastFurnaceMenu::new));

    public static final RegistryObject<MenuType<HandheldSmokerMenu>> HANDHELD_SMOKER = MENUS.register("handheld_smoker",
            () -> IForgeMenuType.create(HandheldSmokerMenu::new));
}
