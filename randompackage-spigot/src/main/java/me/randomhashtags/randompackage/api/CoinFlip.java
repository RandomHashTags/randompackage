package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.api.events.CoinFlipEndEvent;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.addons.objects.CoinFlipMatch;
import me.randomhashtags.randompackage.addons.objects.CoinFlipOption;
import me.randomhashtags.randompackage.addons.objects.CoinFlipStats;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class CoinFlip extends RPFeature implements CommandExecutor {
    private static CoinFlip instance;
    public static CoinFlip getCoinFlip() {
        if(instance == null) instance = new CoinFlip();
        return instance;
    }
    public YamlConfiguration config;

    private boolean isLegacy = false;
    private UInventory gui, options, challenge;
    private int countdownStart;
    private ItemStack countdown;
    private double tax;
    private long minWager;
    private String wagerName, yourSelection, opponentSelection;
    private List<String> addedlore, wagerLore, wagerAvailable, wagerUnavailable, winnerLore, rollingLore;
    private List<CoinFlipMatch> available;

    private LinkedHashMap<Integer, CoinFlipOption> optionz;
    private HashMap<String, Integer> challengeSlots;
    private HashMap<OfflinePlayer, BigDecimal> picking;
    private HashMap<CoinFlipMatch, List<Integer>> tasks;
    private HashMap<Player, CoinFlipMatch> goingToChallenge, active;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewCoinFlips(player);
        } else {
            final String a = args[0];
            if(a.equals("cancel")) {
                tryCancelling(player);
            } else if(a.equals("stats")) {
                viewStats(player);
            } else {
                final long m = a.endsWith("k") ? 1000 : a.endsWith("m") ? 1000000 : a.endsWith("b") ? 1000000000 : 1;
                final BigDecimal w = BigDecimal.valueOf(getRemainingDouble(a)*m);
                if(w.doubleValue() <= 0) {
                    sendStringListMessage(player, config.getStringList("messages.must enter valid amount"), null);
                } else {
                    tryCreating(player, w);
                }
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "coinflip.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "coinflip.yml"));

        isLegacy = version.contains("1.8") || version.contains("1.9") || version.contains("1.10") || version.contains("1.11");

        minWager = config.getLong("min wager");
        tax = config.getDouble("wager.tax");
        wagerAvailable = colorizeListString(config.getStringList("wager.status.can afford"));
        wagerUnavailable = colorizeListString(config.getStringList("wager.status.cannot afford"));
        wagerName = ChatColor.translateAlternateColorCodes('&', config.getString("wager.name"));
        wagerLore = colorizeListString(config.getStringList("wager.lore"));

        yourSelection = ChatColor.translateAlternateColorCodes('&', config.getString("challenge.your selection"));
        opponentSelection = ChatColor.translateAlternateColorCodes('&', config.getString("challenge.opponent selection"));
        winnerLore = colorizeListString(config.getStringList("challenge.winner.lore"));
        countdown = d(config, "challenge.countdown");
        rollingLore = colorizeListString(config.getStringList("challenge.rolling.lore"));

        gui = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        options = new UInventory(null, config.getInt("gui.options.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.options.title")));
        challenge = new UInventory(null, config.getInt("challenge.size"), ChatColor.translateAlternateColorCodes('&', config.getString("challenge.title")));
        countdownStart = config.getInt("gui.options.countdown");
        addedlore = colorizeListString(config.getStringList("gui.options.added lore"));
        optionz = new LinkedHashMap<>();

        final Inventory oi = options.getInventory();
        for(String s : config.getConfigurationSection("gui.options").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("countdown") && !s.equals("added lore")) {
                final String p = "gui.options." + s + ".";
                final int slot = config.getInt(p + "slot");
                final ItemStack dis = d(config, "gui.options." + s);
                itemMeta = dis.getItemMeta();
                itemMeta.setLore(addedlore);
                dis.setItemMeta(itemMeta);
                final CoinFlipOption o = new CoinFlipOption(s, slot, ChatColor.translateAlternateColorCodes('&', config.getString(p + "chosen")), dis, d(config, p + "selection"), ChatColor.translateAlternateColorCodes('&', config.getString(p + "selection.color")));
                optionz.put(slot, o);
                oi.setItem(slot, dis);
            }
        }

        challengeSlots = new HashMap<>();
        challengeSlots.put("creator", config.getInt("challenge.creator.slot"));
        challengeSlots.put("creatorSelection", config.getInt("challenge.creator selection.slot"));
        challengeSlots.put("winner", config.getInt("challenge.winner.slot"));
        challengeSlots.put("challengerSelection", config.getInt("challenge.challenger selection.slot"));
        challengeSlots.put("challenger", config.getInt("challenge.challenger.slot"));

        picking = new HashMap<>();
        tasks = new HashMap<>();
        goingToChallenge = new HashMap<>();
        active = new HashMap<>();
        available = new ArrayList<>();

        final YamlConfiguration a = otherdata;
        final ConfigurationSection c = a.getConfigurationSection("coinflips");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                final CoinFlipMatch m = new CoinFlipMatch(a.getLong("coinflips." + s + ".created"), Bukkit.getOfflinePlayer(UUID.fromString(s)), CoinFlipOption.paths.get(a.getString("coinflips." + s + ".option")), getBigDecimal(a.getString("coinflips." + s + ".wager")));
                available.add(m);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded Coin Flip &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        tax = 0;
        countdownStart = 0;
        config = null;
        gui = null;
        options = null;
        addedlore = null;
        optionz = null;
        wagerAvailable = null;
        wagerUnavailable = null;
        wagerName = null;
        wagerLore = null;
        winnerLore = null;
        rollingLore = null;
        yourSelection = null;
        opponentSelection = null;
        for(OfflinePlayer p : picking.keySet()) {
            if(p.isOnline()) {
                p.getPlayer().closeInventory();
            }
        }
        for(CoinFlipMatch m : tasks.keySet()) {
            for(int i : tasks.get(m)) {
                scheduler.cancelTask(i);
            }
        }
        final YamlConfiguration a = otherdata;
        a.set("coinflips", null);
        for(CoinFlipMatch m : available) {
            final String u = m.creator().getUniqueId().toString();
            a.set("coinflips." + u + ".created", m.created());
            a.set("coinflips." + u + ".wager", m.wager());
            a.set("coinflips." + u + ".option", m.option().path);
            m.delete();
        }
        saveOtherData();
        for(Player p : active.keySet()) p.closeInventory();
        challengeSlots = null;
        tasks = null;
        picking = null;
        goingToChallenge = null;
        active = null;
        available = null;
        CoinFlipOption.paths = null;
        CoinFlipMatch.matches = null;
    }

    public void viewCoinFlips(Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.view", true)) {
            player.closeInventory();
            final int size = ((available.size()+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            final double bal = eco.getBalance(player);
            for(CoinFlipMatch m : available) {
                item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                final SkullMeta s = (SkullMeta) item.getItemMeta();
                final OfflinePlayer c = m.creator();
                if(isLegacy) s.setOwner(c.getName());
                else s.setOwningPlayer(c);
                s.setDisplayName(wagerName.replace("{PLAYER}", c.getName()));
                final BigDecimal wager = m.wager();
                final double wd = wager.doubleValue();
                final String w = formatBigDecimal(wager), tax = formatBigDecimal(BigDecimal.valueOf(wd*this.tax)), ch = m.option().chosen;
                lore.clear();
                for(String l : wagerLore) {
                    if(l.equals("{STATUS}")) {
                        lore.addAll(bal >= wd ? wagerAvailable : wagerUnavailable);
                    } else {
                        lore.add(l.replace("{WAGER}", w).replace("{TAX}", tax).replace("{CHOSEN}", ch));
                    }
                }
                s.setLore(lore); lore.clear();
                item.setItemMeta(s);
                top.setItem(top.firstEmpty(), item);
            }
            player.updateInventory();
        }
    }
    public void viewStats(Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.stats", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final CoinFlipStats s = pdata.getCoinFlipStats();
            replacements.put("{WINS}", formatBigDecimal(s.wins));
            replacements.put("{LOSSES}", formatBigDecimal(s.losses));
            replacements.put("{WON$}", formatBigDecimal(s.wonCash));
            replacements.put("{LOST$}", formatBigDecimal(s.lostCash));
            replacements.put("{TAXES}", formatBigDecimal(s.taxesPaid));
            sendStringListMessage(player, config.getStringList("messages.stats"), replacements);
        }
    }
    public void tryCreating(Player player, BigDecimal w) {
        if(hasPermission(player, "RandomPackage.coinflip.create", true)) {
            final CoinFlipMatch m = CoinFlipMatch.valueOf(player);
            if(m != null) {
                sendStringListMessage(player, config.getStringList("messages.already in a match"), null);
            } else {
                final double b = eco.getBalance(player), wager = w.doubleValue();
                final HashMap<String, String> replacements = new HashMap<>();
                if(b < wager) {
                    replacements.put("{BAL}", formatDouble(b));
                    sendStringListMessage(player, config.getStringList("messages.cannot afford"), replacements);
                } else if(wager < minWager) {
                    replacements.put("{MIN}", formatLong(minWager));
                    sendStringListMessage(player, config.getStringList("messages.wager needs to be more"), replacements);
                } else {
                    final String ww = formatBigDecimal(w);
                    player.closeInventory();
                    final int size = options.getSize();
                    player.openInventory(Bukkit.createInventory(player, size, options.getTitle()));
                    final Inventory top = player.getOpenInventory().getTopInventory();
                    top.setContents(options.getInventory().getContents());
                    for(int i = 0; i < size; i++) {
                        item = top.getItem(i);
                        if(item != null) {
                            itemMeta = item.getItemMeta(); lore.clear();
                            final List<String> l = itemMeta.getLore();
                            if(l != null) {
                                for(String s : l) {
                                    lore.add(s.replace("{WAGER}", ww));
                                }
                            }
                            itemMeta.setLore(lore); lore.clear();
                            item.setItemMeta(itemMeta);
                        }
                    }
                    player.updateInventory();
                    picking.put(player, w);
                }
            }
        }
    }
    public void tryCancelling(Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.cancel", true)) {
            final CoinFlipMatch m = CoinFlipMatch.valueOf(player);
            if(m == null) {
                sendStringListMessage(player, config.getStringList("messages.cancel dont have one"), null);
            } else {
                final BigDecimal a = m.wager();
                eco.depositPlayer(player, a.doubleValue());
                delete(m);
                sendStringListMessage(player, config.getStringList("messages.cancelled"), null);
            }
        }
    }
    public void tryChallenging(Player player, CoinFlipMatch match) {
        if(hasPermission(player, "RandomPackage.coinflip.challenge", true)) {
            player.closeInventory();
            final CoinFlipMatch f = CoinFlipMatch.valueOf(player);
            if(f != null) {
                sendStringListMessage(player, config.getStringList("messages.already in a match"), null);
            } else if(match != null) {
                if(match.isActive) {
                    player.closeInventory();
                    sendStringListMessage(player, config.getStringList("messages.no longer available"), null);
                    viewCoinFlips(player);
                } else {
                    final String w = formatBigDecimal(match.wager());
                    final int s = options.getSize();
                    player.openInventory(Bukkit.createInventory(player, s, options.getTitle()));
                    final Inventory top = player.getOpenInventory().getTopInventory();
                    top.setContents(options.getInventory().getContents());
                    top.setItem(match.option().slot, new ItemStack(Material.AIR));
                    for(int i = 0; i < s; i++) {
                        item = top.getItem(i);
                        if(item != null) {
                            itemMeta = item.getItemMeta(); lore.clear();
                            if(itemMeta.hasLore()) {
                                for(String l : itemMeta.getLore()) {
                                    lore.add(l.replace("{WAGER}", w));
                                }
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                        }
                    }
                    player.updateInventory();
                    goingToChallenge.put(player, match);
                }
            }
        }
    }
    private ItemStack getSelection(OfflinePlayer player, OfflinePlayer target, CoinFlipMatch m) {
        final boolean isCreator = target.equals(m.creator());
        final CoinFlipOption o = isCreator ? m.option() : m.challengerOption;
        item = o.selection(); itemMeta = item.getItemMeta();
        itemMeta.setDisplayName((player.equals(target) ? yourSelection : opponentSelection).replace("{COLOR}", o.selectionColor).replace("{PLAYER}", target.getName()));
        item.setItemMeta(itemMeta);
        return item;
    }
    private void start(CoinFlipMatch m) {
        available.remove(m);
        m.isActive = true;
        tasks.put(m, new ArrayList<>());

        final CoinFlipOption l = m.option(), r = m.challengerOption;
        final OfflinePlayer a = m.creator(), b = m.challenger;
        final String w = formatBigDecimal(m.wager()), an = a.getName(), bn = b.getName(), c = l.selectionColor, cc = r.selectionColor, cd = Integer.toString(countdownStart), T = challenge.getTitle().replace("{WAGER}", w);
        final int size = challenge.getSize();
        final Inventory inv = Bukkit.createInventory(null, size, T);
        final ItemStack creator = UMaterial.PLAYER_HEAD_ITEM.getItemStack(), challenger = creator.clone();
        final SkullMeta y = (SkullMeta) creator.getItemMeta(), z = (SkullMeta) challenger.getItemMeta();
        if(isLegacy) y.setOwner(a.getName());
        else y.setOwningPlayer(a);
        y.setDisplayName(c+an);
        z.setDisplayName(cc+bn);
        if(isLegacy) z.setOwner(b.getName());
        else z.setOwningPlayer(b);
        creator.setItemMeta(y);
        challenger.setItemMeta(z);

        final int Y = challengeSlots.get("creatorSelection"), Z = challengeSlots.get("challengerSelection");

        inv.setItem(challengeSlots.get("creator"), creator);
        inv.setItem(Y, getSelection(a, a, m));
        inv.setItem(Z, getSelection(a, b, m));
        inv.setItem(challengeSlots.get("challenger"), challenger);

        item = countdown.clone(); itemMeta = item.getItemMeta(); lore.clear();
        item.setAmount(countdownStart);
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TIME}", cd));
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{TIME}", cd));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        inv.setItem(challengeSlots.get("winner"), item);

        final ItemStack[] contents = inv.getContents();

        if(a.isOnline()) {
            final Player p = a.getPlayer();
            p.closeInventory();
            p.openInventory(Bukkit.createInventory(p, size, T));
            p.getOpenInventory().getTopInventory().setContents(contents);
            p.updateInventory();
            active.put(p, m);
        }
        if(b.isOnline()) {
            final Player p = b.getPlayer();
            p.closeInventory();
            p.openInventory(Bukkit.createInventory(p, size, T));
            final Inventory TOP = p.getOpenInventory().getTopInventory();
            TOP.setContents(contents);
            TOP.setItem(Y, getSelection(b, a, m));
            TOP.setItem(Z, getSelection(b, b, m));
            p.updateInventory();
            active.put(p, m);
        }

        final List<Integer> t = tasks.get(m);
        for(int i = 1; i <= countdownStart; i++) {
            final int I = i;
            t.add(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                final String CD = Integer.toString(countdownStart-I);
                item = countdown.clone(); itemMeta = item.getItemMeta(); lore.clear();
                item.setAmount(countdownStart-I);
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TIME}", CD));
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{TIME}", CD));
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                final Player A = a.getPlayer(), B = b.getPlayer();
                final int q = challengeSlots.get("winner");
                if(a.isOnline() && active.containsKey(A)) {
                    A.getOpenInventory().getTopInventory().setItem(q, item);
                    A.updateInventory();
                }
                if(b.isOnline() && active.containsKey(B)) {
                    B.getOpenInventory().getTopInventory().setItem(q, item);
                    B.updateInventory();
                }
                if(I == countdownStart) {
                    final ItemStack F = m.option().selection(), G = m.challengerOption.selection();
                    ItemStack option = F;
                    for(int o = 0; o <= 60; o++) {
                        final int d = o*2;
                        if(o == 60) {
                            t.add(scheduler.scheduleSyncDelayedTask(randompackage, () -> chooseWinner(m), d));
                        } else {
                            item = option.clone(); itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("challenge.rolling.name")));
                            itemMeta.setLore(rollingLore);
                            item.setItemMeta(itemMeta);
                            t.add(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                                if(a.isOnline() && active.containsKey(A)) {
                                    A.getOpenInventory().getTopInventory().setItem(q, item);
                                }
                                if(b.isOnline() && active.containsKey(B)) {
                                    B.getOpenInventory().getTopInventory().setItem(q, item);
                                }
                            }, d));
                            option = option == F ? G : F;
                        }
                    }

                }
            }, 20*i));
        }
    }
    private void chooseWinner(CoinFlipMatch m) {
        available.remove(m);
        m.isActive = false;
        final OfflinePlayer a = m.creator(), b = m.challenger;
        final CoinFlipOption l = m.option(), r = m.challengerOption;
        final BigDecimal wager = m.wager(), t = BigDecimal.valueOf(wager.doubleValue()*tax), total = BigDecimal.valueOf(wager.doubleValue()*2), taxed = BigDecimal.valueOf(total.doubleValue()*(tax*2));
        final boolean zero = random.nextInt(2) == 0;
        final CoinFlipOption winningOption = zero ? l : r, losingOption = zero ? r : l;
        final OfflinePlayer winner = zero ? a : b, loser = winner == a ? b : a;
        final RPPlayer W = RPPlayer.get(winner.getUniqueId()), ll = RPPlayer.get(loser.getUniqueId());
        final boolean wL = W.isLoaded, lL = ll.isLoaded;
        if(!wL) W.load();
        if(!lL) ll.load();

        final CoinFlipStats s1 = W.getCoinFlipStats(), s2 = ll.getCoinFlipStats();
        s1.wonCash = s1.wonCash.add(t);
        s1.wins = s1.wins.add(BigDecimal.ONE);
        s1.taxesPaid = s1.taxesPaid.add(taxed);
        s2.lostCash = s2.lostCash.add(t);
        s2.losses = s2.losses.add(BigDecimal.ONE);
        s2.taxesPaid = s2.taxesPaid.add(taxed);

        if(!wL) W.unload();
        if(!lL) ll.unload();
        final Player ap = a.getPlayer(), bp = b.getPlayer();
        if(active.containsKey(ap)) {
            active.put(ap, null);
        }
        if(active.containsKey(bp)) {
            active.put(bp, null);
        }
        final String winnerName = winner.getName(), color = winningOption.selectionColor, Lcolor = losingOption.selectionColor;
        eco.depositPlayer(winner, total.doubleValue()-taxed.doubleValue());
        item = winningOption.appear(); itemMeta = item.getItemMeta(); lore.clear();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("challenge.winner.name")).replace("{COLOR}", color).replace("{PLAYER}", winnerName));
        for(String s : winnerLore) {
            lore.add(s.replace("{PLAYER}", winnerName).replace("{COLOR}", color));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        final int slot = challengeSlots.get("winner");
        if(a.isOnline() && active.containsKey(ap)) {
            final Player p = a.getPlayer();
            p.getOpenInventory().getTopInventory().setItem(slot, item);
            p.updateInventory();
        }
        if(b.isOnline() && active.containsKey(bp)) {
            final Player p = b.getPlayer();
            p.getOpenInventory().getTopInventory().setItem(slot, item);
            p.updateInventory();
        }
        stopTasks(m);
        m.delete();

        final List<String> w = colorizeListString(config.getStringList("messages.winner"));
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{WINNING_COLOR}", color);
        replacements.put("{LOSING_COLOR}", Lcolor);
        replacements.put("{WINNER}", winnerName);
        replacements.put("{LOSER}", loser.getName());
        replacements.put("{WAGER}", formatBigDecimal(total));
        for(String s : w) {
            for(String re : replacements.keySet()) s = s.replace(re, replacements.get(re));
            sendConsoleMessage(s);
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(RPPlayer.get(p.getUniqueId()).coinflipNotifications) {
                sendStringListMessage(p, w, replacements);
            }
        }
        final CoinFlipEndEvent e = new CoinFlipEndEvent(winner.getUniqueId(), loser.getUniqueId(), wager, BigDecimal.valueOf(tax));
        pluginmanager.callEvent(e);
    }
    private void stopTasks(CoinFlipMatch m) {
        if(tasks.containsKey(m)) {
            for(int i : tasks.get(m)) scheduler.cancelTask(i);
            tasks.remove(m);
        }
    }
    private void delete(CoinFlipMatch m) {
        stopTasks(m);
        available.remove(m);
        m.delete();
    }
    public void create(OfflinePlayer player, CoinFlipOption picked, boolean withdraw, boolean sendMsg) {
        final BigDecimal wager = picking.get(player);
        picking.remove(player);
        if(withdraw) eco.withdrawPlayer(player, wager.doubleValue());
        if(sendMsg && player.isOnline()) sendStringListMessage(player.getPlayer(), config.getStringList("messages.created"), null);
        final CoinFlipMatch m = new CoinFlipMatch(System.currentTimeMillis(), player, picked, wager);
        available.add(m);
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        picking.remove(player);
        goingToChallenge.remove(player);
        if(active.containsKey(player)) {
            final CoinFlipMatch m = active.get(player);
            active.remove(player);
            if(m != null && m.isActive && !active.containsKey(m.challenger.getPlayer())) {
                chooseWinner(m);
                m.delete();
            }
        }
    }
    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final boolean p = picking.containsKey(player), ch = goingToChallenge.containsKey(player), g = event.getView().getTitle().equals(gui.getTitle()), a = active.containsKey(player);
        if(p || ch || g || a) {
            event.setCancelled(true);
            player.updateInventory();
            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(r < 0 || r > top.getSize() || c == null || c.getType().equals(Material.AIR) || a) return;

            if(g) {
                final SkullMeta m = (SkullMeta) c.getItemMeta();
                final OfflinePlayer n = isLegacy ? Bukkit.getOfflinePlayer(m.getOwner()) : m.getOwningPlayer();
                final CoinFlipMatch f = CoinFlipMatch.valueOf(n);
                tryChallenging(player, f);
            } else if(ch) {
                final CoinFlipMatch f = goingToChallenge.get(player);
                final double b = eco.getBalance(player);
                final BigDecimal w = f.wager();
                if(b < w.doubleValue()) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{BAL}", formatDouble(b));
                    sendStringListMessage(player, config.getStringList("messages.cannot afford"), replacements);
                } else {
                    player.closeInventory();
                    if(!available.contains(f) || f.isActive) {
                        sendStringListMessage(player, config.getStringList("messages.no longer available"), null);
                        viewCoinFlips(player);
                    } else if(optionz.containsKey(r)) {
                        eco.withdrawPlayer(player, w.doubleValue());
                        f.challenger = player;
                        f.challengerOption = optionz.get(r);
                        start(f);
                    }
                }
            } else {
                final CoinFlipOption o = optionz.getOrDefault(r, null);
                if(o != null) {
                    create(player, o, true, true);
                    player.closeInventory();
                }
            }
        }
    }
}
