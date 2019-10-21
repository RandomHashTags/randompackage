package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Factions extends RPFeature implements CommandExecutor {
    private static Factions instance;
    public static Factions getFactions() {
        if(instance == null) instance = new Factions();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public String getIdentifier() { return "FACTIONS"; }
    protected RPFeature getFeature() { return getFactions(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Factions &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void loadBackup() {
    }
    public void backup() {
    }
}
