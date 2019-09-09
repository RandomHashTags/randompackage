package me.randomhashtags.randompackage.addon.legacy;

import me.randomhashtags.randompackage.api.Shop;
import me.randomhashtags.randompackage.addon.util.Inventoryable;
import me.randomhashtags.randompackage.addon.obj.ShopItem;
import me.randomhashtags.randompackage.util.addon.RPAddon;

import java.util.List;

public abstract class ShopCategory extends RPAddon implements Inventoryable {
    public static Shop shop;
    public abstract List<ShopItem> getShopItems();
    public abstract ShopItem getItem(int slot);
}
