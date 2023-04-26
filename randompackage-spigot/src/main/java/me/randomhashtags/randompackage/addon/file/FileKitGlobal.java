package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.CustomKitGlobal;
import me.randomhashtags.randompackage.api.addon.KitsGlobal;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;

public final class FileKitGlobal extends RPKitSpigot implements CustomKitGlobal {
    private final boolean is_heroic;
    private final ItemStack item;

    public FileKitGlobal(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = json.getJSONObject("settings");
        is_heroic = parse_boolean_in_json(settings_json, "heroic");

        item = create_item_stack(json, "gui settings");
        if(isHeroic()) {
            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(KitsGlobal.getKitsGlobal().heroicPrefix.replace("{NAME}", itemMeta.hasDisplayName() ? ChatColor.stripColor(itemMeta.getDisplayName()) : item.getType().name()));
            item.setItemMeta(itemMeta);
        }
        register(Feature.CUSTOM_KIT, this);
    }
    @Override
    public @NotNull Kits getKitClass() {
        return KitsGlobal.getKitsGlobal();
    }

    public boolean isHeroic() {
        return is_heroic;
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
}
