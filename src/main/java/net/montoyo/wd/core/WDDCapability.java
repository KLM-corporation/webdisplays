/*
 * Copyright (C) 2019 BARBOTIN Nicolas
 */

package net.montoyo.wd.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class WDDCapability implements IWDDCapability {

    public static final EntityCapability<IWDDCapability, Void> CAP = EntityCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath("webdisplays", "wddcapability"), IWDDCapability.class);

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(CAP, EntityType.PLAYER, (entity, ctx) -> new WDDCapability());
    }

    private boolean firstRun = true;

    public WDDCapability() {
    }

    @Override
    public boolean isFirstRun() {
        return firstRun;
    }

    @Override
    public void clearFirstRun() {
        firstRun = false;
    }

    @Override
    public void cloneTo(IWDDCapability dst) {
        if (!isFirstRun())
            dst.clearFirstRun();
    }
}
