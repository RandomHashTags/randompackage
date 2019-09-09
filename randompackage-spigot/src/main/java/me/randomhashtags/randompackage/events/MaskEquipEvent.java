package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.Mask;
import me.randomhashtags.randompackage.events.armor.ArmorEventReason;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaskEquipEvent extends RPEventCancellable {
    public final Mask mask;
    public final ItemStack helmet;
    public final ArmorEventReason reason;
    public MaskEquipEvent(Player player, Mask mask, ItemStack helmet, ArmorEventReason reason) {
        super(player);
        this.mask = mask;
        this.helmet = helmet;
        this.reason = reason;
    }
}
