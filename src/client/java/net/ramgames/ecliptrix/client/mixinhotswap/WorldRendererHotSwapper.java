package net.ramgames.ecliptrix.client.mixinhotswap;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.ramgames.ecliptrix.client.EcliptrixClient;
import org.joml.Matrix4f;

public interface WorldRendererHotSwapper {

    static float changeSunHeading(ClientWorld world, float deg) {
        if(world == null) return deg;
        if(deg == -90.0F) return deg - EcliptrixClient.getHeading(world.getTimeOfDay(), EcliptrixClient.daysInSolarCycle, 30);
        return deg;
    }

    static Matrix4f changeMoonHeading(ClientWorld world, MatrixStack matrices, float tickDelta, Matrix4f matrix4f2) {
        if(world == null) return matrix4f2;
        matrices.pop();
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f - EcliptrixClient.getHeading(world.getTimeOfDay(), EcliptrixClient.daysInLunarCycle, 40)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
        return matrices.peek().getPositionMatrix();
    }
}
