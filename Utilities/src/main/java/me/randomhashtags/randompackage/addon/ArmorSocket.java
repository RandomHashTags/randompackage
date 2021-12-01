package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Nameable;

public interface ArmorSocket extends Nameable, Identifiable {
    String getItemType();
    int getLimit();
}
