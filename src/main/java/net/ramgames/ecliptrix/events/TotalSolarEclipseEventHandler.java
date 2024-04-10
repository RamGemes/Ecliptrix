package net.ramgames.ecliptrix.events;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.ramgames.ecliptrix.CelestialEventHandler;
import net.ramgames.ecliptrix.CelestialEvents;
import net.ramgames.ecliptrix.Ecliptrix;

import java.util.function.Supplier;

public class TotalSolarEclipseEventHandler implements CelestialEventHandler {
    @Override
    public boolean modifiesAmbientDarkness() {
        return true;
    }

    @Override
    public int ambientDarkness(int original, long curTime, long climax, World world) {
        //Ecliptrix.LOGGER.info("isClient: {}", world.isClient());
        Supplier<Integer> run = () -> {
            if(false)Ecliptrix.LOGGER.info("", original);
            long timeTill = climax - curTime;
            if(timeTill > 1300) return original;
            else if(timeTill > 100) return (int) (Math.pow((1200-timeTill - 100) / 1200d, 5) * original);
            else if(timeTill > -100) return 0;
            else if(timeTill > -1300) return (int) (15-(Math.pow((-timeTill - 100) / 1200d, 5) * original));
            else return 0;
        };
        int result = run.get();
        //Ecliptrix.LOGGER.info("result: {}", result);
        return result;
    }

    @Override
    public Identifier getEventType() {
        return CelestialEvents.TOTAL_SOLAR;
    }
}
