package net.montoyo.wd.net.server_bound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.montoyo.wd.item.ItemMinePad2;
import net.montoyo.wd.net.Packet;

import java.util.UUID;

public class C2SMessageMinepadUrl implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<C2SMessageMinepadUrl> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("webdisplays", "minepad_url"));
	public static final StreamCodec<FriendlyByteBuf, C2SMessageMinepadUrl> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.write(buf), C2SMessageMinepadUrl::new);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
	UUID id;
	String url;
	
	public C2SMessageMinepadUrl(UUID id, String url) {
		this.id = id;
		this.url = url;
	}
	
	public C2SMessageMinepadUrl(FriendlyByteBuf buf) {
		this.id = buf.readUUID();
		this.url = buf.readUtf();
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(id);
		buf.writeUtf(url);
	}
	
	protected void merge(ItemStack stack) {
		if (url.equals("")) {
			stack.remove(net.montoyo.wd.core.WDComponents.PAD_ID);
		} else {
			stack.set(net.montoyo.wd.core.WDComponents.PAD_ID, id);
			stack.set(net.montoyo.wd.core.WDComponents.PAD_URL, url);
		}
	}
	
	public void handle(IPayloadContext ctx) {
		// check if the player is holding a minePad with the requested id
		// if the player is, then update that pad
		for (InteractionHand value : InteractionHand.values()) {
			ItemStack stack = Packet.sender(ctx).getItemInHand(value);
			if (stack.getItem() instanceof ItemMinePad2 && stack.has(net.montoyo.wd.core.WDComponents.PAD_ID)) {
				UUID padId = stack.get(net.montoyo.wd.core.WDComponents.PAD_ID);
				if (padId.equals(id)) {
					merge(stack);
					return;
				}
			}
		}
		
		// if the player is not holding the requested minePad, update the first one that does not already have an ID
		for (InteractionHand value : InteractionHand.values()) {
			ItemStack stack = Packet.sender(ctx).getItemInHand(value);
			if (stack.getItem() instanceof ItemMinePad2 && !stack.has(net.montoyo.wd.core.WDComponents.PAD_ID)) {
				merge(stack);
				return;
			}
		}
	}
}
