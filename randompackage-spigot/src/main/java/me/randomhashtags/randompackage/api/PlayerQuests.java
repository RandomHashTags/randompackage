package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.file.FilePlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.attribute.IncreasePQuest;
import me.randomhashtags.randompackage.attributesys.EventAttributeListener;
import me.randomhashtags.randompackage.attributesys.EventExecutor;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.PlayerQuestData;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.mob.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.perms.PlayerQuestPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum PlayerQuests implements RPFeatureSpigot, EventExecutor, CommandExecutor, EventAttributeListener, Listener {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory gui, shop;
    private ItemStack returnToQuests, active, locked, background, claim, claimed;
    public List<Integer> questSlots;
    private int questMasterShopSlot;
    private HashMap<Integer, ItemStack> shop_items;
    private HashMap<Integer, Integer> token_cost;
    private int returnToQuestsSlot;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.PLAYER_QUEST;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            view(player);
        } else {
            final String argument = args[0];
            if(argument.equals("shop")) {
                viewShop(player);
            } else if(argument.equals("reroll") && hasPermission(player, PlayerQuestPermission.COMMAND_REROLL, true)) {
                FileRPPlayer.get(player.getUniqueId()).getPlayerQuestData().setQuests(null);
            }
        }
        return true;
    }

    @Override
    public void load() {
        save("player quests", "_settings.yml");

        new IncreasePQuest().load();
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "player quests", "_settings.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        shop = new UInventory(null, config.getInt("shop.size"), colorize(config.getString("shop.title")));
        background = createItemStack(config, "shop.background");
        returnToQuests = createItemStack(config, "shop.return to quests");
        returnToQuestsSlot = config.getInt("shop.return to quests.slot");
        active = createItemStack(config, "gui.active");
        claim = createItemStack(config, "gui.claim");
        claimed = createItemStack(config, "gui.claimed");
        locked = createItemStack(config, "gui.locked");

        shop_items = new HashMap<>();
        token_cost = new HashMap<>();

        questMasterShopSlot = config.getInt("gui.quest master shop.slot");
        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("quest slots") && !s.equals("active") && !s.equals("claim") && !s.equals("claimed") && !s.equals("locked")) {
                gi.setItem(config.getInt("gui." + s + ".slot"), createItemStack(config, "gui." + s));
            }
        }

        int SLOT = config.getInt("shop.default settings.starting slot");
        final List<String> addedLore = colorizeListString(config.getStringList("shop.added lore"));
        final Inventory shopInv = shop.getInventory();
        final int defaultCost = config.getInt("shop.default settings.cost");
        for(String s : config.getConfigurationSection("shop").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background") && !s.equals("added lore") && !s.equals("default settings")) {
                final boolean returnToQuests = s.equals("return to quests");
                final boolean d = !returnToQuests && config.get("shop." + s + ".slot") == null;
                final int slot = d ? SLOT : config.getInt("shop." + s + ".slot"), cost = config.getInt("shop." + s + ".cost", defaultCost);
                if(d) {
                    SLOT++;
                }
                final ItemStack r = createItemStack(config, "shop." + s);
                if(r != null && !r.getType().equals(Material.AIR)) {
                    final ItemStack item = r.clone();
                    if(!returnToQuests) {
                        final ItemMeta itemMeta = item.getItemMeta();
                        final List<String> lore = new ArrayList<>();
                        if(itemMeta.hasLore()) {
                            lore.addAll(itemMeta.getLore());
                        }
                        for(String string : addedLore) {
                            lore.add(string.replace("{COST}", Integer.toString(cost)));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        shop_items.put(slot, r);
                        token_cost.put(slot, cost);
                    }
                    shopInv.setItem(slot, item);
                }
            }
        }
        for(int i = 0; i < shop.getSize(); i++) {
            if(shopInv.getItem(i) == null && i < SLOT) {
                shopInv.setItem(i, background);
            }
        }

        questSlots = new ArrayList<>();
        for(String s : config.getString("gui.quest slots").replace(" ", "").split(",")) {
            questSlots.add(Integer.parseInt(s));
        }

        if(!OTHER_YML.getBoolean("saved default player quests")) {
            generateDefaultPlayerQuests();
            OTHER_YML.set("saved default player quests", true);
            saveOtherData();
        }
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "player quests")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FilePlayerQuest(f);
            }
        }
    }
    @Override
    public void unload() {
    }

    public ActivePlayerQuest valueOf(Player player, ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final String name = is.getItemMeta().getDisplayName();
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final HashMap<PlayerQuest, ActivePlayerQuest> quests = pdata.getPlayerQuestData().getQuests();
            if(!quests.isEmpty()) {
                for(ActivePlayerQuest quest : quests.values()) {
                    final String targetName = (quest.isCompleted() ? quest.hasClaimedRewards() ? claimed : claim : active).getItemMeta().getDisplayName();
                    if(targetName.replace("{NAME}", getLocalizedName(quest.getQuest())).equals(name)) {
                        return quest;
                    }
                }
            }
        }
        return null;
    }
    private ItemStack getStatus(long time, ActivePlayerQuest a, List<String> available, List<String> completed, List<String> claimed) {
        final PlayerQuest quest = a.getQuest();
        final boolean isCompleted = a.isCompleted(), hasClaimed = a.hasClaimedRewards(), expired = a.isExpired();
        final String completion = getCompletion(quest), p = Double.toString(round(a.getProgress(), 2)), expiration = getRemainingTime(a.getExpirationTime()-time);
        final ItemStack item = (isCompleted ? hasClaimed ? this.claimed : claim : active).clone();
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", getLocalizedName(quest)));
        final List<String> lore = new ArrayList<>(), questLore = quest.getLore(), rewards = quest.getRewards();
        for(String string : itemMeta.getLore()) {
            if(string.equals("{LORE}")) {
                lore.addAll(questLore);
            } else if(string.contains("{REWARDS}")) {
                for(String r : rewards) {
                    lore.add(string.replace("{REWARDS}", r.split(":")[1]));
                }
            } else if(string.equals("{STATUS}")) {
                final List<String> l = hasClaimed ? claimed : isCompleted ? completed : !expired ? available : null;
                if(l != null) {
                    for(String e : l) {
                        lore.add(e.replace("{TIME}", expiration));
                    }
                }
            } else {
                lore.add(string.replace("{COMPLETION}", completion).replace("{PROGRESS}", p));
            }
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    private String getCompletion(PlayerQuest quest) {
        String completion = quest.getCompletion();
        try {
            completion = formatDouble(Double.parseDouble(completion)).split("E")[0];
        } catch (Exception ignored) {
        }
        return completion;
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, PlayerQuestPermission.VIEW, true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());

            final long time = System.currentTimeMillis();
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final PlayerQuestData data = pdata.getPlayerQuestData();
            final HashMap<PlayerQuest, ActivePlayerQuest> activeQuests = data.getQuests();
            final int size = activeQuests != null ? activeQuests.size() : 0;
            final Object[] activeQuestsArray = activeQuests.values().toArray();
            final List<String> available = getStringList(config, "status.available"), completed = getStringList(config, "status.completed"), claimed = getStringList(config, "status.claimed");
            final String tokens = formatInt(data.getTokens().intValue());
            int q = 0;
            for(int i = 0; i < gui.getSize(); i++) {
                ItemStack item = top.getItem(i);
                ItemMeta itemMeta = null;
                if(questSlots.contains(i)) {
                    final boolean isActive = q < size;
                    item = (isActive ? active : locked).clone();
                    itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(i+1)));
                    }
                    if(itemMeta.hasLore()) {
                        lore.addAll(itemMeta.getLore());
                    }
                    if(isActive) {
                        item = getStatus(time, (ActivePlayerQuest) activeQuestsArray[q], available, completed, claimed);
                        itemMeta = item.getItemMeta();
                        q++;
                    } else {
                        itemMeta.setLore(lore);
                    }
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                } else if(item != null && !item.getType().equals(Material.AIR)) {
                    itemMeta = item.getItemMeta();
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TOKENS}", tokens));
                    }
                    if(itemMeta.hasLore()) {
                        final List<String> lore = new ArrayList<>();
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{TOKENS}", tokens));
                        }
                        itemMeta.setLore(lore);
                    }
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                }
            }
            player.updateInventory();
        }
    }
    public void viewShop(@NotNull Player player) {
        if(hasPermission(player, PlayerQuestPermission.VIEW_SHOP, true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, shop.getSize(), shop.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(shop.getInventory().getContents());

            final String tokens = formatInt(FileRPPlayer.get(player.getUniqueId()).getPlayerQuestData().getTokens().intValue());
            for(int i = 0; i < top.getSize(); i++) {
                final ItemStack item = top.getItem(i);
                if(item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{TOKENS}", tokens));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                }
            }
            player.updateInventory();
        }
    }

    @NotNull
    private ItemStack getReturnToQuests(@NotNull Player player) {
        final String t = formatInt(FileRPPlayer.get(player.getUniqueId()).getPlayerQuestData().getTokens().intValue());
        final ItemStack item = returnToQuests.clone();
        final ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta != null) {
            final List<String> lore = new ArrayList<>(), itemLore = itemMeta.getLore();
            if(itemLore != null) {
                for(String string : itemLore) {
                    lore.add(string.replace("{TOKENS}", t));
                }
            }
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        return item;
    }
    private void updateReturnToQuests(@NotNull Player player) {
        final ItemStack is = getReturnToQuests(player);
        player.getOpenInventory().getTopInventory().setItem(returnToQuestsSlot, is);
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final String title = event.getView().getTitle(), shop_title = shop.getTitle();
        if(title.equals(gui.getTitle()) || title.equals(shop_title)) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            final boolean inShop = title.equals(shop_title);
            final int slot = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(!inShop) {
                if(questSlots.contains(slot)) {
                    final ActivePlayerQuest active = valueOf(player, c);
                    if(active != null) {
                        if(!active.isCompleted()) {
                            sendStringListMessage(player, getStringList(config, "messages.not completed"), null);
                        } else if(!active.hasClaimedRewards()) {
                            final PlayerQuest quest = active.getQuest();
                            final List<String> claimed = getStringList(config, "status.claimed");
                            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                            final PlayerQuestData data = pdata.getPlayerQuestData();
                            active.setHasClaimedRewards(true);
                            for(String b : quest.getRewards()) {
                                if(b.startsWith("questtokens=")) {
                                    data.setTokens(data.getTokens().add(BigInteger.valueOf(Integer.parseInt(b.split("=")[1].split(":")[0]))));
                                } else {
                                    giveItem(player, createItemStack(null, b.split(":")[0]));
                                }
                                event.setCurrentItem(getStatus(System.currentTimeMillis(), active, null, null, claimed));
                            }
                        } else {
                            sendStringListMessage(player, getStringList(config, "messages.already claimed"), null);
                        }
                    }
                } else if(slot == questMasterShopSlot) {
                    viewShop(player);
                }
            } else if(shop_items.containsKey(slot)) {
                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                final PlayerQuestData data = pdata.getPlayerQuestData();
                final BigInteger current = data.getTokens();
                final int cost = token_cost.get(slot), currentInt = current.intValue();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TOKENS}", formatInt(currentInt));
                replacements.put("{COST}", Integer.toString(cost));
                final boolean isEnough = currentInt >= cost;
                final String msg = isEnough ? "purchase" : "not enough tokens";
                if(isEnough) {
                    final BigInteger newAmount = current.subtract(BigInteger.valueOf(cost));
                    data.setTokens(newAmount);
                    giveItem(player, shop_items.get(slot));
                    updateReturnToQuests(player);
                    replacements.put("{TOKENS}", formatInt(newAmount.intValue()));
                }
                sendStringListMessage(player, getStringList(config, "messages." + msg), replacements);
            } else if(slot == returnToQuestsSlot) {
                view(player);
            }
            player.updateInventory();
        }
    }

    private void triggerPlayerQuests(@NotNull Event event, @NotNull Player player) {
        final HashMap<String, Entity> entities = getEntities(event);
        final Collection<ActivePlayerQuest> quests = FileRPPlayer.get(player.getUniqueId()).getPlayerQuestData().getQuests().values();
        final String[] replacements = getReplacements(event);
        for(ActivePlayerQuest quest : quests) {
            trigger(event, entities, quest.getQuest().getTrigger(), replacements);
        }
    }
    @EventHandler
    private void fallenHeroSlainEvent(FallenHeroSlainEvent event) {
        final LivingEntity l = event.killer;
        if(l instanceof Player) {
            triggerPlayerQuests(event, (Player) l);
        }
    }

    public void called(@NotNull Event event) {
        final HashMap<String, Entity> entities = getEntities(event);
        final Player player = entities.containsKey("Player") ? (Player) entities.get("Player") : null;
        if(player != null) {
            final String[] replacements = getReplacements(event);
            final Collection<ActivePlayerQuest> a = FileRPPlayer.get(player.getUniqueId()).getPlayerQuestData().getQuests().values();
            for(ActivePlayerQuest quest : a) {
                trigger(event, entities, quest.getQuest().getTrigger(), replacements);
            }
        }
    }
}
