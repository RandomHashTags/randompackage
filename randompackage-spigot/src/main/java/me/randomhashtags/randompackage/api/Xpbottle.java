package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.SecondaryData;
import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import me.randomhashtags.randompackage.perms.XpbottlePermission;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum Xpbottle implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    public ItemStack bottle;
    private int xpbottleValueSlot;
    private List<String> teleportCauses;
    private HashMap<String, Integer> minbottles, expexhaustion;
    private HashMap<String, Double> teleportationDelay, teleportMinDelay, teleportationVariable;
    private HashMap<Player, String> delayed;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && hasPermission(sender, XpbottlePermission.COMMAND, true)) {
            if(args.length == 0) {
                sendStringListMessage(player, getStringList(config, "messages.argument zero"), null);
            } else {
                final BigDecimal amount = BigDecimal.valueOf(getRemainingInt(args[0]));
                final int i = amount.intValue();
                if(i <= 0) {
                    sendStringListMessage(sender, getStringList(config, "messages.withdraw at least"), null);
                } else if(i > getTotalExperience(player)) {
                    sendStringListMessage(player, getStringList(config, "messages.not enough to bottle"), null);
                } else {
                    getBottle(player, amount);
                }
            }
        }
        return true;
    }

    @Override
    public String getIdentifier() {
        return "XPBOTTLE";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "xpbottle.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "xpbottle.yml"));

        bottle = GivedpItem.INSTANCE.items.get("xpbottle");
        int i = 0;
        for(String s : bottle.getItemMeta().getLore()) {
            if(s.contains("{VALUE}")) {
                xpbottleValueSlot = i;
            }
            i++;
        }

        minbottles = new HashMap<>();
        for(String string : config.getStringList("xpbottle.min bottle")) {
            final String[] values = string.split("=");
            minbottles.put(values[0], Integer.parseInt(values[1]));
        }
        expexhaustion = new HashMap<>();
        for(String string : config.getStringList("xpbottle.exp exhaustion")) {
            final String[] values = string.split("=");
            expexhaustion.put(values[0], Integer.parseInt(values[1]));
        }
        teleportationDelay = new HashMap<>();
        for(String string : config.getStringList("xpbottle.teleportation delay")) {
            final String[] values = string.split("=");
            teleportationDelay.put(values[0], Double.parseDouble(values[1]));
        }
        teleportMinDelay = new HashMap<>();
        for(String string : config.getStringList("xpbottle.teleport min delay")) {
            final String[] values = string.split("=");
            teleportMinDelay.put(values[0], Double.parseDouble(values[1]));
        }
        teleportationVariable = new HashMap<>();
        for(String string : config.getStringList("xpbottle.teleportation variable")) {
            final String[] values = string.split("=");
            teleportationVariable.put(values[0], Double.parseDouble(values[1]));
        }

        teleportCauses = config.getStringList("xpbottle.teleport causes");
        delayed = new HashMap<>();
        sendConsoleDidLoadFeature("Xpbottle", started);
    }
    @Override
    public void unload() {
    }

    public int getMinBottle(@NotNull World world) {
        return minbottles.getOrDefault(world.getName(), 0);
    }
    public int getExpExhaustion(@NotNull World world) {
        return expexhaustion.getOrDefault(world.getName(), 0);
    }
    public double getTeleportationDelay(@NotNull World world) {
        return teleportationDelay.getOrDefault(world.getName(), 0.00);
    }
    public double getTeleportMinDelay(@NotNull World world) {
        return teleportMinDelay.getOrDefault(world.getName(), 0.00);
    }
    public double getTeleportationVariable(@NotNull World world) {
        return teleportationVariable.getOrDefault(world.getName(), 0.00);
    }

    public void getBottle(@NotNull Player player, @NotNull BigDecimal amount) {
        final int minbottle = getMinBottle(player.getWorld());
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{MIN}", Integer.toString(minbottle));
        replacements.put("{VALUE}", formatBigDecimal(amount));
        final int amountInt = amount.intValue();
        if(amountInt < minbottle) {
            sendStringListMessage(player, getStringList(config, "messages.withdraw at least"), replacements);
        } else {
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final SecondaryData secondaryData = pdata.getSecondaryData();
            final long time = System.currentTimeMillis();
            if(secondaryData.isXPExhausted() && !hasPermission(player, XpbottlePermission.BYPASS_EXHAUSTION, false)) {
                replacements.put("{TIME}", getRemainingTime(secondaryData.getXPExhaustionExpiration() - time));
                sendStringListMessage(player, getStringList(config, "messages.cannot xpbottle"), replacements);
            } else {
                sendStringListMessage(player, getStringList(config, "messages.withdraw"), replacements);
                giveItem(player, GivedpItem.INSTANCE.getXPBottle(amount, player.getName()));

                final int xp = Math.round(getTotalExperience(player));
                setTotalExperience(player, xp-amountInt);
                playSound(config, "xpbottle.sounds.withdraw", player, player.getLocation(), false);
                final int exh = getExpExhaustion(player.getWorld());
                if(exh != -1) {
                    secondaryData.setXPExhaustionExpiration(time+(exh*1000*60));
                    replacements.put("{MIN}", Integer.toString(exh));
                    sendStringListMessage(player, getStringList(config, "messages.afflict"), replacements);
                }
            }
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final Player player = event.getPlayer();
            if(bottle.getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final int amount = getRemainingInt(ChatColor.stripColor(is.getItemMeta().getLore().get(xpbottleValueSlot)));
                player.giveExp(amount);
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{VALUE}", formatInt(amount));
                replacements.put("{ENCHANTER}", "Server");
                replacements.put("{PLAYER}", "Server");
                sendStringListMessage(player, getStringList(config, "messages.deposit"), replacements);
                playSound(config, "xpbottle.sounds.redeem", player, player.getLocation(), false);
            }
        }
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final PlayerTeleportDelayEvent tel = PlayerTeleportDelayEvent.TELEPORTING.getOrDefault(event.getPlayer(), null);
        if(tel != null) {
            tel.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String m = event.getMessage().toLowerCase();
        for(String s : getStringList(config, "xpbottle.delayed commands")) {
            if(m.startsWith(s.toLowerCase())) {
                delayed.put(player, s);
                SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> delayed.remove(player), 1);
                return;
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerMoveEvent(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PlayerTeleportDelayEvent tel = PlayerTeleportDelayEvent.TELEPORTING.getOrDefault(player, null);
        if(tel != null) {
            final Location to = player.getLocation(), from = tel.getFrom();
            if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
                return;
            } else {
                final HashMap<Player, PlayerTeleportDelayEvent> events = PlayerTeleportDelayEvent.TELEPORTING;
                sendStringListMessage(player, getStringList(config, "messages.teleport cancelled"), null);
                tel.setCancelled(true);
                SCHEDULER.cancelTask(events.get(player).getTask());
                events.remove(player);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerTeleportEvent(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if(teleportCauses.contains(event.getCause().name()) && delayed.containsKey(player)) {
            delayed.remove(player);
            final World world = player.getWorld();
            double delay = getTeleportationDelay(world);
            if(hasPermission(player, XpbottlePermission.BYPASS_DELAY, false) || delay <= 0) {
                return;
            }
            final UUID uuid = player.getUniqueId();
            final FileRPPlayer pdata = FileRPPlayer.get(uuid);
            final SecondaryData secondaryData = pdata.getSecondaryData();
            if(secondaryData.isXPExhausted()) {
                final String remaining = getRemainingTime(secondaryData.getXPExhaustionExpiration() - System.currentTimeMillis());
                for(String s : getStringList(config, "messages.cannot teleport")) {
                    if(s.contains("{TIME}")) s = s.replace("{TIME}", remaining);
                    player.sendMessage(colorize(s));
                }
            } else {
                final HashMap<Player, PlayerTeleportDelayEvent> events = PlayerTeleportDelayEvent.TELEPORTING;
                final PlayerTeleportDelayEvent previous = events.getOrDefault(player, null);
                final boolean hasPrevious = previous != null;
                final double mindelay = getTeleportMinDelay(world);
                delay -= getTotalExperience(player) / getTeleportationVariable(world);
                delay = round(delay, 3);
                if(delay < mindelay) delay = mindelay;
                if(hasPrevious) {
                    previous.setCancelled(true);
                    SCHEDULER.cancelTask(previous.getTask());
                    events.remove(player);
                }
                final PlayerTeleportDelayEvent e = new PlayerTeleportDelayEvent(player, delay, event.getFrom(), event.getTo());
                PLUGIN_MANAGER.callEvent(e);
                if(!e.isCancelled()) {
                    final long de = (long) ((((long) delay * 20)) + (20 * Double.parseDouble("0." + Double.toString(e.getDelay()).split("\\.")[1])));
                    final int t = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                        player.teleport(e.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        events.remove(player);
                    }, de);
                    e.setTask(t);
                    events.put(player, e);
                    final HashMap<String, String> replacements = new HashMap<String, String>() {{ put("{SECS", roundDoubleString(e.getDelay(), 3)); }};
                    sendStringListMessage(player, getStringList(config, "messages.pending teleport"), replacements);
                } else {
                    SCHEDULER.cancelTask(e.getTask());
                    events.remove(player);
                }
            }
            event.setCancelled(true);
        }
    }
}
