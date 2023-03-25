package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.JackpotData;
import me.randomhashtags.randompackage.event.JackpotPurchaseTicketsEvent;
import me.randomhashtags.randompackage.perms.JackpotPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public enum Jackpot implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    public int task;
    public List<Integer> countdownTasks;

    private UInventory gui;
    private List<Integer> confirm_slots, cancel_slots;
    public BigDecimal tax, ticket_cost, maxTickets, minTickets, playersPerPage, value;
    public long winnerPickedEvery, pick_next_winner;
    public HashMap<UUID, BigInteger> tickets_sold, top, purchasing;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0) {
            view(sender);
        } else {
            switch (args[0]) {
                case "pickwinner":
                    if(hasPermission(sender, JackpotPermission.COMMAND_PICK_WINNER, true)) {
                        pick_winner();
                    }
                    break;
                case "stats":
                    if(player != null) {
                        viewStats(player);
                    }
                    break;
                case "top":
                    viewTop(sender, l == 1 ? 1 : getRemainingInt(args[1]));
                    break;
                case "buy":
                    final List<String> b = getStringList(config, "messages.enter valid ticket amount");
                    if(l == 1) {
                        sendStringListMessage(player, b, null);
                    } else {
                        final BigInteger amount = BigInteger.valueOf(getRemainingInt(args[1]));
                        final int amt = amount.intValue();
                        if(amt < minTickets.intValue() || amt > maxTickets.intValue()) {
                            sendStringListMessage(player, b, null);
                        } else {
                            confirmPurchaseTickets(player, amount);
                        }
                    }
                    break;
                case "toggle":
                case "stfu":
                    if(player != null) {
                        tryToggleNotifications(player);
                    }
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
        final long started = System.currentTimeMillis();
        save(null, "jackpot.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "jackpot.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        final ItemStack confirm = createItemStack(config, "gui.confirm"), cancel = createItemStack(config, "gui.cancel");
        confirm_slots = new ArrayList<>();
        cancel_slots = new ArrayList<>();
        for(String s : getConfigurationSectionKeys(config, "gui", false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final int slot = config.getInt("gui." + s + ".slot");
                final String i = config.getString("gui." + s + ".item").toLowerCase();
                final boolean isConfirm = i.equals("confirm"), isCancel = i.equals("cancel");
                if(isConfirm) {
                    confirm_slots.add(slot);
                } else if(isCancel) {
                    cancel_slots.add(slot);
                }
                final ItemStack item = isConfirm ? confirm : isCancel ? cancel : createItemStack(config, "gui." + s);
                gi.setItem(slot, item);
            }
        }

        tax = BigDecimal.valueOf(config.getDouble("settings.tax"));
        ticket_cost = BigDecimal.valueOf(config.getInt("settings.ticket cost"));
        maxTickets = BigDecimal.valueOf(config.getInt("settings.max tickets"));
        minTickets = BigDecimal.valueOf(config.getInt("settings.min tickets"));
        playersPerPage = BigDecimal.valueOf(config.getInt("messages.players per page"));
        winnerPickedEvery = config.getInt("settings.winner picked every");
        tickets_sold = new HashMap<>();
        top = new HashMap<>();
        purchasing = new HashMap<>();

        final long e = OTHER_YML.getLong("jackpot.pick next winner");
        pick_next_winner = e == 0 ? started + winnerPickedEvery*1000 : e;

        value = BigDecimal.valueOf(OTHER_YML.getDouble("jackpot.value"));
        for(String s : getConfigurationSectionKeys(OTHER_YML, "jackpot", false)) {
            if(!s.equals("value") && !s.equals("pick next winner")) {
                final UUID uuid = UUID.fromString(s);
                final BigInteger b = BigInteger.valueOf(OTHER_YML.getInt("jackpot." + s));
                tickets_sold.put(uuid, b);
            }
        }
        startTask(started);
    }
    @Override
    public void unload() {
        OTHER_YML.set("jackpot", null);
        OTHER_YML.set("jackpot.pick next winner", pick_next_winner);
        OTHER_YML.set("jackpot.value", value);
        for(UUID uuid : tickets_sold.keySet()) {
            OTHER_YML.set("jackpot." + uuid.toString(), tickets_sold.get(uuid).intValue());
        }
        saveOtherData();
        SCHEDULER.cancelTask(task);
        for(int i : countdownTasks) {
            SCHEDULER.cancelTask(i);
        }
    }

    public void pick_winner() {
        final List<UUID> tickets = getTickets();
        final int size = tickets.size();
        if(size > 0) {
            final UUID winner = tickets.get(RANDOM.nextInt(size));
            final OfflinePlayer op = Bukkit.getOfflinePlayer(winner);
            final HashMap<String, String> replacements = new HashMap<>();
            final BigInteger winnerTickets = tickets_sold.get(winner);
            final BigDecimal taxed = value.multiply(tax), total = value.subtract(taxed);
            ECONOMY.depositPlayer(op, total.doubleValue());

            final JackpotData data = FileRPPlayer.get(winner).getJackpotData();
            data.setTotalWins(data.getTotalWins().add(BigInteger.ONE));
            data.setTotalWonCash(data.getTotalWonCash().add(total));

            final String percent = formatDouble(getPercent(winnerTickets, size)), tt = formatInt(size);
            replacements.put("{PLAYER}", op.getName());
            replacements.put("{TICKETS}", formatNumber(winnerTickets, false));
            replacements.put("{TICKETS%}", percent);
            replacements.put("{TOTAL_TICKETS}", tt);
            replacements.put("{$}", formatBigDecimal(total));

            final Collection<? extends Player> o = Bukkit.getOnlinePlayers();
            for(String s : getStringList(config, "messages.won")) {
                for(String r : replacements.keySet()) {
                    s = s.replace(r, replacements.get(r));
                }
                for(Player p : o) {
                    p.sendMessage(s);
                }
            }
            value = BigDecimal.ZERO;
            tickets_sold.clear();
        }
        final long time = System.currentTimeMillis();
        pick_next_winner = time + winnerPickedEvery * 1000;
        startTask(time);
    }
    public List<UUID> getTickets() {
        final List<UUID> list = new ArrayList<>();
        for(UUID uuid : tickets_sold.keySet()) {
            for(int i = 1; i <= tickets_sold.get(uuid).intValue(); i++) {
                list.add(uuid);
            }
        }
        return list;
    }
    public double getPercent(@NotNull BigInteger tickets, int size) {
        final BigDecimal big = BigDecimal.valueOf((double) (size == 0 ? 1 : size)), hundred = BigDecimal.valueOf(100);
        return round(BigDecimal.valueOf(tickets.doubleValue()/big.doubleValue()).multiply(hundred).doubleValue(), 2);
    }
    private void broadcastCountdown(long timeleft) {
        final List<String> msg = getStringList(config, "messages.countdown");
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{TIME}", getRemainingTime(timeleft));
        replacements.put("{$}", formatBigDecimal(value));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(FileRPPlayer.get(player.getUniqueId()).getJackpotData().receivesNotifications()) {
                sendStringListMessage(player, msg, replacements);
            }
        }
    }
    private void startTask(long time) {
        final long pick = ((pick_next_winner -time)/1000)*20;
        if(countdownTasks != null) {
            for(int i : countdownTasks) {
                SCHEDULER.cancelTask(i);
            }
        }
        task = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, this::pick_winner, pick);
        countdownTasks = new ArrayList<>();
        for(String s : getStringList(config, "messages.countdowns")) {
            final long delay = (getDelay(s)/1000)*20;
            countdownTasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                broadcastCountdown((delay/20)*1000);
            }, pick-delay));
        }
    }
    public void confirmPurchaseTickets(@NotNull Player player, @NotNull BigInteger tickets) {
        if(hasPermission(player, JackpotPermission.BUY_TICKETS, true)) {
            player.closeInventory();
            purchasing.put(player.getUniqueId(), tickets);
            final String amount = formatNumber(tickets, false), cost = formatBigDecimal(ticket_cost.multiply(new BigDecimal(tickets)));
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle().replace("{AMOUNT}", amount)));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                final ItemStack item = top.getItem(i);
                if(item != null) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        for(String string : itemMeta.getLore()) {
                            lore.add(string.replace("{AMOUNT}", amount).replace("{$}", cost));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
            }
            player.updateInventory();
        }
    }
    public void purchaseTickets(@NotNull Player player, @NotNull BigInteger tickets) {
        final BigDecimal cost = ticket_cost.multiply(new BigDecimal(tickets));
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{AMOUNT}", formatNumber(tickets, false));
        replacements.put("{$}", formatBigDecimal(cost));
        if(ECONOMY.withdrawPlayer(player, cost.doubleValue()).transactionSuccess()) {
            final JackpotPurchaseTicketsEvent e = new JackpotPurchaseTicketsEvent(player, tickets, cost);
            PLUGIN_MANAGER.callEvent(e);
            if(!e.isCancelled()) {
                final UUID uuid = player.getUniqueId();
                final JackpotData pdata = FileRPPlayer.get(uuid).getJackpotData();
                pdata.setTotalTicketsBought(pdata.getTotalTicketsBought().add(tickets));
                tickets_sold.put(uuid, tickets_sold.getOrDefault(uuid, BigInteger.ZERO).add(tickets));
                value = value.add(cost);
                sendStringListMessage(player, getStringList(config, "messages.purchased"), replacements);
            }
        } else {
            sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
        }
    }

    public void view(@NotNull CommandSender sender) {
        if(hasPermission(sender, JackpotPermission.VIEW, true)) {
            final String time = getRemainingTime(pick_next_winner -System.currentTimeMillis());
            final HashMap<String, String> replacements = new HashMap<>();
            final BigInteger playerTickets = sender instanceof Player ? tickets_sold.getOrDefault(((Player) sender).getUniqueId(), BigInteger.ZERO) : BigInteger.ZERO;
            final int s = getTickets().size();
            replacements.put("{VALUE}", formatBigDecimal(value));
            replacements.put("{TICKETS}", formatDouble(getTickets().size()));
            replacements.put("{YOUR_TICKETS}", formatNumber(playerTickets, false));
            replacements.put("{YOUR_TICKETS%}", formatDouble(getPercent(playerTickets, s)));
            replacements.put("{TIME}", time);
            sendStringListMessage(sender, getStringList(config, "messages.view"), replacements);
        }
    }
    public void viewStats(@NotNull Player player) {
        if(hasPermission(player, JackpotPermission.VIEW_STATS, true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final JackpotData data = pdata.getJackpotData();
            replacements.put("{$}", formatBigDecimal(data.getTotalWonCash()));
            replacements.put("{TICKETS}", formatNumber(data.getTotalTicketsBought(), false));
            replacements.put("{WINS}", formatNumber(data.getTotalWins(), false));
            sendStringListMessage(player, getStringList(config, "messages.stats"), replacements);
        }
    }
    public void viewTop(@NotNull CommandSender sender, int page) {
        if(hasPermission(sender, JackpotPermission.VIEW_TOP, true)) {
            final List<String> list = colorizeListString(getStringList(config, "messages.top"));
            final String p = Integer.toString(page);
            final int perPage = config.getInt("messages.players per page");
            for(String s : list) {
                if(s.contains("{PLACE}")) {
                    // TODO: fix dis
                } else {
                    s = s.replace("{PAGE}", p);
                    sender.sendMessage(s);
                }
            }
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, JackpotPermission.VIEW_HELP, true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void tryToggleNotifications(@NotNull Player player) {
        if(hasPermission(player, JackpotPermission.TOGGLE_NOTIFICATIONS, true)) {
            final JackpotData data = FileRPPlayer.get(player.getUniqueId()).getJackpotData();
            final boolean status = !data.receivesNotifications();
            data.setReceivesNotifications(status);
            sendStringListMessage(player, getStringList(config, "messages.toggle notifications." + (status ? "on" : "off")), null);
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        purchasing.remove(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        if(purchasing.containsKey(uuid)) {
            event.setCancelled(true);
            player.updateInventory();
            final int slot = event.getRawSlot();
            final ItemStack current = event.getCurrentItem();
            if(slot < 0 || slot >= player.getOpenInventory().getTopInventory().getSize() || current == null || current.getType().equals(Material.AIR)) {
                return;
            }

            if(confirm_slots.contains(slot)) {
                purchaseTickets(player, purchasing.get(uuid));
            } else if(cancel_slots.contains(slot)) {
            } else {
                return;
            }
            player.closeInventory();
        }
    }
}
