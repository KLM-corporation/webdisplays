/*
 * Copyright (C) 2018 BARBOTIN Nicolas
 */

package net.montoyo.wd.net.client_bound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.montoyo.wd.WebDisplays;
import net.montoyo.wd.net.Packet;
import net.montoyo.wd.utilities.serialization.NameUUIDPair;

public class S2CMessageACResult implements CustomPacketPayload {
    private static NameUUIDPair[] result;

	public static final CustomPacketPayload.Type<S2CMessageACResult> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("webdisplays", "ac_result"));
	public static final StreamCodec<FriendlyByteBuf, S2CMessageACResult> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.write(buf), S2CMessageACResult::new);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

    public S2CMessageACResult(NameUUIDPair[] pairs) {
        result = pairs;
    }
    
    public S2CMessageACResult(FriendlyByteBuf buf) {
        
        int cnt = buf.readByte();
        result = new NameUUIDPair[cnt];

        for(int i = 0; i < cnt; i++)
            result[i] = new NameUUIDPair(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(result.length);

        for(NameUUIDPair pair : result)
            pair.writeTo(buf);
    }

    public void handle(IPayloadContext ctx) {
        if (Packet.checkClient(ctx)) {
            Packet.enqueueWork(ctx, () -> {
                WebDisplays.PROXY.onAutocompleteResult(result);
            });
        }
    }
}
