package net.montoyo.wd.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

/**
 * Helper utilities shared by all WebDisplays network payloads (NeoForge 1.21.1 payload API).
 */
public class Packet {

    public static boolean checkClient(IPayloadContext ctx) {
        return ctx.flow().equals(PacketFlow.CLIENTBOUND);
    }

    public static boolean checkServer(IPayloadContext ctx) {
        return ctx.flow().equals(PacketFlow.SERVERBOUND);
    }

    public static ServerPlayer sender(IPayloadContext ctx) {
        return ctx instanceof ServerPayloadContext spc ? spc.player() : null;
    }

    public static void enqueueWork(IPayloadContext ctx, Runnable r) {
        if (ctx instanceof ServerPayloadContext spc) {
            spc.enqueueWork(r);
        } else if (ctx instanceof ClientPayloadContext cpc) {
            cpc.enqueueWork(r);
        }
    }

    public static void respond(IPayloadContext ctx, CustomPacketPayload packet) {
        ctx.reply(packet);
    }

    public static void respondLater(IPayloadContext ctx, CustomPacketPayload packet) {
        enqueueWork(ctx, () -> {
            if (checkClient(ctx)) {
                PacketDistributor.sendToServer(packet);
            } else if (sender(ctx) != null) {
                PacketDistributor.sendToPlayer(sender(ctx), packet);
            } else {
                ctx.reply(packet);
            }
        });
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}
