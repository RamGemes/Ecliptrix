package net.ramgames.ecliptrix.client.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.ramgames.ecliptrix.client.CelestialEventRenderer;
import net.ramgames.ecliptrix.client.EcliptrixClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Shadow private static float red;

    @Shadow private static float green;

    @Shadow private static float blue;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getThunderGradient(F)F", shift = At.Shift.BEFORE))
    private static void modifyFogColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        if(MinecraftClient.getInstance().world == null) return;
        Vec3d coloration = new Vec3d(red, green, blue);
        for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers()) coloration = renderer.modifyFogColor(MinecraftClient.getInstance().world.getTimeOfDay(), EcliptrixClient.eventClimax, MinecraftClient.getInstance().world, tickDelta, coloration);
        red = (float) coloration.x;
        green = (float) coloration.y;
        blue = (float) coloration.z;
    }


}
