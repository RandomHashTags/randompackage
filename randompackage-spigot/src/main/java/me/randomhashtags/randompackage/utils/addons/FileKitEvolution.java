package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.legacy.CustomKit;
import me.randomhashtags.randompackage.addons.Kits;
import me.randomhashtags.randompackage.api.addons.KitsEvolution;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileKitEvolution extends RPKit {
    private ItemStack item, upgradeGem;

    public FileKitEvolution(File f) {
        load(f);
        addKit(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }
    public Kits getKitClass() { return KitsEvolution.getKitsEvolution(); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "gui settings");
        return item.clone();
    }
    public int getUpgradeChance() { return yml.getInt("settings.upgrade chance"); }
    public ItemStack getUpgradeGem() {
        if(upgradeGem == null) upgradeGem = api.d(yml, "upgrade gem");
        return upgradeGem != null ? upgradeGem.clone() : null;
    }

    public static FileKitEvolution valueOfUpgradeGem(ItemStack is) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                if(k instanceof FileKitEvolution) {
                    final ItemStack i = ((FileKitEvolution) k).getUpgradeGem();
                    if(i != null && i.isSimilar(is)) {
                        return (FileKitEvolution) k;
                    }
                }
            }
        }
        return null;
    }
}
