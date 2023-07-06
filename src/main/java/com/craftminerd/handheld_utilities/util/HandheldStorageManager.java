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

public class HandheldStorageManager extends SavedData {
    private static final String NAME = HandheldUtilities.MOD_ID + "_handheld_storage_data";

    private static final HashMap<UUID, HandheldChestData> data = new HashMap<>();

    public static final HandheldStorageManager blankClient = new HandheldStorageManager();

    public HashMap<UUID, HandheldChestData> getMap() { return data; }

    public static HandheldStorageManager get() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(HandheldStorageManager::load, HandheldStorageManager::new, NAME);
        else
            return blankClient;
    }

    public HandheldChestData getOrCreateHandheldStorage(UUID uuid) {
        return data.computeIfAbsent(uuid, id -> {
            setDirty();
            return new HandheldChestData(id);
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

    public static HandheldStorageManager load(CompoundTag nbt) {
        if (nbt.contains("HandheldStorages")) {
            ListTag list = nbt.getList("HandheldStorages", Tag.TAG_COMPOUND);
            list.forEach((handheld_storageNBT) -> HandheldChestData.fromNBT((CompoundTag) handheld_storageNBT).ifPresent((handheld_storage) -> data.put(handheld_storage.getUuid(), handheld_storage)));
        }
        return new HandheldStorageManager();
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag compound) {
        ListTag handheld_storages = new ListTag();
        data.forEach(((uuid, handheld_storageData) -> handheld_storages.add(handheld_storageData.toNBT())));
        compound.put("HandheldStorages", handheld_storages);
        return compound;
    }

}
