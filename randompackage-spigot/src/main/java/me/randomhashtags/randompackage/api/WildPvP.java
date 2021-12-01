package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.addon.obj.PvPCountdownMatch;
import me.randomhashtags.randompackage.addon.obj.PvPMatch;
import me.randomhashtags.randompackage.perms.WildPvPPermission;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import java.util.*;

public final class WildPvP extends RPFeatureSpigot implements CommandExecutor {
    public static final WildPvP INSTANCE = new WildPvP();

    public YamlConfiguration config;
    private UInventory gui, viewInventory;
    private ItemStack enterQueue, request;
    private List<String> blockedCommands;
    private boolean isLegacy = false;
    private HashMap<Player, HashSet<Integer>> tasks;
    private HashSet<Player> viewing;
    private HashMap<Player, Location> countdown;

    private int invincibilityDuration, nearbyRadius;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        if(args.length == 0) {
            viewQueue(player);
        } else {
            if(args[0].equals("leave")) {
                final PvPMatch match = PvPMatch.valueOf(player);
                if(match != null) {
                    leaveQueue(match, getStringList(config, "messages.leave"));
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

    @Override
    public String getIdentifier() {
        return "WILD_PVP";
    }
    @Override
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
        viewing = new HashSet<>();
        viewInventory = new UInventory(null, 54, colorize(config.getString("view inventory.title")));

        sendConsoleDidLoadFeature("Wild PvP", started);
    }
    public void unload() {
        for(HashSet<Integer> set : tasks.values()) {
            for(int taskID : set) {
                SCHEDULER.cancelTask(taskID);
            }
        }
        for(Player player : new ArrayList<>(viewing)) {
            player.closeInventory();
        }
        final HashMap<Player, PvPMatch> matches = PvPMatch.MATCHES;
        if(matches != null) {
            for(PvPMatch p : new ArrayList<>(matches.values())) {
                delete(p);
            }
        }
        final List<PvPCountdownMatch> countdowns = PvPCountdownMatch.COUNTDOWNS;
        if(countdowns != null) {
            for(PvPCountdownMatch p : new ArrayList<>(countdowns)) {
                p.delete();
            }
        }
        PvPMatch.MATCHES = null;
        PvPCountdownMatch.COUNTDOWNS = null;
    }

    public void viewQueue(@NotNull Player player) {
        if(hasPermission(player, WildPvPPermission.VIEW_QUEUE, true)) {
            player.closeInventory();
            player.openInventory(gui.getInventory());
        }
    }
    public void joinQueue(@NotNull Player player) {
        if(hasPermission(player, WildPvPPermission.JOIN_QUEUE, true)) {
            player.closeInventory();
            final PvPMatch match = PvPMatch.valueOf(player);
            if(match == null) {
                final Location location = player.getLocation();
                final Chunk chunk = location.getChunk();
                final RegionalAPI regions = RegionalAPI.INSTANCE;
                final String f = ChatColor.stripColor(regions.getFactionTagAt(location));
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

                    final String playerName = player.getName(), factionTag = regions.getFactionTag(player.getUniqueId()), HP = roundDoubleString(hp, 0), nearbyCount = Integer.toString(nearby);
                    final ItemStack skull = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                    final SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    if(isLegacy) {
                        skullMeta.setOwner(player.getName());
                    } else {
                        skullMeta.setOwningPlayer(player);
                    }
                    final List<String> lore = new ArrayList<>();
                    for(String s : request.getItemMeta().getLore()) {
                        lore.add(s.replace("{NEARBY_PLAYERS}", nearbyCount).replace("{FACTION}", factionTag != null ? factionTag : "").replace("{HP}", HP));
                    }
                    skullMeta.setLore(lore);
                    skull.setItemMeta(skullMeta);
                    i.setItem(slot, skull);

                    for(String string : getStringList(config, "messages.created broadcast")) {
                        Bukkit.broadcastMessage(string.replace("{PLAYER}", playerName));
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
        if(hasPermission(match.getCreator(), WildPvPPermission.LEAVE_QUEUE, true)) {
            sendStringListMessage(match.getCreator(), reason, null);
            delete(match);
        }
    }
    public void leaveCountdown(@NotNull Player player) {
        if(countdown.containsKey(player) && hasPermission(player, WildPvPPermission.LEAVE_QUEUE_DURING_COUNTDOWN, true)) {
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
        if(hasPermission(player, WildPvPPermission.VIEW_QUEUE_INVENTORY, true)) {
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
        if(hasPermission(player, WildPvPPermission.CHALLENGE, true)) {
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

            tasks.put(creator, new HashSet<>());
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
    private void delete(PvPMatch match) {
        final int slot = match.slot;
        final Inventory inv = gui.getInventory();
        inv.setItem(slot, new ItemStack(Material.AIR));
        for(int i = slot; i < gui.getSize(); i++) {
            final PvPMatch targetMatch = PvPMatch.valueOf(i);
            if(targetMatch != null && match != targetMatch) {
                targetMatch.slot -= 1;
                inv.setItem(i-1, inv.getItem(i));
            }
        }
        match.delete();
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        viewing.remove((Player) event.getPlayer());
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PvPMatch match = PvPMatch.valueOf(player);
        if(match != null) {
            delete(match);
        } else {
            final PvPCountdownMatch countdownMatch = PvPCountdownMatch.valueOf(player);
            if(countdownMatch != null) {
                countdownMatch.delete();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final PvPMatch match = PvPMatch.valueOf(player);
        if(match != null) {
            final String msg = event.getMessage();
            for(String command : blockedCommands) {
                if(msg.toLowerCase().startsWith(command)) {
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
        PvPMatch match = PvPMatch.valueOf(player);
        if(match != null) {
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
                    final SkullMeta skullmeta = (SkullMeta) current.getItemMeta();
                    final Player targetPlayer = isLegacy ? Bukkit.getPlayer(skullmeta.getOwner()) : skullmeta.getOwningPlayer().getPlayer();
                    match = PvPMatch.valueOf(targetPlayer);
                    if(match != null) {
                        final String clickType = event.getClick().name();
                        player.closeInventory();
                        if(clickType.contains("RIGHT")) {
                            viewInventoryOfQueue(player, match);
                        } else if(clickType.contains("LEFT")) {
                            challenge(player, match);
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        final PvPMatch match = PvPMatch.valueOf(event.getPlayer());
        if(match != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageEvent(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            final Player player = (Player) entity;
            final PvPCountdownMatch countdownMatch = PvPCountdownMatch.valueOf(player);
            if(countdownMatch != null) {
                event.setCancelled(true);
            } else {
                final PvPMatch match = PvPMatch.valueOf(player);
                if(match != null) {
                    sendStringListMessage(player, getStringList(config, "messages.left due to taken damage"), null);
                    delete(match);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerMoveEvent(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PvPMatch match = PvPMatch.valueOf(player);
        if(match != null && player.getLocation().getChunk() != match.getChunk()) {
            delete(match);
            sendStringListMessage(player, getStringList(config, "messages.left due to leaving chunk"), null);
        }
    }
}
