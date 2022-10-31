package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.addon.living.ActiveRaidEvent;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public enum RaidEvents implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    private ActiveRaidEvent active;
    private YamlConfiguration config;

    private String rewardSize;
    private boolean canRepeatRewards;
    private List<String> rewards;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final int l = args.length;
        if(l == 0) {
            viewActive(sender);
        } else {
            if(args[0].equals("help")) viewHelp(sender);
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "raid events.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "raid events.yml"));

        rewardSize = config.getString("reward size");
        canRepeatRewards = config.getBoolean("can repeat rewards");
        rewards = config.getStringList("rewards");

        sendConsoleMessage("&6[RandomPackage] &aLoaded Raid Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.raidevents.help", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            sendStringListMessage(sender, config.getStringList("messages.help"), replacements);
        }
    }
    public void viewActive(@NotNull CommandSender sender) {
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
