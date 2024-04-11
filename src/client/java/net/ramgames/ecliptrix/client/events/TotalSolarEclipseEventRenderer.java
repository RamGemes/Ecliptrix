package net.ramgames.ecliptrix.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.ramgames.ecliptrix.CelestialEvents;
import net.ramgames.ecliptrix.client.CelestialEventRenderer;
import net.ramgames.ecliptrix.client.EcliptrixClient;
import org.joml.Matrix4f;

public class TotalSolarEclipseEventRenderer implements CelestialEventRenderer {

    @Override
    public void render(long curTime, long climax, ClientWorld world, MatrixStack stack, BufferBuilder bufferBuilder, float tickDelta) {
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90 - EcliptrixClient.getHeading(curTime, EcliptrixClient.daysInSolarCycle, 30)));
        double timeBeforeClimax = climax - curTime;
        float angle;
        if(timeBeforeClimax > 1300) angle = 0;
        else if(timeBeforeClimax > 100) angle = (float) ((1300 - timeBeforeClimax) * 0.18570) / 26f;
        else if(timeBeforeClimax > -100) angle = 8.57f;
        else if(timeBeforeClimax > -1300) angle = (float) ((1100 - timeBeforeClimax) * 0.18570) / 26f;
        else angle = 0;
        float dilation;
        if(timeBeforeClimax > 90) dilation = 400;
        else if(timeBeforeClimax > 60) dilation = (float) (400 + 25 * EcliptrixClient.sineEaseInOut(1-(timeBeforeClimax-60)/30d));
        else if(timeBeforeClimax > -60) dilation = 425;
        else if(timeBeforeClimax > -90) dilation = (float) (425 - 25 * EcliptrixClient.sineEaseInOut((timeBeforeClimax-60)/30d));
        else dilation = 400;
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360+angle-8.57f));
        Matrix4f position = stack.peek().getPositionMatrix();
        RenderSystem.defaultBlendFunc();
        if(angle <= 0f || angle > 17.14f) return;
        float rise = Math.max(-30f,30f-angle*7);
        float fall = Math.min(30f, 30-angle*7+60);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        if(dilation != 400) {
            RenderSystem.setShaderTexture(0, EcliptrixClient.SOLAR_CORONA);
            EcliptrixClient.drawShape(-30f, 30f, rise, fall, 400, position, bufferBuilder);
        }
        RenderSystem.setShaderTexture(0, EcliptrixClient.ECLIPSE);
        EcliptrixClient.drawShape(-30f, 30f, rise, fall, dilation, position, bufferBuilder);
    }

    @Override
    public Vec3d modifySkyColor(long curTime, long climax, ClientWorld world, MatrixStack stack, float tickDelta, Vec3d original) {
        long timeTill = climax - curTime;
        if(timeTill > 1300) return original;
        if(timeTill > 100) return original.multiply(1-skyModifier(timeTill));
        else if(timeTill > -100) return original.multiply(0d);
        else if(timeTill > -1300) return original.multiply(1-skyModifier(-timeTill));
        return original;
    }

    @Override
    public float modifySkyBrightness(long curTime, long climax, ClientWorld world, float original) {
        long timeTill = climax - curTime;
        if(timeTill > 1300) return original;
        if(timeTill > 100) return (float) (original * (1 - skyModifier(timeTill)));
        else if(timeTill > -100) return 0;
        else if(timeTill > -1300) {
            return (float) (original * (1 - skyModifier(-timeTill)));
        }
        return original;
    }

    @Override
    public float sunshineVisibility(long curTime, long climax, ClientWorld world, float original) {
        long timeTill = climax - curTime;
        if(timeTill > 1300) return original;
        else if(timeTill > 100) return (float) (original * (timeTill-100)/1200d);
        else if(timeTill > -100) return 0;
        else if (timeTill > -1300) return (float) (original * ((-timeTill-100)/1200d));
        return original;
    }

    @Override
    public Vec3d modifyFogColor(long curTime, long climax, ClientWorld world, Vec3d original) {
        //Ecliptrix.LOGGER.info("orig: {}", original);
        float v = (float) original.x;
        return original.multiply(modifySkyBrightness(curTime, climax, world, v));
    }

    private double skyModifier(long timeTill) {
        return Math.pow((1200-timeTill + 100) / 1200d, 5);
    }

    @Override
    public Identifier getEventType() {
        return CelestialEvents.TOTAL_SOLAR;
    }
}
