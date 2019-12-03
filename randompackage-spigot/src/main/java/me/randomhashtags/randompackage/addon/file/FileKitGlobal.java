package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.CustomKitGlobal;
import me.randomhashtags.randompackage.api.addon.KitsGlobal;
import me.randomhashtags.randompackage.dev.Feature;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class FileKitGlobal extends RPKit implements CustomKitGlobal {
    public static String heroicprefix;
    private ItemStack item;

    public FileKitGlobal(File f) {
        load(f);
        register(Feature.CUSTOM_KIT, this);
    }
    public String getIdentifier() { return getYamlName(); }
    public Kits getKitClass() { return KitsGlobal.getKitsGlobal(); }

    public boolean isHeroic() { return yml.getBoolean("settings.heroic"); }
    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "gui settings");
            if(isHeroic()) {
                final ItemMeta m = item.getItemMeta();
                m.setDisplayName(heroicprefix.replace("{NAME}", m.hasDisplayName() ? ChatColor.stripColor(m.getDisplayName()) : item.getType().name()));
                item.setItemMeta(m);
            }
        }
        return getClone(item);
    }
}
