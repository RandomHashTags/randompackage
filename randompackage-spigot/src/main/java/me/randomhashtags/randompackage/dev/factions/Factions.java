package me.randomhashtags.randompackage.dev.factions;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Factions extends RPFeature implements CommandExecutor {
    private static Factions instance;
    public static Factions getFactions() {
        if(instance == null) instance = new Factions();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        switch (l) {
            case 0:
            case 1:
                switch (args[0]) {
                    case "power": break;
                    case "leave": break;
                    case "join": break;
                    case "create": break;
                    case "money": break;
                    case "unclaim": break;
                    case "unclaimall": break;
                    case "map": break;
                    case "disband": break;
                    case "status": break;
                    case "open": break;
                    default:
                        viewHelp(sender);
                        break;
                }
                break;
            case 2:
                switch (args[0]) {
                    case "list": break;
                    case "f":
                    case "show": break;
                    case "join": break;
                    case "chat":
                    case "c": break;
                    case "description":
                    case "desc": break;
                    case "tag": break;
                    case "focus": break;
                    case "warp": break;
                    case "ban": break;
                    case "unban": break;
                    case "create": break;
                    default:
                        viewHelp(sender);
                        break;
                }
                break;
            default:
                viewHelp(sender);
                break;
        }
        return true;
    }

    public String getIdentifier() { return "FACTIONS"; }
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

    public void viewHelp(@NotNull CommandSender sender) {
    }

    private boolean isInFaction(Player player) {
        return false;
    }


    public void tryChangingDescription(Player player, String input) {
    }
    public void tryWarping(Player player, String input) {
    }
    public void tryBanning(Player player, String input) {
    }
    public void tryUnbanning(Player player, String input) {
    }
}
