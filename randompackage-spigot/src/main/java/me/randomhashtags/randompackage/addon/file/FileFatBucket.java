package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FatBucket;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileFatBucket extends RPAddon implements FatBucket {
    private ItemStack bucket;
    public FileFatBucket(File f) {
        load(f);
        register(Feature.FAT_BUCKET, this);
    }

    public String getIdentifier() { return getYamlName(); }
    public ItemStack getItem() {
        if(bucket == null) bucket = api.d(yml, "item");
        return getClone(bucket);
    }
    public int getUses() { return yml.getInt("settings.uses"); }
    public int getSourcesRequired() { return yml.getInt("settings.sources required"); }
    public String getUsesLeftStatus() { return colorize(yml.getString("settings.status.uses left")); }
    public String getPercentFullStatus() { return colorize(yml.getString("settings.status.percent full")); }
    public List<String> getEnabledWorlds() { return yml.getStringList("settings.enabled worlds"); }
    public List<String> getFillableInWorlds() { return getStringList(yml, "settings.fillable in worlds"); }
    public List<String> getOnlyFillableInWorldsMsg() { return getStringList(yml, "messages.only fillable in worlds"); }
}
