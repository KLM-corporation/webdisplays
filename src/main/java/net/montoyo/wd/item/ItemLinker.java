/*
 * Copyright (C) 2018 BARBOTIN Nicolas
 */

package net.montoyo.wd.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.montoyo.wd.WebDisplays;
import net.montoyo.wd.block.ScreenBlock;
import net.montoyo.wd.core.IPeripheral;
import net.montoyo.wd.core.ScreenRights;
import net.montoyo.wd.entity.ScreenData;
import net.montoyo.wd.entity.ScreenBlockEntity;
import net.montoyo.wd.utilities.data.BlockSide;
import net.montoyo.wd.utilities.Multiblock;
import net.montoyo.wd.utilities.serialization.Util;
import net.montoyo.wd.utilities.math.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemLinker extends Item implements WDItem {
    public ItemLinker(Properties properties) {
        super(properties
                        .stacksTo(1)
//            .tab(WebDisplays.CREATIVE_TAB)
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide())
            return InteractionResult.SUCCESS;

        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        if (stack.has(net.montoyo.wd.core.WDComponents.LINK_SCREEN_X) && stack.has(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Y) && stack.has(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Z) && stack.has(net.montoyo.wd.core.WDComponents.LINK_SCREEN_SIDE)) {
            {
                BlockState state = context.getLevel().getBlockState(context.getClickedPos());
                IPeripheral target;

                if (state.getBlock() instanceof IPeripheral)
                    target = (IPeripheral) state.getBlock();
                else {
                    BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
                    if (te == null || !(te instanceof IPeripheral)) {
                        if (context.getPlayer().isShiftKeyDown()) {
                            Util.toast(context.getPlayer(), ChatFormatting.GOLD, "linkAbort");
                            stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_X);
                            stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Y);
                            stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Z);
                            stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_SIDE);
                        } else
                            Util.toast(context.getPlayer(), "peripheral");

                        return InteractionResult.SUCCESS;
                    }

                    target = (IPeripheral) te;
                }

                Vector3i tePos = new Vector3i(stack.get(net.montoyo.wd.core.WDComponents.LINK_SCREEN_X), stack.get(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Y), stack.get(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Z));
                BlockSide scrSide = BlockSide.values()[stack.get(net.montoyo.wd.core.WDComponents.LINK_SCREEN_SIDE)];

                if (target.connect(context.getLevel(), context.getClickedPos(), state, tePos, scrSide)) {
                    Util.toast(context.getPlayer(), ChatFormatting.AQUA, "linked");

                    if (context.getPlayer() instanceof ServerPlayer)
                        WebDisplays.INSTANCE.criterionLinkPeripheral.trigger((ServerPlayer) context.getPlayer());
                } else
                    Util.toast(context.getPlayer(), "linkError");

                stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_X);
                stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Y);
                stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Z);
                stack.remove(net.montoyo.wd.core.WDComponents.LINK_SCREEN_SIDE);
                return InteractionResult.SUCCESS;
            }
        }

        if (!(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ScreenBlock)) {
            Util.toast(context.getPlayer(), "notAScreen");
            return InteractionResult.SUCCESS;
        }

        Vector3i pos = new Vector3i(context.getClickedPos());
        BlockSide side = BlockSide.values()[context.getClickedFace().ordinal()];
        Multiblock.findOrigin(context.getLevel(), pos, side, null);

        BlockEntity te = context.getLevel().getBlockEntity(pos.toBlock());
        if (te == null || !(te instanceof ScreenBlockEntity)) {
            Util.toast(context.getPlayer(), "turnOn");
            return InteractionResult.SUCCESS;
        }

        ScreenData scr = ((ScreenBlockEntity) te).getScreen(side);
        if(scr == null)
            Util.toast(context.getPlayer(), "turnOn");
        else if ((scr.rightsFor(context.getPlayer()) & ScreenRights.MANAGE_UPGRADES) == 0)
            Util.toast(context.getPlayer(), "restrictions");
        else {
            stack.set(net.montoyo.wd.core.WDComponents.LINK_SCREEN_X, pos.x);
            stack.set(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Y, pos.y);
            stack.set(net.montoyo.wd.core.WDComponents.LINK_SCREEN_Z, pos.z);
            stack.set(net.montoyo.wd.core.WDComponents.LINK_SCREEN_SIDE, (byte) side.ordinal());
            Util.toast(context.getPlayer(), ChatFormatting.AQUA, "screenSet2");
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public String getWikiName(@Nonnull ItemStack is) {
        return is.getItem().getName(is).getString();
    }
}
