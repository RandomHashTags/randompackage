package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Lootbox;
import me.randomhashtags.randompackage.addon.enums.LootboxRewardType;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.addon.FileLootbox;
import me.randomhashtags.randompackage.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lootboxes extends RPFeature implements CommandExecutor {
    private static Lootboxes instance;
    public static Lootboxes getLootboxes() {
        if(instance == null) instance = new Lootboxes();
        return instance;
    }
    public YamlConfiguration config;

    private UInventory gui;
    private HashMap<Lootbox, Long> started;
    private int countdownStart = 0;
    private ItemStack background;
    private List<String> opened, rewardFormat;
    private HashMap<Integer, Lootbox> guiLootboxes;
    private HashMap<Player, Lootbox> redeeming;
    private List<Player> viewing;
    private HashMap<Player, List<Integer>> tasks;

    public String getIdentifier() { return "LOOTBOXES"; }
    protected RPFeature getFeature() { return getLootboxes(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null) {
            viewLootbox(player);
        }
        return true;
    }

    public void load() {
        final long sc = System.currentTimeMillis();
        save("lootboxes", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd + separator + "lootboxes", "_settings.yml"));

        started = new HashMap<>();
        countdownStart = config.getInt("settings.countdown start");
        opened = colorizeListString(config.getStringList("messages.opened"));
        rewardFormat = colorizeListString(config.getStringList("messages.reward format"));

        guiLootboxes = new HashMap<>();
        redeeming = new HashMap<>();
        viewing = new ArrayList<>();
        tasks = new HashMap<>();

        final String title = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")), type = config.getString("gui.type");
        final int size = config.getInt("gui.size");
        if(type != null) {
            final InventoryType i = InventoryType.valueOf(type);
            gui = new UInventory(null, i, title);
        } else {
            gui = new UInventory(null, size, title);
        }
        background = d(config, "gui.background");

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default lootboxes")) {
            final String[] l = new String[] {"SNOW_DAY", "ICY_ADVENTURES", "SURVIVAL_KIT", "BOX_OF_CHOCOLATES", "BAKED", "LUCKY"};
            for(String s : l) save("lootboxes", s + ".yml");
            a.set("saved default lootboxes", true);
            saveOtherData();
        }
        final File folder = new File(rpd + separator + "lootboxes");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                    new FileLootbox(f);
                }
            }
        }

        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("type") && !s.equals("size") && !s.equals("background")) {
                final ItemStack is = d(config, "gui." + s);
                final int slot = config.getInt("gui." + s + ".slot");
                final Lootbox l = valueOf(is);
                if(l != null) guiLootboxes.put(slot, l);
                gi.setItem(slot, is);
            }
        }
        for(int i = 0; i < size; i++) {
            item = gi.getItem(i);
            if(item == null) gi.setItem(i, background);
        }

        final ConfigurationSection c = a.getConfigurationSection("lootboxes.started");
        if(c == null) {
            for(int i : guiLootboxes.keySet()) {
                started.put(guiLootboxes.get(i), System.currentTimeMillis());
            }
        } else {
            for(String s : c.getKeys(false)) {
                final ConfigurationSection K = a.getConfigurationSection("lootboxes.started." + s);
                if(K != null) {
                    for(String k : K.getKeys(false)) {
                        started.put(getLootbox(k), a.getLong("lootboxes.started." + s));
                    }
                }
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (lootboxes != null ? lootboxes.size() : 0) + " lootboxes &e(took " + (System.currentTimeMillis()-sc) + "ms)");
    }
    public void unload() {
        for(Lootbox l : started.keySet()) {
            otherdata.set("lootboxes.started." + l.getIdentifier(), started.get(l));
        }
        saveOtherData();
        for(Player p : new ArrayList<>(viewing)) p.closeInventory();
        lootboxes = null;
    }

    public void viewLootbox(Player player) {
        if(hasPermission(player, "RandomPackage.lootbox", true)) {
            player.closeInventory();
            final int size = gui.getSize();
            final long time = System.currentTimeMillis();
            player.openInventory(Bukkit.createInventory(player, gui.getType(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
            }
            final List<String> a = colorizeListString(config.getStringList("lores.available")), e = colorizeListString(config.getStringList("lores.expired")), p = colorizeListString(config.getStringList("lores.preview"));
            for(int i : guiLootboxes.keySet()) {
                final Lootbox l = guiLootboxes.get(i);
                final long L = started.get(l), ex = L+l.getAvailableFor()*1000;
                final String n = l.getName();
                item = top.getItem(i); itemMeta = item.getItemMeta(); lore.clear();
                lore.addAll(itemMeta.getLore());
                for(String s : time < ex ? a : e) {
                    lore.add(s.replace("{NAME}", n).replace("{TIME}", getRemainingTime(ex-time)));
                }
                lore.addAll(p);
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
            }
            viewing.add(player);
            player.updateInventory();
        }
    }
    public boolean isAvailable(Lootbox lootbox) {
        return started.containsKey(lootbox) && started.get(lootbox)+(lootbox.getAvailableFor()*1000)-System.currentTimeMillis() > 0;
    }
    public void tryClaiming(Player player, Lootbox lootbox) {
        final String y = lootbox.getIdentifier();
        final HashMap<String, Integer> L = RPPlayer.get(player.getUniqueId()).getUnclaimedLootboxes();
        if(L.getOrDefault(y, 0) > 0) {
            if(isAvailable(lootbox)) {
                if(L.get(y) == 1) {
                    L.remove(y);
                } else {
                    L.put(y, L.get(y)-1);
                }
                giveItem(player, lootbox.getItem());
            }
        } else {
            sendStringListMessage(player, config.getStringList("messages.unlock"), null);
        }
        player.closeInventory();
    }
    public void openLootbox(Player player, Lootbox lootbox) {
        player.closeInventory();
        final int size = lootbox.getGuiSize();
        player.openInventory(Bukkit.createInventory(player, size, lootbox.getGuiTitle()));
        final List<String> format = lootbox.getGuiFormat();
        final ItemStack background = lootbox.getBackground();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final List<Integer> countdownSlots = new ArrayList<>(), lootSlots = new ArrayList<>(), bonusSlots = new ArrayList<>();

        final List<ItemStack> bonus = lootbox.getAllRewards(LootboxRewardType.BONUS);
        List<ItemStack> regular = new ArrayList<>(lootbox.getAllRewards(LootboxRewardType.REGULAR));
        regular.addAll(lootbox.getAllRewards(LootboxRewardType.JACKPOT));

        redeeming.put(player, lootbox);
        for(int i = 0; i < format.size(); i++) {
            final String L = format.get(i);
            for(int p = 0; p < L.length(); p++) {
                final int slot = i*9+p;
                final String s = L.substring(p, p+1);
                switch (s) {
                    case "X":
                        top.setItem(slot, background);
                        break;
                    case "C":
                        item = background.clone();
                        item.setAmount(countdownStart);
                        top.setItem(slot, item);
                        countdownSlots.add(slot);
                        break;
                    case "B":
                        int a = random.nextInt(bonus.size());
                        if(a >= 0) {
                            final ItemStack b = bonus.get(a);
                            top.setItem(slot, b);
                            bonus.remove(b);
                            bonusSlots.add(slot);
                        }
                        break;
                    case "L":
                        a = random.nextInt(regular.size());
                        if(a >= 0) {
                            final ItemStack r = regular.get(a);
                            top.setItem(slot, r);
                            regular.remove(r);
                            lootSlots.add(slot);
                        }
                        break;
                    default: break;
                }
            }
        }
        tasks.put(player, new ArrayList<>());
        tasks.get(player).add(scheduler.scheduleSyncDelayedTask(randompackage, () -> startCountdown(player, top, countdownSlots, lootSlots, bonusSlots, background), 10));
        player.updateInventory();
    }
    private void startCountdown(Player player, Inventory top, List<Integer> countdownSlots, List<Integer> lootSlots, List<Integer> bonusSlots, ItemStack background) {
        final Lootbox l = redeeming.get(player);
        final ItemStack air = new ItemStack(Material.AIR);
        final List<Integer> T = tasks.get(player);
        List<ItemStack> L = new ArrayList<>(l.getAllRewards(LootboxRewardType.REGULAR));
        L.addAll(l.getAllRewards(LootboxRewardType.JACKPOT));

        for(int i = 1; i <= countdownStart; i++) {
            final int k = i;
            T.add(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                item = background.clone();
                item.setAmount(countdownStart-k);
                for(int c : countdownSlots) {
                    top.setItem(c, item);
                }
                if(k == countdownStart) {
                    for(int n = 0; n < top.getSize(); n++) {
                        if(!lootSlots.contains(n) && !bonusSlots.contains(n)) {
                            top.setItem(n, air);
                        }
                    }
                } else {
                    for(int z = 0; z <= 18; z += 3) {
                        T.add(scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                            final List<ItemStack> loot = new ArrayList<>(L);
                            for(int s : lootSlots) {
                                final ItemStack r = loot.get(random.nextInt(loot.size()));
                                loot.remove(r);
                                top.setItem(s, r);
                            }
                        }, z));
                    }
                }
                player.updateInventory();
            }, 20*i));
        }
    }
    public void previewLootbox(Player player, Lootbox lootbox) {
        if(hasPermission(player, "RandomPackage.lootbox.preview", true)) {
            player.closeInventory();
            final List<ItemStack> items = lootbox.getAllRewards();
            final int s = ((items.size()+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, s, lootbox.getPreviewTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            for(ItemStack is : items) top.setItem(top.firstEmpty(), is);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String t = event.getView().getTitle();
        final Lootbox l = valueOf(t), L = l == null ? valueof(t) : null;
        if(redeeming.containsKey(player)) {
            event.setCancelled(true);
            player.updateInventory();
        } else if(viewing.contains(player) || l != null || L != null) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;
            if(t.equals(gui.getTitle())) {
                final Lootbox lootbox = guiLootboxes.getOrDefault(r, null);
                final String click = event.getClick().name();
                if(lootbox != null) {
                    if(click.contains("LEFT")) tryClaiming(player, lootbox);
                    else if(click.contains("RIGHT")) previewLootbox(player, lootbox);
                }
            } else if(L != null) {
                player.closeInventory();
                sendStringListMessage(player, config.getStringList("messages.unlock"), null);
            } else {
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack i = event.getItem();
        final Lootbox l = valueOf(i);
        if(l != null && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);
            player.updateInventory();
            removeItem(player, i ,1);
            openLootbox(player, l);
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        viewing.remove(player);
        if(redeeming.containsKey(player)) {
            final Inventory i = event.getInventory();
            final Lootbox l = redeeming.get(player);
            final ItemStack background = l.getBackground();
            final List<ItemStack> rewards = new ArrayList<>();
            for(ItemStack is : i.getContents()) {
                if(is != null && !is.getType().equals(Material.AIR) && !is.isSimilar(background)) {
                    rewards.add(is);
                    giveItem(player, is);
                }
            }
            final String p = player.getName(), lb = l.getName();
            for(String s : opened) {
                if(s.equals("{REWARDS}")) {
                    for(ItemStack is : rewards) {
                        final String amount = Integer.toString(is.getAmount()), itemname = is.hasItemMeta() && is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : toMaterial(is.getType().name(), false);
                        for(String m : rewardFormat) {
                            Bukkit.broadcastMessage(m.replace("{AMOUNT}", amount).replace("{ITEM_NAME}", itemname));
                        }
                    }
                } else {
                    Bukkit.broadcastMessage(s.replace("{PLAYER}", p).replace("{NAME}", lb));
                }
            }
            redeeming.remove(player);
            for(int task : tasks.get(player)) {
                scheduler.cancelTask(task);
            }
            tasks.remove(player);
        }
    }

    public Lootbox valueOf(String guiTitle) {
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values()) {
                if(l.getGuiTitle().equals(guiTitle)) {
                    return l;
                }
            }
        }
        return null;
    }
    public Lootbox valueof(String previewTitle) {
        if(lootboxes != null) {
            previewTitle = ChatColor.stripColor(previewTitle);
            for(Lootbox l : lootboxes.values()) {
                if(ChatColor.stripColor(l.getPreviewTitle()).equals(previewTitle)) {
                    return l;
                }
            }
        }
        return null;
    }
    public Lootbox valueOf(ItemStack is) {
        if(lootboxes != null && is != null && is.hasItemMeta()) {
            for(Lootbox l : lootboxes.values()) {
                if(l.getItem().isSimilar(is)) {
                    return l;
                }
            }
        }
        return null;
    }
    public Lootbox valueOf(int priority) {
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values()) {
                if(l.getPriority() == priority) {
                    return l;
                }
            }
        }
        return null;
    }
    public Lootbox latest() {
        int p = 0;
        Lootbox lo = null;
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values()) {
                final int P = l.getPriority();
                if(lo == null || P > p) {
                    p = P;
                    lo = l;
                }
            }
        }
        return lo;
    }
}
