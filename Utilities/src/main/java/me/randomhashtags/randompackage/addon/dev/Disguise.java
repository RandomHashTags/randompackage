package me.randomhashtags.randompackage.addon.dev;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Slotable;

public interface Disguise extends Identifiable, Nameable, Slotable {
    String getEntityType();
    boolean allowsBaby();
}
