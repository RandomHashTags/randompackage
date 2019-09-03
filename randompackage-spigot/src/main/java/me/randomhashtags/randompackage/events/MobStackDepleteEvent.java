package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.objects.StackedEntity;
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
