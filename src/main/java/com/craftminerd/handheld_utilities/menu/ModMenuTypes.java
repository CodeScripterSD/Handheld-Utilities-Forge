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
}
