package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;
import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.addon.obj.FactionUpgradeLevelObj;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class FileFactionUpgrade extends RPAddonSpigot implements FactionUpgrade {
    private final boolean is_enabled, item_amount_equals_tier;
    private final FactionUpgradeType type;
    private final int slot;
    private final ItemStack item;
    private final LinkedHashMap<Integer, FactionUpgradeLevel> levels;
    private final List<String> attributes;

    public FileFactionUpgrade(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = json.getJSONObject("settings");
        is_enabled = parse_boolean_in_json(settings_json, "enabled");
        if(is_enabled) {
            final String type_string = parse_string_in_json(settings_json, "type");
            type = getFactionUpgradeType(type_string);
            slot = parse_int_in_json(settings_json, "slot");
            item_amount_equals_tier = parse_boolean_in_json(settings_json, "item amount=tier");

            attributes = parse_list_string_in_json(json, "attributes");

            item = create_item_stack(json, "item");
            if(item != null) {
                final List<String> lore = item.getItemMeta().getLore(), format = getType().getFormat(), formatted_lore = new ArrayList<>();
                final ItemMeta meta = item.getItemMeta();
                for(String format_string : format) {
                    if(format_string.equals("{LORE}")) {
                        if(lore != null) {
                            formatted_lore.addAll(lore);
                        }
                    } else {
                        formatted_lore.add(format_string);
                    }
                }
                meta.setLore(formatted_lore);
                item.setItemMeta(meta);
            }

            levels = new LinkedHashMap<>();
            final JSONObject levels_json = json.optJSONObject("levels");
            if(levels_json != null) {
                levels.put(0, new FactionUpgradeLevelObj(0, -1, "", new ArrayList<>()));
                for(int level = 1; level <= 100; level++) {
                    final JSONObject level_json = levels_json.optJSONObject(Integer.toString(level));
                    if(level_json != null) {
                        final double value = parse_double_in_json(level_json, "value");
                        final String string = parse_string_in_json(level_json, "string");
                        final List<String> cost = parse_list_string_in_json(level_json, "cost");
                        final FactionUpgradeLevelObj o = new FactionUpgradeLevelObj(level, value, string, cost);
                        levels.put(level, o);
                    }
                }
            }

            register(Feature.FACTION_UPGRADE, this);
        } else {
            type = null;
            slot = 0;
            item_amount_equals_tier = false;
            item = null;
            levels = null;
            attributes = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return is_enabled;
    }

    @Override
    @Nullable
    public ItemStack getItem() {
        return getClone(item);
    }
    @Override
    public @NotNull FactionUpgradeType getType() {
        return type;
    }
    @Override
    public int getSlot() {
        return slot;
    }
    @Override
    public boolean itemAmountEqualsTier() {
        return item_amount_equals_tier;
    }

    @Override
    public @NotNull LinkedHashMap<Integer, FactionUpgradeLevel> getLevels() {
        return levels;
    }

    @Override
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
