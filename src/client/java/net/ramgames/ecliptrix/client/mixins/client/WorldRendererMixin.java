package net.ramgames.ecliptrix.client.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.ramgames.ecliptrix.client.CelestialEventRenderer;
import net.ramgames.ecliptrix.client.EcliptrixClient;
import net.ramgames.ecliptrix.client.mixinhotswap.WorldRendererHotSwapper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {


    @Shadow @Nullable private ClientWorld world;

    @Shadow private int cameraChunkX;

    @ModifyArg(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
    private float changeSunHeading(float deg) {
        return WorldRendererHotSwapper.changeSunHeading(this.world, deg);
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getMoonPhase()I"))
    private void changeMoonHeading(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(ordinal = 1) LocalRef<Matrix4f> matrix4f2) {
        matrix4f2.set(WorldRendererHotSwapper.changeMoonHeading(this.world, matrices, tickDelta, matrix4f2.get()));
    }


    @WrapOperation(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getMoonPhase()I"))
    private int changeMoonPhase(ClientWorld instance, Operation<Integer> original) {
        return (int) ((instance.getTimeOfDay() % (int)(24000 * EcliptrixClient.daysInLunarCycle)) / (24000 * EcliptrixClient.daysInLunarCycle) * 8);
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;method_23787(F)F", shift = At.Shift.BEFORE))
    private void renderCelestialEvents(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local BufferBuilder builder) {
        if(this.world == null) return;
        for(CelestialEventRenderer renderer : EcliptrixClient.getRenderers()) {
            if(renderer.getEventType() != EcliptrixClient.curEvent) return;
            matrices.pop();
            matrices.push();
            renderer.render(this.world.getTimeOfDay(), EcliptrixClient.eventClimax, this.world, matrices, builder, tickDelta);
        }
        matrices.pop();
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFogColor()V"))
    private void renderSkyCelestialEvents(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(ordinal = 1) LocalFloatRef f, @Local(ordinal = 2) LocalFloatRef g, @Local(ordinal = 3) LocalFloatRef h) {
        if(this.world == null) return;
        Vec3d color = new Vec3d(f.get(), g.get(), h.get());
        for(CelestialEventRenderer renderer : EcliptrixClient.getRenderers()) {
            if(renderer.getEventType() != EcliptrixClient.curEvent) return;
            color = renderer.modifySkyColor(this.world.getTimeOfDay(), EcliptrixClient.eventClimax, this.world, matrices, tickDelta, color);
        }
        f.set((float) color.getX());
        g.set((float) color.getY());
        h.set((float) color.getZ());
    }
}
