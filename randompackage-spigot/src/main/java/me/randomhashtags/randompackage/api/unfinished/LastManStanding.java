package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class LastManStanding extends RPFeature implements CommandExecutor {
    private static LastManStanding instance;
    public static LastManStanding getLastManStanding() {
        if(instance == null) instance = new LastManStanding();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "LAST_MAN_STANDING"; }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final int l = args.length;
        if(l == 0 || l == 1 && args[0].equals("help")) viewHelp(sender);
        else {
            final String a = args[0];
            if(a.equals("top")) {
                final int page = l == 1 ? 1 : getRemainingInt(args[1]);
                viewTop(sender, page);
            }
        }
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "last man standing.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "last man standing.yml"));
        sendConsoleMessage("&6[RandomPackage] &aLoaded Last Man Standing &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.lastmanstanding.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void viewTop(CommandSender sender, int page) {
        if(hasPermission(sender, "RandomPackage.lastmanstanding.top", true)) {
            final List<String> msg = colorizeListString(config.getStringList("messages.top survivors"));
            final String p = Integer.toString(page);
            for(String s : msg) {
                s = s.replace("{PAGE}", p);
                if(s.contains("{SURVIVOR}")) {
                } else {
                    sender.sendMessage(s);
                }
            }
        }
    }
}
