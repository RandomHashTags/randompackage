package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.Mask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MaskApplyEvent extends RPEvent {
    public final Mask mask;
    public final ItemStack item;
    public MaskApplyEvent(Player player, Mask mask, ItemStack item) {
        super(player);
        this.mask = mask;
        this.item = item;
    }
}
