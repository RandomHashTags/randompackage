package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.obj.KitItem;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class RPKitSpigot extends RPAddonSpigot implements CustomKit {
    private final int max_level, slot;
    private final long cooldown;
    private final FallenHero fallen_hero;
    private List<KitItem> items;

    public RPKitSpigot(@Nullable File file) {
        super(file);
        final JSONObject json = parse_json_from_file(file);
        final JSONObject settings_json = json.getJSONObject("settings");
        max_level = parse_int_in_json(settings_json, "max level");
        cooldown = parse_long_in_json(settings_json, "cooldown");
        fallen_hero = getFallenHero(parse_string_in_json(settings_json, "fallen hero"));
        final JSONObject gui_settings_json = json.getJSONObject("gui settings");
        slot = parse_int_in_json(gui_settings_json, "slot");

        items = new ArrayList<>();
        final JSONObject items_json = json.getJSONObject("items");
        for(String i : items_json.keySet()) {
            final JSONObject item_json = items_json.getJSONObject(i);
            final String t = item_json.getString("item");
            if(t != null) {
                final String item_string = parse_string_in_json(item_json, "item");
                final String amount = parse_string_in_json(item_json, "amount");
                final MultilingualString name = parse_multilingual_string_in_json(item_json, "name");
                final List<String> lore = parse_list_string_in_json(item_json, "lore");
                final int chance = parse_int_in_json(item_json, "chance", 100);
                final int required_level = parse_int_in_json(item_json, "required level");
                items.add(new KitItem(this, i, item_string, amount, name, lore, chance, required_level));
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return max_level;
    }
    @Override
    public long getCooldown() {
        return cooldown;
    }
    @Override
    public int getSlot() {
        return slot;
    }
    @Override
    public FallenHero getFallenHero() {
        return fallen_hero;
    }
    @Override
    public List<KitItem> getItems() {
        return items;
    }
    public void setItems(List<KitItem> items) {
        this.items = items;
    }
}
