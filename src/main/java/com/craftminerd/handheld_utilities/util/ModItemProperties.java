package com.craftminerd.handheld_utilities.util;

import com.craftminerd.handheld_utilities.item.ModItems;
import com.craftminerd.handheld_utilities.item.custom.HandheldFurnaceItem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

public class ModItemProperties {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void addCustomProperties() {
        makeHandheldFurnace(ModItems.HANDHELD_FURNACE.get());
        makeHandheldFurnace(ModItems.HANDHELD_BLAST_FURNACE.get());
        makeHandheldFurnace(ModItems.HANDHELD_SMOKER.get());
    }

    private static void makeHandheldFurnace(Item item) {
        ItemProperties.register(item, new ResourceLocation("lit"),  (itemStack, clientLevel, livingEntity, p_174633_) -> {
            return livingEntity != null && (itemStack.getItem() instanceof HandheldFurnaceItem ? HandheldFurnaceItem.getData(itemStack).getStoredData().get(0) : 0) > 0 ? 1.0F : 0.0F;
        });
    }
}
