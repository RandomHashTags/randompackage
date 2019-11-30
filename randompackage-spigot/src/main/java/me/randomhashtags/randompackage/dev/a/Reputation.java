package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Reputation extends RPFeature {
    private static Reputation instance;
    public static Reputation getReputation() {
        if(instance == null) instance = new Reputation();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "REPUTATION"; }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "reputation.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "reputation.yml"));
        sendConsoleMessage("&6[RandomPackage] &aLoaded Reputation &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public int getReputationPoints(UUID player) {
        return RPPlayer.get(player).getReputationPoints();
    }

    public void view(CommandSender sender, String target) {
        final boolean isSelf = target == null;
        if(hasPermission(sender, "RandomPackage.reputation.view" + (isSelf ? "" : ".other"), true)) {
            final List<String> msg;
            final int level;
            final HashMap<String, String> replacements = new HashMap<>();
            if(isSelf || target.equals(sender.getName())) {
                if(!(sender instanceof Player)) return;
                msg = config.getStringList("messages.view");
                level = getReputationPoints(((Player) sender).getUniqueId());
            } else {
                msg = config.getStringList("messages.view player");
                replacements.put("{TARGET}", target);
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                level = getReputationPoints(op.getUniqueId());
            }
            replacements.put("{LEVEL}", Integer.toString(level));
            sendStringListMessage(sender, msg, replacements);
        }
    }
}
