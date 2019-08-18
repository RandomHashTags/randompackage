package me.randomhashtags.randompackage.addons.legacy;

import me.randomhashtags.randompackage.api.Shop;
import me.randomhashtags.randompackage.addons.utils.Inventoryable;
import me.randomhashtags.randompackage.addons.objects.ShopItem;
import me.randomhashtags.randompackage.utils.addons.RPAddon;

import java.util.List;

public abstract class ShopCategory extends RPAddon implements Inventoryable {
    public static Shop shop;
    public abstract List<ShopItem> getShopItems();
    public abstract ShopItem getItem(int slot);
}
