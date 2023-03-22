package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.obj.CoinFlipMatch;
import me.randomhashtags.randompackage.addon.obj.CoinFlipOption;
import me.randomhashtags.randompackage.data.CoinFlipData;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.event.CoinFlipEndEvent;
import me.randomhashtags.randompackage.perms.CoinFlipPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public enum CoinFlip implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
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

    @Override
    public void load() {
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
                final ItemMeta itemMeta = dis.getItemMeta();
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

        for(String key : getConfigurationSectionKeys(OTHER_YML, "coinflips", false)) {
            final CoinFlipMatch m = new CoinFlipMatch(OTHER_YML.getLong("coinflips." + key + ".created"), Bukkit.getOfflinePlayer(UUID.fromString(key)), CoinFlipOption.PATHS.get(OTHER_YML.getString("coinflips." + key + ".option")), getBigDecimal(OTHER_YML.getString("coinflips." + key + ".wager")));
            available.add(m);
        }
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
        OTHER_YML.set("coinflips", null);
        for(CoinFlipMatch m : available) {
            final String u = m.getCreator().getUniqueId().toString(), path = "coinflips." + u + ".";
            OTHER_YML.set(path + "created", m.getCreationTime());
            OTHER_YML.set(path + "wager", m.getWager());
            OTHER_YML.set(path + "option", m.getCreatorOption().path);
            m.delete();
        }
        saveOtherData();
        for(Player player : new ArrayList<>(active.keySet())) {
            player.closeInventory();
        }
        CoinFlipOption.PATHS = null;
        CoinFlipMatch.MATCHES = null;
    }

    public void viewCoinFlips(@NotNull Player player) {
        if(hasPermission(player, CoinFlipPermission.VIEW_MATCHES, true)) {
            player.closeInventory();
            final int size = ((available.size()+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            final double bal = ECONOMY.getBalance(player);
            final List<String> canAfford = getStringList(config, "wager.status.can afford"), cannotAfford = getStringList(config, "wager.status.cannot afford");
            for(CoinFlipMatch match : available) {
                final ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
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
                final List<String> lore = new ArrayList<>();
                for(String l : getStringList(config, "wager.lore")) {
                    if(l.equals("{STATUS}")) {
                        lore.addAll(bal >= wd ? canAfford : cannotAfford);
                    } else {
                        lore.add(l.replace("{WAGER}", w).replace("{TAX}", tax).replace("{CHOSEN}", ch));
                    }
                }
                skullMeta.setLore(lore);
                item.setItemMeta(skullMeta);
                top.setItem(top.firstEmpty(), item);
            }
            player.updateInventory();
        }
    }
    public void viewStats(@NotNull Player player) {
        if(hasPermission(player, CoinFlipPermission.VIEW_STATS, true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final CoinFlipData data = FileRPPlayer.get(player.getUniqueId()).getCoinFlipData();
            replacements.put("{WINS}", formatBigDecimal(data.getWins()));
            replacements.put("{LOSSES}", formatBigDecimal(data.getLosses()));
            replacements.put("{WON$}", formatBigDecimal(data.getWonCash()));
            replacements.put("{LOST$}", formatBigDecimal(data.getLostCash()));
            replacements.put("{TAXES}", formatBigDecimal(data.getTaxesPaid()));
            sendStringListMessage(player, getStringList(config, "messages.stats"), replacements);
        }
    }
    public void tryToggleNotifications(@NotNull Player player) {
        if(hasPermission(player, CoinFlipPermission.TOGGLE_NOTIFICATIONS, true)) {
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final CoinFlipData data = pdata.getCoinFlipData();
            final boolean status = !data.receivesNotifications();
            data.setReceivesNotifications(status);
            sendStringListMessage(player, getStringList(config, "messages.toggle notifications." + (status ? "on" : "off")), null);
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, CoinFlipPermission.VIEW_HELP, true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void tryCreating(@NotNull Player player, @NotNull  BigDecimal wager) {
        if(hasPermission(player, CoinFlipPermission.CREATE_MATCH, true)) {
            final CoinFlipMatch match = CoinFlipMatch.valueOfCreator(player);
            if(match != null) {
                sendStringListMessage(player, getStringList(config, "messages.already in a match"), null);
            } else {
                final double balance = ECONOMY.getBalance(player), wagerAmount = wager.doubleValue();
                final HashMap<String, String> replacements = new HashMap<>();
                if(balance < wagerAmount) {
                    replacements.put("{BAL}", formatDouble(balance));
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                } else if(wagerAmount < minWager) {
                    replacements.put("{MIN}", formatLong(minWager));
                    sendStringListMessage(player, getStringList(config, "messages.wager needs to be more"), replacements);
                } else {
                    final String ww = formatBigDecimal(wager);
                    player.closeInventory();
                    final int size = options.getSize();
                    player.openInventory(Bukkit.createInventory(player, size, options.getTitle()));
                    final Inventory top = player.getOpenInventory().getTopInventory();
                    top.setContents(options.getInventory().getContents());
                    for(int i = 0; i < size; i++) {
                        final ItemStack item = top.getItem(i);
                        if(item != null) {
                            final ItemMeta itemMeta = item.getItemMeta();
                            final List<String> lore = new ArrayList<>(), existingLore = itemMeta.getLore();
                            if(existingLore != null) {
                                for(String s : existingLore) {
                                    lore.add(s.replace("{WAGER}", ww));
                                }
                            }
                            itemMeta.setLore(lore); lore.clear();
                            item.setItemMeta(itemMeta);
                        }
                    }
                    player.updateInventory();
                    picking.put(player, wager);
                }
            }
        }
    }
    public void tryCancelling(@NotNull Player player) {
        if(hasPermission(player, CoinFlipPermission.CANCEL_MATCH, true)) {
            final CoinFlipMatch match = CoinFlipMatch.valueOfCreator(player);
            if(match == null) {
                sendStringListMessage(player, getStringList(config, "messages.cancel dont have one"), null);
            } else {
                final BigDecimal wager = match.getWager();
                ECONOMY.depositPlayer(player, wager.doubleValue());
                delete(match);
                sendStringListMessage(player, getStringList(config, "messages.cancelled"), null);
            }
        }
    }
    public void tryChallenging(@NotNull Player player, @NotNull CoinFlipMatch match) {
        if(hasPermission(player, CoinFlipPermission.CHALLENGE_MATCH, true)) {
            player.closeInventory();
            final CoinFlipMatch f = CoinFlipMatch.valueOfCreator(player);
            if(f != null) {
                sendStringListMessage(player, getStringList(config, "messages.already in a match"), null);
            } else if(match.isActive()) {
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
                    final ItemStack item = top.getItem(i);
                    if(item != null) {
                        final ItemMeta itemMeta = item.getItemMeta();
                        final List<String> lore = new ArrayList<>();
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
    private ItemStack getSelection(OfflinePlayer player, OfflinePlayer target, CoinFlipMatch m) {
        final boolean isCreator = target.equals(m.getCreator());
        final CoinFlipOption o = isCreator ? m.getCreatorOption() : m.getChallengerOption();
        final ItemStack item = o.selection();
        final ItemMeta itemMeta = item.getItemMeta();
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

        final ItemStack countdownItem = countdown.clone();
        final ItemMeta countdownMeta = countdownItem.getItemMeta();
        countdownItem.setAmount(countdownStart);
        countdownMeta.setDisplayName(countdownMeta.getDisplayName().replace("{TIME}", cd));
        final List<String> countdownLore = new ArrayList<>();
        for(String s : countdownMeta.getLore()) {
            countdownLore.add(s.replace("{TIME}", cd));
        }
        countdownMeta.setLore(countdownLore);
        countdownItem.setItemMeta(countdownMeta);
        inv.setItem(challengeSlots.get("winner"), countdownItem);

        final ItemStack[] contents = inv.getContents();

        if(creator.isOnline()) {
            final Player creatorPlayer = creator.getPlayer();
            creatorPlayer.closeInventory();
            creatorPlayer.openInventory(Bukkit.createInventory(creatorPlayer, size, T));
            creatorPlayer.getOpenInventory().getTopInventory().setContents(contents);
            creatorPlayer.updateInventory();
            active.put(creatorPlayer, match);
        }
        if(challenger.isOnline()) {
            final Player challengerPlayer = challenger.getPlayer();
            challengerPlayer.closeInventory();
            challengerPlayer.openInventory(Bukkit.createInventory(challengerPlayer, size, T));
            final Inventory challengerTop = challengerPlayer.getOpenInventory().getTopInventory();
            challengerTop.setContents(contents);
            challengerTop.setItem(Y, getSelection(challenger, creator, match));
            challengerTop.setItem(Z, getSelection(challenger, challenger, match));
            challengerPlayer.updateInventory();
            active.put(challengerPlayer, match);
        }

        final String rollingName = colorize(config.getString("challenge.rolling.name"));
        final List<Integer> t = tasks.get(match);
        for(int i = 1; i <= countdownStart; i++) {
            final int I = i;
            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                final String CD = Integer.toString(countdownStart-I);
                ItemStack item = countdown.clone();
                ItemMeta itemMeta = item.getItemMeta();
                item.setAmount(countdownStart-I);
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TIME}", CD));
                final List<String> lore = new ArrayList<>();
                for(String string : itemMeta.getLore()) {
                    lore.add(string.replace("{TIME}", CD));
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                final Player creatorPlayer = creator.getPlayer(), challengerPlayer = challenger.getPlayer();
                final int q = challengeSlots.get("winner");
                if(creator.isOnline() && active.containsKey(creatorPlayer)) {
                    creatorPlayer.getOpenInventory().getTopInventory().setItem(q, item);
                    creatorPlayer.updateInventory();
                }
                if(challenger.isOnline() && active.containsKey(challengerPlayer)) {
                    challengerPlayer.getOpenInventory().getTopInventory().setItem(q, item);
                    challengerPlayer.updateInventory();
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
                            item = option.clone();
                            itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(rollingName.replace("{SELECTION_COLOR}", selectionColor));
                            itemMeta.setLore(getStringList(config, "challenge.rolling.lore"));
                            item.setItemMeta(itemMeta);
                            final ItemStack finalItem = item;
                            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                                if(creator.isOnline() && active.containsKey(creatorPlayer)) {
                                    creatorPlayer.getOpenInventory().getTopInventory().setItem(q, finalItem);
                                }
                                if(challenger.isOnline() && active.containsKey(challengerPlayer)) {
                                    challengerPlayer.getOpenInventory().getTopInventory().setItem(q, finalItem);
                                }
                            }, d));
                            final boolean isF = option == F;
                            option = isF ? G : F;
                            selectionColor = isF ? color2 : color1;
                        }
                    }
                }
            }, 20L * i));
        }
    }
    private void chooseWinner(CoinFlipMatch match) {
        available.remove(match);
        match.setActive(false);
        final OfflinePlayer creator = match.getCreator(), challenger = match.getChallenger();
        final CoinFlipOption creatorOption = match.getCreatorOption(), challengerOption = match.getChallengerOption();
        final BigDecimal wager = match.getWager(), tax = BigDecimal.valueOf(wager.doubleValue() * this.tax), total = BigDecimal.valueOf(wager.doubleValue()*2), taxed = BigDecimal.valueOf(total.doubleValue()*(this.tax *2));
        final boolean zero = RANDOM.nextInt(2) == 0;
        final CoinFlipOption winningOption = zero ? creatorOption : challengerOption, losingOption = zero ? challengerOption : creatorOption;
        final OfflinePlayer winner = zero ? creator : challenger, loser = winner == creator ? challenger : creator;
        final FileRPPlayer winnerRPPlayer = FileRPPlayer.get(winner.getUniqueId()), loserRPPlayer = FileRPPlayer.get(loser.getUniqueId());

        final CoinFlipData winnerData = winnerRPPlayer.getCoinFlipData(), loserData = loserRPPlayer.getCoinFlipData();
        winnerData.setWonCash(winnerData.getWonCash().add(tax));
        winnerData.setWins(winnerData.getWins().add(BigDecimal.ONE));
        winnerData.setTaxesPaid(winnerData.getTaxesPaid().add(taxed));
        loserData.setLostCash(loserData.getLostCash().add(tax));
        loserData.setLosses(loserData.getLosses().add(BigDecimal.ONE));
        loserData.setTaxesPaid(loserData.getTaxesPaid().add(taxed));

        final Player creatorPlayer = creator.getPlayer(), challengerPlayer = challenger.getPlayer();
        if(active.containsKey(creatorPlayer)) {
            active.put(creatorPlayer, null);
        }
        if(active.containsKey(challengerPlayer)) {
            active.put(challengerPlayer, null);
        }
        final String winnerName = winner.getName(), color = winningOption.selectionColor, loserColor = losingOption.selectionColor;
        ECONOMY.depositPlayer(winner, total.doubleValue()-taxed.doubleValue());
        final ItemStack item = winningOption.appear();
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(getString(config, "challenge.winner.name").replace("{COLOR}", color).replace("{PLAYER}", winnerName));
        final List<String> lore = new ArrayList<>();
        for(String s : getStringList(config, "challenge.winner.lore")) {
            lore.add(s.replace("{PLAYER}", winnerName).replace("{COLOR}", color));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        final int slot = challengeSlots.get("winner");
        if(active.containsKey(creatorPlayer)) {
            creatorPlayer.getOpenInventory().getTopInventory().setItem(slot, item);
            creatorPlayer.updateInventory();
        }
        if(active.containsKey(challengerPlayer)) {
            challengerPlayer.getOpenInventory().getTopInventory().setItem(slot, item);
            challengerPlayer.updateInventory();
        }
        stopTasks(match);
        match.delete();

        final List<String> msg = getStringList(config, "messages.winner");
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{WINNING_COLOR}", color);
        replacements.put("{LOSING_COLOR}", loserColor);
        replacements.put("{WINNER}", winnerName);
        replacements.put("{LOSER}", loser.getName());
        replacements.put("{WAGER}", formatBigDecimal(total));
        for(String string : msg) {
            for(String re : replacements.keySet()) {
                string = string.replace(re, replacements.get(re));
            }
            sendConsoleMessage(string);
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(FileRPPlayer.get(player.getUniqueId()).getCoinFlipData().receivesNotifications()) {
                sendStringListMessage(player, msg, replacements);
            }
        }
        final CoinFlipEndEvent endEvent = new CoinFlipEndEvent(winner, loser, wager, BigDecimal.valueOf(this.tax));
        PLUGIN_MANAGER.callEvent(endEvent);
    }
    private void stopTasks(CoinFlipMatch match) {
        if(tasks.containsKey(match)) {
            for(int i : tasks.get(match)) {
                SCHEDULER.cancelTask(i);
            }
            tasks.remove(match);
        }
    }
    private void delete(CoinFlipMatch match) {
        stopTasks(match);
        available.remove(match);
        match.delete();
    }
    public void create(OfflinePlayer player, CoinFlipOption picked, boolean withdraw, boolean sendMsg) {
        final BigDecimal wager = picking.get(player);
        picking.remove(player);
        if(withdraw) {
            ECONOMY.withdrawPlayer(player, wager.doubleValue());
        }
        if(sendMsg && player.isOnline()) {
            sendStringListMessage(player.getPlayer(), getStringList(config, "messages.created"), null);
        }
        final CoinFlipMatch match = new CoinFlipMatch(System.currentTimeMillis(), player, picked, wager);
        available.add(match);
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        picking.remove(player);
        pickingChallengeOption.remove(player);
        if(active.containsKey(player)) {
            final CoinFlipMatch match = active.get(player);
            active.remove(player);
            if(match != null && match.isActive() && !active.containsKey(match.getChallenger().getPlayer())) {
                chooseWinner(match);
                match.delete();
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
            final int rawSlot = event.getRawSlot();
            final ItemStack currentItem = event.getCurrentItem();
            if(rawSlot < 0 || rawSlot > top.getSize() || currentItem == null || currentItem.getType().equals(Material.AIR) || hasActive) {
                return;
            }

            if(isViewing) {
                final SkullMeta m = (SkullMeta) currentItem.getItemMeta();
                final OfflinePlayer n = isLegacy ? Bukkit.getOfflinePlayer(m.getOwner()) : m.getOwningPlayer();
                final CoinFlipMatch f = CoinFlipMatch.valueOfCreator(n);
                tryChallenging(player, f);
            } else if(isChallenging) {
                final CoinFlipMatch targetMatch = pickingChallengeOption.get(player);
                final double balance = ECONOMY.getBalance(player), wagerAmount = targetMatch.getWager().doubleValue();
                player.closeInventory();
                if(!available.contains(targetMatch) || targetMatch.isActive()) {
                    sendStringListMessage(player, getStringList(config, "messages.no longer available"), null);
                    viewCoinFlips(player);
                } else if(balance < wagerAmount) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{BAL}", formatDouble(balance));
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                } else if(optionz.containsKey(rawSlot)) {
                    ECONOMY.withdrawPlayer(player, wagerAmount);
                    targetMatch.setChallenger(player);
                    targetMatch.setChallengerOption(optionz.get(rawSlot));
                    start(targetMatch);
                }
            } else {
                final CoinFlipOption option = optionz.getOrDefault(rawSlot, null);
                if(option != null) {
                    create(player, option, true, true);
                    player.closeInventory();
                }
            }
        }
    }
}
