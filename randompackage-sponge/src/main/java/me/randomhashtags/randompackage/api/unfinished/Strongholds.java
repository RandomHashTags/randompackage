package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Strongholds extends RandomPackageAPI implements Listener, CommandExecutor {

    private static Strongholds instance;
    public static final Strongholds getStrongholds() {
        if(instance == null) instance = new Strongholds();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save(null, "strongholds.yml");
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;

        sendConsoleMessage("&6[RandomPackage] &aLoaded Strongholds &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        config = null;
        HandlerList.unregisterAll(this);
    }
}
