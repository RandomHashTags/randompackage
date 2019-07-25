package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.Mask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaskApplyEvent extends AbstractEvent {
    public final Player player;
    public final Mask mask;
    public final ItemStack item;
    public MaskApplyEvent(Player player, Mask mask, ItemStack item) {
        this.player = player;
        this.mask = mask;
        this.item = item;
    }
}
