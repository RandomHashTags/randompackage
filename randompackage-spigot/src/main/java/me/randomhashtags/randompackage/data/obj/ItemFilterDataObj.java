package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.ItemFilterData;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class ItemFilterDataObj implements ItemFilterData {
    private boolean active;
    private Set<UMaterial> filter;

    public ItemFilterDataObj(boolean active, @NotNull Set<UMaterial> filter) {
        this.active = active;
        this.filter = filter;
    }

    @Override
    public boolean isActive() {
        return active;
    }
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void toggle() {
        active = !active;
    }

    @Override
    public @NotNull Set<UMaterial> getFilteredItems() {
        return filter;
    }

    @Override
    public void addFilteredItem(@NotNull UMaterial material) {
        filter.add(material);
    }
}
