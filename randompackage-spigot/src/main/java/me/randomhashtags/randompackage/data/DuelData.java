package me.randomhashtags.randompackage.data;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface DuelData {
    boolean receivesNotifications();
    void setReceivesNotifications(boolean receivesNotifications);
    List<ItemStack> getCollection();
    DuelRankedData getRankedData();
}
