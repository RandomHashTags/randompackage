package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.objects.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class ShopPurchaseEvent extends RPEvent {
    public final ShopItem shopitem;
    public final ItemStack item;
    public final int amount;
    public final BigDecimal cost;
    public ShopPurchaseEvent(Player player, ShopItem shopitem, ItemStack item, int amount, BigDecimal cost) {
        super(player);
        this.shopitem = shopitem;
        this.item = item;
        this.amount = amount;
        this.cost = cost;
    }
}
