package com.craftminerd.handheld_utilities.menu;

import com.craftminerd.handheld_utilities.item.custom.HandheldAnvil;
import com.craftminerd.handheld_utilities.item.custom.HandheldStorageItem;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;

public class HandheldAnvilMenu extends AnvilMenu {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG_COST = false;
    public static final int MAX_NAME_LENGTH = 50;
    public int repairItemCountCost;
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;

    public HandheldAnvilMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public HandheldAnvilMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pContainerId, pPlayerInventory, pAccess);
//        this.addDataSlot(this.cost);
    }
    @Override
    protected boolean isValidBlock(BlockState pState) {
        return true;
    }

    @Override
    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        return (pPlayer.getAbilities().instabuild || pPlayer.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return getHeldItem(pPlayer).getItem() instanceof HandheldAnvil;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }

        float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(player, stack, HandheldAnvilMenu.this.inputSlots.getItem(0), HandheldAnvilMenu.this.inputSlots.getItem(1));

        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
                itemstack.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        ItemStack playerHeldItem = getHeldItem(player);
        this.access.execute((level, blockPos) -> {
            if (!player.getAbilities().instabuild && playerHeldItem != ItemStack.EMPTY && player.getRandom().nextFloat() < breakChance) {
                playerHeldItem.hurtAndBreak(1, player, (p_41300_) -> {
                    p_41300_.broadcastBreakEvent(player.getUsedItemHand());
                });
                if (playerHeldItem.getCount() <= 0) {
                    level.levelEvent(1029, blockPos, 0);
                } else {
                    level.levelEvent(1030, blockPos, 0);
                }
            } else {
                level.levelEvent(1030, blockPos, 0);
            }
        });
    }

    private static ItemStack getHeldItem(Player player) {
        // Determine which held item is a handheld storage (if either)
        if (isHandheldAnvil(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (isHandheldAnvil(player.getOffhandItem())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static boolean isHandheldAnvil(ItemStack stack) {
        return stack.getItem() instanceof HandheldAnvil;
    }

    @Override
    public void createResult() {
        ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (itemstack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        } else {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
            j += itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
            this.repairItemCountCost = 0;
            boolean flag = false;

            if (!itemstack2.isEmpty()) {
                if (!onAnvilChange(this, itemstack, itemstack2, resultSlots, itemName, j, this.player)) return;
                flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    int i3;
                    for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                        int j3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(j3);
                        ++i;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = i3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for(Enchantment enchantment1 : map1.keySet()) {
                        if (enchantment1 != null) {
                            int i2 = map.getOrDefault(enchantment1, 0);
                            int j2 = map1.get(enchantment1);
                            j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                            boolean flag1 = enchantment1.canEnchant(itemstack);
                            if (this.player.getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK)) {
                                flag1 = true;
                            }

                            for(Enchantment enchantment : map.keySet()) {
                                if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                    flag1 = false;
                                    ++i;
                                }
                            }

                            if (!flag1) {
                                flag3 = true;
                            } else {
                                flag2 = true;
                                if (j2 > enchantment1.getMaxLevel()) {
                                    j2 = enchantment1.getMaxLevel();
                                }

                                map.put(enchantment1, j2);
                                int k3 = 0;
                                switch(enchantment1.getRarity()) {
                                    case COMMON:
                                        k3 = 1;
                                        break;
                                    case UNCOMMON:
                                        k3 = 2;
                                        break;
                                    case RARE:
                                        k3 = 4;
                                        break;
                                    case VERY_RARE:
                                        k3 = 8;
                                }

                                if (flag) {
                                    k3 = Math.max(1, k3 / 2);
                                }

                                i += k3 * j2;
                                if (itemstack.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (StringUtils.isBlank(this.itemName)) {
                if (itemstack.hasCustomHoverName()) {
                    k = 1;
                    i += k;
                    itemstack1.resetHoverName();
                }
            } else if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                k = 1;
                i += k;
                itemstack1.setHoverName(new TextComponent(this.itemName));
            }
            if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

            this.cost.set(j + i);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && this.cost.get() >= 40) {
                this.cost.set(39);
            }

            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getBaseRepairCost();
                if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
                    k2 = itemstack2.getBaseRepairCost();
                }

                if (k != i || k == 0) {
                    k2 = calculateIncreasedRepairCost(k2);
                }

                itemstack1.setRepairCost(k2);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }

            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        }
    }

    private static boolean onAnvilChange(HandheldAnvilMenu container, @Nonnull ItemStack left, @Nonnull ItemStack right, Container outputSlot, String name, int baseCost, Player player)
    {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput().isEmpty()) return true;

        outputSlot.setItem(0, e.getOutput());
        container.setMaximumCost(e.getCost());
        container.repairItemCountCost = e.getMaterialCost();
        return false;
    }
    @Override
    public void setItemName(String pNewName) {
        this.itemName = pNewName;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemstack = this.getSlot(2).getItem();
            if (StringUtils.isBlank(pNewName)) {
                itemstack.resetHoverName();
            } else {
                itemstack.setHoverName(new TextComponent(this.itemName));
            }
        }

        this.createResult();
    }
    @Override
    public int getCost() {
        return this.cost.get();
    }

    @Override
    public void setMaximumCost(int value) {
        this.cost.set(value);
    }

}
