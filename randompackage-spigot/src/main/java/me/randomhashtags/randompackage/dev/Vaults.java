package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public enum Vaults implements RPFeature, CommandExecutor {
    INSTANCE;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "VAULTS";
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Vaults &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
