package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public final class FileInventoryPet extends RPAddonSpigot implements InventoryPet {
    private final boolean is_enabled;
    private final int max_level;
    private final ItemStack item, egg;
    private final String owner;
    private final HashMap<Integer, String> values;
    private final HashMap<Integer, Integer> cooldowns, required_xp;
    private final List<String> attributes;
    public FileInventoryPet(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = parse_json_in_json(json, "settings");
        is_enabled = parse_boolean_in_json(settings_json, "enabled");
        max_level = parse_int_in_json(settings_json, "max level");
        ItemStack item = create_item_stack(json, "item");
        if(item != null) {
            final ItemMeta im = item.getItemMeta();
            item = getSkull(im.getDisplayName(), im.getLore(), LEGACY || THIRTEEN);
        }
        this.item = item;

        final JSONObject item_json = parse_json_in_json(json, "item");
        owner = parse_string_in_json(item_json, "texture", parse_string_in_json(item_json, "owner", "null"));
        egg = create_item_stack(json, "egg");

        attributes = parse_list_string_in_json(json, "attributes");

        values = new HashMap<>();
        final JSONObject values_json = settings_json.getJSONObject("values");
        for(String level : values_json.keySet()) {
            values.put(Integer.parseInt(level), values_json.getString(level));
        }

        cooldowns = new HashMap<>();
        final JSONObject cooldown_json = settings_json.getJSONObject("cooldown");
        for(String s : cooldown_json.keySet()) {
            final int cooldown = cooldown_json.getInt(s) * 1000;
            if(s.equals("all")) {
                cooldowns.put(-1, cooldown);
            } else {
                cooldowns.put(Integer.parseInt(s), cooldown);
            }
        }

        required_xp = new HashMap<>();
        final JSONObject required_xp_json = settings_json.getJSONObject("exp to level");
        for(String s : required_xp_json.keySet()) {
            final int amount = required_xp_json.getInt(s);
            if(s.equals("all")) {
                required_xp.put(-1, amount);
            } else {
                required_xp.put(Integer.parseInt(s), amount);
            }
        }

        register(Feature.INVENTORY_PET, this);
    }

    @Override
    public boolean isEnabled() {
        return is_enabled;
    }
    @Override
    public int getMaxLevel() {
        return max_level;
    }
    @Override
    @NotNull
    public HashMap<Integer, String> getValues() {
        return values;
    }
    @Override
    public @NotNull HashMap<Integer, Integer> getCooldowns() {
        return cooldowns;
    }

    @Override
    @NotNull
    public HashMap<Integer, Integer> getRequiredXp() {
        return required_xp;
    }
    @Override
    public String getOwner() {
        return owner;
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }

    @Override
    public @NotNull ItemStack getEgg() {
        return getClone(egg);
    }
    public LinkedHashMap<InventoryPet, Integer> getEggRequiredPets() {
        return null;
    }
    @Override
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
