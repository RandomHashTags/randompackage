package me.randomhashtags.randompackage.api.dev;

import me.randomhashtags.randompackage.addon.obj.BattleRoyaleTeam;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public enum BattleRoyale implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public LinkedHashMap<Integer, BattleRoyaleTeam> teams;
    public YamlConfiguration config;
    public ItemStack lootbag;

    private long startTime, nextStartTime;
    private boolean active, hasStarted;
    private Scoreboard scoreboard;
    private String world;
    private int activeTask, startTask, maxTeamSize, maxPlayers, maxTeams;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        switch (l) {
            case 0:
                viewCurrent(sender);
                break;
            case 1:
                switch (args[0]) {
                    case "join":
                        if(player != null) tryJoining(player);
                        break;
                    case "loot":
                    default: // help
                        viewHelp(sender);
                        break;
                }
            default: break;
        }
        return true;
    }

    @Override
    public void load() {
        save(null, "battle royale.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "battle royale.yml"));
        teams = new LinkedHashMap<>();
        world = config.getString("settings.world");
        maxPlayers = config.getInt("settings.max players");
        maxTeamSize = config.getInt("settings.team sizes");
        maxTeams = maxPlayers/maxTeamSize;

        scoreboard = SCOREBOARD_MANAGER.getNewScoreboard();
        final Objective obj = scoreboard.registerNewObjective("dummy", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(colorize(config.getString("scoreboard.title")));
        int score = config.getInt("scoreboard.score start");
        for(String s : config.getStringList("scoreboard.list")) {
            obj.getScore(colorize(s)).setScore(score);
            score--;
        }
    }
    @Override
    public void unload() {
        end(null);
        if(activeTask != -1) {
            SCHEDULER.cancelTask(activeTask);
        }
        if(startTask != -1) {
            SCHEDULER.cancelTask(startTask);
        }
    }

    public final void viewCurrent(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.battleroyale", true)) {
        }
    }
    public final void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.battleroyale.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public final void tryJoining(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.battleroyale.join", true)) {
            if(active) {
                final BattleRoyaleTeam team = getEmptyTeam(player);
                if(team != null) {
                    team.join(player);
                } else {
                }
            } else {
                sendStringListMessage(player, getStringList(config, "messages.not joinable"), null);
            }
        }
    }
    @Nullable
    private BattleRoyaleTeam getEmptyTeam(@NotNull Player player) {
        if(active && !hasStarted) {
            final int size = teams.size();
            if(size != maxTeams) {
                final BattleRoyaleTeam t = new BattleRoyaleTeam(size+1);
                t.join(player);
                teams.put(t.getID(), t);
                return t;
            } else {
                for(BattleRoyaleTeam team : getTeams()) {
                    if(team.getPlayers().size() < maxTeamSize) {
                        return team;
                    }
                }
            }
        }
        return null;
    }

    public final void start() {
        if(!active) {
            active = true;
            startTime = System.currentTimeMillis();
            startTask = -1;
            for(String s : colorizeListString(getStringList(config, "messages.now joinable"))) {
                Bukkit.broadcastMessage(s);
            }
            final Objective obj = scoreboard.getObjective("dummy");
            final String dn = obj.getDisplayName();
            final DisplaySlot ds = obj.getDisplaySlot();
            final Set<Score> scores = null;
            activeTask = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
                for(Player player : Bukkit.getWorld(world).getPlayers()) {
                    updateScoreboard(player, dn, ds, getRuntime(), scores);
                }
            }, 20, 20);
        }
    }
    public final void begin() {
        if(active) {
            hasStarted = true;
        }
    }
    @NotNull
    public final String getRuntime() {
        return getRemainingTime(System.currentTimeMillis()-startTime);
    }
    @NotNull
    public final String getNextGame() {
        return getRemainingTime(nextStartTime-System.currentTimeMillis());
    }
    public final void end(@Nullable BattleRoyaleTeam winningTeam) {
        if(active) {
            active = false;
            hasStarted = false;
            SCHEDULER.cancelTask(activeTask);
            activeTask = -1;

            final long delay = (long) (20*evaluate(config.getString("settings.game interval")));
            startTask = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, this::start, delay);
            nextStartTime = System.currentTimeMillis()+(delay*1000);

            final List<String> receivedLootbag = colorizeListString(getStringList(config, "messages.won.received lootbag"));
            if(winningTeam != null) {
                final HashMap<Player, Boolean> status = winningTeam.getPlayers();
                final List<String> winners = new ArrayList<>();
                for(Player player : status.keySet()) {
                    winners.add((status.get(player) ? "" : "") + player.getName());
                    if(player.isOnline()) {
                        giveItem(player, lootbag);
                        sendStringListMessage(player, receivedLootbag, null);
                    } else {
                        final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                        pdata.getUnclaimedPurchases().add(lootbag);
                        pdata.unload();
                    }
                }
                final String string = winners.toString(), players = string.substring(0, string.length()-1);
                for(String s : colorizeListString(getStringList(config, "messages.ended"))) {
                    s = s.replace("{TEAM}", players);
                    Bukkit.broadcastMessage(s);
                }
            }
            final Scoreboard main = SCOREBOARD_MANAGER.getMainScoreboard();
            for(Player player : Bukkit.getWorld(world).getPlayers()) {
                player.setScoreboard(main);
            }
            for(BattleRoyaleTeam team : getTeams()) {
                restoreTeam(team);
            }
            teams.clear();
        }
    }
    @NotNull
    public Collection<BattleRoyaleTeam> getTeams() {
        return teams.values();
    }

    public final void restoreTeam(@NotNull BattleRoyaleTeam team) {
        final HashMap<Player, Location> locs = team.getPreviousLocations();
        final HashMap<Player, ItemStack[]> invs = team.getPreviousInventories();
        for(Player player : team.getPlayers().keySet()) {
            if(player.isOnline()) {
                final PlayerInventory inv = player.getInventory();
                inv.clear();
                player.teleport(locs.get(player));
                inv.setContents(invs.get(player));
                player.updateInventory();
            }
        }
    }
    @Nullable
    public final BattleRoyaleTeam getTeam(@NotNull Player player) {
        for(BattleRoyaleTeam team : getTeams()) {
            if(team.getPlayers().containsKey(player)) {
                return team;
            }
        }
        return null;
    }
    private void updateScoreboard(Player player, String dn, DisplaySlot ds, String runtime, Set<Score> scores) {
        final BattleRoyaleTeam team = getTeam(player);
        if(team != null) {
            final Scoreboard sb = SCOREBOARD_MANAGER.getNewScoreboard();
            final Objective obj = sb.registerNewObjective("dummy", "dummy");
            obj.setDisplayName(dn);
            obj.setDisplaySlot(ds);
            for(Score score : scores) {
                final String key = score.getEntry().replace("{RUNTIME}", runtime);
                obj.getScore(key).setScore(score.getScore());
            }
            player.setScoreboard(sb);
        }
    }

    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        if(active) {
            final Player player = event.getPlayer();
            if(player.getWorld().getName().equals(world)) {
                final BattleRoyaleTeam team = getTeam(player);
                final HashMap<Player, Boolean> players = team != null ? team.getPlayers() : null;
                if(players != null) {
                    if(hasStarted) {
                        if(players.get(player)) {
                            players.put(player, false);
                            player.teleport(team.getPreviousLocations().get(player));
                        }
                    } else {
                        team.quit(player);
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageEvent(EntityDamageEvent event) {
        if(active) {
            final Entity victim = event.getEntity();
            if(victim.getWorld().getName().equals(world) && victim instanceof Player) {
                final Player player = (Player) victim;
                if(player.getHealth() - event.getDamage() <= 0.00) {
                    player.updateInventory();
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(active) {
            final Entity damager = event.getDamager();
            if(damager.getWorld().getName().equals(world) && damager instanceof Player) {
                final Entity victim = event.getEntity();
                if(victim instanceof Player) {
                    final Player playerDamager = (Player) damager, playerVictim = (Player) victim;
                    final BattleRoyaleTeam team = getTeam(playerDamager);
                    if(team != null && team.getPlayers().containsKey(playerVictim)) {
                        event.setCancelled(true);
                        playerDamager.updateInventory();
                        playerVictim.updateInventory();
                        sendStringListMessage(playerDamager, getStringList(config, "messages.cannot hurt team members"), null);
                    }
                }
            }
        }
    }
}
