package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.Mask;
import me.randomhashtags.randompackage.event.enums.ArmorEventReason;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MaskEquipEvent extends RPEventCancellable {
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
