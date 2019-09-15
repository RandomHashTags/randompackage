package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class FatBuckets extends RPFeature {
    private static FatBuckets instance;
    public static FatBuckets getFatBuckets() {
        if(instance == null) instance = new FatBuckets();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "FAT_BUCKETS"; }
    protected RPFeature getFeature() { return getFatBuckets(); }

    public void load() {
    }
    public void unload() {
    }
}
