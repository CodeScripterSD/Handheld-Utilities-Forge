package com.craftminerd.handheld_utilities.item.custom;

import com.craftminerd.handheld_utilities.menu.HandheldAnvilMenu;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public class HandheldAnvil extends Item {
    public HandheldAnvil(Properties pProperties) {
        super(pProperties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        if (pLevel.isClientSide()) {
            return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        } else {
            Component component = new TranslatableComponent("container.repair");
            if (!pPlayer.getItemInHand(pUsedHand).getHoverName().equals(this.getDefaultInstance().getHoverName())) {
                component = pPlayer.getItemInHand(pUsedHand).getHoverName();
            }
            NetworkHooks.openGui((ServerPlayer) pPlayer, new SimpleMenuProvider((p_53124_, p_53125_, p_53126_)
                    -> new HandheldAnvilMenu(p_53124_, p_53125_, ContainerLevelAccess.create(pLevel,
                    new BlockPos(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ()))), component));
            pPlayer.awardStat(Stats.INTERACT_WITH_ANVIL);
            return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
        }
    }
}
