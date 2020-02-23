package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.ItemFilterData;
import me.randomhashtags.randompackage.universal.UMaterial;

import java.util.List;

public class ItemFilterDataObj implements ItemFilterData {
    private boolean active;
    private List<UMaterial> filter;

    public ItemFilterDataObj(boolean active, List<UMaterial> filter) {
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
    public List<UMaterial> getFilteredItems() {
        return filter;
    }
}
