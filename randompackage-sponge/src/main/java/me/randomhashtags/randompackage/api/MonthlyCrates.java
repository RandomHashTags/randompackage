package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.MonthlyCrate;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonthlyCrates extends RandomPackageAPI implements Listener, CommandExecutor {

    private static MonthlyCrates instance;
    public static final MonthlyCrates getMonthlyCrates() {
        if(instance == null) instance = new MonthlyCrates();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;

    private UInventory gui, categoryView;
    private ItemStack locked, background, alreadyClaimed, categoryViewBackground;
    public ItemStack mysterycrate, heroicmysterycrate, superiormysterycrate;
    private HashMap<Player, List<Integer>> regularRewardsLeft, bonusRewardsLeft, playertimers;
    private HashMap<Integer, Integer> categories;
    private HashMap<Integer, UInventory> categoriez;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(args.length == 0 && player != null) {
            viewCrates(player);
        } else if(args.length == 2 && args[0].equals("reset") && hasPermission(player, "RandomPackage.monthlycrates.reset", true)) {
            reset(player, Bukkit.getOfflinePlayer(args[1]));
        }
        return true;
    }

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save(null, "monthly crates.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "monthly crates.yml"));
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        categoryView = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', config.getString("category view.title")));
        regularRewardsLeft = new HashMap<>();
        bonusRewardsLeft = new HashMap<>();
        playertimers = new HashMap<>();
        categories = new HashMap<>();
        categoriez = new HashMap<>();

        mysterycrate = d(config, "items.mystery crate");
        heroicmysterycrate = d(config, "items.heroic mystery crate");
        superiormysterycrate = d(config, "items.superior mystery crate");
        background = d(config, "gui.background");
        alreadyClaimed = d(config, "category view.already claimed");
        locked = d(config, "category view.locked");
        categoryViewBackground = d(config, "category view.background");

        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background") && !s.equals("already claimed") && !s.equals("locked")) {
                final int slot = config.getInt("gui." + s + ".slot");
                try {
                    final int a = Integer.parseInt(s);
                    categories.put(slot, a);
                    categoriez.put(a, new UInventory(null, 54, categoryView.getTitle().replace("{CATEGORY}", Integer.toString(a))));
                } catch (NumberFormatException e) {}
                gi.setItem(slot, d(config, "gui." + s));
            }
        }
        for(int i = 0; i < gui.getSize(); i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }

        givedpitem.items.put("mysterycrate", mysterycrate);
        givedpitem.items.put("heroicmysterycrate", heroicmysterycrate);
        givedpitem.items.put("superiormysterycrate", superiormysterycrate);
        givedpitem.items.put("superiorcrate", superiormysterycrate);

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default monthly crates")) {
            final String[] c = new String[] {
                    "APRIL_2016", "APRIL_2017", "APRIL_2018",
                    "AUGUST_2016", "AUGUST_2017", "AUGUST_2018",
                    "BLACK_FRIDAY_2016",
                    "DECEMBER_2015", "DECEMBER_2016", "DECEMBER_2017", "DECEMBER_2018",
                    "FEBRUARY_2016", "FEBRUARY_2018",
                    "HALLOWEEN_2016", "HALLOWEEN_2017", "HALLOWEEN_2018",
                    "HOLIDAY_2016", "HOLIDAY_2017",
                    "JANUARY_2016", "JANUARY_2017", "JANUARY_2018",
                    "JULY_2016", "JULY_2017", "JULY_2018",
                    "JUNE_2017", "JUNE_2018",
                    "MARCH_2016", "MARCH_2017", "MARCH_2018",
                    "MAY_2017", "MAY_2018",
                    "NOVEMBER_2016", "NOVEMBER_2017", "NOVEMBER_2018",
                    "OCTOBER_2016", "OCTOBER_2017", "OCTOBER_2018",
                    "SCHOOL_2016", "SCHOOL_2017",
                    "SEPTEMBER_2017", "SEPTEMBER_2018",
                    "THANKSGIVING_2017",
                    "VALENTINES_2017", "VALENTINES_2018",
            };
            for(String s : c) save("monthly crates", s + ".yml");

            a.set("saved default monthly crates", true);
            saveOtherData();
        }
        final HashMap<Integer, HashMap<Integer, ItemStack>> K = new HashMap<>();
        for(File f : new File(rpd + separator + "monthly crates").listFiles()) {
            final MonthlyCrate m = new MonthlyCrate(f);
            final int z = m.getCategory();
            if(categoriez.containsKey(z)) {
                if(!K.containsKey(z)) K.put(z, new HashMap<>());
                K.get(z).put(m.getCategorySlot(), m.getItem());
            }
        }
        final HashMap<Integer, HashMap<Integer, MonthlyCrate>> M = MonthlyCrate.categorySlots;
        for(int i = 0; i < gui.getSize(); i++) {
            item = gi.getItem(i);
            if(categories.containsKey(i)) {
                final HashMap<Integer, MonthlyCrate> A = M.get(categories.get(i));
                itemMeta = item.getItemMeta(); lore.clear();
                for(String s : itemMeta.getLore()) {
                    if(s.contains("{CRATE}")) {
                        for(int S : A.keySet()) {
                            lore.add(s.replace("{CRATE}", A.get(S).getItem().getItemMeta().getDisplayName()));
                        }
                    } else {
                        lore.add(s);
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
            }
        }
        for(int i : K.keySet()) {
            final HashMap<Integer, ItemStack> O = K.get(i);
            final int s = O.size();
            categoriez.put(i, new UInventory(null, s%9 == 0 ? s : (((s+9)/9)*9), categoriez.get(i).getTitle()));
            final Inventory inv = categoriez.get(i).getInventory();
            for(int S : O.keySet()) {
                inv.setItem(S, O.get(S));
            }
        }
        final HashMap<String, MonthlyCrate> MC = MonthlyCrate.crates;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (MC != null ? MC.size() : 0) + " Monthly Crates &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        config = null;
        mysterycrate = null;
        heroicmysterycrate = null;
        alreadyClaimed = null;
        locked = null;
        background = null;
        regularRewardsLeft = null;
        bonusRewardsLeft = null;
        categoryView = null;
        categories = null;
        categoriez = null;
        categoryViewBackground = null;
        for(Player p : playertimers.keySet()) p.closeInventory();
        playertimers = null;
        isEnabled = false;
        MonthlyCrate.deleteAll();
        HandlerList.unregisterAll(this);
    }

    public void viewCrates(Player player) {
        if(hasPermission(player, "RandomPackage.monthlycrates", true)) {
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final List<String> owned = pdata.getMonthlyCrates(), claimed = pdata.getClaimedMonthlyCrates();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            final String playerName = player.getName();
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null) {
                    final MonthlyCrate cc = MonthlyCrate.valueOf(item);
                    if(cc != null) {
                        final String n = cc.getYamlName();
                        final boolean owns = owned.contains(n), hasPerm = player.hasPermission("RandomPackage.monthlycrates." + n), h = !owns && !hasPerm;
                        itemMeta = item.getItemMeta(); lore.clear();
                        if(h) {
                            item = locked.clone();
                        } else if(claimed.contains(n)) {
                            item = alreadyClaimed.clone();
                        }
                        check(playerName, item, cc, h || claimed.contains(n));
                        top.setItem(i, item);
                    }
                }
            }
            player.updateInventory();
        }
    }
    private void check(String playerName, ItemStack is, MonthlyCrate crate, boolean unlocked) {
        if(is != null && is.hasItemMeta()) {
            final ItemStack j = crate.getItem();
            itemMeta = is.getItemMeta();
            if(itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", j.getItemMeta().getDisplayName()));
            }
            lore.clear();
            if(itemMeta.hasLore()) {
                for(String s : (unlocked ? j : locked).getItemMeta().getLore()) {
                    if(s.equals("{LORE}")) {
                        for(String p : j.getItemMeta().getLore()) if(!p.contains("{UNLOCKED_BY}")) lore.add(p);
                    } else
                        lore.add(s.replace("{UNLOCKED_BY}", playerName));
                }
            }
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
        }
    }
    public void openMonthlyCrate(Player player, MonthlyCrate crate) {
        final String p = crate.getYamlName();
        final UInventory inv = crate.getRegular();
        final List<Integer> rewardSlots = crate.getRewardSlots(), bonusRewardSlots = crate.getBonusRewardSlots();
        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(inv.getInventory().getContents());
        regularRewardsLeft.put(player, new ArrayList<>(rewardSlots));
        for(int i = 0; i < top.getSize(); i++) {
            if(rewardSlots.contains(i) || bonusRewardSlots.contains(i)) {
                item = top.getItem(i); itemMeta = item.getItemMeta(); lore.clear();
                if(item.hasItemMeta()) {
                    if(itemMeta.hasDisplayName()) itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PATH}", p));
                    if(itemMeta.hasLore())
                        for(String s : itemMeta.getLore())
                            lore.add(s.replace("{PATH}", p));
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
            }
        }
        player.updateInventory();
    }
    private void doAnimation(Player player, MonthlyCrate m) {
        final String p = m.getYamlName();
        final Inventory b = m.getBonus().getInventory();
        final List<Integer> r = m.getRewardSlots(), rb = m.getBonusRewardSlots();
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(int i = 0; i < top.getSize(); i++)
            if(!r.contains(i) && !rb.contains(i))
                top.setItem(i, b.getItem(i));
        for(int i : rb) {
            item = b.getItem(i).clone(); itemMeta = item.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) {
                for(String s : itemMeta.getLore()) {
                    if(s.contains("{PATH}")) s = s.replace("{PATH}", p);
                    lore.add(s);
                }
                itemMeta.setLore(lore); lore.clear();
            }
            item.setItemMeta(itemMeta);
            top.setItem(i, item);
        }
        player.updateInventory();
    }
    private void exit(Player player, Inventory inv, MonthlyCrate m) {
        stopTimers(player);
        final String p = player.getName();
        final ItemStack cmd = givedpitem.items.get("commandreward").clone();
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();
        final boolean rrl = regularRewardsLeft.containsKey(player), brl = bonusRewardsLeft.containsKey(player);
        if(rrl) {
            final List<String> r = MonthlyCrate.revealedRegular.getOrDefault(player, null);
            for(int i : m.getRewardSlots()) {
                final ItemStack is = regularRewardsLeft.get(player).contains(i) ? m.getRandomReward(player, r, false) : inv.getItem(i);
                if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(cmd.getItemMeta().getDisplayName()) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() == cmd.getItemMeta().getLore().size()) {
                    Bukkit.dispatchCommand(sender, is.getItemMeta().getLore().get(0).substring(1).replace("<player>", p));
                } else {
                    giveItem(player, is);
                }
            }
        }
        if(brl || rrl) {
            final List<String> r = MonthlyCrate.revealedBonus.getOrDefault(player, null);
            for(int i : m.getBonusRewardSlots()) {
                final ItemStack is = !brl || bonusRewardsLeft.get(player).contains(i) ? m.getRandomBonusReward(player, r, false) : inv.getItem(i);
                if(is.hasItemMeta() && is.getItemMeta().getDisplayName().equals(cmd.getItemMeta().getDisplayName()) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() == cmd.getItemMeta().getLore().size()) {
                    Bukkit.dispatchCommand(sender, is.getItemMeta().getLore().get(0).substring(1).replace("<player>", p));
                } else {
                    giveItem(player, is);
                }
            }
        }
        regularRewardsLeft.remove(player);
        bonusRewardsLeft.remove(player);
        MonthlyCrate.revealedRegular.remove(player);
        MonthlyCrate.revealedBonus.remove(player);
    }
    private void stopTimers(Player player) {
        if(playertimers.containsKey(player)) {
            for(int i : playertimers.get(player)) scheduler.cancelTask(i);
            playertimers.remove(player);
        }
    }
    public void reset(Player sender, OfflinePlayer target) {
        if(target == null || !target.isOnline()) {
            sendStringListMessage(sender, config.getStringList("messages.reset.target doesnt exist"), null);
        } else {
            final RPPlayer pdata = RPPlayer.get(target.getUniqueId());
            pdata.getClaimedMonthlyCrates().clear();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TARGET}", target.getName());
            sendStringListMessage(sender, config.getStringList("messages.reset.success"), replacements);
        }
    }
    public void give(RPPlayer pdata, Player player, MonthlyCrate crate, boolean claimed) {
        item = crate.getItem(); itemMeta = item.getItemMeta(); lore.clear();
        if(item.hasItemMeta()) {
            if(itemMeta.hasLore()) for(String s : itemMeta.getLore()) lore.add(s.replace("{UNLOCKED_BY}", player.getName()));
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
        }
        giveItem(player, item);
        if(claimed) {
            pdata.getClaimedMonthlyCrates().add(crate.getYamlName());
        }
    }
    public void viewCategory(Player player, int category) {
        if(categoriez.containsKey(category)) {
            final String n = player.getName();
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final List<String> owned = pdata.getMonthlyCrates(), claimed = pdata.getClaimedMonthlyCrates();
            final UInventory i = categoriez.get(category);
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, i.getSize(), i.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(i.getInventory().getContents());
            for(int o = 0; o < top.getSize(); o++) {
                item = top.getItem(o);
                if(item == null) {
                    top.setItem(o, categoryViewBackground);
                } else {
                    final MonthlyCrate m = MonthlyCrate.valueOf(item);
                    if(m != null) {
                        final String N = m.getYamlName();
                        if(claimed.contains(N)) {
                            item = alreadyClaimed.clone();
                        } else if(!owned.contains(N) && !hasPermission(player, "RandomPackage.monthlycrate." + N, false)) {
                            item = locked.clone();
                        }
                        check(n, item, m, owned.contains(N));
                        top.setItem(o, item);
                    }
                }
            }
            player.updateInventory();
        }
    }
    public int valueOfCategory(String title) {
        for(int i : categoriez.keySet()) {
            final UInventory in = categoriez.get(i);
            if(title.equals(in.getTitle())) {
                return i;
            }
        }
        return -1;
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final ItemStack c = event.getCurrentItem();
        if(!event.isCancelled() && c != null && !c.getType().equals(Material.AIR) && top.getHolder() == player) {
            final int r = event.getRawSlot();
            final String title = event.getView().getTitle();
            if(title.equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final MonthlyCrate mc = MonthlyCrate.valueOf(player, c);
                if(mc != null) {
                    final String n = mc.getYamlName();
                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    final boolean hasPerm = pdata.getMonthlyCrates().contains(n) || player.hasPermission("RandomPackage.monthlycrates." + n);
                    if(!hasPerm) {
                        sendStringListMessage(player, config.getStringList("messages.no access"), null);
                    } else if(!pdata.getClaimedMonthlyCrates().contains(n)) {
                        give(pdata, player, mc, true);
                    }
                    player.closeInventory();
                } else if(categories.containsKey(r)) {
                    viewCategory(player, categories.get(r));
                }
            } else {
                final int category = valueOfCategory(title);
                final MonthlyCrate m = category == -1 ? MonthlyCrate.valueOf(title) : null;
                if(category != -1) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final MonthlyCrate v = MonthlyCrate.valueOf(category, r);
                    if(v != null) {
                        final String N = v.getYamlName();
                        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                        if(!pdata.getClaimedMonthlyCrates().contains(N) && (pdata.getMonthlyCrates().contains(N) || hasPermission(player, "RandomPackage.monthlycrate." + N, false))) {
                            give(RPPlayer.get(player.getUniqueId()), player, v, true);
                        }
                    }
                } else if(m != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    if(r >= top.getSize()) return;
                    final Object rr = event.getRawSlot();
                    final int b = bonusRewardsLeft.containsKey(player) ? bonusRewardsLeft.get(player).size() : -1;
                    if(regularRewardsLeft.containsKey(player) && regularRewardsLeft.get(player).contains(r)) {
                        if(!regularRewardsLeft.get(player).isEmpty()) {
                            final List<String> R = MonthlyCrate.revealedRegular.get(player);
                            top.setItem(r, m.getRandomReward(player, R, false));
                            regularRewardsLeft.get(player).remove(rr);
                            if(regularRewardsLeft.get(player).size() == 0 && !bonusRewardsLeft.containsKey(player)) {
                                bonusRewardsLeft.put(player, new ArrayList<>(m.getBonusRewardSlots()));
                                doAnimation(player, m);
                            }
                        }
                    } else if(bonusRewardsLeft.containsKey(player) && regularRewardsLeft.get(player).isEmpty() && bonusRewardsLeft.get(player).contains(r)) {
                        if(!bonusRewardsLeft.isEmpty()) {
                            final List<String> R = MonthlyCrate.revealedBonus.get(player);
                            top.setItem(event.getRawSlot(), m.getRandomReward(player, R, false));
                            bonusRewardsLeft.get(player).remove(rr);
                        }
                    }
                    player.updateInventory();
                    if(regularRewardsLeft.containsKey(player) && regularRewardsLeft.get(player).isEmpty() && bonusRewardsLeft.containsKey(player) && b == 0) player.closeInventory();
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()) {
            final ItemMeta m = i.getItemMeta();
            final Player player = event.getPlayer();
            final String n = player.getName();
            final MonthlyCrate c = MonthlyCrate.valueOf(player, i);
            if(c != null) {
                event.setCancelled(true);
                player.updateInventory();
                openMonthlyCrate(player, c);
                removeItem(player, i, 1);
            } else if(i.hasItemMeta() && (m.equals(heroicmysterycrate.getItemMeta()) || m.equals(mysterycrate.getItemMeta()) || m.equals(superiormysterycrate.getItemMeta()))) {
                final String p = m.equals(superiormysterycrate.getItemMeta()) ? "superior mystery crate" : m.equals(heroicmysterycrate.getItemMeta()) ? "heroic mystery crate" : "mystery crate";
                final List<String> kk = config.getStringList("items." + p + ".can obtain");
                final String r = kk.get(random.nextInt(kk.size()));
                final ItemStack I = givedpitem.valueOf("monthlycrate:" + r);
                itemMeta = I.getItemMeta(); lore.clear();
                if(itemMeta != null && itemMeta.hasLore()) {
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{UNLOCKED_BY}")) string = string.replace("{UNLOCKED_BY}", n);
                        lore.add(string);
                    }
                    itemMeta.setLore(lore); lore.clear();
                    I.setItemMeta(itemMeta);
                }
                event.setCancelled(true);
                removeItem(player, i, 1);
                giveItem(player, I);
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Inventory i = event.getInventory();
        final Player player = (Player) event.getPlayer();
        if(i.getHolder() == player) {
            final MonthlyCrate mc = MonthlyCrate.valueOf(event.getView().getTitle());
            if(mc != null) {
                exit(player, i, mc);
            }
        }
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final MonthlyCrate m = MonthlyCrate.valueOf(player.getOpenInventory().getTitle());
        if(m != null)
            exit(player, top, m);
    }
}
