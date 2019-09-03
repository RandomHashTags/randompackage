package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.Mask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaskEquipEvent extends AbstractCancellable {
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
}
