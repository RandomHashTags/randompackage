package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Disguises extends RandomPackageAPI implements Listener, CommandExecutor {

    private static Disguises instance;
    public static final Disguises getDisguises() {
        if(instance == null) instance = new Disguises();
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
        isEnabled = true;
        save(null, "disguises.yml");
        pluginmanager.registerEvents(this, randompackage);

        sendConsoleMessage("&6[RandomPackage] &aLoaded Disguises &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        HandlerList.unregisterAll(this);
    }
}
