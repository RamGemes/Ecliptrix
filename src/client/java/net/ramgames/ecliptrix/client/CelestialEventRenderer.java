package net.ramgames.ecliptrix.client;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public interface CelestialEventRenderer {

    void render(long curTime, long climax, ClientWorld world, MatrixStack stack, BufferBuilder bufferBuilder, float tickDelta);

    void renderWithTintedSpyglass(long curTime, long climax, ClientWorld world, MatrixStack stack, BufferBuilder bufferBuilder, float tickDelta);

    Vec3d modifySkyColor(long curTime, long climax, ClientWorld world, float tickDelta, Vec3d original);

    float modifySkyBrightness(long curTime, long climax, ClientWorld world, float original);

    float sunshineVisibility(long curTime, long climax, ClientWorld world, float original);

    Vec3d modifyFogColor(long curTime, long climax, ClientWorld world, float tickDelta, Vec3d original);

    Identifier getEventType();
}
