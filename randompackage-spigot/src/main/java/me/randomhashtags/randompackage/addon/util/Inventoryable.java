package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.util.universal.UInventory;

public interface Inventoryable extends Identifiable {
    String getTitle();
    UInventory getInventory();
}
