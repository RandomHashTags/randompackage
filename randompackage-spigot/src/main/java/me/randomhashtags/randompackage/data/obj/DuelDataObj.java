package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.DuelData;
import me.randomhashtags.randompackage.data.DuelRankedData;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class DuelDataObj implements DuelData {
    private boolean notifications;
    private final List<ItemStack> collection;
    private final DuelRankedData ranked;

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
    public void setReceivesNotifications(boolean receivesNotifications) {
        this.notifications = receivesNotifications;
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
