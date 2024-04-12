package net.ramgames.ecliptrix.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.ramgames.ecliptrix.CelestialEvents;
import net.ramgames.ecliptrix.ModNetworking;
import net.ramgames.ecliptrix.client.events.TotalSolarEclipseEventRenderer;
import net.ramgames.ecliptrix.client.packets.S2CSyncTimeInDay;
import net.ramgames.ecliptrix.client.screens.SpyglassScreen;
import net.ramgames.ecliptrix.screen_handlers.SpyGlassScreenHandler;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class EcliptrixClient implements ClientModInitializer {

    public static final Identifier ECLIPSE = new Identifier("ecliptrix:textures/environment/eclipse.png");

    public static final Identifier SOLAR_CORONA = new Identifier("ecliptrix:textures/environment/solar_corona.png");

    public static final Identifier SUNSHINE = new Identifier("ecliptrix:textures/environment/sunshine.png");

    public static double daysInLunarCycle = 27.32;

    public static double daysInSolarCycle = 365;

    public static Identifier curEvent = CelestialEvents.TOTAL_SOLAR;

    public static long eventClimax = 6000L;

    protected static final List<CelestialEventRenderer> renderers = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC_CURRENT_EVENT, S2CSyncTimeInDay::receive);
        registerCelestialEventRenderer(new TotalSolarEclipseEventRenderer());
        HandledScreens.register(SpyGlassScreenHandler.SPY_GLASS_SCREEN_HANDLER_SCREEN_HANDLER_TYPE, SpyglassScreen::new);
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

    public static List<CelestialEventRenderer> getActiveRenderers() {
        return getRenderers().stream().filter(render -> render.getEventType() == curEvent).toList();
    }

    public static void drawShape(float x1, float x2, float y1, float y2, float size, Matrix4f positionMatrix, BufferBuilder bufferBuilder) {
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(positionMatrix, x1, size, y1).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(positionMatrix, x2, size, y1).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(positionMatrix, x2, size, y2).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(positionMatrix, x1, size, y2).texture(0.0f, 1.0f).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static Item getCurrentSpyglassLens() {
        if(MinecraftClient.getInstance().player == null) return Items.AIR;
        if(MinecraftClient.getInstance().player.getMainHandStack().getItem() != Items.SPYGLASS) return Items.AIR;
        if(!MinecraftClient.getInstance().player.isUsingSpyglass()) return Items.AIR;
        NbtCompound nbt = MinecraftClient.getInstance().player.getMainHandStack().getOrCreateNbt();
        if(!nbt.contains("lens")) return Items.GLASS;
        if(!Registries.ITEM.containsId(new Identifier(nbt.getString("lens")))) return Items.GLASS;
        return Registries.ITEM.get(new Identifier(nbt.getString("lens")));
    }
}
