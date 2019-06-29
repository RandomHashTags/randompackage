package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;
import me.randomhashtags.randompackage.utils.classes.Mask;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class MaskEquipEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final Mask mask;
    public final ItemStack helmet;
    public final PlayerArmorEvent.ArmorEventReason reason;
    public MaskEquipEvent(Player player, Mask mask, ItemStack helmet, PlayerArmorEvent.ArmorEventReason reason) {
        this.player = player;
        this.mask = mask;
        this.helmet = helmet;
        this.reason = reason;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
