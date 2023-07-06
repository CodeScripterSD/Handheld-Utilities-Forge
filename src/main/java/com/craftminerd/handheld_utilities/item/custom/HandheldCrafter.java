package com.craftminerd.handheld_utilities.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class HandheldCrafter extends Item {
    public HandheldCrafter(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide()) return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        Component component = new TranslatableComponent("container.crafting");
        if (!pPlayer.getItemInHand(pUsedHand).getDisplayName().equals(this.getDefaultInstance().getDisplayName())) {
            component = pPlayer.getItemInHand(pUsedHand).getDisplayName();
        }
        NetworkHooks.openGui((ServerPlayer) pPlayer, new SimpleMenuProvider((id, inventory, player) -> {
            return new CraftingMenu(id, inventory, ContainerLevelAccess.create(pLevel,
                    new BlockPos(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ()))) {
                @Override
                public boolean stillValid(Player pPlayer) {
                    return pPlayer.getItemInHand(pUsedHand).getItem() instanceof HandheldCrafter;
                }
            };
        }, component));
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }
}
