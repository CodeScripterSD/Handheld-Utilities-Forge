package com.craftminerd.handheld_utilities.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public class HandheldEnderChest extends HandheldStorageItem {
    public HandheldEnderChest(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        PlayerEnderChestContainer playerenderchestcontainer = pPlayer.getEnderChestInventory();
        if (pLevel.isClientSide()) {
            return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        } else {
            Component component = new TranslatableComponent("container.enderchest");
            if (!pPlayer.getItemInHand(pUsedHand).getHoverName().equals(this.getDefaultInstance().getHoverName())) {
                component = pPlayer.getItemInHand(pUsedHand).getHoverName();
            }
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, (float) 0.5, 1);
            NetworkHooks.openGui((ServerPlayer) pPlayer, new SimpleMenuProvider((p_53124_, p_53125_, p_53126_)
                    -> ChestMenu.threeRows(p_53124_, p_53125_, playerenderchestcontainer), component));
            pPlayer.awardStat(Stats.OPEN_ENDERCHEST);
            return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
        }
    }

}
