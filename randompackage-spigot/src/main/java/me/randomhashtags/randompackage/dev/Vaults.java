package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class Vaults extends RPFeature implements CommandExecutor, Listener {
    private static Vaults instance;
    public static Vaults getVaults() {
        if(instance == null) instance = new Vaults();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public String getIdentifier() { return "VAULTS"; }

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Vaults &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
