package me.randomhashtags.randompackage.event.mob;

import me.randomhashtags.randompackage.addon.obj.StackedEntity;
import me.randomhashtags.randompackage.event.AbstractCancellable;

public class MobStackMergeEvent extends AbstractCancellable {
    public final StackedEntity stackedEntity;
    public final int newSize;
    public MobStackMergeEvent(StackedEntity stackedEntity, int newSize) {
        this.stackedEntity = stackedEntity;
        this.newSize = newSize;
    }
}
