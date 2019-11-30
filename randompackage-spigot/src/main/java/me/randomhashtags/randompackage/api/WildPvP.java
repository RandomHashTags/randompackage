package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.obj.PvPCountdownMatch;
import me.randomhashtags.randompackage.addon.obj.PvPMatch;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WildPvP extends RPFeature implements CommandExecutor {
    private static WildPvP instance;
    public static WildPvP getWildPvP() {
        if(instance == null) instance = new WildPvP();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui, viewInventory;
    private ItemStack enterQueue, request;
    private List<String> blockedCommands;
    private boolean legacy = false;
    private HashMap<Player, ArrayList<Integer>> tasks;
    private List<Player> viewing;
    private HashMap<Player, Location> countdown;

    private int invincibilityDuration, nearbyRadius;

    public String getIdentifier() { return "WILD_PVP"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewQueue(player);
        } else {
            final String a = args[0];
            if(a.equals("leave")) {
                final PvPMatch m = PvPMatch.valueOf(player);
                if(m != null) {
                    leaveQueue(m, config.getStringList("messages.leave"));
                } else if(countdown.containsKey(player)) {
                    leaveCountdown(player);
                } else {
                    sendStringListMessage(player, config.getStringList("messages.leave not in queue"), null);
                }
            } else {
                viewQueue(player);
            }
        }
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "wild pvp.yml");

        legacy = EIGHT || NINE || TEN || ELEVEN;
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "wild pvp.yml"));
        blockedCommands = new ArrayList<>();
        for(String s : config.getStringList("settings.blocked commands")) {
            blockedCommands.add(s.toLowerCase());
        }

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        enterQueue = d(config, "gui.enter queue");
        request = d(config, "request");
        gui.getInventory().setItem(config.getInt("gui.enter queue.slot"), enterQueue);
        invincibilityDuration = config.getInt("settings.invincibility duration");
        nearbyRadius = config.getInt("settings.nearby radius");

        tasks = new HashMap<>();
        countdown = new HashMap<>();
        viewing = new ArrayList<>();

        viewInventory = new UInventory(null, 54, colorize(config.getString("view inventory.title")));

        sendConsoleMessage("&6[RandomPackage] &aLoaded Wild PvP &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player p : tasks.keySet()) {
            for(int i : tasks.get(p)) {
                scheduler.cancelTask(i);
            }
        }
        for(Player p : new ArrayList<>(viewing)) {
            p.closeInventory();
        }
        final HashMap<Player, PvPMatch> m = PvPMatch.matches;
        if(m != null) {
            for(PvPMatch p : new ArrayList<>(m.values())) {
                delete(p);
            }
        }
        final List<PvPCountdownMatch> c = PvPCountdownMatch.countdowns;
        if(c != null) {
            for(PvPCountdownMatch p : new ArrayList<>(c)) {
                p.delete();
            }
        }
        PvPMatch.matches = null;
        PvPCountdownMatch.countdowns = null;
    }

    public void viewQueue(Player player) {
        if(hasPermission(player, "RandomPackage.wildpvp.view", true)) {
            player.closeInventory();
            player.openInventory(gui.getInventory());
        }
    }
    public void joinQueue(Player player) {
        if(hasPermission(player, "RandomPackage.wildpvp.create", true)) {
            player.closeInventory();
            final PvPMatch m = PvPMatch.valueOf(player);
            if(m == null) {
                final Location l = player.getLocation();
                final Chunk chunk = l.getChunk();
                final String f = ChatColor.stripColor(regions.getFactionTagAt(l));
                if(f == null || f.equals("Wilderness")) {
                    final PvPMatch ma = new PvPMatch(player, player.getInventory(), chunk);
                    final Inventory i = gui.getInventory();
                    final int slot = i.firstEmpty();
                    int nearby = 0;
                    for(Entity e : player.getNearbyEntities(nearbyRadius, nearbyRadius, nearbyRadius)) {
                        if(e instanceof Player) {
                            nearby++;
                        }
                    }
                    final double hp = player.getHealth();
                    ma.slot = slot;

                    final String n = player.getName(), fac = regions.getFactionTag(player.getUniqueId()), HP = roundDoubleString(hp, 0), N = Integer.toString(nearby);
                    final ItemStack skull = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                    final SkullMeta sm = (SkullMeta) skull.getItemMeta();
                    if(legacy) {
                        sm.setOwner(player.getName());
                    } else {
                        sm.setOwningPlayer(player);
                    }
                    lore.clear();
                    for(String s : request.getItemMeta().getLore()) {
                        lore.add(s.replace("{NEARBY_PLAYERS}", N).replace("{FACTION}", fac != null ? fac : "").replace("{HP}", HP));
                    }
                    sm.setLore(lore);
                    skull.setItemMeta(sm);
                    i.setItem(slot, skull);

                    for(String s : colorizeListString(config.getStringList("messages.created broadcast"))) {
                        Bukkit.broadcastMessage(s.replace("{PLAYER}", n));
                    }
                    sendStringListMessage(player, config.getStringList("messages.created"), null);
                } else {
                    sendStringListMessage(player, config.getStringList("messages.must be in wilderness"), null);
                }
            } else {
                sendStringListMessage(player, config.getStringList("messages.already in queue"), null);
            }
        }
    }
    public void leaveQueue(PvPMatch match, List<String> reason) {
        if(hasPermission(match.getCreator(), "RandomPackage.wildpvp.leave", true)) {
            sendStringListMessage(match.getCreator(), reason, null);
            delete(match);
        }
    }
    public void leaveCountdown(Player player) {
        if(countdown.containsKey(player) && hasPermission(player, "RandomPackage.wildpvp.leave.countdown", true)) {
            player.teleport(countdown.get(player), PlayerTeleportEvent.TeleportCause.UNKNOWN);
            countdown.remove(player);
            final PvPCountdownMatch c = PvPCountdownMatch.valueOf(player);
            if(c != null) {
                sendStringListMessage(player, config.getStringList("messages.leave"), null);
                for(int i : tasks.get(c.getCreator())) {
                    scheduler.cancelTask(i);
                }
                c.delete();
            }
        }
    }
    public void viewInventoryOfQueue(Player player, PvPMatch match) {
        if(hasPermission(player, "RandomPackage.wildpvp.viewinventory", true)) {
            player.closeInventory();
            final Inventory i = match.getInventory();
            player.openInventory(Bukkit.createInventory(player, 54, viewInventory.getTitle().replace("{PLAYER}", match.getCreator().getName())));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(viewInventory.getInventory().getContents());
            viewing.add(player);
            final ItemStack[] c = i.getContents();
            for(int s = 0; s < c.length; s++) {
                top.setItem(s, c[s]);
            }
            player.updateInventory();
        }
    }
    public void challenge(Player player, PvPMatch match) {
        if(hasPermission(player, "RandomPackage.wildpvp.challenge", true)) {
            final Player c = match.getCreator();
            player.closeInventory();
            c.closeInventory();

            countdown.put(player, player.getLocation());

            player.teleport(c.getLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);

            final List<String> e = colorizeListString(config.getStringList("messages.invincibility enabled")), expire = colorizeListString(config.getStringList("messages.invincibility expired")), cd = colorizeListString(config.getStringList("messages.invincibility expiring"));
            final HashMap<String, String> r = new HashMap<>();
            r.put("{SEC}", Integer.toString(invincibilityDuration));

            sendStringListMessage(player, e, r);
            sendStringListMessage(c, e, r);

            tasks.put(c, new ArrayList<>());

            final PvPCountdownMatch cdm = new PvPCountdownMatch(c, player);

            for(int i = 0; i <= invincibilityDuration; i++) {
                final int a = i;
                tasks.get(c).add(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                    if(cdm.getCreator() != null && cdm.getChallenger() != null) {
                        r.put("{SEC}", Integer.toString(invincibilityDuration-a));
                        if(a == invincibilityDuration) {
                            cdm.delete();
                            countdown.remove(player);
                            sendStringListMessage(player, expire, null);
                            sendStringListMessage(c, expire, null);
                        } else {
                            sendStringListMessage(player, cd, r);
                            sendStringListMessage(c, cd, r);
                        }
                    } else {
                        countdown.remove(player);
                        for(int t : tasks.get(c)) {
                            scheduler.cancelTask(t);
                        }
                    }
                }, 20*i));
            }
            delete(match);
        }
    }

    private void delete(PvPMatch m) {
        final int s = m.slot;
        final Inventory gi = gui.getInventory();
        gi.setItem(s, new ItemStack(Material.AIR));
        for(int i = s; i < gui.getSize(); i++) {
            final PvPMatch ma = PvPMatch.valueOf(i);
            if(ma != null && m != ma) {
                ma.slot -= 1;
                gi.setItem(i-1, gi.getItem(i));
            }
        }
        m.delete();
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        viewing.remove((Player) event.getPlayer());
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PvPMatch m = PvPMatch.valueOf(player);
        if(m != null) {
            delete(m);
        } else {
            final PvPCountdownMatch p = PvPCountdownMatch.valueOf(player);
            if(p != null) {
                p.delete();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final PvPMatch match = PvPMatch.valueOf(player);
        if(match != null) {
            final String m = event.getMessage();
            for(String s : blockedCommands) {
                if(m.toLowerCase().startsWith(s)) {
                    event.setCancelled(true);
                    sendStringListMessage(player, config.getStringList("messages.cannot use blocked command"), null);
                    return;
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        PvPMatch m = PvPMatch.valueOf(player);
        if(m != null) {
            event.setCancelled(true);
            player.updateInventory();
            player.closeInventory();
            sendStringListMessage(player, config.getStringList("messages.cannot modify inventory"), null);
        } else {
            final Inventory top = player.getOpenInventory().getTopInventory();
            final boolean v = viewing.contains(player);
            if(v || event.getView().getTitle().equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();

                final int r = event.getRawSlot();
                final ItemStack c = event.getCurrentItem();
                if(v || r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;
                if(c.equals(enterQueue)) {
                    joinQueue(player);
                } else if(c.getItemMeta() instanceof SkullMeta) {
                    final SkullMeta me = (SkullMeta) c.getItemMeta();
                    final Player o = legacy ? Bukkit.getPlayer(me.getOwner()) : me.getOwningPlayer().getPlayer();
                    m = PvPMatch.valueOf(o);
                    if(m != null) {
                        final String cl = event.getClick().name();
                        player.closeInventory();
                        if(cl.contains("RIGHT")) {
                            viewInventoryOfQueue(player, m);
                        } else if(cl.contains("LEFT")) {
                            challenge(player, m);
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        final PvPMatch m = PvPMatch.valueOf(event.getPlayer());
        if(m != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageEvent(EntityDamageEvent event) {
        final Entity e = event.getEntity();
        if(e instanceof Player) {
            final Player player = (Player) e;
            final PvPCountdownMatch p = PvPCountdownMatch.valueOf(player);
            if(p != null) {
                event.setCancelled(true);
            } else {
                final PvPMatch m = PvPMatch.valueOf(player);
                if(m != null) {
                    sendStringListMessage(player, config.getStringList("messages.left due to taken damage"), null);
                    delete(m);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerMoveEvent(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PvPMatch m = PvPMatch.valueOf(player);
        if(m != null) {
            if(player.getLocation().getChunk() != m.getChunk()) {
                delete(m);
                sendStringListMessage(player, config.getStringList("messages.left due to leaving chunk"), null);
            }
        }
    }
}
