package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.item.custom.HandheldStorageItem;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class HandheldStorageMenu extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    private int blocked = -1;
    private int rows = 0;

    public static HandheldStorageMenu fromNetwork(final int windowId, final Inventory playerInventory, FriendlyByteBuf data) {
        return new HandheldStorageMenu(windowId, playerInventory, new ItemStackHandler(27), 3);
    }

    public HandheldStorageMenu(int id, Inventory playerInventory, IItemHandler handler, int rows) {
        super(ModMenuTypes.HANDHELD_CHEST.get(), id);
        itemHandler = handler;
        this.rows = rows;

        // Add handheld storage slots (rows of 9)
        int i = (rows - 4) * 18;

        for(int j = 0; j < rows; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new SlotItemHandler(handler, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return !isHandheldStorage(stack);
                    }
                });
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            Slot slot = addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i) {
                @Override
                public boolean mayPickup(Player pPlayer) {
                    return playerInventory.findSlotMatchingItem(getHeldItem(pPlayer)) != index;
                }
            });
        }
    }
    private static ItemStack getHeldItem(Player player) {
        // Determine which held item is a handheld storage (if either)
        if (isHandheldStorage(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (isHandheldStorage(player.getOffhandItem())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return getHeldItem(playerIn).getItem() instanceof HandheldStorageItem;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            if (isHandheldStorage(itemstack1)) return ItemStack.EMPTY;
            itemstack = itemstack1.copy();
            if (index < this.rows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId < 0 || slotId > slots.size()) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }

        Slot slot = slots.get(slotId);
        if (!canTake(slotId, slot, dragType, player, clickTypeIn)) {
            return;
        }

        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    private static boolean isHandheldStorage(ItemStack stack) {
        return stack.getItem() instanceof HandheldStorageItem;
    }

    private boolean canTake(int slotId, Slot slot, int button, Player player, ClickType clickType) {
        if (slotId == blocked || slotId <= itemHandler.getSlots() - 1 && isHandheldStorage(this.getCarried())) {
            return false;
        }
        if(!filtered(slotId, this.getCarried().getItem()))
            return false;
        if (slotId > itemHandler.getSlots()) return true;
        Inventory inventory = player.getInventory();
        // Hotbar swapping via number keys
        if (clickType == ClickType.SWAP) {
            ItemStack itemstack4 = inventory.getItem(button);
            return !isHandheldStorage(itemstack4);
        }
        return true;
    }

    //For filtering what items are allowed in the HandheldStorage
    private boolean filtered(int slotId, Item item) {
        if(slotId < itemHandler.getSlots()) {
            if(slotId < rows*9 && !(item instanceof Item))
                return false;
        }
        return true;
    }

    public int getRowCount() {
        return this.rows;
    }

}
