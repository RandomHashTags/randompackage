package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.utils.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopPurchaseEvent extends AbstractEvent {
    public final Player player;
    public final ShopItem shopitem;
    public final ItemStack item;
    public final int amount;
    public final double cost;
    public ShopPurchaseEvent(Player player, ShopItem shopitem, ItemStack item, int amount, double cost) {
        this.player = player;
        this.shopitem = shopitem;
        this.item = item;
        this.amount = amount;
        this.cost = cost;
    }
}
