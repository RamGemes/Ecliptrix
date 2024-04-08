package net.ramgames.ecliptrix.events;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.ramgames.ecliptrix.Ecliptrix;

public class OnPlayerJoin {
    public static void start(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        Ecliptrix.alertOfTimeInDay(serverPlayNetworkHandler.getPlayer(), Ecliptrix.alertAllOfTimeInDay(minecraftServer));
    }
}
