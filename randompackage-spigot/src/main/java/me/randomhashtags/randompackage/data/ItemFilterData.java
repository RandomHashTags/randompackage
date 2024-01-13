package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface ItemFilterData {
    boolean isActive();
    void setActive(boolean active);
    void toggle();
    @NotNull Set<UMaterial> getFilteredItems();
    void addFilteredItem(@NotNull UMaterial material);
}
