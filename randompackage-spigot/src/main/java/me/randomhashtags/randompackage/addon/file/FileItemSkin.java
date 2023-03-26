package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ItemSkin;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public final class FileItemSkin extends RPAddonSpigot implements ItemSkin {

    private final MultilingualString name;
    private final String material;
    private final List<String> lore, attributes;

    public FileItemSkin(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        name = parse_multilingual_string_in_json(json, "name");
        material = parse_string_in_json(json, "material").toUpperCase();
        lore = parse_list_string_in_json(json, "lore");
        attributes = parse_list_string_in_json(json, "attributes");
        register(Feature.ITEM_SKIN, this);
    }
    public @NotNull MultilingualString getName() {
        return name;
    }
    public @NotNull String getMaterial() {
        return material;
    }
    public @NotNull List<String> getLore() {
        return lore;
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
