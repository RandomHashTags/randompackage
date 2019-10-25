package me.randomhashtags.randompackage.dev.duels;

import me.randomhashtags.randompackage.addon.living.ActiveDuel;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Duels extends RPFeature implements CommandExecutor {
    private static Duels instance;
    public static Duels getDuels() {
        if(instance == null) instance = new Duels();
        return instance;
    }
    public YamlConfiguration config;
    private UInventory type, godset;
    public List<ActiveDuel> activeDuels;

    public String getIdentifier() { return "DUELS"; }
    protected RPFeature getFeature() { return getDuels(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewTypes(player);
        } else {
            final String a = args[0];
            if(a.equals("godset")) {
                viewGodset(player);
            } else {

            }
        }
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save("duel arenas", "_settings.yml");

        config = YamlConfiguration.loadConfiguration(new File(rpd + separator + "duel arenas", "_settings.yml"));
        type = new UInventory(null, config.getInt("type.size"), ChatColor.translateAlternateColorCodes('&', config.getString("type.title")));
        godset = new UInventory(null, config.getInt("godset.size"), ChatColor.translateAlternateColorCodes('&', config.getString("godset.title")));

        activeDuels = new ArrayList<>();

        sendConsoleMessage("&6[RandomPackage] &aLoaded Duels &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        duelArenas = null;
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
    public void editGodset(Player player) {
    }

    public void tryJoiningnUnranked(Player player) {
    }
    public void tryJoiningRanked(Player player) {
    }
    public void trySpectating(Player player, DuelArena arena) {
    }

    public void sendRequest(Player player, String target) {
    }
    public void acceptRequest(Player player) {
    }

    public void viewTop(CommandSender sender, int page) {
        if(hasPermission(sender, "RandomPackage.duel.top", true)) {
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
