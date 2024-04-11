package net.ramgames.ecliptrix.events;

import net.minecraft.util.Identifier;
import net.ramgames.ecliptrix.CelestialEventHandler;
import net.ramgames.ecliptrix.CelestialEvents;

public class TotalSolarEclipseEventHandler implements CelestialEventHandler {

    @Override
    public Identifier getEventType() {
        return CelestialEvents.TOTAL_SOLAR;
    }
}
