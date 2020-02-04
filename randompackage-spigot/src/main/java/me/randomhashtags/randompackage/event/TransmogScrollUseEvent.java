package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.Scroll;
import me.randomhashtags.randompackage.addon.TransmogScroll;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TransmogScrollUseEvent extends ScrollUseEvent {
    public TransmogScrollUseEvent(Player player, Scroll scroll, ItemStack usedOnItem) {
        super(player, scroll, usedOnItem);
    }
    @Override
    public TransmogScroll getScroll() {
        return (TransmogScroll) scroll;
    }
}
