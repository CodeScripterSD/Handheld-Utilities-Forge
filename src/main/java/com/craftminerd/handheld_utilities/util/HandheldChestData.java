package com.craftminerd.handheld_utilities.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;
import java.util.UUID;

public class HandheldChestData {
    private final UUID uuid;
    private final HandheldStorageItemHandler inventory;
    private final LazyOptional<IItemHandler> optional;
    public final Metadata meta = new Metadata();

    public LazyOptional<IItemHandler> getOptional() {
        return this.optional;
    }

    public HandheldStorageItemHandler getHandler() {
        return this.inventory;
    }

    public void updateAccessRecords(String player, long time) {
        if (this.meta.firstAccessedTime == 0) {
            // new storage, create access data
            this.meta.firstAccessedTime = time;
            this.meta.firstAccessedPlayer = player;
        }

        this.meta.setLastAccessedTime(time);
        this.meta.setLastAccessedPlayer(player);
    }

    public HandheldChestData(UUID uuid) {
        this.uuid= uuid;
        this.inventory = new HandheldStorageItemHandler(27);
        this.optional = LazyOptional.of(() -> this.inventory);
    }

    public HandheldChestData(UUID uuid, CompoundTag incomingNBT) {
        this.uuid = uuid;

        this.inventory = new HandheldStorageItemHandler(9);

        // Fixes FTBTeam/FTB-Modpack-Issues #478 Part 2
        if (incomingNBT.getCompound("Inventory").contains("Size")) {
            if (incomingNBT.getCompound("Inventory").getInt("Size") != 27)
                incomingNBT.getCompound("Inventory").putInt("Size", 27);
        }
        this.inventory.deserializeNBT(incomingNBT.getCompound("Inventory"));
        this.optional = LazyOptional.of(() -> this.inventory);

        if (incomingNBT.contains("Metadata"))
            this.meta.deserializeNBT(incomingNBT.getCompound("Metadata"));
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static Optional<HandheldChestData> fromNBT(CompoundTag nbt) {
        if (nbt.contains("UUID")) {
            UUID uuid = nbt.getUUID("UUID");
            return Optional.of(new HandheldChestData(uuid, nbt));
        }
        return Optional.empty();
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putUUID("UUID", this.uuid);

        nbt.put("Inventory", this.inventory.serializeNBT());

        nbt.put("Metadata", this.meta.serializeNBT());

        return nbt;
    }



    public static class Metadata implements INBTSerializable<CompoundTag> {
        private String firstAccessedPlayer = "";

        private long firstAccessedTime = 0;
        private String lastAccessedPlayer = "";
        private long lastAccessedTime = 0;

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public void setLastAccessedPlayer(String lastAccessedPlayer) {
            this.lastAccessedPlayer = lastAccessedPlayer;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            nbt.putString("firstPlayer", this.firstAccessedPlayer);
            nbt.putLong("firstTime", this.firstAccessedTime);
            nbt.putString("lastPlayer", this.lastAccessedPlayer);
            nbt.putLong("lastTime", this.lastAccessedTime);

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.firstAccessedPlayer = nbt.getString("firstPlayer");
            this.firstAccessedTime = nbt.getLong("firstTime");
            this.lastAccessedPlayer = nbt.getString("lastPlayer");
            this.lastAccessedTime = nbt.getLong("lastTime");
        }
    }
}
