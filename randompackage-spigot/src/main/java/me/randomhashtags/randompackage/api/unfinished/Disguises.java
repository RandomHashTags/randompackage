package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Disguises extends RPFeature {
    private static Disguises instance;
    public static final Disguises getDisguises() {
        if(instance == null) instance = new Disguises();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "DISGUISES"; }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "disguises.yml");
        pluginmanager.registerEvents(this, randompackage);

        sendConsoleMessage("&6[RandomPackage] &aLoaded Disguises &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        config = null;
        instance = null;
    }
}
