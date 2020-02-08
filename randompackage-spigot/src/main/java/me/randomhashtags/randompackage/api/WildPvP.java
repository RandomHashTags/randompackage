package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randompackage.addon.obj.PvPCountdownMatch;
import me.randomhashtags.randompackage.addon.obj.PvPMatch;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
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
    private boolean isLegacy = false;
    private HashMap<Player, List<Integer>> tasks;
    private List<Player> viewing;
    private HashMap<Player, Location> countdown;

    private int invincibilityDuration, nearbyRadius;

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
                    leaveQueue(m, getStringList(config, "messages.leave"));
                } else if(countdown.containsKey(player)) {
                    leaveCountdown(player);
                } else {
                    sendStringListMessage(player, getStringList(config, "messages.leave not in queue"), null);
                }
            } else {
                viewQueue(player);
            }
        }
        return true;
    }

    public String getIdentifier() {
        return "WILD_PVP";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "wild pvp.yml");

        isLegacy = EIGHT || NINE || TEN || ELEVEN;
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "wild pvp.yml"));
        blockedCommands = new ArrayList<>();
        for(String s : config.getStringList("settings.blocked commands")) {
            blockedCommands.add(s.toLowerCase());
        }

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        enterQueue = createItemStack(config, "gui.enter queue");
        request = createItemStack(config, "request");
        gui.getInventory().setItem(config.getInt("gui.enter queue.slot"), enterQueue);
        invincibilityDuration = config.getInt("settings.invincibility duration");
        nearbyRadius = config.getInt("settings.nearby radius");

        tasks = new HashMap<>();
        countdown = new HashMap<>();
        viewing = new ArrayList<>();
        viewInventory = new UInventory(null, 54, colorize(config.getString("view inventory.title")));

        sendConsoleDidLoadFeature("Wild PvP", started);
    }
    public void unload() {
        for(Player p : tasks.keySet()) {
            for(int i : tasks.get(p)) {
                SCHEDULER.cancelTask(i);
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

    public void viewQueue(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.wildpvp.view", true)) {
            player.closeInventory();
            player.openInventory(gui.getInventory());
        }
    }
    public void joinQueue(@NotNull Player player) {
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
                    if(isLegacy) {
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

                    for(String s : getStringList(config, "messages.created broadcast")) {
                        Bukkit.broadcastMessage(s.replace("{PLAYER}", n));
                    }
                    sendStringListMessage(player, getStringList(config, "messages.created"), null);
                } else {
                    sendStringListMessage(player, getStringList(config, "messages.must be in wilderness"), null);
                }
            } else {
                sendStringListMessage(player, getStringList(config, "messages.already in queue"), null);
            }
        }
    }
    public void leaveQueue(@NotNull PvPMatch match, @Nullable List<String> reason) {
        if(hasPermission(match.getCreator(), "RandomPackage.wildpvp.leave", true)) {
            sendStringListMessage(match.getCreator(), reason, null);
            delete(match);
        }
    }
    public void leaveCountdown(@NotNull Player player) {
        if(countdown.containsKey(player) && hasPermission(player, "RandomPackage.wildpvp.leave.countdown", true)) {
            player.teleport(countdown.get(player), PlayerTeleportEvent.TeleportCause.UNKNOWN);
            countdown.remove(player);
            final PvPCountdownMatch match = PvPCountdownMatch.valueOf(player);
            if(match != null) {
                sendStringListMessage(player, getStringList(config, "messages.leave"), null);
                for(int i : tasks.get(match.getCreator())) {
                    SCHEDULER.cancelTask(i);
                }
                match.delete();
            }
        }
    }
    public void viewInventoryOfQueue(@NotNull Player player, @NotNull PvPMatch match) {
        if(hasPermission(player, "RandomPackage.wildpvp.viewinventory", true)) {
            player.closeInventory();
            final Inventory inv = match.getInventory();
            player.openInventory(Bukkit.createInventory(player, 54, viewInventory.getTitle().replace("{PLAYER}", match.getCreator().getName())));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(viewInventory.getInventory().getContents());
            viewing.add(player);
            final ItemStack[] contents = inv.getContents();
            for(int s = 0; s < contents.length; s++) {
                top.setItem(s, contents[s]);
            }
            player.updateInventory();
        }
    }
    public void challenge(@NotNull Player player, @NotNull PvPMatch match) {
        if(hasPermission(player, "RandomPackage.wildpvp.challenge", true)) {
            final Player creator = match.getCreator();
            player.closeInventory();
            creator.closeInventory();

            countdown.put(player, player.getLocation());
            player.teleport(creator.getLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);

            final List<String> enabled = getStringList(config, "messages.invincibility enabled"), expire = getStringList(config, "messages.invincibility expired"), invincibilityExpiring = getStringList(config, "messages.invincibility expiring");
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{SEC}", Integer.toString(invincibilityDuration));
            sendStringListMessage(player, enabled, replacements);
            sendStringListMessage(creator, enabled, replacements);

            tasks.put(creator, new ArrayList<>());
            final PvPCountdownMatch cdm = new PvPCountdownMatch(creator, player);
            for(int i = 0; i <= invincibilityDuration; i++) {
                final int interval = i;
                tasks.get(creator).add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                    if(cdm.getCreator() != null && cdm.getChallenger() != null) {
                        replacements.put("{SEC}", Integer.toString(invincibilityDuration-interval));
                        if(interval == invincibilityDuration) {
                            cdm.delete();
                            countdown.remove(player);
                            sendStringListMessage(player, expire, null);
                            sendStringListMessage(creator, expire, null);
                        } else {
                            sendStringListMessage(player, invincibilityExpiring, replacements);
                            sendStringListMessage(creator, invincibilityExpiring, replacements);
                        }
                    } else {
                        countdown.remove(player);
                        for(int t : tasks.get(creator)) {
                            SCHEDULER.cancelTask(t);
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
        viewing.remove(event.getPlayer());
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
            final String msg = event.getMessage();
            for(String s : blockedCommands) {
                if(msg.toLowerCase().startsWith(s)) {
                    event.setCancelled(true);
                    sendStringListMessage(player, getStringList(config, "messages.cannot use blocked command"), null);
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
            sendStringListMessage(player, getStringList(config, "messages.cannot modify inventory"), null);
        } else {
            final Inventory top = player.getOpenInventory().getTopInventory();
            final boolean isViewing = viewing.contains(player);
            if(isViewing || event.getView().getTitle().equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();

                final int slot = event.getRawSlot();
                final ItemStack current = event.getCurrentItem();
                if(isViewing || slot < 0 || slot >= top.getSize() || current == null || current.getType().equals(Material.AIR)) return;
                if(current.equals(enterQueue)) {
                    joinQueue(player);
                } else if(current.getItemMeta() instanceof SkullMeta) {
                    final SkullMeta me = (SkullMeta) current.getItemMeta();
                    final Player o = isLegacy ? Bukkit.getPlayer(me.getOwner()) : me.getOwningPlayer().getPlayer();
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
                    sendStringListMessage(player, getStringList(config, "messages.left due to taken damage"), null);
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
                sendStringListMessage(player, getStringList(config, "messages.left due to leaving chunk"), null);
            }
        }
    }
}
