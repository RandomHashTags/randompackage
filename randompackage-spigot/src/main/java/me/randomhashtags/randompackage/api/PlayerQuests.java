package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.PlayerQuest;
import me.randomhashtags.randompackage.utils.addons.FilePlayerQuest;
import me.randomhashtags.randompackage.events.*;
import me.randomhashtags.randompackage.events.customenchant.*;
import me.randomhashtags.randompackage.utils.EventAttributes;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class PlayerQuests extends EventAttributes implements CommandExecutor {
    private static PlayerQuests instance;
    public static PlayerQuests getPlayerQuests() {
        if(instance == null) instance = new PlayerQuests();
        return instance;
    }
    public YamlConfiguration config;

    private UInventory gui, shop;
    private ItemStack returnToQuests, active, locked, background, claim, claimed;
    public List<Integer> questSlots;
    private int questMasterShopSlot;
    private HashMap<Integer, ItemStack> shopitems;
    private HashMap<Integer, Integer> tokencost;

    private int returnToQuestsSlot;

    public String getIdentifier() { return "PLAYER_QUESTS"; }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            view(player);
        } else {
            final String a = args[0];
            if(a.equals("shop")) {
                viewShop(player);
            } else if(a.equals("reroll") && hasPermission(player, "RandomPackage.playerquests.reroll", true)) {
                RPPlayer.get(player.getUniqueId()).setQuests(null);
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "player quests.yml");

        config = YamlConfiguration.loadConfiguration(new File(rpd, "player quests.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        shop = new UInventory(null, config.getInt("shop.size"), ChatColor.translateAlternateColorCodes('&', config.getString("shop.title")));
        background = d(config, "shop.background");
        returnToQuests = d(config, "shop.return to quests");
        returnToQuestsSlot = config.getInt("shop.return to quests.slot");
        active = d(config, "gui.active");
        claim = d(config, "gui.claim");
        claimed = d(config, "gui.claimed");
        locked = d(config, "gui.locked");

        shopitems = new HashMap<>();
        tokencost = new HashMap<>();

        questMasterShopSlot = config.getInt("gui.quest master shop.slot");
        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("quest slots") && !s.equals("active") && !s.equals("claim") && !s.equals("claimed") && !s.equals("locked")) {
                gi.setItem(config.getInt("gui." + s + ".slot"), d(config, "gui." + s));
            }
        }

        int SLOT = config.getInt("shop.default settings.starting slot");
        final List<String> addedlore = colorizeListString(config.getStringList("shop.added lore"));
        final Inventory si = shop.getInventory();
        final int defaultCost = config.getInt("shop.default settings.cost");
        for(String s : config.getConfigurationSection("shop").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background") && !s.equals("added lore") && !s.equals("default settings")) {
                final boolean rtq = s.equals("return to quests");
                final boolean d = !rtq && config.get("shop." + s + ".slot") == null;
                final int slot = d ? SLOT : config.getInt("shop." + s + ".slot"), cost = config.getInt("shop." + s + ".cost", defaultCost);
                if(d) SLOT++;
                final ItemStack r = d(config, "shop." + s);
                if(r != null && !r.getType().equals(Material.AIR)) {
                    item = r.clone();
                    if(!rtq) {
                        itemMeta = item.getItemMeta(); lore.clear();
                        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                        for(String a : addedlore) lore.add(a.replace("{COST}", Integer.toString(cost)));
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        shopitems.put(slot, r);
                        tokencost.put(slot, cost);
                    }
                    si.setItem(slot, item);
                }
            }
        }
        for(int i = 0; i < shop.getSize(); i++) {
            if(si.getItem(i) == null && i < SLOT) {
                si.setItem(i, background);
            }
        }

        questSlots = new ArrayList<>();
        for(String s : config.getString("gui.quest slots").replace(" ", "").split(",")) {
            questSlots.add(Integer.parseInt(s));
        }

        if(!otherdata.getBoolean("saved default player quests")) {
            final String[] q = new String[] {
                    "A_LITTLE_GRIND", "A_MEDIUM_GRIND", "A_BIG_GRIND",
                    "BEGINNERS_LUCK",
                    "BIGGER_SPENDER", "BIGGEST_SPENDER",
                    "DEFINITELY_AFK",
                    "DISGUISED",
                    "DUNGEON_NOOB",
                    "DUNGEON_RUNNER",
                    "ELITE_ENCHANTER",
                    "ENDER_LORD",
                    "ENVOY_LOOTER_II",
                    "ENVOY_SUMMONER_III",
                    "EQUIPMENT_LOOTER",
                    "GAMBLER_I", "GAMBLER_II", "GAMBLER_III",
                    "HANGING_ON",
                    "HERO_DOMINATOR",
                    "HEROIC_ENCHANTER",
                    "HEROIC_ENVOY_LOOTER_II",
                    "ITEM_CUSTOMIZATION",
                    "KOTH_KILLER_II",
                    "LAST_NOOB_STANDING", "LAST_MASTER_STANDING",
                    "LEGENDARY_LOOTER",
                    "MASTER_KIT_LEVELING",
                    "MASTER_MINER",
                    "MOB_EXAMINER_II",
                    "NOVICE_ALCHEMIST",
                    "NOVICE_EXCAVATOR",
                    "NOVICE_MERCHANT", "SKILLED_MERCHANT",
                    "NOVICE_MINER",
                    "NOVICE_TINKERER",
                    "OUTPOST_DEFENDER",
                    "QUEST_MASTER",
                    "RANDOMIZER_II", "RANDOMIZER_III",
                    "RIGGED",
                    "SIMPLE_ENCHANTER",
                    "SIMPLE_LOOTER",
                    "SKILL_BOOSTER_I", "SKILL_BOOSTER_III",
                    "SLAUGHTER_HOUSE_I", "SLAUGHTER_HOUSE_II", "SLAUGHTER_HOUSE_III",
                    "SOUL_COLLECTOR_I", "SOUL_ENCHANTER",
                    "SPIDER_SLAYER",
                    "STRONGHOLD_LOOTER_I",
                    "THIRSTY",
                    "ULTIMATE_ENCHANTER",
                    "ULTIMATE_LOOTER",
                    "UNIQUE_ENCHANTER",
                    "VERY_UNLUCKY",
                    "XP_BOOSTED_I",
            };
            for(String s : q) save("player quests", s + ".yml");
            otherdata.set("saved default player quests", true);
            saveOtherData();
        }
        final File folder = new File(rpd + separator + "player quests");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                new FilePlayerQuest(f);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (playerquests != null ? playerquests.size() : 0) + " Player Quests &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        config = null;
        gui = null;
        shop = null;
        returnToQuests = null;
        active = null;
        claim = null;
        claimed = null;
        locked = null;
        background = null;
        questSlots = null;
        shopitems = null;
        tokencost = null;
        playerquests = null;
    }

    public ActivePlayerQuest valueOf(Player player, ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final String n = is.getItemMeta().getDisplayName();
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final HashMap<PlayerQuest, ActivePlayerQuest> q = pdata.getQuests();
            if(!q.isEmpty()) {
                for(ActivePlayerQuest a : q.values()) {
                    final String N = (a.isCompleted() ? a.hasClaimedRewards() ? claimed : claim : active).getItemMeta().getDisplayName();
                    if(N.replace("{NAME}", a.getQuest().getName()).equals(n)) {
                        return a;
                    }
                }
            }
        }
        return null;
    }
    private ItemStack getStatus(long time, ActivePlayerQuest a, List<String> available, List<String> completed, List<String> claimed) {
        final PlayerQuest pq = a.getQuest();
        final boolean isCompleted = a.isCompleted(), hasClaimed = a.hasClaimedRewards(), expired = a.isExpired();
        final String completion = getCompletion(pq), p = Double.toString(round(a.getProgress(), 2)), expiration = getRemainingTime(a.getExpirationTime()-time);
        item = (isCompleted ? hasClaimed ? this.claimed : claim : active).clone();
        itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", pq.getName()));
        final List<String> L = new ArrayList<>(), LORE = pq.getLore(), rewards = pq.getRewards();
        for(String s : itemMeta.getLore()) {
            if(s.equals("{LORE}")) {
                L.addAll(LORE);
            } else if(s.contains("{REWARDS}")) {
                for(String r : rewards) {
                    L.add(s.replace("{REWARDS}", r.split(":")[1]));
                }
            } else if(s.equals("{STATUS}")) {
                final List<String> l = hasClaimed ? claimed : isCompleted ? completed : !expired ? available : null;
                if(l != null) {
                    for(String e : l) {
                        L.add(e.replace("{TIME}", expiration));
                    }
                }
            } else {
                L.add(s.replace("{COMPLETION}", completion).replace("{PROGRESS}", p));
            }
        }
        itemMeta.setLore(L); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    private String getCompletion(PlayerQuest quest) {
        String completion = quest.getCompletion();
        try {
            completion = formatDouble(Double.parseDouble(completion)).split("E")[0];
        } catch(Exception e) {}
        return completion;
    }

    public void view(Player player) {
        if(hasPermission(player, "RandomPackage.playerquests.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());

            final long time = System.currentTimeMillis();
            final HashMap<PlayerQuest, ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests();
            final int size = a != null ? a.size() : 0;
            final List<String> available = colorizeListString(config.getStringList("status.available")), completed = colorizeListString(config.getStringList("status.completed")), claimed = colorizeListString(config.getStringList("status.claimed"));
            final String tokens = Integer.toString(RPPlayer.get(player.getUniqueId()).questTokens);
            int q = 0;
            for(int i = 0; i < gui.getSize(); i++) {
                item = top.getItem(i);
                if(questSlots.contains(i)) {
                    final boolean ac = q < size;
                    item = (ac ? active : locked).clone();
                    itemMeta = item.getItemMeta();
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(i+1)));
                    }
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                    if(ac) {
                        item = getStatus(time, (ActivePlayerQuest) a.values().toArray()[q], available, completed, claimed);
                        itemMeta = item.getItemMeta();
                        q++;
                    } else {
                        itemMeta.setLore(lore);
                    }
                    lore.clear();
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                } else if(item != null && !item.getType().equals(Material.AIR)) {
                    itemMeta = item.getItemMeta();
                    if(itemMeta.hasDisplayName()) itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TOKENS}", tokens));
                    if(itemMeta.hasLore()) {
                        lore.clear();
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{TOKENS}", tokens));
                        }
                        itemMeta.setLore(lore); lore.clear();
                    }
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                }
            }
            player.updateInventory();
        }
    }
    public void viewShop(Player player) {
        if(hasPermission(player, "RandomPackage.playerquests.view.shop", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, shop.getSize(), shop.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(shop.getInventory().getContents());

            final String t = formatLong(RPPlayer.get(player.getUniqueId()).questTokens);
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    itemMeta = item.getItemMeta(); lore.clear();
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{TOKENS}", t));
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                }
            }
            player.updateInventory();
        }
    }

    private ItemStack getReturnToQuests(Player player) {
        if(player != null) {
            final String t = formatLong(RPPlayer.get(player.getUniqueId()).questTokens);
            item = returnToQuests.clone(); itemMeta = item.getItemMeta(); lore.clear();
            for(String s : itemMeta.getLore()) lore.add(s.replace("{TOKENS}", t));
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            return item;
        }
        return null;
    }
    private void updateReturnToQuests(Player player) {
        final ItemStack is = getReturnToQuests(player);
        player.getOpenInventory().getTopInventory().setItem(returnToQuestsSlot, is);
        player.updateInventory();
    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final String t = event.getView().getTitle(), s = shop.getTitle();
            if(t.equals(gui.getTitle()) || t.equals(s)) {
                final Player player = (Player) event.getWhoClicked();
                event.setCancelled(true);
                final boolean inShop = t.equals(s);
                final int r = event.getRawSlot();
                final ItemStack c = event.getCurrentItem();
                if(!inShop) {
                    if(questSlots.contains(r)) {
                        final ActivePlayerQuest a = valueOf(player, c);
                        if(a != null) {
                            final PlayerQuest q = a.getQuest();
                            if(a.isCompleted()) {
                                if(!a.hasClaimedRewards()) {
                                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                                    a.setHasClaimedRewards(true);
                                    for(String b : q.getRewards()) {
                                        if(b.startsWith("questtokens=")) {
                                            pdata.questTokens += Integer.parseInt(b.split("=")[1].split(":")[0]);
                                        } else {
                                            giveItem(player, d(null, b.split(":")[0]));
                                        }
                                        event.setCurrentItem(getStatus(System.currentTimeMillis(), a, null, null, colorizeListString(config.getStringList("status.claimed"))));
                                    }
                                } else {
                                    sendStringListMessage(player, config.getStringList("messages.already claimed"), null);
                                }
                            } else {
                                sendStringListMessage(player, config.getStringList("messages.not completed"), null);
                            }
                        }
                    } else if(r == questMasterShopSlot) {
                        viewShop(player);
                    }
                } else if(shopitems.containsKey(r)) {
                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    final int cost = tokencost.get(r), current = pdata.questTokens;
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{TOKENS}", Integer.toString(current));
                    replacements.put("{COST}", Integer.toString(cost));
                    if(current >= cost) {
                        pdata.questTokens -= cost;
                        giveItem(player, shopitems.get(r));
                        updateReturnToQuests(player);
                        replacements.put("{TOKENS}", Integer.toString(pdata.questTokens));
                        sendStringListMessage(player, config.getStringList("messages.purchase"), replacements);
                    } else {
                        sendStringListMessage(player, config.getStringList("messages.not enough tokens"), replacements);
                    }
                } else if(r == returnToQuestsSlot) {
                    view(player);
                }
                player.updateInventory();
            }
        }
    }

    private void doCompletion(Player player, ActivePlayerQuest quest, String completion) {
        if(player != null && quest != null && completion != null && !quest.isCompleted()) {
            final PlayerQuest q = quest.getQuest();
            for(String s : completion.split("&&")) {
                try {
                    if(s.startsWith("+")) {
                        quest.setProgress(quest.getProgress()+Double.parseDouble(s.split("\\+")[1]));
                        final double timer = q.getTimedCompletion();
                        if(timer > 0.00) {
                        } else if(quest.getProgress() >= Double.parseDouble(q.getCompletion())) {
                            quest.setCompleted(true);
                            final PlayerQuestCompleteEvent e = new PlayerQuestCompleteEvent(player, quest);
                            pluginmanager.callEvent(e);
                            final HashMap<String, String> replacements = new HashMap<>();
                            replacements.put("{NAME}", q.getName());
                            sendStringListMessage(player, config.getStringList("messages.completed"), replacements);
                        }
                    } else if(s.startsWith("-")) {
                        final double n = quest.getProgress()-Double.parseDouble(s.split("-")[1]);
                        if(n >= 0.00) {
                            quest.setProgress(n);
                        }
                    }
                } catch(Exception e) {
                    System.out.println("[RandomPackage] PlayerQuest error details:");
                    System.out.println("player=" + player.getName() + ";quest=" + quest.getQuest().getIdentifier());
                    System.out.println("completion=" + completion);
                    System.out.println("s=" + s);
                    e.printStackTrace();
                }
            }
        }
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        if(player != null) {
            final String xp = Integer.toString(event.getDroppedExp());
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                String e = executeAttributes(player, event, quest.getQuest().getTrigger());
                doCompletion(player, quest, e != null ? e.replace("xp", xp) : null);
            }
        }
    }
    @EventHandler
    private void shopPurchaseEvent(ShopPurchaseEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @EventHandler
    private void shopSellEvent(ShopSellEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @EventHandler
    private void jackpotPurchaseTicketsEvent(JackpotPurchaseTicketsEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void serverCrateOpenEvent(ServerCrateOpenEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @EventHandler
    private void fallenHeroSlainEvent(FallenHeroSlainEvent event) {
        final LivingEntity l = event.killer;
        if(l instanceof Player) {
            final Player player = (Player) l;
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void randomizationScrollUseEvent(RandomizationScrollUseEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerClaimEnvoyCrateEvent(PlayerClaimEnvoyCrateEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @EventHandler
    private void playerApplyCustomEnchantEvent(PlayerApplyCustomEnchantEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void enchanterPurchaseEvent(EnchanterPurchaseEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void alchemistExchangeEvent(AlchemistExchangeEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void itemNameTagUseEvent(ItemNameTagUseEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void mysteryMobSpawnerOpenEvent(MysteryMobSpawnerOpenEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
}
