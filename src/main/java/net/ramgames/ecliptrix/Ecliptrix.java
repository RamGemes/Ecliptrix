package net.ramgames.ecliptrix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.ramgames.ecliptrix.events.TotalSolarEclipseEventHandler;
import net.ramgames.ecliptrix.game_events.OnPlayerJoin;
import net.ramgames.ecliptrix.screen_handlers.SpyGlassScreenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Ecliptrix implements ModInitializer {

    public static final String MOD_ID = "ecliptrix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final List<CelestialEventHandler> CELESTIAL_EVENT_HANDLERS = new ArrayList<>();

    private static final CelestialEvent CURRENT_CELESTIAL_EVENT = new CelestialEvent(CelestialEvents.TOTAL_SOLAR, 6000L);

    @Override
    public void onInitialize() {
        LOGGER.info("Ecliptrix is reading the stars");
        ServerPlayConnectionEvents.JOIN.register(OnPlayerJoin::start);
        registerCelestialEvent(new TotalSolarEclipseEventHandler());
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "spyglass"), SpyGlassScreenHandler.SPY_GLASS_SCREEN_HANDLER_SCREEN_HANDLER_TYPE);
    }

    public static PacketByteBuf alertAllOfTimeInDay(MinecraftServer server) {
        return PacketByteBufs.create();
    }

    public static void alertOfTimeInDay(ServerPlayerEntity player, PacketByteBuf packet) {
        ServerPlayNetworking.send(player, ModNetworking.SYNC_CURRENT_EVENT, packet);
    }

    public static void registerCelestialEvent(CelestialEventHandler celestialEventHandler) {
        CELESTIAL_EVENT_HANDLERS.add(celestialEventHandler);
    }

    public static List<CelestialEventHandler> getHandlers() {
        return CELESTIAL_EVENT_HANDLERS;
    }

    public static CelestialEvent getCurrentCelestialEvent() {
        return CURRENT_CELESTIAL_EVENT;
    }
}
