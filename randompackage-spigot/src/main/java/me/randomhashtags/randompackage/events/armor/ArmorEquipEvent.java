package me.randomhashtags.randompackage.events.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipEvent extends ArmorEvent {
    public ArmorEquipEvent(Player player, ArmorEventReason reason, ItemStack item) {
        super(player, reason, item);
    }
}
