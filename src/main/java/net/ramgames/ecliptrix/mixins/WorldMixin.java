package net.ramgames.ecliptrix.mixins;

import net.minecraft.world.World;
import net.ramgames.ecliptrix.CelestialEventHandler;
import net.ramgames.ecliptrix.Ecliptrix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public abstract long getTimeOfDay();

    @Shadow private int ambientDarkness;

    @Inject(method = "calculateAmbientDarkness", at = @At("TAIL"))
    private void modifyAmbientDarkness(CallbackInfo ci) {
        int original = this.ambientDarkness;
        for(CelestialEventHandler handler : Ecliptrix.getCelestialEventHandlers())
            if(handler.getEventType() == Ecliptrix.getCurrentCelestialEvent().id()) original = handler.ambientDarkness(original, getTimeOfDay(), Ecliptrix.getCurrentCelestialEvent().climax(), (World)(Object) this);
        this.ambientDarkness = original;
    }

}
