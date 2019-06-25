package me.randomhashtags.randompackage.api.events.mobstacker;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.StackedEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Cancellable;

public class MobStackDepleteEvent extends RandomPackageEvent implements Cancellable {
    private boolean cancelled;
    public final StackedEntity stack;
    public final Entity killer;
    public int amount;
    public MobStackDepleteEvent(StackedEntity stack, Entity killer, int amount) {
        this.stack = stack;
        this.killer = killer;
        this.amount = amount;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
