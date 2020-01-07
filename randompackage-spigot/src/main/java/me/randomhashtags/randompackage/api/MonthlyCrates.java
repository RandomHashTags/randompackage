package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randompackage.addon.MonthlyCrate;
import me.randomhashtags.randompackage.addon.file.FileMonthlyCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class MonthlyCrates extends RPFeature implements CommandExecutor {
    private static MonthlyCrates instance;
    public static MonthlyCrates getMonthlyCrates() {
        if(instance == null) instance = new MonthlyCrates();
        return instance;
    }
    public YamlConfiguration config;

    private UInventory gui, categoryView;
    private ItemStack locked, background, alreadyClaimed, categoryViewBackground;
    public ItemStack mysterycrate, heroicmysterycrate, superiormysterycrate;
    private HashMap<Player, List<Integer>> regularRewardsLeft, bonusRewardsLeft, playertimers;
    private HashMap<Integer, Integer> categories;
    private HashMap<Integer, UInventory> categoriez;

    public String getIdentifier() { return "MONTHLY_CRATES"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(args.length == 0 && player != null) {
            viewCrates(player);
        } else if(args.length == 2 && args[0].equals("reset") && hasPermission(player, "RandomPackage.monthlycrates.reset", true)) {
            reset(player, Bukkit.getOfflinePlayer(args[1]));
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("monthly crates", "_settings.yml");
        final String folder = DATA_FOLDER + SEPARATOR + "monthly crates";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        categoryView = new UInventory(null, 54, colorize(config.getString("category view.title")));
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

        if(!otherdata.getBoolean("saved default monthly crates")) {
            generateDefaultMonthlyCrates();
            otherdata.set("saved default monthly crates", true);
            saveOtherData();
        }
        final HashMap<Integer, HashMap<Integer, MonthlyCrate>> categorySlots = new HashMap<>();
        final HashMap<Integer, HashMap<Integer, ItemStack>> K = new HashMap<>();
        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileMonthlyCrate m = new FileMonthlyCrate(f);
                final int z = m.getCategory();
                if(!categorySlots.containsKey(z)) categorySlots.put(z, new HashMap<>());
                if(categoriez.containsKey(z)) {
                    if(!K.containsKey(z)) K.put(z, new HashMap<>());
                    final int slot = m.getCategorySlot();
                    K.get(z).put(slot, m.getItem());
                    categorySlots.get(z).put(slot, m);
                }
            }
        }

        for(int i = 0; i < gui.getSize(); i++) {
            item = gi.getItem(i);
            if(categories.containsKey(i)) {
                final HashMap<Integer, MonthlyCrate> A = categorySlots.get(categories.get(i));
                itemMeta = item.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    for(String s : itemMeta.getLore()) {
                        if(s.contains("{CRATE}")) {
                            for(int S : A.keySet()) {
                                lore.add(s.replace("{CRATE}", A.get(S).getItem().getItemMeta().getDisplayName()));
                            }
                        } else {
                            lore.add(s);
                        }
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
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.MONTHLY_CRATE).size() + " Monthly Crates &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player p : new ArrayList<>(playertimers.keySet())) {
            p.closeInventory();
        }
        unregister(Feature.MONTHLY_CRATE);
    }

    public void viewCrates(@NotNull Player player) {
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
                    final MonthlyCrate cc = valueOfMonthlyCrate(item);
                    if(cc != null) {
                        final String n = cc.getIdentifier();
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
    public void openMonthlyCrate(@NotNull Player player, @NotNull MonthlyCrate crate) {
        final String p = crate.getIdentifier();
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
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PATH}", p));
                    }
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore())
                            lore.add(s.replace("{PATH}", p));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
            }
        }
        player.updateInventory();
    }
    private void doAnimation(Player player, MonthlyCrate m) {
        final String p = m.getIdentifier();
        final Inventory inv = m.getBonus().getInventory();
        final List<Integer> rewardSlots = m.getRewardSlots(), bonusSlots = m.getBonusRewardSlots();
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(int i = 0; i < top.getSize(); i++) {
            if(!rewardSlots.contains(i) && !bonusSlots.contains(i)) {
                top.setItem(i, inv.getItem(i));
            }
        }
        for(int i : bonusSlots) {
            item = inv.getItem(i).clone(); itemMeta = item.getItemMeta(); lore.clear();
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
        final List<Integer> regular = regularRewardsLeft.getOrDefault(player, null), bonus = bonusRewardsLeft.getOrDefault(player, null);
        if(regular != null) {
            final List<String> r = MonthlyCrate.revealedRegular.getOrDefault(player, null);
            for(int i : m.getRewardSlots()) {
                final ItemStack is = regular.contains(i) ? m.getRandomReward(player, r, false) : inv.getItem(i);
                if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(cmd.getItemMeta().getDisplayName()) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() == cmd.getItemMeta().getLore().size()) {
                    Bukkit.dispatchCommand(CONSOLE, is.getItemMeta().getLore().get(0).substring(1).replace("<player>", p));
                } else {
                    giveItem(player, is);
                }
            }
        }
        if(bonus != null || regular != null) {
            final List<String> r = MonthlyCrate.revealedBonus.getOrDefault(player, null);
            for(int i : m.getBonusRewardSlots()) {
                final ItemStack is = bonus == null || bonus.contains(i) ? m.getRandomBonusReward(player, r, false) : inv.getItem(i);
                if(is.hasItemMeta() && is.getItemMeta().getDisplayName().equals(cmd.getItemMeta().getDisplayName()) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() == cmd.getItemMeta().getLore().size()) {
                    Bukkit.dispatchCommand(CONSOLE, is.getItemMeta().getLore().get(0).substring(1).replace("<player>", p));
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
            for(int i : playertimers.get(player)) {
                SCHEDULER.cancelTask(i);
            }
            playertimers.remove(player);
        }
    }
    public void reset(@NotNull Player sender, @Nullable OfflinePlayer target) {
        if(target == null || !target.isOnline()) {
            sendStringListMessage(sender, getStringList(config, "messages.reset.target doesnt exist"), null);
        } else {
            final RPPlayer pdata = RPPlayer.get(target.getUniqueId());
            pdata.getClaimedMonthlyCrates().clear();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TARGET}", target.getName());
            sendStringListMessage(sender, getStringList(config, "messages.reset.success"), replacements);
        }
    }
    public void give(RPPlayer pdata, Player player, MonthlyCrate crate, boolean claimed) {
        item = crate.getItem(); itemMeta = item.getItemMeta(); lore.clear();
        if(item.hasItemMeta()) {
            if(itemMeta.hasLore()) {
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{UNLOCKED_BY}", player.getName()));
                }
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
        }
        giveItem(player, item);
        if(claimed) {
            pdata.getClaimedMonthlyCrates().add(crate.getIdentifier());
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
                    final MonthlyCrate m = valueOfMonthlyCrate(item);
                    if(m != null) {
                        final String N = m.getIdentifier();
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final ItemStack current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR) && top.getHolder() == player) {
            final int slot = event.getRawSlot();
            final String title = event.getView().getTitle();
            if(title.equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final MonthlyCrate crate = valueOfMonthlyCrate(player, current);
                if(crate != null) {
                    final String n = crate.getIdentifier();
                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    final boolean hasPerm = pdata.getMonthlyCrates().contains(n) || player.hasPermission("RandomPackage.monthlycrates." + n);
                    if(!hasPerm) {
                        sendStringListMessage(player, getStringList(config, "messages.no access"), null);
                    } else if(!pdata.getClaimedMonthlyCrates().contains(n)) {
                        give(pdata, player, crate, true);
                    }
                    player.closeInventory();
                } else if(categories.containsKey(slot)) {
                    viewCategory(player, categories.get(slot));
                }
            } else {
                final int category = valueOfCategory(title);
                final MonthlyCrate m = category == -1 ? valueOfMonthlyCrate(title) : null;
                if(category != -1) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final MonthlyCrate v = valueOfMonthlyCrate(category, slot);
                    if(v != null) {
                        final String N = v.getIdentifier();
                        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                        if(!pdata.getClaimedMonthlyCrates().contains(N) && (pdata.getMonthlyCrates().contains(N) || hasPermission(player, "RandomPackage.monthlycrate." + N, false))) {
                            give(pdata, player, v, true);
                        }
                    }
                } else if(m != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    if(slot >= top.getSize()) return;
                    final List<Integer> regular = regularRewardsLeft.getOrDefault(player, null), bonus = bonusRewardsLeft.getOrDefault(player, null);
                    if(regular != null && regular.contains(slot)) {
                        top.setItem(slot, m.getRandomReward(player, MonthlyCrate.revealedRegular.get(player), false));
                        regular.remove(slot);
                        if(regular.size() == 0 && bonus != null) {
                            bonusRewardsLeft.put(player, new ArrayList<>(m.getBonusRewardSlots()));
                            doAnimation(player, m);
                        }
                    } else if(bonus != null && (regular == null || regular.isEmpty()) && bonus.contains(slot)) {
                        top.setItem(slot, m.getRandomReward(player, MonthlyCrate.revealedBonus.get(player), false));
                        bonus.remove(slot);
                    }
                    player.updateInventory();
                    if(regular != null && regular.isEmpty() && bonus != null && bonus.isEmpty()) {
                        player.closeInventory();
                    }
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
            final MonthlyCrate c = valueOfMonthlyCrate(player, i);
            if(c != null) {
                event.setCancelled(true);
                player.updateInventory();
                openMonthlyCrate(player, c);
                removeItem(player, i, 1);
            } else if(i.hasItemMeta() && (m.equals(heroicmysterycrate.getItemMeta()) || m.equals(mysterycrate.getItemMeta()) || m.equals(superiormysterycrate.getItemMeta()))) {
                final String p = (m.equals(superiormysterycrate.getItemMeta()) ? "superior " : m.equals(heroicmysterycrate.getItemMeta()) ? "heroic " : "") + "mystery crate";
                final List<String> obtainable = getStringList(config, "items." + p + ".can obtain");
                final String r = obtainable.get(RANDOM.nextInt(obtainable.size()));
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
            final MonthlyCrate mc = valueOfMonthlyCrate(event.getView().getTitle());
            if(mc != null) {
                exit(player, i, mc);
            }
        }
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final InventoryView open = player.getOpenInventory();
        final MonthlyCrate m = valueOfMonthlyCrate(open.getTitle());
        if(m != null) {
            exit(player, open.getTopInventory(), m);
        }
    }
}
