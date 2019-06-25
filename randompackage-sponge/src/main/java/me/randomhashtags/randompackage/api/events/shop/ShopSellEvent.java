package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.shop.ShopItem;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public class ShopSellEvent extends RandomPackageEvent {
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
