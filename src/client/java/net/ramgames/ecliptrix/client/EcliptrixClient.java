package net.ramgames.ecliptrix.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.ramgames.ecliptrix.CelestialEvents;
import net.ramgames.ecliptrix.ModNetworking;
import net.ramgames.ecliptrix.client.events.TotalSolarEclipseEventRenderer;
import net.ramgames.ecliptrix.client.packets.S2CSyncTimeInDay;

import java.util.ArrayList;
import java.util.List;

public class EcliptrixClient implements ClientModInitializer {

    public static final Identifier ECLIPSE = new Identifier("ecliptrix:textures/environment/eclipse.png");

    public static double daysInLunarCycle = 27.32;

    public static double daysInSolarCycle = 365;

    public static Identifier curEvent = CelestialEvents.TOTAL_SOLAR;

    public static long eventClimax = 6000L;

    protected static final List<CelestialEventRenderer> renderers = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC_CURRENT_EVENT, S2CSyncTimeInDay::receive);
        registerCelestialEventRenderer(new TotalSolarEclipseEventRenderer());
    }

    // Finds the percent complete in the cycle and applies it to cosine
    public static float getHeading(long curTime, double daysInCycle, double variation) {
        return (float) (((Math.cos(2 * Math.PI * (double) (curTime % ((int)(24000 * daysInCycle))) / (24000 * daysInCycle)) + 1) / 2d) * 2 * variation - variation);
    }

    public static double sineEaseInOut(double x) {
        return -(Math.cos(Math.PI * x)-1) /2d;
    }

    public static void registerCelestialEventRenderer(CelestialEventRenderer renderer) {
        renderers.add(renderer);
    }

    public static List<CelestialEventRenderer> getRenderers() {
        return renderers;
    }
}
