package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Mathable;

public interface RPFeature extends UVersionable, Identifiable, Mathable, NonThrowableJSONBehavior {

    boolean isEnabled();
    boolean canBeEnabled();

    void enable();
    void disable();

    void load();
    void unload();
}
