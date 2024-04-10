package net.ramgames.ecliptrix.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
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

    private static final Identifier ID = new Identifier("ecliptrix:total_solar");

    @Override
    public void render(long curTime, long climax, ClientWorld world, MatrixStack stack, BufferBuilder bufferBuilder, float tickDelta) {
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90 - EcliptrixClient.getHeading(curTime, EcliptrixClient.daysInSolarCycle, 30)));
        //Ecliptrix.LOGGER.info("climax: {}", climax);
        //Ecliptrix.LOGGER.info("curTime: {}", curTime);
        double timeBeforeClimax = climax - curTime;
        float angle;
        //Ecliptrix.LOGGER.info("tbc: {}", timeBeforeClimax);
        if(timeBeforeClimax > 1300) angle = 0;
        else if(timeBeforeClimax > 100) angle = (float) ((1300 - timeBeforeClimax) * 0.18570) / 26f;
        else if(timeBeforeClimax > -100) angle = 8.57f;
        else if(timeBeforeClimax > -1300) angle = (float) ((1100 - timeBeforeClimax) * 0.18570) / 26f;
        else angle = 0;
        float dilation;
        if(timeBeforeClimax > 60) dilation = 400;
        else if(timeBeforeClimax > 40) dilation = (float) (400 + 25 * EcliptrixClient.sineEaseInOut(1-(timeBeforeClimax-40)/20d));
        else if(timeBeforeClimax > -40) dilation = 425;
        else if(timeBeforeClimax > -60) dilation = (float) (425 - 25 * EcliptrixClient.sineEaseInOut((timeBeforeClimax-40)/20d));
        else dilation = 400;
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360+angle-8.57f));
        Matrix4f position = stack.peek().getPositionMatrix();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, EcliptrixClient.ECLIPSE);
        if(angle <= 0f || angle > 17.14f) return;
        float rise = Math.max(-30f,30f-angle*7);
        float fall = Math.min(30f, 30-angle*7+60);
        drawShape(-30f, 30f, rise, fall, dilation, position, bufferBuilder);
        /*if(timeBeforeClimax < 40 && timeBeforeClimax > -40) {
            RenderSystem.setShader(GameRenderer::getRenderTypeSolidProgram);
            RenderSystem.setShaderColor(1f,0f,0f,1f);
            drawShape(-30f, 30f, rise, fall, dilation-10, position, bufferBuilder);
            RenderSystem.setShaderColor(0f,0f,1f,1f);
            drawShape(-30f, 30f, rise, fall, dilation-10, position, bufferBuilder);
            //RenderSystem.setShaderColor(0f,1f,0f,1f);
            //drawShape(-30f, 30f, rise, fall, dilation-10, position, bufferBuilder);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        }*/
    }

    @Override
    public Vec3d modifySkyColor(long curTime, long climax, ClientWorld world, MatrixStack stack, float tickDelta, Vec3d skyColor) {
        long timeTill = climax - curTime;
        if(timeTill > 1300) return skyColor;
        if(timeTill > 100) skyColor = skyColor.multiply(1-Math.pow((1200-timeTill - 100) / 1200d, 5));
        else if(timeTill > -100) skyColor = skyColor.multiply(0d);
        else if(timeTill > -1300) skyColor = skyColor.multiply(Math.pow((1200-timeTill - 100) / 1200d, 5));
        return skyColor;
    }

    private void drawShape(float x1, float x2, float y1, float y2, float size, Matrix4f positionMatrix, BufferBuilder bufferBuilder) {
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(positionMatrix, x1, size, y1).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(positionMatrix, x2, size, y1).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(positionMatrix, x2, size, y2).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(positionMatrix, x1, size, y2).texture(0.0f, 1.0f).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    @Override
    public Identifier getEventType() {
        return CelestialEvents.TOTAL_SOLAR;
    }
}
