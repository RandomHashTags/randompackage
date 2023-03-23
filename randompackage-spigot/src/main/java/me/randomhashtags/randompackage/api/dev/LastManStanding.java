package me.randomhashtags.randompackage.api.dev;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum LastManStanding implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private PolyBoundary boundary;
    private HashMap<Player, Long> playerStartTimes;
    private HashMap<Long, List<String>> rewards;
    private int task;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final int l = args.length;
        if(l == 0) {
            viewHelp(sender);
        } else {
            switch (args[0]) {
                case "top":
                    final int page = l == 1 ? 1 : getRemainingInt(args[1]);
                    viewTop(sender, page);
                    break;
                default:
                    viewHelp(sender);
                    break;
            }
        }
        return true;
    }
    @Override
    public void load() {
        save(null, "last man standing.yml");
        if(OTHER_YML.get("last man standing.boundary") != null) {
            final String[] values = OTHER_YML.getString("last man standing.boundary").split("\\|");
            boundary = new PolyBoundary(string_to_location(values[0]), Integer.parseInt(values[1]));
        }
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "last man standing.yml"));
        rewards = new HashMap<>();
        for(String s : getConfigurationSectionKeys(config, "rewards", false)) {
            rewards.put(Long.parseLong(s), config.getStringList("rewards." + s));
        }

        playerStartTimes = new HashMap<>();
        final long interval = 40;
        task = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, this::check, interval, interval);
    }
    @Override
    public void unload() {
        SCHEDULER.cancelTask(task);
    }

    public void setBoundary(@NotNull PolyBoundary boundary) {
        this.boundary = boundary;
        OTHER_YML.set("last man standing.boundary", location_to_string(boundary.getCenter()) + "|" + boundary.getRadius());
        saveOtherData();
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.lastmanstanding.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void viewTop(@NotNull CommandSender sender, int page) {
        if(hasPermission(sender, "RandomPackage.lastmanstanding.top", true)) {
            final String p = Integer.toString(page);
            for(String s : getStringList(config, "messages.top survivors")) {
                s = s.replace("{PAGE}", p);
                if(s.contains("{SURVIVOR}")) {
                } else {
                    sender.sendMessage(s);
                }
            }
        }
    }

    private void check() { // TODO: fix dis bruh
        if(boundary != null) {
            final long time = System.currentTimeMillis();
            final HashMap<Player, Long> startTimes = new HashMap<>(), startTimesClone = (HashMap<Player, Long>) playerStartTimes.clone();
            final World world = boundary.getWorld();
            final List<Player> players = world.getPlayers();
            if(!players.isEmpty()) {
                final int radius = boundary.getRadius();
                final Collection<Entity> entities = world.getNearbyEntities(boundary.getCenter(), radius, radius, radius);
                for(Entity entity : entities) {
                    if(entity instanceof Player) {
                        final Player player = (Player) entity;
                        if(!startTimesClone.containsKey(player)) {
                            startTimes.put(player, time);
                        } else {
                            final long prev = startTimesClone.get(player), total = time-prev;
                            startTimes.put(player, total);
                            tryGivingReward(player, prev, total);
                        }
                    }
                }
            }
            playerStartTimes = startTimes;
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
