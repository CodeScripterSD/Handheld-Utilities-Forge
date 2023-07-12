package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.item.custom.HandheldFurnaceItem;
import com.craftminerd.handheld_utilities.item.custom.HandheldStorageItem;
import com.craftminerd.handheld_utilities.menu.slot.HandheldFurnaceFuelSlot;
import com.craftminerd.handheld_utilities.menu.slot.HandheldFurnaceResultSlot;
import com.craftminerd.handheld_utilities.menu.slot.HandheldFurnaceSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class AbstractHandheldFurnaceMenu extends AbstractContainerMenu {
    public static final int INGREDIENT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    public static final int SLOT_COUNT = 3;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final IItemHandler handler;
    private final ContainerData data;
    protected final Level level;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;

    public AbstractHandheldFurnaceMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, int pContainerId, Inventory pPlayerInventory) {
        this(menuType, recipeType, pContainerId, pPlayerInventory, new ItemStackHandler(3), new SimpleContainerData(4));
    }

    public AbstractHandheldFurnaceMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, int pContainerId, Inventory pPlayerInventory, IItemHandler handler, ContainerData pData) {
        super(menuType, pContainerId);
        this.recipeType = recipeType;
        checkHandlerSize(handler, 3);
        checkContainerDataCount(pData, 4);
        this.handler = handler;
        this.data = pData;
        this.level = pPlayerInventory.player.level;
        this.addSlot(new HandheldFurnaceSlot(handler, 0, 56, 17));
        this.addSlot(new HandheldFurnaceFuelSlot(this, handler, 1, 56, 53));
        this.addSlot(new HandheldFurnaceResultSlot(pPlayerInventory.player, handler, 2, 116, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(pData);
    }

    private void checkHandlerSize(IItemHandler handler, int min) {
        int i = handler.getSlots();
        if (i < min) {
            throw new IllegalArgumentException("Handler size " + i + " is smaller than expected " + min);
        }
    }

    public int getResultSlotIndex() {
        return 2;
    }

    public int getGridWidth() {
        return 1;
    }

    public int getGridHeight() {
        return 1;
    }

    public int getSize() {
        return 3;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex != 1 && pIndex != 0) {
                if (this.canSmelt(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= 3 && pIndex < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= 30 && pIndex < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    protected boolean canSmelt(ItemStack pStack) {
        return this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) this.recipeType, new SimpleContainer(pStack), this.level).isPresent();
    }

    public boolean isFuel(ItemStack pStack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(pStack, this.recipeType) > 0;
    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (pSlotId < 0 || pSlotId > slots.size()) {
            super.clicked(pSlotId, pButton, pClickType, pPlayer);
            return;
        }

        Slot slot = slots.get(pSlotId);
        if (!canTake(pSlotId, slot, pButton, pPlayer, pClickType)) {
            return;
        }

        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }

    private boolean canTake(int slotId, Slot slot, int button, Player player, ClickType clickType) {
        if (slotId <= handler.getSlots() - 1 && isBlockedItem(this.getCarried())) {
            return false;
        }
        if(!filtered(slotId, this.getCarried().getItem()))
            return false;
        Inventory inventory = player.getInventory();
        // Hotbar swapping via number keys
        if (clickType == ClickType.SWAP) {
            ItemStack itemstack4 = inventory.getItem(button);
            return !isBlockedItem(itemstack4);
        }
        return true;
    }

    private boolean isBlockedItem(ItemStack item) {
        return item.getItem() instanceof HandheldFurnaceItem || item.getItem() instanceof HandheldStorageItem;
    }

    //For filtering what items are allowed in the HandheldStorage
    private boolean filtered(int slotId, Item item) {
        if(slotId < handler.getSlots()) {
            if(slotId < 3 && !(item instanceof Item))
                return false;
        }
        return true;
    }

    public int getBurnProgress() {
        int i = this.data.get(2);
        int j = this.data.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }

        return this.data.get(0) * 13 / i;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }
}