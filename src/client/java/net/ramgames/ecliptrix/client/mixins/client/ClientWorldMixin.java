package net.ramgames.ecliptrix.client.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.world.ClientWorld;
import net.ramgames.ecliptrix.client.CelestialEventRenderer;
import net.ramgames.ecliptrix.client.EcliptrixClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Shadow @Final private ClientWorld.Properties clientWorldProperties;

    @ModifyReturnValue(method = "getSkyBrightness", at = @At("RETURN"))
    private float modifySkyBrightness(float original) {
        for(CelestialEventRenderer renderer : EcliptrixClient.getActiveRenderers())
            if(renderer.getEventType() == EcliptrixClient.curEvent) original = renderer.modifySkyBrightness(clientWorldProperties.getTimeOfDay(), EcliptrixClient.eventClimax, (ClientWorld)(Object)this, original);
        return original;
    }

}
