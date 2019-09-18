package me.randomhashtags.randompackage.event.mob;

import me.randomhashtags.randompackage.addon.obj.StackedEntity;
import me.randomhashtags.randompackage.event.AbstractCancellable;
import org.bukkit.entity.Entity;

public class MobStackDepleteEvent extends AbstractCancellable {
    public final StackedEntity stack;
    public final Entity killer;
    public int amount;
    public MobStackDepleteEvent(StackedEntity stack, Entity killer, int amount) {
        this.stack = stack;
        this.killer = killer;
        this.amount = amount;
    }
}
