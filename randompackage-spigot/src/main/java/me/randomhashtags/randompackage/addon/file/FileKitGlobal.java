package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.CustomKitGlobal;
import me.randomhashtags.randompackage.api.addon.KitsGlobal;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public final class FileKitGlobal extends RPKit implements CustomKitGlobal {
    private ItemStack item;

    public FileKitGlobal(File f) {
        load(f);
        register(Feature.CUSTOM_KIT, this);
    }
    @Override
    public String getIdentifier() {
        return getYamlName();
    }
    @Override
    public Kits getKitClass() {
        return KitsGlobal.getKitsGlobal();
    }

    public boolean isHeroic() {
        return yml.getBoolean("settings.heroic");
    }
    public ItemStack getItem() {
        if(item == null) {
            item = createItemStack(yml, "gui settings");
            if(isHeroic()) {
                final ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(KitsGlobal.getKitsGlobal().heroicPrefix.replace("{NAME}", itemMeta.hasDisplayName() ? ChatColor.stripColor(itemMeta.getDisplayName()) : item.getType().name()));
                item.setItemMeta(itemMeta);
            }
        }
        return getClone(item);
    }
}
