package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.api.Shop;
import me.randomhashtags.randompackage.addons.utils.Inventoryable;
import me.randomhashtags.randompackage.addons.objects.ShopItem;

import java.util.List;

public abstract class ShopCategory extends Inventoryable {
    protected static Shop shop;
    public abstract List<ShopItem> getShopItems();
    public abstract ShopItem getItem(int slot);
}
