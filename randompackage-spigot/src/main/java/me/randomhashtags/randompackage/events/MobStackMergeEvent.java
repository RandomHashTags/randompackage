package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.objects.StackedEntity;

public class MobStackMergeEvent extends AbstractCancellable {
    public final StackedEntity stackedEntity;
    public final int newSize;
    public MobStackMergeEvent(StackedEntity stackedEntity, int newSize) {
        this.stackedEntity = stackedEntity;
        this.newSize = newSize;
    }
}
