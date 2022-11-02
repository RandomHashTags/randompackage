package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Lootbox;
import me.randomhashtags.randompackage.addon.file.FileLootbox;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.addon.enums.LootboxRewardType;
import me.randomhashtags.randompackage.perms.LootboxPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Lootboxes implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null) {
            viewLootbox(player);
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("lootboxes", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "lootboxes", "_settings.yml"));

        this.started = new HashMap<>();
        countdownStart = config.getInt("settings.countdown start");
        opened = getStringList(config, "messages.opened");
        rewardFormat = getStringList(config, "messages.reward format");

        guiLootboxes = new HashMap<>();
        redeeming = new HashMap<>();
        viewing = new ArrayList<>();
        tasks = new HashMap<>();

        final String title = colorize(config.getString("gui.title")), type = config.getString("gui.type");
        final int size = config.getInt("gui.size");
        if(type != null) {
            final InventoryType i = InventoryType.valueOf(type);
            gui = new UInventory(null, i, title);
        } else {
            gui = new UInventory(null, size, title);
        }
        background = createItemStack(config, "gui.background");

        if(!OTHER_YML.getBoolean("saved default lootboxes")) {
            generateDefaultLootboxes();
            OTHER_YML.set("saved default lootboxes", true);
            saveOtherData();
        }
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "lootboxes")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileLootbox(f);
            }
        }

        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("type") && !s.equals("size") && !s.equals("background")) {
                final ItemStack is = createItemStack(config, "gui." + s);
                final int slot = config.getInt("gui." + s + ".slot");
                final Lootbox l = valueOfLootbox(is);
                if(l != null) {
                    guiLootboxes.put(slot, l);
                }
                gi.setItem(slot, is);
            }
        }
        for(int i = 0; i < size; i++) {
            final ItemStack item = gi.getItem(i);
            if(item == null) {
                gi.setItem(i, background);
            }
        }

        final ConfigurationSection section = OTHER_YML.getConfigurationSection("lootboxes.started");
        if(section == null) {
            for(Lootbox l : guiLootboxes.values()) {
                this.started.put(l, started);
            }
        } else {
            for(String s : section.getKeys(false)) {
                for(String id : getConfigurationSectionKeys(OTHER_YML, "lootboxes.started." + s, false)) {
                    this.started.put(getLootbox(id), OTHER_YML.getLong("lootboxes.started." + s));
                }
            }
        }
        sendConsoleDidLoadFeature(getAll(Feature.LOOTBOX).size() + " Lootboxes", started);
    }
    @Override
    public void unload() {
        for(Lootbox l : started.keySet()) {
            OTHER_YML.set("lootboxes.started." + l.getIdentifier(), started.get(l));
        }
        saveOtherData();
        for(Player player : new ArrayList<>(viewing)) {
            player.closeInventory();
        }
        unregister(Feature.LOOTBOX);
    }

    public void viewLootbox(@NotNull Player player) {
        if(hasPermission(player, LootboxPermission.VIEW, true)) {
            player.closeInventory();
            final int size = gui.getSize();
            final long time = System.currentTimeMillis();
            player.openInventory(Bukkit.createInventory(player, gui.getType(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                final ItemStack item = top.getItem(i);
            }
            final List<String> available = getStringList(config, "lores.available"), expired = getStringList(config, "lores.expired"), preview = getStringList(config, "lores.preview");
            for(int i : guiLootboxes.keySet()) {
                final Lootbox lootbox = guiLootboxes.get(i);
                final long startTime = started.get(lootbox), expirationTime = startTime+lootbox.getAvailableFor()*1000;
                final String n = lootbox.getName();
                final ItemStack item = top.getItem(i);
                if(item != null) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    if(itemMeta != null) {
                        final List<String> lore = itemMeta.getLore();
                        for(String s : time < expirationTime ? available : expired) {
                            lore.add(s.replace("{NAME}", n).replace("{TIME}", getRemainingTime(expirationTime-time)));
                        }
                        lore.addAll(preview);
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                    }
                }
            }
            viewing.add(player);
            player.updateInventory();
        }
    }
    public boolean isAvailable(@NotNull Lootbox lootbox) {
        return started.containsKey(lootbox) && started.get(lootbox)+(lootbox.getAvailableFor()*1000)-System.currentTimeMillis() > 0;
    }
    public void tryClaiming(@NotNull Player player, @NotNull Lootbox lootbox) {
        final String identifier = lootbox.getIdentifier();
        final HashMap<String, Integer> lootboxes = FileRPPlayer.get(player.getUniqueId()).getUnclaimedLootboxes();
        final int amount = lootboxes.getOrDefault(identifier, 0);
        if(amount > 0) {
            if(isAvailable(lootbox)) {
                if(amount == 1) {
                    lootboxes.remove(identifier);
                } else {
                    lootboxes.put(identifier, amount-1);
                }
                giveItem(player, lootbox.getItem());
            }
        } else {
            sendStringListMessage(player, getStringList(config, "messages.unlock"), null);
        }
        player.closeInventory();
    }
    public void openLootbox(@NotNull Player player, @NotNull Lootbox lootbox) {
        player.closeInventory();
        final int size = lootbox.getGuiSize();
        player.openInventory(Bukkit.createInventory(player, size, lootbox.getGuiTitle()));
        final List<String> guiFormat = lootbox.getGuiFormat();
        final ItemStack background = lootbox.getBackground();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final List<Integer> countdownSlots = new ArrayList<>(), lootSlots = new ArrayList<>(), bonusSlots = new ArrayList<>();

        final List<ItemStack> bonus = lootbox.getAllRewards(LootboxRewardType.BONUS);
        final List<ItemStack> regular = new ArrayList<>(lootbox.getAllRewards(LootboxRewardType.REGULAR));
        regular.addAll(lootbox.getAllRewards(LootboxRewardType.JACKPOT));

        redeeming.put(player, lootbox);
        for(int i = 0; i < guiFormat.size(); i++) {
            final String guiFormatString = guiFormat.get(i);
            for(int p = 0; p < guiFormatString.length(); p++) {
                final int slot = i*9 + p;
                final String formatCharacter = guiFormatString.substring(p, p+1);
                switch (formatCharacter) {
                    case "X":
                        top.setItem(slot, background);
                        break;
                    case "C":
                        final ItemStack item = background.clone();
                        item.setAmount(countdownStart);
                        top.setItem(slot, item);
                        countdownSlots.add(slot);
                        break;
                    case "B":
                        int rewardIndex = RANDOM.nextInt(bonus.size());
                        final ItemStack bonusItem = bonus.get(rewardIndex);
                        top.setItem(slot, bonusItem);
                        bonus.remove(bonusItem);
                        bonusSlots.add(slot);
                        break;
                    case "L":
                        rewardIndex = RANDOM.nextInt(regular.size());
                        final ItemStack rewardItem = regular.get(rewardIndex);
                        top.setItem(slot, rewardItem);
                        regular.remove(rewardItem);
                        lootSlots.add(slot);
                        break;
                    default:
                        break;
                }
            }
        }
        tasks.put(player, new ArrayList<>());
        tasks.get(player).add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> startCountdown(player, top, countdownSlots, lootSlots, bonusSlots, background), 10));
        player.updateInventory();
    }
    private void startCountdown(Player player, Inventory top, List<Integer> countdownSlots, List<Integer> lootSlots, List<Integer> bonusSlots, ItemStack background) {
        final Lootbox lootbox = redeeming.get(player);
        final ItemStack air = new ItemStack(Material.AIR);
        final List<Integer> playerTasks = tasks.get(player);
        final List<ItemStack> list = new ArrayList<>(lootbox.getAllRewards(LootboxRewardType.REGULAR));
        list.addAll(lootbox.getAllRewards(LootboxRewardType.JACKPOT));

        final ItemStack item = background.clone();
        for(int i = 1; i <= countdownStart; i++) {
            final int k = i;
            playerTasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                item.setAmount(countdownStart-k);
                for(int slot : countdownSlots) {
                    top.setItem(slot, item);
                }
                if(k == countdownStart) {
                    for(int slot = 0; slot < top.getSize(); slot++) {
                        if(!lootSlots.contains(slot) && !bonusSlots.contains(slot)) {
                            top.setItem(slot, air);
                        }
                    }
                } else {
                    for(int z = 0; z <= 18; z += 3) {
                        playerTasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                            final List<ItemStack> loot = new ArrayList<>(list);
                            for(int slot : lootSlots) {
                                final ItemStack randomLoot = loot.get(RANDOM.nextInt(loot.size()));
                                loot.remove(randomLoot);
                                top.setItem(slot, randomLoot);
                            }
                        }, z));
                    }
                }
                player.updateInventory();
            }, 20L * i));
        }
    }
    public void previewLootbox(@NotNull Player player, @NotNull Lootbox lootbox) {
        if(hasPermission(player, LootboxPermission.PREVIEW_LOOT, true)) {
            player.closeInventory();
            final List<ItemStack> items = lootbox.getAllRewards();
            final int size = ((items.size()+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, size, lootbox.getPreviewTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            for(ItemStack is : items) {
                top.setItem(top.firstEmpty(), is);
            }
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String title = event.getView().getTitle();
        final Lootbox targetLootbox = valueOfLootboxTitle(title), targetLootbox2 = targetLootbox == null ? valueOfLootboxPreviewTitle(title) : null;
        if(redeeming.containsKey(player)) {
            event.setCancelled(true);
            player.updateInventory();
        } else if(viewing.contains(player) || targetLootbox != null || targetLootbox2 != null) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack currentItem = event.getCurrentItem();
            final int rawSlot = event.getRawSlot();
            if(rawSlot < 0 || rawSlot >= top.getSize() || currentItem == null || currentItem.getType().equals(Material.AIR)) return;
            if(title.equals(gui.getTitle())) {
                final Lootbox lootbox = guiLootboxes.getOrDefault(rawSlot, null);
                final String click = event.getClick().name();
                if(lootbox != null) {
                    if(click.contains("LEFT")) tryClaiming(player, lootbox);
                    else if(click.contains("RIGHT")) previewLootbox(player, lootbox);
                }
            } else if(targetLootbox2 != null) {
                player.closeInventory();
                sendStringListMessage(player, getStringList(config, "messages.unlock"), null);
            } else {
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack i = event.getItem();
        final Lootbox l = valueOfLootbox(i);
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
            final String playerName = player.getName(), lootboxName = l.getName();
            for(String s : opened) {
                if(s.equals("{REWARDS}")) {
                    for(ItemStack is : rewards) {
                        final String amount = Integer.toString(is.getAmount()), itemname = is.hasItemMeta() && is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : toMaterial(is.getType().name(), false);
                        for(String m : rewardFormat) {
                            Bukkit.broadcastMessage(m.replace("{AMOUNT}", amount).replace("{ITEM_NAME}", itemname));
                        }
                    }
                } else {
                    Bukkit.broadcastMessage(s.replace("{PLAYER}", playerName).replace("{NAME}", lootboxName));
                }
            }
            redeeming.remove(player);
            if(tasks.containsKey(player)) {
                for(int task : tasks.get(player)) {
                    SCHEDULER.cancelTask(task);
                }
                tasks.remove(player);
            }
        }
    }

    @Nullable
    public Lootbox valueOfLootboxTitle(@NotNull String title) {
        for(Lootbox lootbox : getAllLootboxes().values()) {
            if(title.equals(lootbox.getGuiTitle())) {
                return lootbox;
            }
        }
        return null;
    }
    @Nullable
    public Lootbox valueOfLootboxPreviewTitle(@NotNull String title) {
        for(Lootbox lootbox : getAllLootboxes().values()) {
            if(title.equals(lootbox.getPreviewTitle())) {
                return lootbox;
            }
        }
        return null;
    }
    @Nullable
    public Lootbox valueOfLootboxPriority(int priority) {
        for(Lootbox l : getAllLootboxes().values()) {
            if(l.getPriority() == priority) {
                return l;
            }
        }
        return null;
    }
    public Lootbox latest() {
        int topPriority = 0;
        Lootbox lo = null;
        for(Lootbox l : getAllLootboxes().values()) {
            final int targetPriority = l.getPriority();
            if(lo == null || targetPriority > topPriority) {
                topPriority = targetPriority;
                lo = l;
            }
        }
        return lo;
    }
}
