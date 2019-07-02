package me.randomhashtags.randompackage.recode.utils;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.recode.RPStorage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Random;

public abstract class AbstractRPFeature extends RPStorage {
    protected static RandomPackageAPI api = RandomPackageAPI.api;
    protected Random random = new Random();

    private File f;
    private YamlConfiguration yml;
    public void load(File f) {
        if(!f.exists()) {
        }
        this.f = f;
        yml = YamlConfiguration.loadConfiguration(f);
    }
    public File getFile() { return f; }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return f.getName().split("\\.yml")[0]; }

    public abstract String getIdentifier();
    public abstract void initilize();
}
