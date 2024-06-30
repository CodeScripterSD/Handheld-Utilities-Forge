package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

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
                Field enchantSlots = null;
                Field access = null;
                Field random = null;
                Field enchantmentSeed = null;
                // I do all of this for loop stuff because im using mappings but when i run it in the base game the functions are unmapped and
                // I felt this was easier than making a bunch of try/catch checking for the field names
                for (Field enchMenuField : enchMenu.getDeclaredFields()) {
                    if (enchMenuField.getType() == Container.class) {
                        enchantSlots = enchMenuField;
                    } else if (enchMenuField.getType() == ContainerLevelAccess.class) {
                        access = enchMenuField;
                    } else if (enchMenuField.getType() == Random.class) {
                        random = enchMenuField;
                    } else if (enchMenuField.getType() == DataSlot.class) {
                        enchantmentSeed = enchMenuField;
                    }
                }
                if (enchantSlots == null || access == null || random == null || enchantmentSeed == null) {
                    return null;
                }

                enchantSlots.setAccessible(true);
                access.setAccessible(true);
                random.setAccessible(true);
                enchantmentSeed.setAccessible(true);

                Method getEnchantmentList = null;
                for (Method enchMenuMethod : enchMenu.getDeclaredMethods()) {
                    if (enchMenuMethod.getReturnType() == List.class) {
                        getEnchantmentList = enchMenuMethod;
                    }
                }

                if (getEnchantmentList == null) {
                    return null;
                }

                getEnchantmentList.setAccessible(true);

                // I have no idea what these are but my IDE told me to put them here and it errors without it so
                Field finalEnchantSlots = enchantSlots;
                Field finalAccess = access;
                Field finalRandom = random;
                Field finalEnchantmentSeed = enchantmentSeed;
                Method finalGetEnchantmentList = getEnchantmentList;

                return new EnchantmentMenu(id, inventory, ContainerLevelAccess.create(pLevel,
                        new BlockPos(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ()))) {

                    @Override
                    public boolean stillValid(Player pPlayer) {
                        return heldItem.getItem() instanceof HandheldEnchantingTable;
                    }

                    @Override
                    public void slotsChanged(Container pInventory) {
                        try {
                            if (pInventory == finalEnchantSlots.get(this)) {
                                ItemStack itemstack = pInventory.getItem(0);
                                if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                                    ((ContainerLevelAccess) finalAccess.get(this)).execute((pLevel, pPos) -> {
                                        float j = 0;
//                                        j = getEnchantPowerFromItem(heldItem);
//                                        NonNullList<BlockState> states =  getBlockStatesOfAttachedItems(heldItem);
//                                        for (BlockState state : states) {
//                                            j += state.is(Blocks.BOOKSHELF) ? 1 : 0;
//                                        }

//                                        for(BlockPos blockpos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
//                                            if (EnchantmentTableBlock.isValidBookShelf(pLevel, pPos, blockpos)) {
//                                                j += pLevel.getBlockState(pPos.offset(blockpos)).getEnchantPowerBonus(pLevel, pPos.offset(blockpos));
//                                            }
//                                        }

                                        try {
                                            ((Random) finalRandom.get(this)).setSeed((long)((DataSlot) finalEnchantmentSeed.get(this)).get());
                                        } catch (IllegalAccessException ex) {
                                            throw new RuntimeException(ex);
                                        }

                                        for(int k = 0; k < 3; ++k) {
                                            try {
                                                this.costs[k] = EnchantmentHelper.getEnchantmentCost(((Random) finalRandom.get(this)), k, (int)j, itemstack);
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
                                                    list = (List<EnchantmentInstance>) finalGetEnchantmentList.invoke(this, itemstack, l, this.costs[l]);
                                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                                if (list != null && !list.isEmpty()) {
                                                    EnchantmentInstance enchantmentinstance = null;
                                                    try {
                                                        enchantmentinstance = list.get(((Random) finalRandom.get(this)).nextInt(list.size()));
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

//    private NonNullList<BlockState> getBlockStatesOfAttachedItems(ItemStack stack) {
//        NonNullList<BlockState> blockStates = NonNullList.create();
//        CompoundTag nbt = stack.getTag();
//        if (nbt == null || !nbt.contains("shelfBlocks")) return blockStates;
//        HandheldUtilities.LOGGER.info("Full NBT: " + nbt);
//        CompoundTag shelfBlocks = nbt.getCompound("shelfBlocks");
//        HandheldUtilities.LOGGER.info("NBT of shelfBlocks: " + shelfBlocks);
//        for (String key : shelfBlocks.getAllKeys()) {
//            HandheldUtilities.LOGGER.info("Inner Key (" + key + ") of NBT: " + shelfBlocks.get(key));
//            if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(shelfBlocks.getString(key)))) {
//                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(shelfBlocks.getString(key)));
//                assert block != null;
//                blockStates.add(block.defaultBlockState());
//            }
//        }
//        return blockStates;
//    }

    public int getEnchantPowerFromItem(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("enchantPower")) {
            return nbt.getInt("enchantPower");
        }
        return 0;
//        int enchantPower = 0;
//        CompoundTag nbt = stack.getTag();
//        if (nbt == null || !nbt.contains("shelfBlocks")) return 0;
//        HandheldUtilities.LOGGER.info("Full NBT: " + nbt);
//        CompoundTag shelfBlocks = nbt.getCompound("shelfBlocks");
//        HandheldUtilities.LOGGER.info("NBT of shelfBlocks: " + shelfBlocks);
//        for (String key : shelfBlocks.getAllKeys()) {
//            HandheldUtilities.LOGGER.info("Inner Key (" + key + ") of NBT: " + shelfBlocks.get(key));
//        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        int enchPower = getEnchantPowerFromItem(pStack);
        pTooltipComponents.add(new TextComponent("Bookshelves: ").append(String.valueOf((enchPower))));
    }
}
