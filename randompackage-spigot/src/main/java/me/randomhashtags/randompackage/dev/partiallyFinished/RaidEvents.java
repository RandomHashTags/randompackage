package me.randomhashtags.randompackage.dev.partiallyFinished;

import me.randomhashtags.randompackage.addon.living.ActiveRaidEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class RaidEvents extends RPFeature implements CommandExecutor {
    private static RaidEvents instance;
    public static RaidEvents getRaidEvent() {
        if(instance == null) instance = new RaidEvents();
        return instance;
    }

    private ActiveRaidEvent active;
    private YamlConfiguration config;

    private String rewardSize;
    private boolean canRepeatRewards;
    private List<String> rewards;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final int l = args.length;
        if(l == 0) {
            viewActive(sender);
        } else {
            if(args[0].equals("help")) viewHelp(sender);
        }
        return true;
    }

    public String getIdentifier() { return "RAID_EVENT"; }
    protected RPFeature getFeature() { return getRaidEvent(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "raid events.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "raid events.yml"));

        rewardSize = config.getString("reward size");
        canRepeatRewards = config.getBoolean("can repeat rewards");
        rewards = config.getStringList("rewards");

        sendConsoleMessage("&6[RandomPackage] &aLoaded Raid Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.raidevents.help", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            sendStringListMessage(sender, config.getStringList("messages.help"), replacements);
        }
    }
    public void viewActive(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.raidevents.active", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            if(active != null) {
                replacements.put("{PLAYERS}", formatInt(active.getPlayers()));
                replacements.put("{RUNTIME}", active.getRuntime());
            }
            sendStringListMessage(sender, config.getStringList("messages.active"), replacements);
        }
    }

    public void didClaimLand(Player player, String faction, Chunk c) {
    }
}