package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.objects.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopSellEvent extends AbstractEvent {
    public final Player player;
    public final ShopItem shopitem;
    public final ItemStack item;
    public final int amount;
    public final double profit;
    public ShopSellEvent(Player player, ShopItem shopitem, ItemStack item, int amount, double profit) {
        this.player = player;
        this.shopitem = shopitem;
        this.item = item;
        this.amount = amount;
        this.profit = profit;
    }
}
