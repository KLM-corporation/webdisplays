/*
 * Copyright (C) 2018 BARBOTIN Nicolas
 */

package net.montoyo.wd.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.montoyo.wd.WebDisplays;
import net.montoyo.wd.config.CommonConfig;
import net.montoyo.wd.core.CraftComponent;
import net.montoyo.wd.net.WDNetworkRegistry;
import net.montoyo.wd.net.server_bound.C2SMessageMinepadUrl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class ItemMinePad2 extends Item implements WDItem {
    public ItemMinePad2(Properties properties) {
        super(properties
                        .stacksTo(1)
//				.tab(WebDisplays.CREATIVE_TAB)
        );
    }

    private static String getURL(ItemStack is) {
        if (!is.has(net.montoyo.wd.core.WDComponents.PAD_URL))
            return CommonConfig.Browser.homepage;
        else
            return is.get(net.montoyo.wd.core.WDComponents.PAD_URL);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level world, Player ply, @Nonnull InteractionHand hand) {
        ItemStack is = ply.getItemInHand(hand);
        boolean ok;

        if (ply.isShiftKeyDown()) {
            if (world.isClientSide)
                WebDisplays.PROXY.displaySetPadURLGui(is, getURL(is));

            ok = true;
        } else if (is.has(net.montoyo.wd.core.WDComponents.PAD_ID)) {
            if (world.isClientSide)
                WebDisplays.PROXY.openMinePadGui(is.get(net.montoyo.wd.core.WDComponents.PAD_ID));

            ok = true;
        } else {
            UUID uuid = UUID.randomUUID();
            String url = getURL(is);
            WDNetworkRegistry.sendToServer(new C2SMessageMinepadUrl(uuid, url));
            is.set(net.montoyo.wd.core.WDComponents.PAD_ID, uuid);

            ok = true;
        }

        return new InteractionResultHolder<>(ok ? InteractionResult.SUCCESS : InteractionResult.PASS, is);
    }


    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity ent) {
        if (ent.onGround() && !ent.level().isClientSide) {
            ItemStack entStack = ent.getItem();

            if (entStack.has(net.montoyo.wd.core.WDComponents.THROW_HEIGHT)) {
                //Delete it, it touched the ground
                double height = entStack.get(net.montoyo.wd.core.WDComponents.THROW_HEIGHT);
                UUID thrower = null;

                if (entStack.has(net.montoyo.wd.core.WDComponents.THROWER_MSB) && entStack.has(net.montoyo.wd.core.WDComponents.THROWER_LSB))
                    thrower = new UUID(entStack.get(net.montoyo.wd.core.WDComponents.THROWER_MSB), entStack.get(net.montoyo.wd.core.WDComponents.THROWER_LSB));

                if (entStack.has(net.montoyo.wd.core.WDComponents.PAD_ID) || entStack.has(net.montoyo.wd.core.WDComponents.PAD_URL)) {
                    entStack.remove(net.montoyo.wd.core.WDComponents.THROWER_MSB);
                    entStack.remove(net.montoyo.wd.core.WDComponents.THROWER_LSB);
                    entStack.remove(net.montoyo.wd.core.WDComponents.THROW_HEIGHT);
                } else { //We can delete the whole tag
                    entStack.remove(net.montoyo.wd.core.WDComponents.THROWER_MSB);
                    entStack.remove(net.montoyo.wd.core.WDComponents.THROWER_LSB);
                    entStack.remove(net.montoyo.wd.core.WDComponents.THROW_HEIGHT);
                }

                if (thrower != null && height - ent.getBlockY() >= 20.0) {
                    ent.level().playSound(null, ent.getBlockX(), ent.getBlockY(), ent.getBlockZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 4.0f, 1.0f);
                    ent.level().addFreshEntity(new ItemEntity(ent.level(), ent.getBlockX(), ent.getBlockY(), ent.getBlockZ(), CraftComponent.EXTCARD.makeItemStack()));
                    ent.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);

                    Player ply = ent.level().getPlayerByUUID(thrower);
                    if (ply != null && ply instanceof ServerPlayer)
                        WebDisplays.INSTANCE.criterionPadBreak.trigger((ServerPlayer) ply);
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public String getWikiName(@Nonnull ItemStack is) {
        return is.getItem().getName(is).getString();
    }
}
