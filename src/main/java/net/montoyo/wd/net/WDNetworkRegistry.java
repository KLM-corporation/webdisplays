package net.montoyo.wd.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.montoyo.wd.net.client_bound.*;
import net.montoyo.wd.net.server_bound.*;

public class WDNetworkRegistry {

    public static void init(IEventBus bus) {
        bus.addListener(WDNetworkRegistry::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("webdisplays").versioned("2");

        // login handshake
        registrar.playToClient(S2CMessageServerInfo.TYPE, S2CMessageServerInfo.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
        registrar.playToServer(C2SMessageMiniservConnect.TYPE, C2SMessageMiniservConnect.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
        registrar.playToClient(S2CMessageMiniservKey.TYPE, S2CMessageMiniservKey.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // guis
        registrar.playToClient(S2CMessageCloseGui.TYPE, S2CMessageCloseGui.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
        registrar.playToClient(S2CMessageOpenGui.TYPE, S2CMessageOpenGui.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // screen creation
        registrar.playToClient(S2CMessageAddScreen.TYPE, S2CMessageAddScreen.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // screen modifications
        registrar.playToServer(C2SMessageScreenCtrl.TYPE, C2SMessageScreenCtrl.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
        registrar.playToClient(S2CMessageScreenUpdate.TYPE, S2CMessageScreenUpdate.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // redstone control
        registrar.playToServer(C2SMessageRedstoneCtrl.TYPE, C2SMessageRedstoneCtrl.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // autocomplete
        registrar.playToServer(C2SMessageACQuery.TYPE, C2SMessageACQuery.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
        registrar.playToClient(S2CMessageACResult.TYPE, S2CMessageACResult.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // jsquery
        registrar.playToClient(S2CMessageJSResponse.TYPE, S2CMessageJSResponse.STREAM_CODEC, (p, ctx) -> p.handle(ctx));

        // minepad
        registrar.playToServer(C2SMessageMinepadUrl.TYPE, C2SMessageMinepadUrl.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
    }

    public static void sendToNear(Level world, BlockPos pos, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayersNear((ServerLevel) world, null, pos.getX(), pos.getY(), pos.getZ(), 64.0, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
