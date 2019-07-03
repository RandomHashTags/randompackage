package me.randomhashtags.randompackage.recode;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class RPPlayer extends RPStorage {
    private boolean isLoaded;
    private File file;
    private UUID uuid;
    private YamlConfiguration yml;
    public RPPlayer(File file) {
        this.file = file;
        this.uuid = UUID.fromString(file.getName().split("\\.yml")[0]);
        load();
    }
    public UUID getUUID() { return uuid; }

    public RPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;
            yml = YamlConfiguration.loadConfiguration(file);
        }
        return this;
    }
    public void backup(boolean async) {
        if(async) {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin, () -> backup());
        } else {
            backup();
        }
    }
    private void backup() {
        save();
    }
    private void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void unload(boolean async) {
        if(isLoaded) {
            backup(async);
            isLoaded = false;
            file = null;
            yml = null;
        }
    }
}
