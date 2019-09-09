package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.Mask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaskApplyEvent extends RPEvent {
    public final Mask mask;
    public final ItemStack item;
    public MaskApplyEvent(Player player, Mask mask, ItemStack item) {
        super(player);
        this.mask = mask;
        this.item = item;
    }
}
