package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.obj.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public abstract class ShopEvent extends RPEventCancellable {
    private final ShopItem shopitem;
    private ItemStack item;
    private int amount;
    private BigDecimal cost;
    public ShopEvent(Player player, ShopItem shopitem, ItemStack item, int amount, BigDecimal cost) {
        super(player);
        this.shopitem = shopitem;
        this.item = item;
        this.amount = amount;
        this.cost = cost;
    }
    public ShopItem getShopItem() { return shopitem; }
    public ItemStack getItem() { return item; }
    public void setPurchasedItem(ItemStack item) { this.item = item; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public BigDecimal getTotal() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
}
