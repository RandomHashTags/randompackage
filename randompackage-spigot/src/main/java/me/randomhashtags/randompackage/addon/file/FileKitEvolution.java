package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKitEvolution;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.api.addon.KitsEvolution;
import me.randomhashtags.randompackage.dev.Feature;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileKitEvolution extends RPKit implements CustomKitEvolution {
    private ItemStack item, upgradeGem;

    public FileKitEvolution(File f) {
        load(f);
        register(Feature.CUSTOM_KIT, this);
    }
    public String getIdentifier() { return getYamlName(); }
    public Kits getKitClass() { return KitsEvolution.getKitsEvolution(); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "gui settings");
        return getClone(item);
    }
    public int getUpgradeChance() { return yml.getInt("settings.upgrade chance"); }
    public ItemStack getUpgradeGem() {
        if(upgradeGem == null) upgradeGem = api.d(yml, "upgrade gem");
        return getClone(upgradeGem);
    }
}
