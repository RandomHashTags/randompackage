package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.addon.MonthlyCrate;
import me.randomhashtags.randompackage.addon.file.FileMonthlyCrate;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.MonthlyCrateData;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.perms.MonthlyCratePermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
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

public enum MonthlyCrates implements RPFeature, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory gui, categoryView;
    private ItemStack locked, alreadyClaimed, categoryViewBackground;
    public ItemStack mysteryCrate, heroicMysteryCrate, superiorMysteryCrate;
    private HashMap<Player, List<Integer>> regularRewardsLeft, bonusRewardsLeft, playerTimers;
    private HashMap<Integer, Integer> categories;
    private HashMap<Integer, UInventory> categoriez;

    @Override
    public String getIdentifier() {
        return "MONTHLY_CRATES";
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(args.length == 0 && player != null) {
            viewCrates(player);
        } else if(args.length == 2 && args[0].equals("reset") && hasPermission(player, MonthlyCratePermission.COMMAND_RESET, true)) {
            reset(player, Bukkit.getOfflinePlayer(args[1]));
        }
        return true;
    }

    @Override
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

        mysteryCrate = createItemStack(config, "items.mystery crate");
        heroicMysteryCrate = createItemStack(config, "items.heroic mystery crate");
        superiorMysteryCrate = createItemStack(config, "items.superior mystery crate");
        final ItemStack background = createItemStack(config, "gui.background");
        alreadyClaimed = createItemStack(config, "category view.already claimed");
        locked = createItemStack(config, "category view.locked");
        categoryViewBackground = createItemStack(config, "category view.background");

        if(!OTHER_YML.getBoolean("saved default monthly crates")) {
            generateDefaultMonthlyCrates();
            OTHER_YML.set("saved default monthly crates", true);
            saveOtherData();
        }

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
                gi.setItem(slot, createItemStack(config, "gui." + s));
            }
        }
        for(int i = 0; i < gui.getSize(); i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }

        GivedpItem.INSTANCE.items.put("mysterycrate", mysteryCrate);
        GivedpItem.INSTANCE.items.put("heroicmysterycrate", heroicMysteryCrate);
        GivedpItem.INSTANCE.items.put("superiormysterycrate", superiorMysteryCrate);
        GivedpItem.INSTANCE.items.put("superiorcrate", superiorMysteryCrate);

        final HashMap<Integer, HashMap<Integer, MonthlyCrate>> categorySlots = new HashMap<>();
        final HashMap<Integer, HashMap<Integer, ItemStack>> categoryItemStacks = new HashMap<>();
        for(File f : getFilesInFolder(folder)) {
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
            final ItemStack item = gi.getItem(i);
            if(categories.containsKey(i)) {
                final HashMap<Integer, MonthlyCrate> targetCategorySlots = categorySlots.get(categories.get(i));
                final ItemMeta itemMeta = item.getItemMeta();
                final List<String> lore = new ArrayList<>();
                if(itemMeta.hasLore()) {
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{CRATE}")) {
                            for(int targetSlot : targetCategorySlots.keySet()) {
                                lore.add(string.replace("{CRATE}", targetCategorySlots.get(targetSlot).getItem().getItemMeta().getDisplayName()));
                            }
                        } else {
                            lore.add(string);
                        }
                    }
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
            }
        }
        for(int i : categoryItemStacks.keySet()) {
            final HashMap<Integer, ItemStack> targetCategoryItemStacks = categoryItemStacks.get(i);
            final int size = targetCategoryItemStacks.size();
            categoriez.put(i, new UInventory(null, size % 9 == 0 ? size : (((size+9)/9)*9), categoriez.get(i).getTitle()));
            final Inventory inv = categoriez.get(i).getInventory();
            for(int targetSlot : targetCategoryItemStacks.keySet()) {
                inv.setItem(targetSlot, targetCategoryItemStacks.get(targetSlot));
            }
        }
        sendConsoleDidLoadFeature(getAll(Feature.MONTHLY_CRATE).size() + " Monthly Crates", started);
    }
    @Override
    public void unload() {
        for(Player player : new ArrayList<>(playerTimers.keySet())) {
            player.closeInventory();
        }
        unregister(Feature.MONTHLY_CRATE);
    }

    public void viewCrates(@NotNull Player player) {
        if(hasPermission(player, MonthlyCratePermission.VIEW, true)) {
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final MonthlyCrateData data = pdata.getMonthlyCrateData();
            final HashMap<String, Boolean> owned = data.getOwned();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            final String playerName = player.getName();
            for(int i = 0; i < top.getSize(); i++) {
                ItemStack item = top.getItem(i);
                if(item != null) {
                    final MonthlyCrate targetCrate = valueOfMonthlyCrate(item);
                    if(targetCrate != null) {
                        final String targetIdentifier = targetCrate.getIdentifier();
                        final boolean isOwned = owned.containsKey(targetIdentifier), hasPerm = player.hasPermission(MonthlyCratePermission.HAS_MC_PREFIX + targetIdentifier), notUnlocked = !isOwned && !hasPerm, isClaimed = owned.getOrDefault(targetIdentifier, false);
                        if(notUnlocked) {
                            item = locked.clone();
                        } else if(isClaimed) {
                            item = alreadyClaimed.clone();
                        }
                        check(playerName, item, targetCrate, notUnlocked || isClaimed);
                        top.setItem(i, item);
                    }
                }
            }
            player.updateInventory();
        }
    }
    private void check(String playerName, ItemStack is, MonthlyCrate crate, boolean unlocked) {
        if(is != null && is.hasItemMeta()) {
            final ItemStack crateItem = crate.getItem();
            final ItemMeta itemMeta = is.getItemMeta();
            if(itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", crateItem.getItemMeta().getDisplayName()));
            }
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                for(String string : (unlocked ? crateItem : locked.clone()).getItemMeta().getLore()) {
                    if(string.equals("{LORE}")) {
                        for(String crateLoreString : crateItem.getItemMeta().getLore()) {
                            if(!crateLoreString.contains("{UNLOCKED_BY}")) {
                                lore.add(crateLoreString);
                            }
                        }
                    } else {
                        lore.add(string.replace("{UNLOCKED_BY}", playerName));
                    }
                }
            }
            itemMeta.setLore(lore);
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
                final ItemStack item = top.getItem(i);
                final ItemMeta itemMeta = item.getItemMeta();
                final List<String> lore = new ArrayList<>();
                if(item.hasItemMeta()) {
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PATH}", identifier));
                    }
                    if(itemMeta.hasLore()) {
                        for(String string : itemMeta.getLore()) {
                            lore.add(string.replace("{PATH}", identifier));
                        }
                    }
                }
                itemMeta.setLore(lore);
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
            final ItemStack item = inv.getItem(i).clone();
            final ItemMeta itemMeta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                for(String s : itemMeta.getLore()) {
                    if(s.contains("{PATH}")) {
                        s = s.replace("{PATH}", p);
                    }
                    lore.add(s);
                }
                itemMeta.setLore(lore);
            }
            item.setItemMeta(itemMeta);
            top.setItem(i, item);
        }
        player.updateInventory();
    }
    private void exit(Player player, Inventory inv, MonthlyCrate crate) {
        stopTimers(player);
        final String playerName = player.getName();
        final ItemStack cmd = GivedpItem.INSTANCE.items.get("commandreward").clone();
        final List<Integer> regular = regularRewardsLeft.getOrDefault(player, null), bonus = bonusRewardsLeft.getOrDefault(player, null);
        if(regular != null) {
            final List<String> r = MonthlyCrate.REVEALED_REGULAR.getOrDefault(player, null);
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
            final List<String> r = MonthlyCrate.REVEALED_BONUS.getOrDefault(player, null);
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
        MonthlyCrate.REVEALED_REGULAR.remove(player);
        MonthlyCrate.REVEALED_BONUS.remove(player);
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
            final FileRPPlayer pdata = FileRPPlayer.get(target.getUniqueId());
            pdata.getMonthlyCrateData().getOwned().clear();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TARGET}", target.getName());
            sendStringListMessage(sender, getStringList(config, "messages.reset.success"), replacements);
        }
    }
    public void give(RPPlayer pdata, Player player, MonthlyCrate crate, boolean claimed) {
        final ItemStack item = crate.getItem();
        final ItemMeta itemMeta = item.getItemMeta();
        if(item.hasItemMeta()) {
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                final String name = player.getName();
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{UNLOCKED_BY}", name));
                }
            }
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        giveItem(player, item);
        if(claimed) {
            pdata.getMonthlyCrateData().getOwned().put(crate.getIdentifier(), true);
        }
    }
    public void viewCategory(Player player, int category) {
        if(categoriez.containsKey(category)) {
            final String playerName = player.getName();
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final HashMap<String, Boolean> owned = pdata.getMonthlyCrateData().getOwned();
            final UInventory i = categoriez.get(category);
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, i.getSize(), i.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(i.getInventory().getContents());
            for(int o = 0; o < top.getSize(); o++) {
                ItemStack item = top.getItem(o);
                if(item == null) {
                    top.setItem(o, categoryViewBackground);
                } else {
                    final MonthlyCrate crate = valueOfMonthlyCrate(item);
                    if(crate != null) {
                        final String identifier = crate.getIdentifier();
                        final boolean isOwned = owned.containsKey(identifier), isClaimed = owned.getOrDefault(identifier, false);
                        if(isClaimed) {
                            item = alreadyClaimed.clone();
                        } else if(!isOwned && !hasPermission(player, MonthlyCratePermission.HAS_MC_PREFIX + identifier, false)) {
                            item = locked.clone();
                        }
                        check(playerName, item, crate, isOwned);
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
                    final FileRPPlayer pdata = FileRPPlayer.get(uuid);
                    final MonthlyCrateData data = pdata.getMonthlyCrateData();
                    final HashMap<String, Boolean> owned = data.getOwned();
                    final boolean hasPerm = owned.containsKey(identifier) || player.hasPermission(MonthlyCratePermission.HAS_MC_PREFIX + identifier);
                    final boolean isClaimed = owned.getOrDefault(identifier, false);
                    if(!hasPerm) {
                        sendStringListMessage(player, getStringList(config, "messages.no access"), null);
                    } else if(!isClaimed) {
                        give(pdata, player, crate, true);
                    }
                    player.closeInventory();
                } else if(categories.containsKey(slot)) {
                    viewCategory(player, categories.get(slot));
                }
            } else {
                final int category = valueOfCategory(title);
                final MonthlyCrate monthlyCrate = category == -1 ? valueOfMonthlyCrate(title) : null;
                if(category != -1) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final MonthlyCrate crate = valueOfMonthlyCrate(category, slot);
                    if(crate != null) {
                        final String identifier = crate.getIdentifier();
                        final FileRPPlayer pdata = FileRPPlayer.get(uuid);
                        final MonthlyCrateData data = pdata.getMonthlyCrateData();
                        final HashMap<String, Boolean> owned = data.getOwned();
                        if(!owned.getOrDefault(identifier, false) && (owned.containsKey(identifier) || hasPermission(player, MonthlyCratePermission.HAS_MC_PREFIX + identifier, false))) {
                            give(pdata, player, crate, true);
                        }
                    }
                } else if(monthlyCrate != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    if(slot >= top.getSize()) {
                        return;
                    }
                    final List<Integer> regular = regularRewardsLeft.getOrDefault(player, null), bonus = bonusRewardsLeft.getOrDefault(player, null);
                    final Object slotObject = slot;
                    if(regular != null && regular.contains(slot)) {
                        top.setItem(slot, monthlyCrate.getRandomReward(player, MonthlyCrate.REVEALED_REGULAR.get(player), false));
                        regular.remove(slotObject);
                        if(regular.size() == 0 && bonus != null) {
                            bonusRewardsLeft.put(player, new ArrayList<>(monthlyCrate.getBonusRewardSlots()));
                            doAnimation(player, monthlyCrate);
                        }
                    } else if(bonus != null && (regular == null || regular.isEmpty()) && bonus.contains(slot)) {
                        top.setItem(slot, monthlyCrate.getRandomReward(player, MonthlyCrate.REVEALED_BONUS.get(player), false));
                        bonus.remove(slotObject);
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
                final ItemStack itemstack = GivedpItem.INSTANCE.valueOfRPItem("monthlycrate:" + id);
                final String playerName = player.getName();
                final ItemMeta itemMeta = itemstack.getItemMeta();
                if(itemMeta != null && itemMeta.hasLore()) {
                    final List<String> lore = new ArrayList<>();
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{UNLOCKED_BY}")) string = string.replace("{UNLOCKED_BY}", playerName);
                        lore.add(string);
                    }
                    itemMeta.setLore(lore);
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
        final Inventory inv = event.getInventory();
        final Player player = (Player) event.getPlayer();
        if(inv.getHolder() == player) {
            final MonthlyCrate crate = valueOfMonthlyCrate(event.getView().getTitle());
            if(crate != null) {
                exit(player, inv, crate);
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
