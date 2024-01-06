package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import static com.ibm.icu.text.PluralRules.Operand.e;

public class HandheldEnchantingTable extends Item {
    public HandheldEnchantingTable(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide()) return InteractionResultHolder.success(heldItem);
        Component component = new TranslatableComponent("container.enchant");
        if (!heldItem.getHoverName().equals(this.getDefaultInstance().getHoverName())) {
            component = heldItem.getHoverName();
        }
        NetworkHooks.openGui((ServerPlayer) pPlayer, new SimpleMenuProvider((id, inventory, player) -> {
            Class<EnchantmentMenu> enchMenu = EnchantmentMenu.class;
            try {
                Field enchantSlots = enchMenu.getDeclaredField("enchantSlots");
                enchantSlots.setAccessible(true);

                Field access = enchMenu.getDeclaredField("access");
                access.setAccessible(true);

                Field random = enchMenu.getDeclaredField("random");
                random.setAccessible(true);

                Field enchantmentSeed = enchMenu.getDeclaredField("enchantmentSeed");
                enchantmentSeed.setAccessible(true);

                Method getEnchantmentList = enchMenu.getDeclaredMethod("getEnchantmentList", ItemStack.class, int.class, int.class);
                getEnchantmentList.setAccessible(true);
                return new EnchantmentMenu(id, inventory, ContainerLevelAccess.create(pLevel,
                        new BlockPos(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ()))) {

                    @Override
                    public boolean stillValid(Player pPlayer) {
                        return heldItem.getItem() instanceof HandheldEnchantingTable;
                    }

                    @Override
                    public void slotsChanged(Container pInventory) {
                        try {
                            if (pInventory == enchantSlots.get(this)) {
                                ItemStack itemstack = pInventory.getItem(0);
                                if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                                    ((ContainerLevelAccess) access.get(this)).execute((pLevel, pPos) -> {
                                        float j = getEnchantPowerFromItem(heldItem);

//                                        for(BlockPos blockpos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
//                                            if (EnchantmentTableBlock.isValidBookShelf(pLevel, pPos, blockpos)) {
//                                                j += pLevel.getBlockState(pPos.offset(blockpos)).getEnchantPowerBonus(pLevel, pPos.offset(blockpos));
//                                            }
//                                        }

                                        try {
                                            ((Random) random.get(this)).setSeed((long)((DataSlot) enchantmentSeed.get(this)).get());
                                        } catch (IllegalAccessException ex) {
                                            throw new RuntimeException(ex);
                                        }

                                        for(int k = 0; k < 3; ++k) {
                                            try {
                                                this.costs[k] = EnchantmentHelper.getEnchantmentCost(((Random) random.get(this)), k, (int)j, itemstack);
                                            } catch (IllegalAccessException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                            this.enchantClue[k] = -1;
                                            this.levelClue[k] = -1;
                                            if (this.costs[k] < k + 1) {
                                                this.costs[k] = 0;
                                            }
                                            this.costs[k] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(pLevel, pPos, k, (int)j, itemstack, costs[k]);
                                        }

                                        for(int l = 0; l < 3; ++l) {
                                            if (this.costs[l] > 0) {
                                                List<EnchantmentInstance> list;
                                                try {
                                                    list = (List<EnchantmentInstance>) getEnchantmentList.invoke(this, itemstack, l, this.costs[l]);
                                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                                if (list != null && !list.isEmpty()) {
                                                    EnchantmentInstance enchantmentinstance = null;
                                                    try {
                                                        enchantmentinstance = list.get(((Random) random.get(this)).nextInt(list.size()));
                                                    } catch (IllegalAccessException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                    this.enchantClue[l] = Registry.ENCHANTMENT.getId(enchantmentinstance.enchantment);
                                                    this.levelClue[l] = enchantmentinstance.level;
                                                }
                                            }
                                        }

                                        this.broadcastChanges();
                                    });
                                } else {
                                    for(int i = 0; i < 3; ++i) {
                                        this.costs[i] = 0;
                                        this.enchantClue[i] = -1;
                                        this.levelClue[i] = -1;
                                    }
                                }
                            }
                        } catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, component)); // new TranslatableComponent("title.handheld_utilities.handheld_etable"
        return InteractionResultHolder.consume(heldItem);
    }

    public int getEnchantPowerFromItem(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("enchantPower")) {
            return nbt.getInt("enchantPower");
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        int enchPower = getEnchantPowerFromItem(pStack);
        pTooltipComponents.add(new TextComponent("Bookshelves: ").append(String.valueOf((enchPower))));
    }
}
