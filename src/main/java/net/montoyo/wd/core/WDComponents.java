package net.montoyo.wd.core;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

/**
 * Custom Data Components (replacement for the old ItemStack NBT tags, removed in 1.21).
 */
public class WDComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "webdisplays");

    // MinePad
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PAD_ID =
            COMPONENTS.register("pad_id", () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PAD_URL =
            COMPONENTS.register("pad_url", () -> DataComponentType.<String>builder().persistent(Codec.STRING).build());

    // Thrown MinePad
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> THROWER_MSB =
            COMPONENTS.register("thrower_msb", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> THROWER_LSB =
            COMPONENTS.register("thrower_lsb", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> THROW_HEIGHT =
            COMPONENTS.register("throw_height", () -> DataComponentType.<Double>builder().persistent(Codec.DOUBLE).build());

    // ItemLinker
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LINK_SCREEN_X =
            COMPONENTS.register("link_screen_x", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LINK_SCREEN_Y =
            COMPONENTS.register("link_screen_y", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LINK_SCREEN_Z =
            COMPONENTS.register("link_screen_z", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> LINK_SCREEN_SIDE =
            COMPONENTS.register("link_screen_side", () -> DataComponentType.<Byte>builder().persistent(Codec.BYTE).build());

    // Ownership Thief
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> OT_POS_X =
            COMPONENTS.register("ot_pos_x", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> OT_POS_Y =
            COMPONENTS.register("ot_pos_y", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> OT_POS_Z =
            COMPONENTS.register("ot_pos_z", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> OT_SIDE =
            COMPONENTS.register("ot_side", () -> DataComponentType.<Byte>builder().persistent(Codec.BYTE).build());
}
