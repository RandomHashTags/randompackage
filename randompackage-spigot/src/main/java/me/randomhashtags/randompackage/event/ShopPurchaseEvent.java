package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.obj.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class ShopPurchaseEvent extends ShopEvent {
    public ShopPurchaseEvent(Player player, ShopItem shopitem, ItemStack item, int amount, BigDecimal cost) {
        super(player, shopitem, item, amount, cost);
    }
}
