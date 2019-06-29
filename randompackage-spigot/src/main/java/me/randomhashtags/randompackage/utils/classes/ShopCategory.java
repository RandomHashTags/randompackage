package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.api.Shop;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopCategory {
    public static HashMap<String, ShopCategory> categories;
    private static Shop shop;

    private File f;
    private YamlConfiguration yml;
    private UInventory inventory;
    private List<ShopItem> items;

    public ShopCategory(File f) {
        if(categories == null) {
            categories = new HashMap<>();
            shop = Shop.getShop();
        }
        this.f = f;
        yml = YamlConfiguration.loadConfiguration(f);
        categories.put(getYamlName(), this);
    }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return f.getName().split("\\.yml")[0]; }
    public String getInventoryTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("title")); }
    public UInventory getInventory() {
        if(inventory == null) {
            inventory = new UInventory(null, yml.getInt("size"), getInventoryTitle());
            final Inventory ii = inventory.getInventory();
            items = new ArrayList<>();
            final ItemStack back = shop.back;
            for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
                final String p = yml.getString("gui." + s + ".prices");
                final String[] o = p != null ? p.split(";") : null;
                final String custom = yml.getString("gui." + s + ".custom.item"), i = yml.getString("gui." + s + ".item"), d = i.toUpperCase();
                final boolean isBack = d.equals("BACK");
                final ItemStack display = isBack ? back : shop.d(yml, "gui." + s);
                final ItemStack purchased = !isBack ? custom != null ? shop.d(yml, "gui." + s + ".custom") : shop.d(null, i) : null;
                if(!isBack && custom == null) purchased.setAmount(display.getAmount());
                final int slot = yml.getInt("gui." + s + ".slot");
                items.add(new ShopItem(s, slot, yml.getString("gui." + s + ".opens"), display, purchased, o != null ? Double.parseDouble(o[0]) : 0, o != null ? Double.parseDouble(o[1]) : 0));
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
    public static void deleteAll() {
        categories = null;
        shop = null;
    }
}
