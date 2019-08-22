package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Wild extends RPFeature implements CommandExecutor {
    private static Wild instance;
    public static Wild getWild() {
        if(instance == null) instance = new Wild();
        return instance;
    }

    public String getIdentifier() { return "WILD"; }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && cmd.getName().equals("wild") && hasPermission(sender, "RandomPackage.wild", true)) {
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Wild &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
