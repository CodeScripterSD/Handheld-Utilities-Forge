package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.item.ModItems;
import com.craftminerd.handheld_utilities.menu.slot.HandheldFurnaceFuelSlot;
import com.craftminerd.handheld_utilities.menu.slot.HandheldFurnaceResultSlot;
import com.craftminerd.handheld_utilities.menu.slot.HandheldFurnaceSlot;
import net.minecraft.network.FriendlyByteBuf;
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

public class HandheldFurnaceMenu extends AbstractContainerMenu {
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
    private int blocked = -1;

    public HandheldFurnaceMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        this(pContainerId, pPlayerInventory, new ItemStackHandler(3), new SimpleContainerData(4));
    }

    public HandheldFurnaceMenu(int pContainerId, Inventory pPlayerInventory, IItemHandler handler, ContainerData pData) {
        super(ModMenuTypes.HANDHELD_FURNACE.get(), pContainerId);
        this.recipeType = RecipeType.SMELTING;
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
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getMainHandItem().is(ModItems.HANDHELD_FURNACE.get());
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        // This method handles shift-clicking to transfer items quickly. This can easily crash the game if not coded
        // correctly. The first slots (index 0 to whatever) are usually the inventory block/item, while player slots
        // start after those.
        Slot slot = this.getSlot(pIndex);

        if (!slot.mayPickup(playerIn)) {
            return slot.getItem();
        }

        if (pIndex == blocked || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        //Filtering what's allowed in the storage, see also far below at line 192
        if(!(slot.getItem().getItem() instanceof Item)) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack newStack = stack.copy();
        int containerSlots = handler.getSlots();
        if (pIndex < containerSlots) {
            if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.setChanged();

            //Filtering what's allowed in the storage, see also far below at line 192
        } else if(stack.getItem() instanceof Item) {
            if (!this.moveItemStackTo(stack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (!this.moveItemStackTo(stack, 0, 3, false)) {
            return ItemStack.EMPTY;
        }


        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(playerIn, newStack);
        return newStack;
    }

    protected boolean canSmelt(ItemStack pStack) {
        return this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) this.recipeType, new SimpleContainer(pStack), this.level).isPresent();
    }

    public boolean isFuel(ItemStack pStack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(pStack, this.recipeType) > 0;
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