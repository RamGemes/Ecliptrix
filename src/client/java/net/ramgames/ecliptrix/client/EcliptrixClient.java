package net.ramgames.ecliptrix.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.ramgames.ecliptrix.ModNetworking;
import net.ramgames.ecliptrix.client.packets.S2CSyncTimeInDay;

public class EcliptrixClient implements ClientModInitializer {

    public static double daysInLunarCycle = 27.32;

    public static double daysInSolarCycle = 365;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC_CURRENT_EVENT, S2CSyncTimeInDay::receive);
    }

    // Finds the percent complete in the cycle and applies it to cosine
    public static float getHeading(long curTime, double daysInCycle, double variation) {
        return (float) (((Math.cos(2 * Math.PI * (double) (curTime % ((int)(24000 * daysInCycle))) / (24000 * daysInCycle)) + 1) / 2d) * 2 * variation - variation);
    }
}
