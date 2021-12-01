package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Mathable;

public abstract class RPFeature extends UVersionable implements Identifiable, Mathable {

    public abstract boolean isEnabled();
    public abstract boolean canBeEnabled();

    public abstract void enable();
    public abstract void disable();

    public abstract void load();
    public abstract void unload();
}
