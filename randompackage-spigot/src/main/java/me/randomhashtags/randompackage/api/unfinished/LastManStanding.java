package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.objects.PolyBoundary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class LastManStanding extends RPFeature implements CommandExecutor {
    private static LastManStanding instance;
    public static LastManStanding getLastManStanding() {
        if(instance == null) instance = new LastManStanding();
        return instance;
    }

    public YamlConfiguration config;
    private PolyBoundary boundary;
    private HashMap<Player, Long> playerStartTimes;
    private HashMap<Long, List<String>> rewards;
    private int task;

    public String getIdentifier() { return "LAST_MAN_STANDING"; }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
        rewards = new HashMap<>();
        final ConfigurationSection c = config.getConfigurationSection("rewards");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                rewards.put(Long.parseLong(s), config.getStringList("rewards." + s));
            }
        }
        final long interval = 40;
        task = scheduler.scheduleSyncRepeatingTask(randompackage, this::check, interval, interval);
        sendConsoleMessage("&6[RandomPackage] &aLoaded Last Man Standing &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        scheduler.cancelTask(task);
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

    private void check() {
        if(boundary != null) {
            final long time = System.currentTimeMillis();
            final List<Player> players = boundary.getWorld().getPlayers();
            if(!players.isEmpty()) {
                for(Player player : players) {
                    if(boundary.contains(player.getLocation())) {
                        if(!playerStartTimes.containsKey(player)) {
                            playerStartTimes.put(player, time);
                        } else {
                            final long prev = playerStartTimes.get(player), total = time-prev;
                            playerStartTimes.put(player, total);
                            tryGivingReward(player, prev, total);
                        }
                    } else {
                        playerStartTimes.remove(player);
                    }
                }
            }
        }
    }

    private void tryGivingReward(Player player, long started, long total) {
        if(playerStartTimes.containsKey(player)) {
        }
    }

    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        playerStartTimes.remove(event.getPlayer());
    }
}
