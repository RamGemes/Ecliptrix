package net.ramgames.ecliptrix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.ramgames.ecliptrix.events.OnPlayerJoin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ecliptrix implements ModInitializer {

    public static final String MOD_ID = "ecliptrix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Ecliptrix is");

        ServerPlayConnectionEvents.JOIN.register(OnPlayerJoin::start);
    }

    public static PacketByteBuf alertAllOfTimeInDay(MinecraftServer server) {
        return PacketByteBufs.create();
    }
    public static void alertOfTimeInDay(ServerPlayerEntity player, PacketByteBuf packet) {
        ServerPlayNetworking.send(player, ModNetworking.SYNC_CURRENT_EVENT, packet);
    }
}
