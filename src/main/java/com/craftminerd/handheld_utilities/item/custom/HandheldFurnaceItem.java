package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.util.HandheldFurnaceData;
import com.craftminerd.handheld_utilities.util.HandheldFurnaceManager;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.slf4j.Logger;

import java.util.UUID;

public class HandheldFurnaceItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();
    public HandheldFurnaceItem(Properties pProperties) {
        super(pProperties);
    }

    public int getInventorySize(ItemStack stack) {
        return 3;
    }

    public IItemHandler getInventory(ItemStack stack) {
        if(stack.isEmpty())
            return null;
        ItemStackHandler stackHandler = new ItemStackHandler(getInventorySize(stack));
        stackHandler.deserializeNBT(stack.getOrCreateTag().getCompound("inventory"));
        return stackHandler;
    }

    public static HandheldFurnaceData getData(ItemStack stack) {

        UUID uuid;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("UUID")) {
            uuid = UUID.randomUUID();
            tag.putUUID("UUID", uuid);
        } else
            uuid = tag.getUUID("UUID");
        return HandheldFurnaceManager.get().getOrCreateHandheldFurnace(uuid);
    }
}
