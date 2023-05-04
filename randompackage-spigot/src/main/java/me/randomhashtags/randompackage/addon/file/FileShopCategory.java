package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.obj.ShopItem;
import me.randomhashtags.randompackage.api.Shop;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FileShopCategory extends ShopCategory {
    private final String title;
    private final UInventory inventory;
    private final List<ShopItem> items;

    public FileShopCategory(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        title = parse_string_in_json(json, "title");

        final int size = parse_int_in_json(json, "size");
        inventory = new UInventory(null, size, title);
        final Inventory ii = inventory.getInventory();
        items = new ArrayList<>();
        final Shop shop = Shop.INSTANCE;
        final ItemStack back = shop.back;
        final BigDecimal zero = BigDecimal.ZERO;
        final JSONObject gui_json = json.getJSONObject("gui");
        final Iterator<String> gui_keys = gui_json.keys();
        for(Iterator<String> it = gui_keys; it.hasNext(); ) {
            String s = it.next();
            final JSONObject gui_item_json = gui_json.getJSONObject(s);
            final String[] prices = parse_string_in_json(gui_item_json, "prices").split(";");
            final JSONObject custom_json = gui_item_json.getJSONObject("custom");
            final String custom = parse_string_in_json(custom_json, "item"), item_string = parse_string_in_json(gui_item_json, "item"), item_string_uppercased = item_string.toUpperCase();
            final boolean isBack = item_string_uppercased.equals("BACK");
            final ItemStack display = isBack ? back : shop.create_item_stack(gui_json, s);
            if(display != null) {
                final boolean custom_is_empty = custom.isEmpty();
                final ItemStack purchased = !isBack ? !custom_is_empty ? shop.create_item_stack(gui_item_json, "custom") : shop.createItemStack(null, item_string) : null;
                if(!isBack && custom_is_empty) {
                    purchased.setAmount(display.getAmount());
                }
                final int slot = parse_int_in_json(gui_item_json, "slot");
                final String opens_category = parse_string_in_json(gui_item_json, "opens", null);
                final List<String> commands = parse_list_string_in_json(gui_item_json, "commands", null);
                items.add(new ShopItem(s, slot, opens_category, display, purchased, prices != null ? BigDecimal.valueOf(Double.parseDouble(prices[0])) : zero, prices != null ? BigDecimal.valueOf(Double.parseDouble(prices[1])): zero, commands));
                ii.setItem(slot, display);
            }
        }

        register(Feature.SHOP_CATEGORY, this);
    }

    public String getTitle() {
        return title;
    }
    public UInventory getInventory() {
        return inventory;
    }
    public List<ShopItem> getShopItems() {
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
