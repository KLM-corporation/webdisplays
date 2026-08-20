/*
 * Copyright (C) 2018 BARBOTIN Nicolas
 */

package net.montoyo.wd.core;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.Optional;

public class Criterion extends SimpleCriterionTrigger<Criterion.Instance> {

    public static class Instance implements SimpleInstance {
        private final ResourceLocation id;

        public Instance(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        @Override
        public void validate(CriterionValidator validator) {
        }
    }

    private final ResourceLocation id;

    public Criterion(@Nonnull String name) {
        id = ResourceLocation.fromNamespaceAndPath("webdisplays", name);
    }

    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public Codec<Instance> codec() {
        return Codec.unit(new Instance(id));
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, instance -> true);
    }
}
