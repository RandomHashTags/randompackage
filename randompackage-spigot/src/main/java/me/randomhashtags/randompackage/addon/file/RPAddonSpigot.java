package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class RPAddonSpigot implements RPFeatureSpigot, RPStorage {
    protected String identifier;

    public RPAddonSpigot(@Nullable File file) {
        if(file != null) {
            identifier = file.getName().split("\\.yml")[0].split("\\.json")[0];
        } else {
            identifier = "NULL";
        }
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    public void save() {
        try {
            //yml.save(file);
            //yml = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
