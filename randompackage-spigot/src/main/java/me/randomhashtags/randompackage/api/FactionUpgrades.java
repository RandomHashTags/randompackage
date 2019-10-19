package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;
import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.addon.obj.FactionUpgradeInfo;
import me.randomhashtags.randompackage.event.FactionUpgradeLevelupEvent;
import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.FileFactionUpgrade;
import me.randomhashtags.randompackage.util.addon.FileFactionUpgradeType;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class FactionUpgrades extends EventAttributes {
    private static FactionUpgrades instance;
    public static FactionUpgrades getFactionUpgrades() {
        if(instance == null) instance = new FactionUpgrades();
        return instance;
    }

    public YamlConfiguration config;

    private File fupgradesF;
    private YamlConfiguration fupgrades;

    private UInventory gui;
    private ItemStack background, locked;
    public ItemStack heroicFactionCrystal, factionCrystal;
    private List<String> aliases;

    private HashMap<String, HashMap<Location, Double>> cropGrowthRate;

    public static HashMap<String, List<FactionUpgradeInfo>> factionUpgrades;

    public String getIdentifier() { return "FACTION_UPGRADES"; }
    protected RPFeature getFeature() { return getFactionUpgrades(); }
    public void load() {
        if(hookedFactionsUUID()) {
            final long started = System.currentTimeMillis();
            save(null, "faction upgrades.yml");
            save("_Data", "faction upgrades.yml");

            if(!otherdata.getBoolean("saved default faction upgrades")) {
                final String[] a = new String[] {
                        "BOSS_MASTERY",
                        "CONQUEST_MASTERY",
                        "DUNGEON_LOOTER", "DUNGEON_MASTER", "DUNGEON_RUNNER",
                        "ENDER_FARMING", "ENHANCED_FLIGHT", "ESCAPE_ARTIST", "EXPLOSIVES_EXPERT",
                        "FACTION_POWER_BOOST", "FAST_ENDERPEARL",
                        "HEROIC_BOSS_MASTERY", "HEROIC_SOUL_MASTERY", "HEROIC_WELL_FED",
                        "HOME_ADVANTAGE",
                        "KIT_EVOLUTION",
                        "MAVERICK", "MAX_FACTION_SIZE", "MCMMO_MASTERY", "MONSTER_FARM",
                        "NATURAL_GROWTH",
                        "OUTPOST_CONTROL",
                        "SOUL_MASTERY",
                        "TP_MASTERY",
                        "WARP_MASTER", "WARZONE_CONTROL", "WELL_FED",
                        "XP_HARVEST",
                };
                for(String s : a) save("faction upgrades", s + ".yml");
                otherdata.set("saved default faction upgrades", true);
            }
            config = YamlConfiguration.loadConfiguration(new File(rpd, "faction upgrades.yml"));
            for(String s : config.getConfigurationSection("types").getKeys(false)) {
                new FileFactionUpgradeType(s);
            }
            gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
            final Inventory fi = gui.getInventory();
            final File folder = new File(rpd + separator + "faction upgrades");
            if(folder.exists()) {
                for(File f : folder.listFiles()) {
                    final FileFactionUpgrade fu = new FileFactionUpgrade(f);
                    fi.setItem(fu.getSlot(), fu.getItem());
                }
            }

            cropGrowthRate = new HashMap<>();

            fupgradesF = new File(rpd + separator + "_Data", "faction upgrades.yml");
            fupgrades = YamlConfiguration.loadConfiguration(fupgradesF);
            aliases = getPlugin.getConfig().getStringList("faction upgrades.cmds");
            heroicFactionCrystal = d(config, "items.heroic faction crystal");
            factionCrystal = d(config, "items.faction crystal");
            background = d(config, "gui.background");
            locked = d(config, "gui.locked");
            addGivedpCategory(Arrays.asList(factionCrystal, heroicFactionCrystal), UMaterial.DIAMOND_SWORD, "Faction Items", "Givedp: Faction Items");

            givedpitem.items.put("heroicfactioncrystal", heroicFactionCrystal);
            givedpitem.items.put("factioncrystal", factionCrystal);

            for(int i = 0; i < gui.getSize(); i++) {
                if(fi.getItem(i) == null) {
                    fi.setItem(i, background);
                }
            }

            loadBackup();
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + (factionupgrades != null ? factionupgrades.size() : 0) + " Faction Upgrades &e(took " + (System.currentTimeMillis()-started) + "ms)");
        } else {
            sendConsoleMessage("&6[RandomPackage] &cDidn't load FactionUpgrades due to no supported Faction plugin installed!");
            HandlerList.unregisterAll(instance);
            disable();
        }
    }
    public void unload() {
        backup();
        factionUpgrades = null;
        factionupgrades = null;
        factionupgradetypes = null;
    }

    private void backup() {
        if(isEnabled()) {
            for(String F : factionUpgrades.keySet()) {
                final List<FactionUpgradeInfo> f = factionUpgrades.get(F);
                fupgrades.set("factions." + F, null);
                for(FactionUpgradeInfo info : f) {
                    fupgrades.set("factions." + F + "." + info.getType().getIdentifier(), info.getLevel().asInt());
                }
            }
            try {
                fupgrades.save(fupgradesF);
                fupgradesF = new File(rpd + separator + "_Data", "faction upgrades.yml");
                fupgrades = YamlConfiguration.loadConfiguration(fupgradesF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadBackup() {
        final ConfigurationSection c = fupgrades.getConfigurationSection("factions");
        factionUpgrades = new HashMap<>();
        if(c != null) {
            for(String s : c.getKeys(false)) {
                final ConfigurationSection f = fupgrades.getConfigurationSection("factions." + s);
                final List<FactionUpgradeInfo> b = new ArrayList<>();
                for(String a : f.getKeys(false)) {
                    final FactionUpgrade u = getFactionUpgrade(a);
                    if(u != null) {
                        b.add(new FactionUpgradeInfo(u, u.getLevels().get(fupgrades.getInt("factions." + s + "." + a))));
                    }
                }
                factionUpgrades.put(s, b);
            }
        }
    }

    public FactionUpgradeInfo getFactionUpgradeInfo(FactionUpgrade upgrade, String faction) { return getFactionUpgradeInfo(upgrade, faction, getDefaultUpgradeInfo(upgrade)); }
    public FactionUpgradeInfo getFactionUpgradeInfo(FactionUpgrade upgrade, String faction, FactionUpgradeInfo def) {
        if(upgrade != null && faction != null && factionUpgrades.containsKey(faction)) {
            for(FactionUpgradeInfo info : factionUpgrades.get(faction)) {
                if(info.getType().equals(upgrade)) {
                    return info;
                }
            }
        }
        return def;
    }
    public FactionUpgradeInfo getDefaultUpgradeInfo(FactionUpgrade upgrade) {
        return new FactionUpgradeInfo(upgrade, upgrade.getLevels().get(0));
    }
    public void tryToUpgrade(Player player, FactionUpgrade upgrade) {
        final String faction = regions.getFactionTag(player.getUniqueId());
        final FactionUpgradeInfo info = getFactionUpgradeInfo(upgrade, faction);
        if(info != null) {
            final FactionUpgradeLevel level = info.getLevel();
            final int tier = level.asInt();
            if(tier >= upgrade.getMaxLevel()) return;
            final FactionUpgradeLevel nextLevel = upgrade.getLevels().get(tier+1);
            BigDecimal requiredCash = BigDecimal.ZERO, requiredSpawnerValue = BigDecimal.ZERO;
            ItemStack requiredItem = null;
            final HashMap<String, String> replacements = new HashMap<>();
            final List<FactionUpgradeInfo> upgrades = factionUpgrades.get(faction);
            for(String s : nextLevel.getCost()) {
                s = s.toLowerCase();
                if(s.startsWith("cash{")) {
                    final double amount = getRemainingDouble(s.split("\\{")[1]);
                    requiredCash = BigDecimal.valueOf(amount);
                    if(eco.getBalance(player) < amount) {
                        replacements.put("{COST}", formatDouble(requiredCash.doubleValue()).split("E")[0]);
                        sendStringListMessage(player, config.getStringList("messages.dont have enough cash"), replacements);
                        return;
                    }
                } else if(s.startsWith("item{")) {
                    final ItemStack a = givedpitem.valueOf(s.split("\\{")[1].split(";")[0]);
                    requiredItem = a;
                    a.setAmount(Integer.parseInt(s.split("\\{")[1].split("amount=")[1].split("}")[0]));
                    if(!player.getInventory().containsAtLeast(a, a.getAmount())) {
                        replacements.put("{ITEM}", s.split("};")[1]);
                        sendStringListMessage(player, config.getStringList("messages.dont have item"), replacements);
                        return;
                    }
                } else if(s.startsWith("spawnervalue{")) {
                    replacements.put("{COST}", formatBigDecimal(BigDecimal.valueOf(Double.parseDouble(s.split("\\{")[1].split("}")[0]))));
                    sendStringListMessage(player, config.getStringList("messages.dont have enough spawner value"), replacements);
                    return;
                } else if(s.startsWith("factionupgrade{")) {
                    final String p = s.split("\\{")[1].split("}")[0];
                    final FactionUpgrade target = getFactionUpgrade(p.split(":")[0]);
                    final int lvl = Integer.parseInt(p.split(":")[1].split("=")[1]);
                    if(!upgrades.contains(getFactionUpgradeInfo(target, faction))) {
                        final String di = ChatColor.stripColor(target.getItem().getItemMeta().getDisplayName());
                        replacements.put("{UPGRADE}", di + " " + toRoman(lvl));
                        sendStringListMessage(player, config.getStringList("messages.dont have f upgrade"), replacements);
                        return;
                    }
                }
            }
            final FactionUpgradeLevelupEvent e = new FactionUpgradeLevelupEvent(player, upgrade, tier);
            pluginmanager.callEvent(e);
            if(e.isCancelled()) return;
            if(!requiredCash.equals(BigDecimal.ZERO)) eco.withdrawPlayer(player, requiredCash.doubleValue());
            if(requiredItem != null) removeItem(player, requiredItem, requiredItem.getAmount());
            info.setLevel(nextLevel);
            final int slot = upgrade.getSlot();
            player.getOpenInventory().getTopInventory().setItem(slot, getUpgrade(faction, slot, ChatColor.translateAlternateColorCodes('&', config.getString("gui.tier")), ChatColor.translateAlternateColorCodes('&', config.getString("gui.locked.tier"))));
            player.updateInventory();
        }
    }
    private ItemStack getUpgrade(String faction, int slot, String W, String L) {
        final FactionUpgrade f = valueOfFactionUpgrade(slot);
        if(f != null) {
            final FactionUpgradeInfo info = getFactionUpgradeInfo(f, faction);
            if(info != null) {
                final HashMap<Integer, FactionUpgradeLevel> levels = f.getLevels();
                final FactionUpgradeLevel level = info.getLevel();
                final int tier = level.asInt(), max = f.getMaxLevel();
                final boolean isTierZero = tier == 0;
                item = f.getItem();
                itemMeta = item.getItemMeta(); lore.clear();
                final FactionUpgradeType type = f.getType();
                final String perkAchieved = type.getPerkAchievedPrefix(), perkUnachieved = type.getPerkUnachievedPrefix(), requirementsPrefix = type.getRequirementsPrefix();
                final List<String> achievedPerks = new ArrayList<>(), unachievedPerks = new ArrayList<>();
                for(int i = 1; i <= max; i++) {
                    final boolean achieved = tier >= i;
                    (achieved ? achievedPerks : unachievedPerks).add(ChatColor.translateAlternateColorCodes('&', (achieved ? perkAchieved : perkUnachieved) + levels.get(i).getString()));
                }
                if(item.hasItemMeta() && itemMeta.hasLore()) {
                    for(String s : itemMeta.getLore()) {
                        if(s.equals("{TIER}")) {
                            lore.add(isTierZero ? L : W.replace("{MAX_TIER}", Integer.toString(max)).replace("{TIER}", Integer.toString(tier)));
                        } else {
                            if(s.equals("{PERKS}")) {
                                lore.addAll(achievedPerks);
                                lore.addAll(unachievedPerks);
                            } else if(s.equals("{REQUIREMENTS}")) {
                                final FactionUpgradeLevel next = levels.getOrDefault(tier+1, null);
                                if(next != null) {
                                    for(String r : next.getCost()) {
                                        lore.add(ChatColor.translateAlternateColorCodes('&', requirementsPrefix + r.split("};")[1]));
                                    }
                                }
                            } else {
                                lore.add(s);
                            }
                        }
                    }
                    for(String s : isTierZero ? type.getUnlock() : type.getUpgrade()) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                    itemMeta.setLore(lore); lore.clear();
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    item.setItemMeta(itemMeta);
                    if(isTierZero) {
                        final String n = itemMeta.getDisplayName();
                        final List<String> l = itemMeta.getLore();
                        ItemStack F = locked.clone(); itemMeta = F.getItemMeta();
                        itemMeta.setDisplayName(n);
                        itemMeta.setLore(l);
                        F.setItemMeta(itemMeta);
                        item = F;
                    }
                    if(f.itemAmountEqualsTier()) {
                        item.setAmount(isTierZero ? 1 : tier);
                    }
                }
                return item;
            }
        }
        return null;
    }
    public void viewFactionUpgrades(Player player) {
        final String faction = regions.getFactionTag(player.getUniqueId());
        if(faction != null) {
            player.closeInventory();
            if(!factionUpgrades.containsKey(faction)) factionUpgrades.put(faction, new ArrayList<>());
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            final String W = ChatColor.translateAlternateColorCodes('&', config.getString("gui.tier")), L = ChatColor.translateAlternateColorCodes('&', config.getString("gui.locked.tier"));
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null) {
                    final ItemStack upgrade = getUpgrade(faction, i, W, L);
                    if(upgrade != null) {
                        top.setItem(i, upgrade);
                    }
                }
            }
            player.updateInventory();
        }
    }

    /*
    public double getCropGrowthMultiplier(String factionName) {
        return cropGrowthMultipliers.getOrDefault(factionName, 1.00);
    }
    public void setCropGrowthMultiplier(String faction, double multiplier) {
        cropGrowthMultipliers.put(faction, multiplier);
    }
    public double getDecreaseRarityGemPercent(String factionName, RarityGem gem) {
        return decreaseRarityGemCost.containsKey(factionName) ? decreaseRarityGemCost.get(factionName).getOrDefault(gem, 0.00) : 0;
    }
    public double getVkitLevelingChance(String factionName) {
        return factionName != null ? vkitLevelingChances.getOrDefault(factionName, 0.00) : 0.00;
    }
    public void setVkitLevelingChance(String factionName, double chance) {
        if(factionName != null) {
            vkitLevelingChances.put(factionName, chance);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockGrowEvent(BlockGrowEvent event) {
        final Location l = event.getBlock().getLocation();
        final String f = regions.getFactionTagAt(l);
        final double cgm = f != null ? getCropGrowthMultiplier(f) : 1.00;
        if(cgm != 1.00) {
            final Material m = l.getBlock().getType();
            if(!cropGrowthRate.containsKey(f)) {
                cropGrowthRate.put(f, new HashMap<>());
                cropGrowthRate.get(f).put(l, 0.00);
            } else if(!cropGrowthRate.get(f).containsKey(l)) {
                cropGrowthRate.get(f).put(l, 0.00);
            }
            cropGrowthRate.get(f).put(l, cropGrowthRate.get(f).get(l) + getCropGrowthMultiplier(f));
            byte d = (byte) (Math.floor(cropGrowthRate.get(f).get(l))), max = (byte) (m.name().equals("CROPS") || m.name().equals("CARROT") || m.name().equals("POTATO") ? 7 : 7);
            if(d > max) {
                d = max;
            }
            event.getNewState().setRawData(d);
        }
    }*/
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String a = event.getMessage().toLowerCase().substring(1);
        if(aliases != null) {
            for(String s : aliases) {
                if(a.startsWith(s)) {
                    event.setCancelled(true);
                    if(a.contains("reset")) {
                        if(hasPermission(player, "RandomPackage.fupgrade.reset", true)) {
                            factionUpgrades.get(regions.getFactionTag(player.getUniqueId())).clear();
                        }
                    } else if(hasPermission(player, "RandomPackage.fupgrade", true)) {
                        viewFactionUpgrades(player);
                    }
                    return;
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player) {
            final String t = event.getView().getTitle();
            if(t.equals(gui.getTitle())) {
                final int r = event.getRawSlot();
                event.setCancelled(true);
                player.updateInventory();
                final String c = event.getClick().name();
                if(r < 0 || r >= top.getSize() || !c.contains("LEFT") && !c.contains("RIGHT") || event.getCurrentItem() == null) return;
                final FactionUpgrade f = valueOfFactionUpgrade(r);
                if(f != null) tryToUpgrade(player, f);
            }
        }
    }

    private void triggerFactionUpgrades(Event event, String faction) {
        if(faction != null && factionUpgrades.containsKey(faction)) {
            final List<FactionUpgradeInfo> upgrades = factionUpgrades.get(faction);
            for(FactionUpgradeInfo info : upgrades) {
                trigger(event, info.getType().getAttributes(), "value", Double.toString(info.getLevel().getValue()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        if(damager instanceof Player) {
            triggerFactionUpgrades(event, getFactionTag((Player) damager));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        triggerFactionUpgrades(event, getFactionTag(event.getDamager()));
    }
    @EventHandler
    private void isDamagedEvent(isDamagedEvent event) {
        triggerFactionUpgrades(event, getFactionTag(event.getEntity()));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerTeleportDelayEvent(PlayerTeleportDelayEvent event) {
        triggerFactionUpgrades(event, getFactionTag(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Location l = event.getBlock().getLocation();
        final String f = regions.getFactionTagAt(l);
        if(f != null && cropGrowthRate.containsKey(f)) {
            cropGrowthRate.get(f).remove(l);
        }
    }
}
