package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Strongholds extends RPFeature implements CommandExecutor {
    private static Strongholds instance;
    public static Strongholds getStrongholds() {
        if(instance == null) instance = new Strongholds();
        return instance;
    }
    public YamlConfiguration config;

    public String getIdentifier() { return "STRONGHOLDS"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "strongholds.yml");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (strongholds != null ? strongholds.size() : 0) + " Strongholds &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
