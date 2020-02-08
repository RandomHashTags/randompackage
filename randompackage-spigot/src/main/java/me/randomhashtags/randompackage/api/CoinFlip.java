package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.obj.CoinFlipMatch;
import me.randomhashtags.randompackage.addon.obj.CoinFlipOption;
import me.randomhashtags.randompackage.addon.stats.CoinFlipStats;
import me.randomhashtags.randompackage.event.CoinFlipEndEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
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
    private boolean isLegacy;
    private UInventory gui, options, challenge;
    private int countdownStart;
    private ItemStack countdown;
    private double tax;
    private long minWager;
    private String wagerName, yourSelection, opponentSelection;
    private List<CoinFlipMatch> available;

    private LinkedHashMap<Integer, CoinFlipOption> optionz;
    private HashMap<String, Integer> challengeSlots;
    private HashMap<OfflinePlayer, BigDecimal> picking;
    private HashMap<CoinFlipMatch, List<Integer>> tasks;
    private HashMap<Player, CoinFlipMatch> pickingChallengeOption, active;

    public String getIdentifier() { return "COIN_FLIP"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewCoinFlips(player);
        } else {
            final String a = args[0];
            switch (a) {
                case "cancel":
                    tryCancelling(player);
                    break;
                case "stats":
                    viewStats(player);
                    break;
                case "toggle":
                case "stfu":
                    tryToggleNotifications(player);
                    break;
                case "help":
                    viewHelp(player);
                    break;
                default:
                    final BigDecimal amount = valueOfBigDecimal(a);
                    if(amount.doubleValue() <= 0) {
                        sendStringListMessage(player, getStringList(config, "messages.must enter valid amount"), null);
                    } else {
                        tryCreating(player, amount);
                    }
                    break;
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "coinflip.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "coinflip.yml"));

        isLegacy = EIGHT || NINE || TEN || ELEVEN;

        minWager = config.getLong("min wager");
        tax = config.getDouble("wager.tax");
        wagerName = colorize(config.getString("wager.name"));

        yourSelection = colorize(config.getString("challenge.your selection"));
        opponentSelection = colorize(config.getString("challenge.opponent selection"));
        countdown = createItemStack(config, "challenge.countdown");

        gui = new UInventory(null, 54, colorize(config.getString("gui.title")));
        options = new UInventory(null, config.getInt("gui.options.size"), colorize(config.getString("gui.options.title")));
        challenge = new UInventory(null, config.getInt("challenge.size"), colorize(config.getString("challenge.title")));
        countdownStart = config.getInt("gui.options.countdown");
        optionz = new LinkedHashMap<>();

        final Inventory oi = options.getInventory();
        final List<String> addedLore = getStringList(config, "gui.options.added lore");
        for(String s : config.getConfigurationSection("gui.options").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("countdown") && !s.equals("added lore")) {
                final String p = "gui.options." + s + ".";
                final int slot = config.getInt(p + "slot");
                final ItemStack dis = createItemStack(config, "gui.options." + s);
                itemMeta = dis.getItemMeta();
                itemMeta.setLore(addedLore);
                dis.setItemMeta(itemMeta);
                final CoinFlipOption o = new CoinFlipOption(s, slot, colorize(config.getString(p + "chosen")), dis, createItemStack(config, p + "selection"), colorize(config.getString(p + "selection.color")));
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
        pickingChallengeOption = new HashMap<>();
        active = new HashMap<>();
        available = new ArrayList<>();

        for(String key : getConfigurationSectionKeys(otherdata, "coinflips", false)) {
            final CoinFlipMatch m = new CoinFlipMatch(otherdata.getLong("coinflips." + key + ".created"), Bukkit.getOfflinePlayer(UUID.fromString(key)), CoinFlipOption.paths.get(otherdata.getString("coinflips." + key + ".option")), getBigDecimal(otherdata.getString("coinflips." + key + ".wager")));
            available.add(m);
        }
        sendConsoleDidLoadFeature("Coin Flip", started);
    }
    public void unload() {
        for(OfflinePlayer p : new ArrayList<>(picking.keySet())) {
            if(p.isOnline()) {
                p.getPlayer().closeInventory();
            }
        }
        for(CoinFlipMatch m : tasks.keySet()) {
            for(int i : tasks.get(m)) {
                SCHEDULER.cancelTask(i);
            }
        }
        otherdata.set("coinflips", null);
        for(CoinFlipMatch m : available) {
            final String u = m.getCreator().getUniqueId().toString(), path = "coinflips." + u + ".";
            otherdata.set(path + "created", m.getCreationTime());
            otherdata.set(path + "wager", m.getWager());
            otherdata.set(path + "option", m.getCreatorOption().path);
            m.delete();
        }
        saveOtherData();
        for(Player player : new ArrayList<>(active.keySet())) {
            player.closeInventory();
        }
        CoinFlipOption.paths = null;
        CoinFlipMatch.matches = null;
    }

    public void viewCoinFlips(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.view", true)) {
            player.closeInventory();
            final int size = ((available.size()+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            final double bal = eco.getBalance(player);
            final List<String> canAfford = getStringList(config, "wager.status.can afford"), cannotAfford = getStringList(config, "wager.status.cannot afford");
            for(CoinFlipMatch match : available) {
                item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                final OfflinePlayer c = match.getCreator();
                if(isLegacy) {
                    skullMeta.setOwner(c.getName());
                } else {
                    skullMeta.setOwningPlayer(c);
                }
                skullMeta.setDisplayName(wagerName.replace("{PLAYER}", c.getName()));
                final BigDecimal wager = match.getWager();
                final double wd = wager.doubleValue();
                final String w = formatBigDecimal(wager), tax = formatBigDecimal(BigDecimal.valueOf(wd*this.tax)), ch = match.getCreatorOption().chosen;
                lore.clear();
                for(String l : getStringList(config, "wager.lore")) {
                    if(l.equals("{STATUS}")) {
                        lore.addAll(bal >= wd ? canAfford : cannotAfford);
                    } else {
                        lore.add(l.replace("{WAGER}", w).replace("{TAX}", tax).replace("{CHOSEN}", ch));
                    }
                }
                skullMeta.setLore(lore); lore.clear();
                item.setItemMeta(skullMeta);
                top.setItem(top.firstEmpty(), item);
            }
            player.updateInventory();
        }
    }
    public void viewStats(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.stats", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final CoinFlipStats s = pdata.getCoinFlipStats();
            replacements.put("{WINS}", formatBigDecimal(s.wins));
            replacements.put("{LOSSES}", formatBigDecimal(s.losses));
            replacements.put("{WON$}", formatBigDecimal(s.wonCash));
            replacements.put("{LOST$}", formatBigDecimal(s.lostCash));
            replacements.put("{TAXES}", formatBigDecimal(s.taxesPaid));
            sendStringListMessage(player, getStringList(config, "messages.stats"), replacements);
        }
    }
    public void tryToggleNotifications(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.toggle", true)) {
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final boolean status = !pdata.doesReceiveCoinFlipNotifications();
            pdata.setReceivesCoinFlipNotifications(status);
            sendStringListMessage(player, getStringList(config, "messages.toggle notifications." + (status ? "on" : "off")), null);
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.coinflip.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void tryCreating(@NotNull Player player, @NotNull  BigDecimal w) {
        if(hasPermission(player, "RandomPackage.coinflip.create", true)) {
            final CoinFlipMatch m = CoinFlipMatch.valueOfCreator(player);
            if(m != null) {
                sendStringListMessage(player, getStringList(config, "messages.already in a match"), null);
            } else {
                final double b = eco.getBalance(player), wager = w.doubleValue();
                final HashMap<String, String> replacements = new HashMap<>();
                if(b < wager) {
                    replacements.put("{BAL}", formatDouble(b));
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                } else if(wager < minWager) {
                    replacements.put("{MIN}", formatLong(minWager));
                    sendStringListMessage(player, getStringList(config, "messages.wager needs to be more"), replacements);
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
    public void tryCancelling(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.coinflip.cancel", true)) {
            final CoinFlipMatch m = CoinFlipMatch.valueOfCreator(player);
            if(m == null) {
                sendStringListMessage(player, getStringList(config, "messages.cancel dont have one"), null);
            } else {
                final BigDecimal a = m.getWager();
                eco.depositPlayer(player, a.doubleValue());
                delete(m);
                sendStringListMessage(player, getStringList(config, "messages.cancelled"), null);
            }
        }
    }
    public void tryChallenging(@NotNull Player player, @NotNull CoinFlipMatch match) {
        if(hasPermission(player, "RandomPackage.coinflip.challenge", true)) {
            player.closeInventory();
            final CoinFlipMatch f = CoinFlipMatch.valueOfCreator(player);
            if(f != null) {
                sendStringListMessage(player, getStringList(config, "messages.already in a match"), null);
            } else if(match != null) {
                if(match.isActive()) {
                    sendStringListMessage(player, getStringList(config, "messages.no longer available"), null);
                    viewCoinFlips(player);
                } else {
                    final String w = formatBigDecimal(match.getWager());
                    final int s = options.getSize();
                    player.openInventory(Bukkit.createInventory(player, s, options.getTitle()));
                    final Inventory top = player.getOpenInventory().getTopInventory();
                    top.setContents(options.getInventory().getContents());
                    top.setItem(match.getCreatorOption().slot, new ItemStack(Material.AIR));
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
                    pickingChallengeOption.put(player, match);
                }
            }
        }
    }
    private ItemStack getSelection(OfflinePlayer player, OfflinePlayer target, CoinFlipMatch m) {
        final boolean isCreator = target.equals(m.getCreator());
        final CoinFlipOption o = isCreator ? m.getCreatorOption() : m.getChallengerOption();
        item = o.selection();
        itemMeta = item.getItemMeta();
        itemMeta.setDisplayName((player.equals(target) ? yourSelection : opponentSelection).replace("{COLOR}", o.selectionColor).replace("{PLAYER}", target.getName()));
        item.setItemMeta(itemMeta);
        return item;
    }
    private void start(CoinFlipMatch match) {
        available.remove(match);
        match.setActive(true);
        tasks.put(match, new ArrayList<>());

        final CoinFlipOption creatorOption = match.getCreatorOption(), challengerOption = match.getChallengerOption();
        final OfflinePlayer creator = match.getCreator(), challenger = match.getChallenger();
        final String w = formatBigDecimal(match.getWager()), creatorName = creator.getName(), challengerName = challenger.getName();
        final String c = creatorOption.selectionColor, cc = challengerOption.selectionColor, cd = Integer.toString(countdownStart), T = challenge.getTitle().replace("{WAGER}", w);
        final int size = challenge.getSize();
        final Inventory inv = Bukkit.createInventory(null, size, T);
        final ItemStack creatorItem = UMaterial.PLAYER_HEAD_ITEM.getItemStack(), challengerItem = creatorItem.clone();
        final SkullMeta creatorMeta = (SkullMeta) creatorItem.getItemMeta(), challengerMeta = (SkullMeta) challengerItem.getItemMeta();
        if(isLegacy) {
            creatorMeta.setOwner(creatorName);
            challengerMeta.setOwner(challengerName);
        } else {
            creatorMeta.setOwningPlayer(creator);
            challengerMeta.setOwningPlayer(challenger);
        }
        creatorMeta.setDisplayName(c+creatorName);
        creatorItem.setItemMeta(creatorMeta);
        challengerMeta.setDisplayName(cc+challengerName);
        challengerItem.setItemMeta(challengerMeta);

        final int Y = challengeSlots.get("creatorSelection"), Z = challengeSlots.get("challengerSelection");

        inv.setItem(challengeSlots.get("creator"), creatorItem);
        inv.setItem(Y, getSelection(creator, creator, match));
        inv.setItem(Z, getSelection(creator, challenger, match));
        inv.setItem(challengeSlots.get("challenger"), challengerItem);

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

        if(creator.isOnline()) {
            final Player p = creator.getPlayer();
            p.closeInventory();
            p.openInventory(Bukkit.createInventory(p, size, T));
            p.getOpenInventory().getTopInventory().setContents(contents);
            p.updateInventory();
            active.put(p, match);
        }
        if(challenger.isOnline()) {
            final Player p = challenger.getPlayer();
            p.closeInventory();
            p.openInventory(Bukkit.createInventory(p, size, T));
            final Inventory TOP = p.getOpenInventory().getTopInventory();
            TOP.setContents(contents);
            TOP.setItem(Y, getSelection(challenger, creator, match));
            TOP.setItem(Z, getSelection(challenger, challenger, match));
            p.updateInventory();
            active.put(p, match);
        }

        final String rollingName = colorize(config.getString("challenge.rolling.name"));
        final List<Integer> t = tasks.get(match);
        for(int i = 1; i <= countdownStart; i++) {
            final int I = i;
            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                final String CD = Integer.toString(countdownStart-I);
                item = countdown.clone(); itemMeta = item.getItemMeta(); lore.clear();
                item.setAmount(countdownStart-I);
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TIME}", CD));
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{TIME}", CD));
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                final Player A = creator.getPlayer(), B = challenger.getPlayer();
                final int q = challengeSlots.get("winner");
                if(creator.isOnline() && active.containsKey(A)) {
                    A.getOpenInventory().getTopInventory().setItem(q, item);
                    A.updateInventory();
                }
                if(challenger.isOnline() && active.containsKey(B)) {
                    B.getOpenInventory().getTopInventory().setItem(q, item);
                    B.updateInventory();
                }
                if(I == countdownStart) {
                    final CoinFlipOption op1 = match.getCreatorOption(), op2 = match.getChallengerOption();
                    final String color1 = op1.selectionColor, color2 = op2.selectionColor;
                    final ItemStack F = op1.selection(), G = op2.selection();
                    ItemStack option = F;
                    String selectionColor = color1;
                    for(int o = 0; o <= 60; o++) {
                        final int d = o*2;
                        if(o == 60) {
                            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> chooseWinner(match), d));
                        } else {
                            item = option.clone(); itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(rollingName.replace("{SELECTION_COLOR}", selectionColor));
                            itemMeta.setLore(getStringList(config, "challenge.rolling.lore"));
                            item.setItemMeta(itemMeta);
                            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                                if(creator.isOnline() && active.containsKey(A)) {
                                    A.getOpenInventory().getTopInventory().setItem(q, item);
                                }
                                if(challenger.isOnline() && active.containsKey(B)) {
                                    B.getOpenInventory().getTopInventory().setItem(q, item);
                                }
                            }, d));
                            final boolean isF = option == F;
                            option = isF ? G : F;
                            selectionColor = isF ? color2 : color1;
                        }
                    }
                }
            }, 20*i));
        }
    }
    private void chooseWinner(CoinFlipMatch m) {
        available.remove(m);
        m.setActive(false);
        final OfflinePlayer a = m.getCreator(), b = m.getChallenger();
        final CoinFlipOption l = m.getCreatorOption(), r = m.getChallengerOption();
        final BigDecimal wager = m.getWager(), t = BigDecimal.valueOf(wager.doubleValue()*tax), total = BigDecimal.valueOf(wager.doubleValue()*2), taxed = BigDecimal.valueOf(total.doubleValue()*(tax*2));
        final boolean zero = RANDOM.nextInt(2) == 0;
        final CoinFlipOption winningOption = zero ? l : r, losingOption = zero ? r : l;
        final OfflinePlayer winner = zero ? a : b, loser = winner == a ? b : a;
        final RPPlayer W = RPPlayer.get(winner.getUniqueId()), ll = RPPlayer.get(loser.getUniqueId());

        final CoinFlipStats s1 = W.getCoinFlipStats(), s2 = ll.getCoinFlipStats();
        s1.wonCash = s1.wonCash.add(t);
        s1.wins = s1.wins.add(BigDecimal.ONE);
        s1.taxesPaid = s1.taxesPaid.add(taxed);
        s2.lostCash = s2.lostCash.add(t);
        s2.losses = s2.losses.add(BigDecimal.ONE);
        s2.taxesPaid = s2.taxesPaid.add(taxed);

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
        itemMeta.setDisplayName(getString(config, "challenge.winner.name").replace("{COLOR}", color).replace("{PLAYER}", winnerName));
        for(String s : getStringList(config, "challenge.winner.lore")) {
            lore.add(s.replace("{PLAYER}", winnerName).replace("{COLOR}", color));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        final int slot = challengeSlots.get("winner");
        if(active.containsKey(ap)) {
            final Player p = a.getPlayer();
            p.getOpenInventory().getTopInventory().setItem(slot, item);
            p.updateInventory();
        }
        if(active.containsKey(bp)) {
            final Player p = b.getPlayer();
            p.getOpenInventory().getTopInventory().setItem(slot, item);
            p.updateInventory();
        }
        stopTasks(m);
        m.delete();

        final List<String> msg = getStringList(config, "messages.winner");
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{WINNING_COLOR}", color);
        replacements.put("{LOSING_COLOR}", Lcolor);
        replacements.put("{WINNER}", winnerName);
        replacements.put("{LOSER}", loser.getName());
        replacements.put("{WAGER}", formatBigDecimal(total));
        for(String s : msg) {
            for(String re : replacements.keySet()) {
                s = s.replace(re, replacements.get(re));
            }
            sendConsoleMessage(s);
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(RPPlayer.get(p.getUniqueId()).doesReceiveCoinFlipNotifications()) {
                sendStringListMessage(p, msg, replacements);
            }
        }
        final CoinFlipEndEvent e = new CoinFlipEndEvent(winner, loser, wager, BigDecimal.valueOf(tax));
        PLUGIN_MANAGER.callEvent(e);
    }
    private void stopTasks(CoinFlipMatch m) {
        if(tasks.containsKey(m)) {
            for(int i : tasks.get(m)) SCHEDULER.cancelTask(i);
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
        if(sendMsg && player.isOnline()) sendStringListMessage(player.getPlayer(), getStringList(config, "messages.created"), null);
        final CoinFlipMatch m = new CoinFlipMatch(System.currentTimeMillis(), player, picked, wager);
        available.add(m);
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        picking.remove(player);
        pickingChallengeOption.remove(player);
        if(active.containsKey(player)) {
            final CoinFlipMatch m = active.get(player);
            active.remove(player);
            if(m != null && m.isActive() && !active.containsKey(m.getChallenger().getPlayer())) {
                chooseWinner(m);
                m.delete();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final boolean isPicking = picking.containsKey(player), isChallenging = pickingChallengeOption.containsKey(player), isViewing = event.getView().getTitle().equals(gui.getTitle()), hasActive = active.containsKey(player);
        if(isPicking || isChallenging || isViewing || hasActive) {
            event.setCancelled(true);
            player.updateInventory();
            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(r < 0 || r > top.getSize() || c == null || c.getType().equals(Material.AIR) || hasActive) return;

            if(isViewing) {
                final SkullMeta m = (SkullMeta) c.getItemMeta();
                final OfflinePlayer n = isLegacy ? Bukkit.getOfflinePlayer(m.getOwner()) : m.getOwningPlayer();
                final CoinFlipMatch f = CoinFlipMatch.valueOfCreator(n);
                tryChallenging(player, f);
            } else if(isChallenging) {
                final CoinFlipMatch targetMatch = pickingChallengeOption.get(player);
                final double bal = eco.getBalance(player), wagerAmount = targetMatch.getWager().doubleValue();
                player.closeInventory();
                if(!available.contains(targetMatch) || targetMatch.isActive()) {
                    sendStringListMessage(player, getStringList(config, "messages.no longer available"), null);
                    viewCoinFlips(player);
                } else if(bal < wagerAmount) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{BAL}", formatDouble(bal));
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                } else if(optionz.containsKey(r)) {
                    eco.withdrawPlayer(player, wagerAmount);
                    targetMatch.setChallenger(player);
                    targetMatch.setChallengerOption(optionz.get(r));
                    start(targetMatch);
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
