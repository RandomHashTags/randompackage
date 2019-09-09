package me.randomhashtags.randompackage.event.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorPieceBreakEvent extends ArmorEvent {
    public ArmorPieceBreakEvent(Player player, ItemStack item) {
        super(player, ArmorEventReason.BREAK, item);
    }
}
