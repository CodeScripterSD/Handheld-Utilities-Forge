package com.craftminerd.handheld_utilities.util;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class HandheldFurnaceManager extends SavedData {
    private static final String NAME = HandheldUtilities.MOD_ID + "_handheld_furnace_data";
    private static final HashMap<UUID, HandheldFurnaceData> data = new HashMap<>();
    public static final HandheldFurnaceManager blankClient = new HandheldFurnaceManager();
    public HashMap<UUID, HandheldFurnaceData> getMap() { return data; }

    public static HandheldFurnaceManager get() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(HandheldFurnaceManager::load, HandheldFurnaceManager::new, NAME);
        else
            return blankClient;
    }

    public HandheldFurnaceData getOrCreateHandheldFurnace(UUID uuid) {
        return data.computeIfAbsent(uuid, id -> {
            setDirty();
            return new HandheldFurnaceData(id);
        });
    }

    public LazyOptional<IItemHandler> getCapability(UUID uuid) {
        if (data.containsKey(uuid))
            return data.get(uuid).getOptional();

        return LazyOptional.empty();
    }

    public LazyOptional<IItemHandler> getCapability(ItemStack stack) {
        if (stack.getOrCreateTag().contains("UUID")) {
            UUID uuid = stack.getTag().getUUID("UUID");
            if (data.containsKey(uuid))
                return data.get(uuid).getOptional();
        }

        return LazyOptional.empty();
    }

    public static HandheldFurnaceManager load(CompoundTag nbt) {
        if (nbt.contains("HandheldFurnaces")) {
            ListTag list = nbt.getList("HandheldFurnaces", Tag.TAG_COMPOUND);
            list.forEach((handheld_furnaceNBT) -> HandheldFurnaceData.fromNBT((CompoundTag) handheld_furnaceNBT).ifPresent((handheld_furnace) -> data.put(handheld_furnace.getUuid(), handheld_furnace)));
        }
        return new HandheldFurnaceManager();
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag compound) {
        ListTag handheld_furnaces = new ListTag();
        data.forEach(((uuid, handheldFurnaceData) -> handheld_furnaces.add(handheldFurnaceData.toNBT())));
        compound.put("HandheldFurnaces", handheld_furnaces);
        return compound;
    }

}
