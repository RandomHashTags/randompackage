package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.DuelData;
import me.randomhashtags.randompackage.data.DuelRankedData;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DuelDataObj implements DuelData {
    private boolean notifications;
    private List<ItemStack> collection;
    private DuelRankedData ranked;

    public DuelDataObj(boolean notifications, List<ItemStack> collection, DuelRankedData ranked) {
        this.notifications = notifications;
        this.collection = collection;
        this.ranked = ranked;
    }

    @Override
    public boolean receivesNotifications() {
        return notifications;
    }

    @Override
    public List<ItemStack> getCollection() {
        return collection;
    }

    @Override
    public DuelRankedData getRankedData() {
        return ranked;
    }
}
