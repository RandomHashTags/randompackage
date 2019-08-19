package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.Kits;
import me.randomhashtags.randompackage.addons.utils.CustomKitGlobal;
import me.randomhashtags.randompackage.api.addons.KitsGlobal;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class FileKitGlobal extends RPKit implements CustomKitGlobal {
    public static String heroicprefix;
    private ItemStack item;

    public FileKitGlobal(File f) {
        load(f);
        addKit(getIdentifier(), this);
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
        return item.clone();
    }
}
