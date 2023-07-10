package com.craftminerd.handheld_utilities.item;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.craftminerd.handheld_utilities.item.custom.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandheldUtilities.MOD_ID);

    public static final RegistryObject<Item> HANDHELD_CRAFTER = ITEMS.register("handheld_crafter", () -> new HandheldCrafter(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_SMITHING = ITEMS.register("handheld_smithing", () -> new HandheldSmithing(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_STONECUTTER = ITEMS.register("handheld_stonecutter", () -> new HandheldStonecutter(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_LOOM = ITEMS.register("handheld_loom", () -> new HandheldLoom(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_CHEST = ITEMS.register("handheld_chest", () -> new HandheldChest(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB).stacksTo(1)));
    public static final RegistryObject<Item> HANDHELD_GRINDSTONE = ITEMS.register("handheld_grindstone", () -> new HandheldGrindstone(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_ETABLE = ITEMS.register("handheld_etable", () -> new HandheldEnchantingTable(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_ECHEST = ITEMS.register("handheld_echest", () -> new HandheldEnderChest(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
    public static final RegistryObject<Item> HANDHELD_ANVIL = ITEMS.register("handheld_anvil", () -> new HandheldAnvil(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB).stacksTo(1).defaultDurability(3)));
    public static final RegistryObject<Item> HANDHELD_CHIPPED_ANVIL = ITEMS.register("handheld_chipped_anvil", () -> new HandheldAnvil(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB).stacksTo(1).defaultDurability(2)));
    public static final RegistryObject<Item> HANDHELD_DAMAGED_ANVIL = ITEMS.register("handheld_damaged_anvil", () -> new HandheldAnvil(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB).stacksTo(1).defaultDurability(1)));

    public static final RegistryObject<Item> HANDHELD_FURNACE = ITEMS.register("handheld_furnace", () -> new HandheldFurnace(new Item.Properties()
            .tab(HandheldUtilities.UTILITIES_TAB)));
}
