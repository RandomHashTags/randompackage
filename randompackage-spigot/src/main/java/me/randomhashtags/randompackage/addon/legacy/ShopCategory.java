package me.randomhashtags.randompackage.addon.legacy;

import me.randomhashtags.randompackage.addon.util.Inventoryable;
import me.randomhashtags.randompackage.addon.obj.ShopItem;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public abstract class ShopCategory extends RPAddonSpigot implements Inventoryable {
    public ShopCategory(@Nullable File file) {
        super(file);
    }

    public abstract List<ShopItem> getShopItems();
    public abstract ShopItem getItem(int slot);
}
