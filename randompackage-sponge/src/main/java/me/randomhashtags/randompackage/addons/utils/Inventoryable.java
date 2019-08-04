package me.randomhashtags.randompackage.addons.utils;

import org.spongepowered.api.item.inventory.Inventory;

public interface Inventoryable extends Identifiable {
    String getTitle();
    Inventory getInventory();
}
