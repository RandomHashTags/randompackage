package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.ShopCategory;
import me.randomhashtags.randompackage.addons.objects.ShopItem;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FileShopCategory extends ShopCategory {
    private UInventory inventory;
    private List<ShopItem> items;

    public FileShopCategory(File f) {
        load(f);
        addShopCategory(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', yml.getString("title"));
    }
    public UInventory getInventory() {
        if(inventory == null) {
            inventory = new UInventory(null, yml.getInt("size"), getTitle());
            final Inventory ii = inventory.getInventory();
            items = new ArrayList<>();
            final ItemStack back = shop.back;
            final BigDecimal zero = BigDecimal.ZERO;
            for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
                final String p = yml.getString("gui." + s + ".prices");
                final String[] o = p != null ? p.split(";") : null;
                final String custom = yml.getString("gui." + s + ".custom.item"), i = yml.getString("gui." + s + ".item"), d = i.toUpperCase();
                final boolean isBack = d.equals("BACK");
                final ItemStack display = isBack ? back : shop.d(yml, "gui." + s);
                final ItemStack purchased = !isBack ? custom != null ? shop.d(yml, "gui." + s + ".custom") : shop.d(null, i) : null;
                if(!isBack && custom == null) purchased.setAmount(display.getAmount());
                final int slot = yml.getInt("gui." + s + ".slot");
                items.add(new ShopItem(s, slot, yml.getString("gui." + s + ".opens"), display, purchased, o != null ? BigDecimal.valueOf(Double.parseDouble(o[0])) : zero, o != null ? BigDecimal.valueOf(Double.parseDouble(o[1])): zero));
                ii.setItem(slot, display);
            }
        }
        return inventory;
    }
    public List<ShopItem> getShopItems() {
        if(items == null) getInventory();
        return items;
    }
    public ShopItem getItem(int slot) {
        for(ShopItem i : getShopItems()) {
            if(i.slot == slot) {
                return i;
            }
        }
        return null;
    }
}
