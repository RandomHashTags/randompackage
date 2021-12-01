package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class RPAddonSpigot implements RPFeatureSpigot, RPStorage {
    protected File file;
    protected YamlConfiguration yml;

    public void load(File file) {
        if(file.exists()) {
            this.file = file;
            yml = YamlConfiguration.loadConfiguration(file);
        }
    }
    public File getFile() {
        return file;
    }
    public YamlConfiguration getYaml() {
        return yml;
    }
    public String getYamlName() {
        return file.getName().split("\\.yml")[0];
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    public void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
