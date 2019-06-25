package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.api.events.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.api.events.customenchant.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.api.events.customenchant.PlayerApplyCustomEnchantEvent;
import me.randomhashtags.randompackage.api.events.customenchant.RandomizationScrollUseEvent;
import me.randomhashtags.randompackage.api.events.envoy.PlayerClaimEnvoyCrateEvent;
import me.randomhashtags.randompackage.api.events.jackpot.JackpotPurchaseTicketsEvent;
import me.randomhashtags.randompackage.api.events.servercrates.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.api.events.shop.ShopPurchaseEvent;
import me.randomhashtags.randompackage.api.events.shop.ShopSellEvent;
import me.randomhashtags.randompackage.utils.EventAttributes;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import me.randomhashtags.randompackage.utils.classes.playerquests.PlayerQuest;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class PlayerQuests extends EventAttributes {

    private static PlayerQuests instance;
    public static PlayerQuests getPlayerQuests() {
        if(instance == null) instance = new PlayerQuests();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;

    private UInventory gui, shop;
    private ItemStack returnToQuests, active, locked, background, claim, claimed;
    public List<Integer> questSlots;
    private HashMap<Integer, ItemStack> shopitems;
    private HashMap<Integer, Integer> tokencost;

    private int returnToQuestsSlot;

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

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save(null, "player quests.yml");
        eventmanager.registerListeners(randompackage, this);
        isEnabled = true;

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
                if(r != null) {
                    item = r.copy();
                    if(!rtq) {
                        itemMeta = item.getItemMeta();
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
            final String[] q = new String[]{
                    "A_LITTLE_GRIND", "A_MEDIUM_GRIND",
                    "BIGGER_SPENDER", "BIGGEST_SPENDER",
                    "DISGUISED", "DUNGEON_RUNNER",
                    "ENDER_LORD",
                    "GAMBLER_I", "GAMBLER_II",
                    "HANGING_ON",
                    "HERO_DOMINATOR",
                    "HEROIC_ENVOY_LOOTER_II",
                    "ITEM_CUSTOMIZATION",
                    "LAST_NOOB_STANDING", "LAST_MASTER_STANDING",
                    "LEGENDARY_LOOTER",
                    "MASTER_KIT_LEVELING",
                    "MASTER_MINER",
                    "MOB_EXAMINER_II",
                    "NOVICE_MERCHANT",
                    "OUTPOST_DEFENDER",
                    "QUEST_MASTER",
                    "RANDOMIZER_II", "RANDOMIZER_III",
                    "RIGGED",
                    "SIMPLE_ENCHANTER",
                    "SKILL_BOOSTER_I", "SKILL_BOOSTER_III",
                    "SLAUGHTER_HOUSE_II", "SLAUGHTER_HOUSE_III",
                    "SOUL_COLLECTOR_I", "SOUL_ENCHANTER",
                    "THIRSTY",
                    "ULTIMATE_ENCHANTER",
                    "VERY_UNLUCKY",
                    "XP_BOOSTED_I",

            };
            for(String s : q) save("player quests", s + ".yml");
            otherdata.set("saved default player quests", true);
            saveOtherData();
        }

        for(File f : new File(rpd + separator + "player quests").listFiles()) {
            new PlayerQuest(f);
        }
        final TreeMap<String, PlayerQuest> q = PlayerQuest.quests;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (q != null ? q.size() : 0) + " Player Quests &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
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
        PlayerQuest.deleteAll();
        eventmanager.unregisterListeners(this);
    }

    public ActivePlayerQuest valueOf(Player player, ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final String n = is.getItemMeta().getDisplayName(), N = active.getItemMeta().getDisplayName();
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final HashMap<PlayerQuest, ActivePlayerQuest> q = pdata.getQuests();
            if(!q.isEmpty()) {
                for(ActivePlayerQuest a : q.values()) {
                    if(N.replace("{NAME}", a.getQuest().getName()).equals(n)) {
                        return a;
                    }
                }
            }
        }
        return null;
    }

    public void view(Player player) {
        if(hasPermission(player, "RandomPackage.playerquests.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());

            final long time = System.currentTimeMillis();
            final HashMap<PlayerQuest, ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests();
            final int size = a.size();
            final List<String> available = colorizeListString(config.getStringList("status.available")), completed = colorizeListString(config.getStringList("status.completed")), claimed = colorizeListString(config.getStringList("status.claimed"));
            int q = 0;
            for(int i = 0; i < gui.getSize(); i++) {
                if(questSlots.contains(i)) {
                    final boolean ac = q < size;
                    item = (ac ? active : locked).copy();
                    itemMeta = item.getItemMeta();
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(i+1)));
                    }
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                    if(ac) {
                        lore.clear();
                        final ActivePlayerQuest apq = (ActivePlayerQuest) a.values().toArray()[q];
                        final boolean isCompleted = apq.isCompleted(), hasClaimed = apq.hasClaimedRewards();

                        item = (hasClaimed ? this.claimed : isCompleted ? claim : active).clone();
                        itemMeta = item.getItemMeta();
                        if(itemMeta.hasDisplayName()) {
                            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(i+1)));
                        }
                        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());

                        final PlayerQuest Q = apq.getQuest();
                        final long expiration = apq.getStartedTime()+Q.getExpiration()*1000;
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", Q.getName()));
                        final String p = formatDouble(round(apq.getProgress(), 2));
                        String completion = Q.getCompletion();
                        try {
                            completion = formatDouble(Double.parseDouble(completion)).split("E")[0];
                        } catch(Exception e) {}
                        final List<String> rewards = Q.getRewards(), LORE = Q.getLore();
                        final List<String> L = new ArrayList<>();
                        for(String s : lore) {
                            if(s.equals("{LORE}")) {
                                L.addAll(LORE);
                            } else if(s.contains("{REWARDS}")) {
                                for(String r : rewards) {
                                    L.add(s.replace("{REWARDS}", r.split(":")[1]));
                                }
                            } else if(s.equals("{STATUS}")) {
                                if(hasClaimed) {
                                    L.addAll(claimed);
                                } else if(isCompleted) {
                                    L.addAll(completed);
                                } else if(time < expiration) {
                                    for(String e : available) {
                                        L.add(e.replace("{TIME}", getRemainingTime(expiration-time)));
                                    }
                                }
                            } else {
                                L.add(s.replace("{COMPLETION}", completion).replace("{PROGRESS}", p));
                            }
                        }
                        itemMeta.setLore(L);
                        q++;
                    } else {
                        itemMeta.setLore(lore);
                    }
                    lore.clear();
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


    @Listener
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final String t = event.getView().getTitle(), s = shop.getTitle();
            if(t.equals(gui.getTitle()) || t.equals(s)) {
                final Player player = (Player) event.getWhoClicked();
                event.setCancelled(true);
                final boolean inShop = t.equals(s);
                final int r = event.getRawSlot();
                final ItemStack c = event.getCurrentItem();
                if(!inShop && questSlots.contains(r)) {
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
                                }
                            } else {
                                sendStringListMessage(player, config.getStringList("messages.already claimed"), null);
                            }
                        } else {
                            sendStringListMessage(player, config.getStringList("messages.not completed"), null);
                        }
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
                if(s.startsWith("+")) {
                    quest.setProgress(quest.getProgress()+Double.parseDouble(s.split("\\+")[1]));
                    final double timer = q.getTimedCompletion();
                    if(timer > 0.00) {
                    } else if(quest.getProgress() >= Double.parseDouble(q.getCompletion())) {
                        quest.setCompleted(true);
                    }
                } else if(s.startsWith("-")) {
                    final double n = quest.getProgress()-Double.parseDouble(s.split("-")[1]);
                    if(n >= 0.00) {
                        quest.setProgress(n);
                    }
                }
            }
        }
    }

    @Listener
    private void entityDeathEvent(EntityDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener
    private void shopPurchaseEvent(ShopPurchaseEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener
    private void shopSellEvent(ShopSellEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener
    private void jackpotPurchaseTicketsEvent(JackpotPurchaseTicketsEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void serverCrateOpenEvent(ServerCrateOpenEvent event) {
        final Player player = event.player;
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if(player != null) {
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener
    private void fallenHeroSlainEvent(FallenHeroSlainEvent event) {
        final Living l = event.killer;
        if(l instanceof Player) {
            final Player player = (Player) l;
            final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
            for(ActivePlayerQuest quest : a) {
                doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
            }
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void randomizationScrollUseEvent(RandomizationScrollUseEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void playerClaimEnvoyCrateEvent(PlayerClaimEnvoyCrateEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @Listener
    private void playerApplyCustomEnchantEvent(PlayerApplyCustomEnchantEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void enchanterPurchaseEvent(EnchanterPurchaseEvent event) {
        final Player player = event.player;
        final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
        for(ActivePlayerQuest quest : a) {
            doCompletion(player, quest, executeAttributes(player, event, quest.getQuest().getTrigger()));
        }
    }
}
