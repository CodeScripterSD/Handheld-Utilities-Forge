package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.menu.HandheldStorageMenu;
import com.craftminerd.handheld_utilities.util.HandheldChestData;
import com.craftminerd.handheld_utilities.util.HandheldStorageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class HandheldChest extends HandheldStorageItem {
    public HandheldChest(Properties properties) {
        super(properties);
    }

    public int getInventorySize(ItemStack stack) {
        return 27;
    }

    public IItemHandler getInventory(ItemStack stack) {
        if(stack.isEmpty())
            return null;
        ItemStackHandler stackHandler = new ItemStackHandler(getInventorySize(stack));
        stackHandler.deserializeNBT(stack.getOrCreateTag().getCompound("inventory"));
        return stackHandler;
    }

    public static HandheldChestData getData(ItemStack stack) {
        if (!(stack.getItem() instanceof HandheldChest))
            return null;
        UUID uuid;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("UUID")) {
            uuid = UUID.randomUUID();
            tag.putUUID("UUID", uuid);
        } else
            uuid = tag.getUUID("UUID");
        return HandheldStorageManager.get().getOrCreateHandheldStorage(uuid);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            ItemStack handheldchest = pPlayer.getItemInHand(pUsedHand);
            HandheldChestData data = HandheldChest.getData(handheldchest);
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, (float) 0.5, 1);
            NetworkHooks.openGui(((ServerPlayer) pPlayer), new SimpleMenuProvider( (windowId, playerInventory, playerEntity) ->
                    new HandheldStorageMenu(windowId, playerInventory, data.getHandler(), 3), handheldchest.getHoverName()));
            pPlayer.awardStat(Stats.OPEN_CHEST);
            return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, pPlayer.getItemInHand(pUsedHand));
    }

}
