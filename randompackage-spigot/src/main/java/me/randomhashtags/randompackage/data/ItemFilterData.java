package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.universal.UMaterial;

import java.util.List;

public interface ItemFilterData {
    boolean isActive();
    void setActive(boolean active);
    void toggle();
    List<UMaterial> getFilteredItems();
}
