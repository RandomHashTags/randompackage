package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.objects.StackedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;

public class MobStackDepleteEvent extends AbstractEvent implements Cancellable {
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
