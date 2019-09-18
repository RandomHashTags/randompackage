package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class FactionTop extends RPFeature implements CommandExecutor {
    private static FactionTop instance;
    public static FactionTop getFactionTop() {
        if(instance == null) instance = new FactionTop();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
    public String getIdentifier() { return "FACTION_TOP"; }
    protected RPFeature getFeature() { return getFactionTop(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Top &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
