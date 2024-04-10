package net.ramgames.ecliptrix;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface CelestialEventHandler {

    boolean modifiesAmbientDarkness();

    int ambientDarkness(int original, long curTime, long climax, World world);

    Identifier getEventType();
}
