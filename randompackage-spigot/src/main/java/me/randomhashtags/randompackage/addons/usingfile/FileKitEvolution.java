package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.utils.RPKit;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileKitEvolution extends RPKit {
    private ItemStack item, upgradeGem;

    public FileKitEvolution(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addKit("EVOLUTION_" + getYamlName(), this); }

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
