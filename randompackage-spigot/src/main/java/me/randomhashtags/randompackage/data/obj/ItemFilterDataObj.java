package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.ItemFilterData;
import me.randomhashtags.randompackage.universal.UMaterial;

import java.util.List;

public class ItemFilterDataObj implements ItemFilterData {
    private boolean enabled;
    private List<UMaterial> filter;

    public ItemFilterDataObj(boolean enabled, List<UMaterial> filter) {
        this.enabled = enabled;
        this.filter = filter;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public List<UMaterial> getFilteredItems() {
        return filter;
    }
}
