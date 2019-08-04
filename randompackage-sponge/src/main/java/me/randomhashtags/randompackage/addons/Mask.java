package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Applyable;

import java.util.List;

public interface Mask extends Applyable {
    String getOwner();
    List<String> getAttributes();
}
