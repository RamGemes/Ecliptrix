package net.ramgames.ecliptrix.client.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.ramgames.ecliptrix.client.CelestialEventRenderer;
import net.ramgames.ecliptrix.client.EcliptrixClient;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Nullable private ClientWorld world;


    @Shadow @Final private static Identifier SUN;

    @ModifyArg(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
    private float changeSunHeading(float deg) {
        if(world == null) return deg;
        if(deg == -90.0F) return deg - EcliptrixClient.getHeading(world.getTimeOfDay(), EcliptrixClient.daysInSolarCycle, 30);
        return deg;
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getMoonPhase()I"))
    private void changeMoonHeading(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(ordinal = 1) LocalRef<Matrix4f> matrix4f2) {
        if(world == null) return;
        matrices.pop();
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f - EcliptrixClient.getHeading(world.getTimeOfDay(), EcliptrixClient.daysInLunarCycle, 40)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
        matrix4f2.set(matrices.peek().getPositionMatrix());
    }


    @WrapOperation(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getMoonPhase()I"))
    private int changeMoonPhase(ClientWorld instance, Operation<Integer> original) {
        return (int) ((instance.getTimeOfDay() % (int)(24000 * EcliptrixClient.daysInLunarCycle)) / (24000 * EcliptrixClient.daysInLunarCycle) * 8);
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;method_23787(F)F"))
    private void renderCelestialEvents(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local BufferBuilder builder) {
        if(this.world == null) return;
        for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers()) {
            matrices.pop();
            matrices.push();
            renderer.render(this.world.getTimeOfDay(), EcliptrixClient.eventClimax, this.world, matrices, builder, tickDelta);
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
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
        for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers()) {
            color = renderer.modifySkyColor(this.world.getTimeOfDay(), EcliptrixClient.eventClimax, this.world, tickDelta, color);
        }
        f.set((float) color.getX());
        g.set((float) color.getY());
        h.set((float) color.getZ());
    }

    @WrapOperation(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    private void renderSunshine(int texture, Identifier id, Operation<Void> original, @Local BufferBuilder builder, @Local(ordinal = 1) Matrix4f positionMatrix) {
        if(id != SUN || this.world == null) {
            original.call(texture, id);
            return;
        }
        RenderSystem.setShaderTexture(0, EcliptrixClient.SUNSHINE);
        float[] colors = RenderSystem.getShaderColor();
        float alpha = 1;
        for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers()) alpha = renderer.sunshineVisibility(world.getTimeOfDay(), EcliptrixClient.eventClimax, world, alpha);
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2],alpha);
        EcliptrixClient.drawShape(-30f, 30f, -30f, 30f, 100, positionMatrix, builder);
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2], 1);

        original.call(texture, id);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void preventRenderIfTintedSpyglass(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        if(this.world == null) return;
        if(EcliptrixClient.getCurrentSpyglassLens() == Items.TINTED_GLASS) {
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
            for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers()) {
                renderer.renderWithTintedSpyglass(this.world.getTimeOfDay(), EcliptrixClient.eventClimax, this.world, matrices, Tessellator.getInstance().getBuffer(), tickDelta);
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            }
            ci.cancel();
        }
    }
}
