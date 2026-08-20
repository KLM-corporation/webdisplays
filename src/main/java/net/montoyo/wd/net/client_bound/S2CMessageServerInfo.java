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
import net.montoyo.wd.miniserv.client.Client;
import net.montoyo.wd.net.Packet;
import net.montoyo.wd.net.server_bound.C2SMessageMiniservConnect;

public class S2CMessageServerInfo implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<S2CMessageServerInfo> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("webdisplays", "serverinfo"));
	public static final StreamCodec<FriendlyByteBuf, S2CMessageServerInfo> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.write(buf), S2CMessageServerInfo::new);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
	
	private int miniservPort;
	
	public S2CMessageServerInfo(int msPort) {
		miniservPort = msPort;
	}
	
	public S2CMessageServerInfo(FriendlyByteBuf buf) {
		miniservPort = buf.readShort();
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeShort(miniservPort);
	}
	
	public void handle(IPayloadContext ctx) {
		if (Packet.checkClient(ctx)) {
			try {
				WebDisplays.PROXY.setMiniservClientPort(miniservPort);
				C2SMessageMiniservConnect message = Client.getInstance().beginConnection();
				Packet.respond(ctx, message);
			} catch (Throwable err) {
				err.printStackTrace();
				throw new RuntimeException(err);
			}
		}
	}
}
