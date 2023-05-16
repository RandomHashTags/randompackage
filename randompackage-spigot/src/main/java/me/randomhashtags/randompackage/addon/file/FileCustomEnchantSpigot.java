package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public final class FileCustomEnchantSpigot extends RPAddonSpigot implements CustomEnchantSpigot {

    private final boolean enabled;
    private final MultilingualString name;
    private final List<String> lore;
    private final int max_level;
    private final List<String> enabled_worlds, applies_to, attributes;
    private final String required_enchant, enchant_proc_value;
    private final List<BigDecimal> alchemist, tinkerer;

    public FileCustomEnchantSpigot(File file) {
        super(file);
        final JSONObject json = parse_json_from_file(file);
        enabled = parse_boolean_in_json(json, "enabled");
        name = parse_multilingual_string_in_json(json, "name");
        lore = parse_list_string_in_json(json, "lore");
        max_level = parse_int_in_json(json, "max level", 1);

        enabled_worlds = parse_list_string_in_json(json, "enabled in worlds");
        applies_to = parse_list_string_in_json(json, "applies to");
        required_enchant = parse_string_in_json(json, "requires", null);
        alchemist = parse_list_big_decimal_in_json(json, "alchemist upgrade costs", null);
        tinkerer = parse_list_big_decimal_in_json(json, "tinkerer", null);
        attributes = parse_list_string_in_json(json, "attributes");
        enchant_proc_value = parse_string_in_json(json, "enchant proc value", "0");
        register(isEnabled() ? Feature.CUSTOM_ENCHANT_ENABLED : Feature.CUSTOM_ENCHANT_DISABLED, this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public @NotNull List<String> getEnabledInWorlds() {
        return enabled_worlds;
    }

    public @NotNull MultilingualString getName() {
        return name;
    }
    public @NotNull List<String> getLore() {
        return lore;
    }
    public int getMaxLevel() {
        return max_level;
    }
    public @NotNull List<String> getAppliesTo() {
        return applies_to;
    }
    @Nullable
    public String getRequiredEnchant() {
        return required_enchant;
    }
    @Override
    public List<BigDecimal> getAlchemist() {
        return alchemist;
    }
    @Override
    public List<BigDecimal> getTinkerer() {
        return tinkerer;
    }
    public String getEnchantProcValue() {
        return enchant_proc_value;
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
