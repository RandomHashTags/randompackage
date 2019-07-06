package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.api.events.JackpotPurchaseTicketsEvent;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class Jackpot extends RPFeature implements CommandExecutor {
    private static Jackpot instance;
    public static Jackpot getJackpot() {
        if(instance == null) instance = new Jackpot();
        return instance;
    }
    public YamlConfiguration config;
    public int task;
    public List<Integer> countdownTasks;

    private UInventory gui;
    private List<Integer> confirmSlots, cancelSlots;
    public double tax, ticketCost, maxTickets, minTickets, playersPerPage, winnerPickedEvery;
    public long value, pickNextWinner;
    public HashMap<UUID, Integer> ticketsSold, top, purchasing;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(args.length == 0) {
            view(sender);
        } else {
            final String a = args[0];
            final int l = args.length;
            if(a.equals("pickwinner") && hasPermission(sender, "RandomSky.jackpot.pickwinner", true)) {
                pickWinner();
            } else if(a.equals("stats") && player != null) {
                viewStats(player);
            } else if(a.equals("top")) {

            } else if(a.equals("buy")) {
                final List<String> b = config.getStringList("messages.enter valid ticket amount");
                if(l == 1) {
                    sendStringListMessage(player, b, null);
                } else {
                    final int amount = getRemainingInt(args[1]);
                    if(amount < minTickets || amount > maxTickets) {
                        sendStringListMessage(player, b, null);
                    } else {
                        confirmPurchaseTickets(player, amount);
                    }
                }
            } else if(a.equals("help")) {
                viewHelp(sender);
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "jackpot.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "jackpot.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        final ItemStack confirm = d(config, "gui.confirm"), cancel = d(config, "gui.cancel");
        confirmSlots = new ArrayList<>();
        cancelSlots = new ArrayList<>();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final int slot = config.getInt("gui." + s + ".slot");
                final String i = config.getString("gui." + s + ".item").toLowerCase();
                final boolean co = i.equals("confirm"), ca = i.equals("cancel");
                if(co) confirmSlots.add(slot);
                else if(ca) cancelSlots.add(slot);
                item = co ? confirm : ca ? cancel : d(config, "gui." + s);
                gi.setItem(slot, item);
            }
        }

        tax = config.getDouble("settings.tax");
        ticketCost = config.getInt("settings.ticket cost");
        maxTickets = config.getInt("settings.max tickets");
        minTickets = config.getInt("settings.min tickets");
        playersPerPage = config.getInt("messages.players per page");
        winnerPickedEvery = config.getInt("settings.winner picked every");
        ticketsSold = new HashMap<>();
        top = new HashMap<>();
        purchasing = new HashMap<>();

        final YamlConfiguration a = otherdata;
        final long e = a.getLong("jackpot.pick next winner");
        pickNextWinner = e == 0 ? (long) (started+winnerPickedEvery*1000) : e;

        value = a.getLong("jackpot.value");
        final ConfigurationSection j = a.getConfigurationSection("jackpot");
        if(j != null) {
            for(String s : j.getKeys(false)) {
                if(!s.equals("value") && !s.equals("pick next winner")) {
                    final UUID u = UUID.fromString(s);
                    final int b = a.getInt("jackpot." + s);
                    ticketsSold.put(u, b);
                }
            }
        }
        startTask(started);
        sendConsoleMessage("&6[RandomPackage] &aLoaded Jackpot &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        final YamlConfiguration a = otherdata;
        a.set("jackpot", null);
        a.set("jackpot.pick next winner", pickNextWinner);
        a.set("jackpot.value", value);
        for(UUID u : ticketsSold.keySet()) {
            a.set("jackpot." + u.toString(), ticketsSold.get(u));
        }
        saveOtherData();
        config = null;
        scheduler.cancelTask(task);
        for(int i : countdownTasks) scheduler.cancelTask(i);
        gui = null;
        confirmSlots = null;
        cancelSlots = null;
        task = 0;
        countdownTasks = null;
        value = 0;
        ticketCost = 0;
        maxTickets = 0;
        minTickets = 0;
        playersPerPage = 0;
        winnerPickedEvery = 0;
        pickNextWinner = 0;
        ticketsSold = null;
        top = null;
        purchasing = null;
    }

    public void pickWinner() {
        final List<UUID> tic = getTickets();
        final int size = tic.size();
        if(size > 0) {
            final UUID w = tic.get(random.nextInt(size));
            final OfflinePlayer op = Bukkit.getOfflinePlayer(w);
            final HashMap<String, String> replacements = new HashMap<>();
            final int t = ticketsSold.get(w);
            final double taxed = value*tax, total = value-taxed;
            eco.depositPlayer(op, total);

            final RPPlayer pdata = RPPlayer.get(w);
            final boolean loaded = pdata.isLoaded;
            if(!loaded) pdata.load();
            pdata.jackpotWins += 1;
            pdata.jackpotWonCash += total;
            if(!loaded) pdata.unload();

            final String percent = formatDouble(getPercent(t, size)), tt = formatInt(size);

            replacements.put("{PLAYER}", op.getName());
            replacements.put("{TICKETS}", formatInt(t));
            replacements.put("{TICKETS%}", percent);
            replacements.put("{TOTAL_TICKETS}", tt);
            replacements.put("{$}", formatDouble(total));

            final Collection<? extends Player> o = Bukkit.getOnlinePlayers();
            for(String s : config.getStringList("messages.won")) {
                for(String r : replacements.keySet()) s = s.replace(r, replacements.get(r));
                s = ChatColor.translateAlternateColorCodes('&', s);
                for(Player p : o) {
                    p.sendMessage(s);
                }
            }
            value = 0;
            ticketsSold.clear();
        }
        final long time = System.currentTimeMillis();
        pickNextWinner = (long) (time+winnerPickedEvery*1000);
        startTask(time);
    }
    public List<UUID> getTickets() {
        final List<UUID> a = new ArrayList<>();
        for(UUID u : ticketsSold.keySet()) {
            for(int i = 1; i <= ticketsSold.get(u); i++) {
                a.add(u);
            }
        }
        return a;
    }
    public double getPercent(int tickets, int size) {
        return round((((double) tickets)/((double) size))*100, 2);
    }
    public void broadcastCountdown(long timeleft) {
        final List<String> c = config.getStringList("messages.countdown");
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{TIME}", getRemainingTime(timeleft));
        replacements.put("{$}", formatLong(value));
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(RPPlayer.get(p.getUniqueId()).jackpotCountdown) {
                sendStringListMessage(p, c, replacements);
            }
        }
    }
    private void startTask(long time) {
        final long pick = ((pickNextWinner-time)/1000)*20;
        task = scheduler.scheduleSyncDelayedTask(randompackage, this::pickWinner, pick);
        countdownTasks = new ArrayList<>();
        for(String s : config.getStringList("messages.countdowns")) {
            final long delay = (getDelay(s)/1000)*20;
            countdownTasks.add(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                broadcastCountdown((delay/20)*1000);
            }, pick-delay));
        }
    }
    public void confirmPurchaseTickets(Player player, int tickets) {
        if(hasPermission(player, "RandomPackage.jackpot.buy", true)) {
            player.closeInventory();
            purchasing.put(player.getUniqueId(), tickets);
            final String a = Integer.toString(tickets), p = formatDouble(tickets*ticketCost);
            final int s = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, s, gui.getTitle().replace("{AMOUNT}", a)));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < s; i++) {
                item = top.getItem(i);
                if(item != null) {
                    itemMeta = item.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) {
                        for(String l : itemMeta.getLore()) {
                            lore.add(l.replace("{AMOUNT}", a).replace("{$}", p));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
            }
            player.updateInventory();
        }
    }
    public void purchaseTickets(Player player, int tickets) {
        final long p = new BigDecimal(tickets*ticketCost).longValue();
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{AMOUNT}", formatInt(tickets));
        replacements.put("{$}", formatLong(p));
        if(eco.withdrawPlayer(player, p).transactionSuccess()) {
            final JackpotPurchaseTicketsEvent e = new JackpotPurchaseTicketsEvent(player, tickets, p);
            pluginmanager.callEvent(e);
            final UUID u = player.getUniqueId();
            RPPlayer.get(u).jackpotTickets += tickets;
            ticketsSold.put(u, ticketsSold.getOrDefault(u, 0)+tickets);
            value += p;
            sendStringListMessage(player, config.getStringList("messages.purchased"), replacements);
        } else {
            sendStringListMessage(player, config.getStringList("messages.cannot afford"), replacements);
        }
    }

    public void view(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.jackpot", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final int t = sender instanceof Player ? ticketsSold.getOrDefault(((Player) sender).getUniqueId(), 0) : 0, s = getTickets().size();
            replacements.put("{VALUE}", formatDouble(value));
            replacements.put("{TICKETS}", formatDouble(getTickets().size()));
            replacements.put("{YOUR_TICKETS}", formatInt(t));
            replacements.put("{YOUR_TICKETS%}", formatDouble(round(((double) t/(double) (s == 0 ? 1 : s))*100, 2)));
            replacements.put("{TIME}", getRemainingTime(pickNextWinner-System.currentTimeMillis()));
            sendStringListMessage(sender, config.getStringList("messages.view"), replacements);
        }
    }
    public void viewStats(Player player) {
        if(hasPermission(player, "RandomPackage.jackpot.stats", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            replacements.put("{$}", formatDouble(pdata.jackpotWonCash));
            replacements.put("{TICKETS}", formatInt(pdata.jackpotTickets));
            replacements.put("{WINS}", formatInt(pdata.jackpotWins));
            sendStringListMessage(player, config.getStringList("messages.stats"), replacements);
        }
    }
    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.jackpot.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        purchasing.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final Player player = (Player) event.getWhoClicked();
            final UUID u = player.getUniqueId();
            if(purchasing.containsKey(u)) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot();
                final ItemStack c = event.getCurrentItem();
                if(r < 0 || r >= player.getOpenInventory().getTopInventory().getSize() || c == null || c.getType().equals(Material.AIR)) return;
                if(confirmSlots.contains(r)) {
                    purchaseTickets(player, purchasing.get(u));
                } else if(cancelSlots.contains(r)) {
                } else return;
                player.closeInventory();
            }
        }
    }
}
