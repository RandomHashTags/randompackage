package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FilterCategory;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

public final class FileFilterCategory extends RPAddonSpigot implements FilterCategory {
    private final String title;
    private final UInventory gui;
    public FileFilterCategory(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        final String title = parse_string_in_json(json, "title");
        this.title = title;

        gui = new UInventory(null, parse_int_in_json(json, "size", 9), title);
        final Inventory i = gui.getInventory();
        final JSONObject gui_json = json.getJSONObject("gui");
        final Iterator<String> gui_keys = gui_json.keys();
        for(Iterator<String> it = gui_keys; it.hasNext(); ) {
            final String key = it.next();
            final JSONObject item_json = gui_json.getJSONObject(key);
            final int slot = parse_int_in_json(item_json, "slot", 0);
            final ItemStack item = create_item_stack(gui_json, key);
            i.setItem(slot, item);
        }
        register(Feature.FILTER_CATEGORY, this);
    }
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public UInventory getInventory() {
        return gui;
    }
}
