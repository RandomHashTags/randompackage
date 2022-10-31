package me.randomhashtags.randompackage.dev.duels;

import me.randomhashtags.randompackage.addon.DuelArena;
import me.randomhashtags.randompackage.addon.dev.enums.DuelEndReason;
import me.randomhashtags.randompackage.addon.living.ActiveDuel;
import me.randomhashtags.randompackage.data.DuelData;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum Duels implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory type, godset;
    public List<ActiveDuel> activeDuels;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewTypes(player);
        } else {
            final String a = args[0];
            switch (a) {
                case "ranked":
                    break;
                case "godset":
                    viewGodset(player);
                    break;
                case "top":
                    viewTop(sender, 1);
                    break;
                case "toggle":
                    toggleRequests(player);
                    break;
                case "collect":
                    break;
                case "spectate":
                    break;
                case "unranked":
                    break;
                case "custom":
                    break;
                default:
                    viewHelp(player);
                    break;
            }
        }
        return true;
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("duel arenas", "_settings.yml");
        final String folder = DATA_FOLDER + SEPARATOR + "duel arenas";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));
        if(!OTHER_YML.getBoolean("saved default duel arenas")) {
            generateDefaultDuelArenas();
            OTHER_YML.set("saved default duel arenas", true);
            saveOtherData();
        }

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileDuelArena(f);
            }
        }

        type = new UInventory(null, config.getInt("type.size"), colorize(config.getString("type.title")));
        godset = new UInventory(null, config.getInt("godset.size"), colorize(config.getString("godset.title")));
        activeDuels = new ArrayList<>();

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.DUEL_ARENA).size() + " Duel Arenas &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        unregister(Feature.DUEL_ARENA);
    }

    public ActiveDuel valueOf(Player player) {
        for(ActiveDuel duel : activeDuels) {
            if(duel.getAccepter().equals(player) || duel.getRequester().equals(player)) {
                return duel;
            }
        }
        return null;
    }

    public void viewTypes(Player player) {
        if(hasPermission(player, "RandomPackage.duel", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, type.getSize(), type.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(type.getInventory().getContents());

            player.updateInventory();
        }
    }
    public void viewGodset(Player player) {
        if(hasPermission(player, "RandomPackage.duel.godset", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, godset.getSize(), godset.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(godset.getInventory().getContents());

            player.updateInventory();
        }
    }
    public void editGodset(@NotNull Player player) {
    }

    public void tryJoiningnUnranked(@NotNull Player player) {
    }
    public void tryJoiningRanked(@NotNull Player player) {
    }
    public void trySpectating(@NotNull Player player, @NotNull DuelArena arena) {
    }

    public void sendRequest(@NotNull Player player, @NotNull String target) {
        if(hasPermission(player, "RandomPackage.duel.request.send", true)) {
            if(player.getName().equalsIgnoreCase(target)) {
            }
        }
    }
    public void acceptRequest(@NotNull Player player) {
    }

    public void viewHelp(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.duel.help", true)) {
            sendStringListMessage(player, getStringList(config, "messages.help"), null);
        }
    }
    public void viewTop(@NotNull CommandSender sender, int page) {
        if(hasPermission(sender, "RandomPackage.duel.top", true)) {
            final List<String> top = getStringList(config, "messages.top");
            for(String s : top) {
            }
        }
    }
    public void toggleRequests(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.duel.toggle", true)) {
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final DuelData stats = pdata.getDuelData();
            final boolean toggled = !stats.receivesNotifications();
            stats.setReceivesNotifications(toggled);
            sendStringListMessage(player, getStringList(config, "messages.toggle " + (toggled ? "on" : "off")), null);
        }
    }

    private void tryEnding(Cancellable event, Player player, double damage) {
        if(player.getHealth()-damage <= 0.00) {
            event.setCancelled(true);
            player.updateInventory();
            final ActiveDuel duel = valueOf(player);
            if(duel != null) {
                duel.end(DuelEndReason.CHOOSE_WINNER);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void isDamaged(isDamagedEvent event) {
        tryEnding(event, event.getEntity(), event.getDamage());
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageEvent(EntityDamageEvent event) {
        final Entity victim = event.getEntity();
        if(victim instanceof Player) {
            tryEnding(event, (Player) victim, event.getDamage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerQuitEvent(PlayerQuitEvent event) {
        final ActiveDuel duel = valueOf(event.getPlayer());
        if(duel != null) {
            duel.end(DuelEndReason.PLAYER_LEFT_QUIT);
        }
    }
}
