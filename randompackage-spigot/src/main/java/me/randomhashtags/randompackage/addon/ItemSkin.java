package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Nameable;

import java.util.List;

public interface ItemSkin extends Nameable, Attributable {
    String getMaterial();
    List<String> getLore();
}
