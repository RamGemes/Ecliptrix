package net.ramgames.ecliptrix.client.mixins.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
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

    @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V", shift = At.Shift.BEFORE))
    private static void modifyFogColor(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Share("colors") LocalRef<float[]> oldShaderColors) {
        if(MinecraftClient.getInstance().world == null) return;
        Vec3d coloration = new Vec3d(red, green, blue);
        oldShaderColors.set(RenderSystem.getShaderColor());
        for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers()) coloration = renderer.modifyFogColor(MinecraftClient.getInstance().world.getTimeOfDay(), EcliptrixClient.eventClimax, MinecraftClient.getInstance().world, coloration);
        RenderSystem.setShaderFogColor((int) coloration.x, (int) coloration.y, (int) coloration.z, oldShaderColors.get()[3]);
    }

    @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogShape(Lnet/minecraft/client/render/FogShape;)V",shift = At.Shift.AFTER))
    private static void returnFogColorToNormal(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Share("colors") LocalRef<float[]> oldShaderColors) {
        float[] colors = oldShaderColors.get();
        RenderSystem.setShaderFogColor(colors[0],colors[1],colors[2],colors[3]);
    }

}
