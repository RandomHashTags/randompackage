package me.randomhashtags.randompackage.api.events;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public abstract class RandomPackageEvent extends AbstractEvent {
    @Override
    public Cause getCause() {
        return Sponge.getCauseStackManager().getCurrentCause();
    }
}
