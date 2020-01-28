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
import java.util.UUID;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

public class MonthlyCrates extends RPFeature implements CommandExecutor {
    private static MonthlyCrates instance;
    public static MonthlyCrates getMonthlyCrates() {
        if(instance == null) instance = new MonthlyCrates();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui, categoryView;
    private ItemStack locked, alreadyClaimed, categoryViewBackground;
    public ItemStack mysteryCrate, heroicMysteryCrate, superiorMysteryCrate;
    private HashMap<Player, List<Integer>> regularRewardsLeft, bonusRewardsLeft, playerTimers;
    private HashMap<Integer, Integer> categories;
    private HashMap<Integer, UInventory> categoriez;

    public String getIdentifier() {
        return "MONTHLY_CRATES";
    }
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
        playerTimers = new HashMap<>();
        categories = new HashMap<>();
        categoriez = new HashMap<>();

        mysteryCrate = d(config, "items.mystery crate");
        heroicMysteryCrate = d(config, "items.heroic mystery crate");
        superiorMysteryCrate = d(config, "items.superior mystery crate");
        final ItemStack background = d(config, "gui.background");
        alreadyClaimed = d(config, "category view.already claimed");
        locked = d(config, "category view.locked");
        categoryViewBackground = d(config, "category view.background");

        final Inventory gi = gui.getInventory();
        for(String s : getConfigurationSectionKeys(config, "gui", false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background") && !s.equals("already claimed") && !s.equals("locked")) {
                final int slot = config.getInt("gui." + s + ".slot");
                try {
                    final int a = Integer.parseInt(s);
                    categories.put(slot, a);
                    categoriez.put(a, new UInventory(null, 54, categoryView.getTitle().replace("{CATEGORY}", Integer.toString(a))));
                } catch (NumberFormatException ignored) {
                }
                gi.setItem(slot, d(config, "gui." + s));
            }
        }
        for(int i = 0; i < gui.getSize(); i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }

        GIVEDP_ITEM.items.put("mysterycrate", mysteryCrate);
        GIVEDP_ITEM.items.put("heroicmysterycrate", heroicMysteryCrate);
        GIVEDP_ITEM.items.put("superiormysterycrate", superiorMysteryCrate);
        GIVEDP_ITEM.items.put("superiorcrate", superiorMysteryCrate);

        if(!otherdata.getBoolean("saved default monthly crates")) {
            generateDefaultMonthlyCrates();
            otherdata.set("saved default monthly crates", true);
            saveOtherData();
        }
        final HashMap<Integer, HashMap<Integer, MonthlyCrate>> categorySlots = new HashMap<>();
        final HashMap<Integer, HashMap<Integer, ItemStack>> categoryItemStacks = new HashMap<>();
        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileMonthlyCrate crate = new FileMonthlyCrate(f);
                final int category = crate.getCategory();
                if(!categorySlots.containsKey(category)) {
                    categorySlots.put(category, new HashMap<>());
                }
                if(categoriez.containsKey(category)) {
                    if(!categoryItemStacks.containsKey(category)) {
                        categoryItemStacks.put(category, new HashMap<>());
                    }
                    final int slot = crate.getCategorySlot();
                    categoryItemStacks.get(category).put(slot, crate.getItem());
                    categorySlots.get(category).put(slot, crate);
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
        for(int i : categoryItemStacks.keySet()) {
            final HashMap<Integer, ItemStack> O = categoryItemStacks.get(i);
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
        for(Player p : new ArrayList<>(playerTimers.keySet())) {
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
                for(String s : (unlocked ? j : locked.clone()).getItemMeta().getLore()) {
                    if(s.equals("{LORE}")) {
                        for(String p : j.getItemMeta().getLore()) {
                            if(!p.contains("{UNLOCKED_BY}")) {
                                lore.add(p);
                            }
                        }
                    } else {
                        lore.add(s.replace("{UNLOCKED_BY}", playerName));
                    }
                }
            }
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
        }
    }
    public void openMonthlyCrate(@NotNull Player player, @NotNull MonthlyCrate crate) {
        final String identifier = crate.getIdentifier();
        final UInventory inv = crate.getRegular();
        final List<Integer> rewardSlots = crate.getRewardSlots(), bonusRewardSlots = crate.getBonusRewardSlots();
        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(inv.getInventory().getContents());
        regularRewardsLeft.put(player, new ArrayList<>(rewardSlots));
        for(int i = 0; i < top.getSize(); i++) {
            if(rewardSlots.contains(i) || bonusRewardSlots.contains(i)) {
                item = top.getItem(i);
                itemMeta = item.getItemMeta(); lore.clear();
                if(item.hasItemMeta()) {
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PATH}", identifier));
                    }
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{PATH}", identifier));
                        }
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
    private void exit(Player player, Inventory inv, MonthlyCrate crate) {
        stopTimers(player);
        final String playerName = player.getName();
        final ItemStack cmd = GIVEDP_ITEM.items.get("commandreward").clone();
        final List<Integer> regular = regularRewardsLeft.getOrDefault(player, null), bonus = bonusRewardsLeft.getOrDefault(player, null);
        if(regular != null) {
            final List<String> r = MonthlyCrate.revealedRegular.getOrDefault(player, null);
            for(int i : crate.getRewardSlots()) {
                final ItemStack is = regular.contains(i) ? crate.getRandomReward(player, r, false) : inv.getItem(i);
                if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(cmd.getItemMeta().getDisplayName()) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() == cmd.getItemMeta().getLore().size()) {
                    Bukkit.dispatchCommand(CONSOLE, is.getItemMeta().getLore().get(0).substring(1).replace("<player>", playerName));
                } else {
                    giveItem(player, is);
                }
            }
        }
        if(bonus != null || regular != null) {
            final List<String> r = MonthlyCrate.revealedBonus.getOrDefault(player, null);
            for(int i : crate.getBonusRewardSlots()) {
                final ItemStack is = bonus == null || bonus.contains(i) ? crate.getRandomBonusReward(player, r, false) : inv.getItem(i);
                if(is.hasItemMeta() && is.getItemMeta().getDisplayName().equals(cmd.getItemMeta().getDisplayName()) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() == cmd.getItemMeta().getLore().size()) {
                    Bukkit.dispatchCommand(CONSOLE, is.getItemMeta().getLore().get(0).substring(1).replace("<player>", playerName));
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
        if(playerTimers.containsKey(player)) {
            for(int i : playerTimers.get(player)) {
                SCHEDULER.cancelTask(i);
            }
            playerTimers.remove(player);
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
            final String playerName = player.getName();
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
                    final MonthlyCrate crate = valueOfMonthlyCrate(item);
                    if(crate != null) {
                        final String identifier = crate.getIdentifier();
                        if(claimed.contains(identifier)) {
                            item = alreadyClaimed.clone();
                        } else if(!owned.contains(identifier) && !hasPermission(player, "RandomPackage.monthlycrate." + identifier, false)) {
                            item = locked.clone();
                        }
                        check(playerName, item, crate, owned.contains(identifier));
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
            final UUID uuid = player.getUniqueId();
            if(title.equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final MonthlyCrate crate = valueOfMonthlyCrate(player, current);
                if(crate != null) {
                    final String identifier = crate.getIdentifier();
                    final RPPlayer pdata = RPPlayer.get(uuid);
                    final boolean hasPerm = pdata.getMonthlyCrates().contains(identifier) || player.hasPermission("RandomPackage.monthlycrates." + identifier);
                    if(!hasPerm) {
                        sendStringListMessage(player, getStringList(config, "messages.no access"), null);
                    } else if(!pdata.getClaimedMonthlyCrates().contains(identifier)) {
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
                    final MonthlyCrate crate = valueOfMonthlyCrate(category, slot);
                    if(crate != null) {
                        final String identifier = crate.getIdentifier();
                        final RPPlayer pdata = RPPlayer.get(uuid);
                        if(!pdata.getClaimedMonthlyCrates().contains(identifier) && (pdata.getMonthlyCrates().contains(identifier) || hasPermission(player, "RandomPackage.monthlycrate." + identifier, false))) {
                            give(pdata, player, crate, true);
                        }
                    }
                } else if(m != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    if(slot >= top.getSize()) {
                        return;
                    }
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
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            final Player player = event.getPlayer();
            final MonthlyCrate crate = valueOfMonthlyCrate(player, is);
            if(crate != null) {
                event.setCancelled(true);
                player.updateInventory();
                openMonthlyCrate(player, crate);
                removeItem(player, is, 1);
            } else if(is.hasItemMeta() && (m.equals(heroicMysteryCrate.getItemMeta()) || m.equals(mysteryCrate.getItemMeta()) || m.equals(superiorMysteryCrate.getItemMeta()))) {
                final String type = (m.equals(superiorMysteryCrate.getItemMeta()) ? "superior " : m.equals(heroicMysteryCrate.getItemMeta()) ? "heroic " : "") + "mystery crate";
                final List<String> obtainable = getStringList(config, "items." + type + ".can obtain");
                final String id = obtainable.get(RANDOM.nextInt(obtainable.size()));
                final ItemStack itemstack = GIVEDP_ITEM.valueOf("monthlycrate:" + id);
                final String playerName = player.getName();
                itemMeta = itemstack.getItemMeta(); lore.clear();
                if(itemMeta != null && itemMeta.hasLore()) {
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{UNLOCKED_BY}")) string = string.replace("{UNLOCKED_BY}", playerName);
                        lore.add(string);
                    }
                    itemMeta.setLore(lore); lore.clear();
                    itemstack.setItemMeta(itemMeta);
                }
                event.setCancelled(true);
                removeItem(player, is, 1);
                giveItem(player, itemstack);
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Inventory i = event.getInventory();
        final Player player = (Player) event.getPlayer();
        if(i.getHolder() == player) {
            final MonthlyCrate crate = valueOfMonthlyCrate(event.getView().getTitle());
            if(crate != null) {
                exit(player, i, crate);
            }
        }
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final InventoryView open = player.getOpenInventory();
        final MonthlyCrate crate = valueOfMonthlyCrate(open.getTitle());
        if(crate != null) {
            exit(player, open.getTopInventory(), crate);
        }
    }
}
