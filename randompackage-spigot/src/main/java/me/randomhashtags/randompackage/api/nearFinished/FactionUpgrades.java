package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.event.FactionUpgradeLevelupEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.FileFactionUpgrade;
import me.randomhashtags.randompackage.util.addon.FileFactionUpgradeType;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class FactionUpgrades extends RPFeature {
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
    private HashMap<String, Double> teleportDelayMultipliers, cropGrowthMultipliers, enemyDamageMultipliers, bossDamageMultipliers, vkitLevelingChances;
    private HashMap<String, HashMap<RarityGem, Double>> decreaseRarityGemCost;

    public static HashMap<String, HashMap<FactionUpgrade, Integer>> factionUpgrades;

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
            teleportDelayMultipliers = new HashMap<>();
            cropGrowthMultipliers = new HashMap<>();
            enemyDamageMultipliers = new HashMap<>();
            bossDamageMultipliers = new HashMap<>();
            vkitLevelingChances = new HashMap<>();
            decreaseRarityGemCost = new HashMap<>();

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
                final HashMap<FactionUpgrade, Integer> f = factionUpgrades.get(F);
                fupgrades.set("factions." + F, null);
                for(FactionUpgrade u : f.keySet()) {
                    fupgrades.set("factions." + F + "." + u.getIdentifier(), f.get(u));
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
                final HashMap<FactionUpgrade, Integer> b = new HashMap<>();
                for(String a : f.getKeys(false)) {
                    final FactionUpgrade u = getFactionUpgrade(a);
                    if(u != null) {
                        b.put(u, fupgrades.getInt("factions." + s + "." + a));
                    }
                }
                factionUpgrades.put(s, b);
            }
        }
    }

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

    private ItemStack getUpgrade(HashMap<FactionUpgrade, Integer> upgrades, int slot, String W, String L) {
        final FactionUpgrade f = valueOfFactionUpgrade(slot);
        if(f != null) {
            final int tier = upgrades != null ? upgrades.getOrDefault(f, 0) : 0;
            item = f.getItem();
            itemMeta = item.getItemMeta(); lore.clear();
            final FactionUpgradeType type = f.getType();
            final String perkAchived = type.getPerkAchievedPrefix(), perkUnachived = type.getPerkUnachievedPrefix(), requirementsPrefix = type.getRequirementsPrefix();
            if(item.hasItemMeta() && itemMeta.hasLore()) {
                for(String s : itemMeta.getLore()) {
                    if(s.equals("{TIER}")) {
                        lore.add(tier == 0 ? L : W.replace("{MAX_TIER}", Integer.toString(f.getMaxLevel())).replace("{TIER}", Integer.toString(tier)));
                    } else {
                        boolean did = false;
                        int a = 0;
                        if(s.equals("{PERKS}")) {
                            did = true;
                            for(String p : f.getPerks()) {
                                a += 1;
                                final int targetTier = getRemainingInt(p.replace("#", Integer.toString(a)).split(";")[0]);
                                lore.add(ChatColor.translateAlternateColorCodes('&', (tier >= targetTier ? perkAchived : perkUnachived) + p.replace("#", Integer.toString(a)).split(";")[2]));
                                if(a == 1 && f.getPerks().size() == 1 && p.split(";")[0].contains("#")) {
                                    for(int g = a + 1; g <= f.getMaxLevel(); g++) {
                                        lore.add(ChatColor.translateAlternateColorCodes('&', (tier >= g ? perkAchived : perkUnachived) + p.replace("#", Integer.toString(g)).split(";")[2]));
                                    }
                                }
                            }
                        } else if(s.equals("{REQUIREMENTS}")) {
                            did = true;
                            for(String r : f.getRequirements()) {
                                final int targetTier = getRemainingInt(r.split(";")[0]);
                                final String R = r.contains("}") ? r.split("};")[1] : r.split(";")[2];
                                if(tier+1 == targetTier) {
                                    lore.add(ChatColor.translateAlternateColorCodes('&', requirementsPrefix + R));
                                }
                            }
                        }
                        if(!did) lore.add(s);
                    }
                }
                for(String s : tier == 0 ? type.getUnlock() : type.getUpgrade()) lore.add(ChatColor.translateAlternateColorCodes('&', s));
                itemMeta.setLore(lore); lore.clear();
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                item.setItemMeta(itemMeta);
                if(tier == 0) {
                    final String n = itemMeta.getDisplayName();
                    final List<String> l = itemMeta.getLore();
                    ItemStack F = locked.clone(); itemMeta = F.getItemMeta();
                    itemMeta.setDisplayName(n);
                    itemMeta.setLore(l);
                    F.setItemMeta(itemMeta);
                    item = F;
                }
                if(f.itemAmountEqualsTier()) item.setAmount(tier == 0 ? 1 : tier);
            }
            return item;
        } else {
            return null;
        }
    }
    public void viewFactionUpgrades(Player player) {
        final String f = regions.getFactionTag(player.getUniqueId());
        if(f != null) {
            player.closeInventory();
            if(!factionUpgrades.containsKey(f)) factionUpgrades.put(f, new HashMap<>());
            final HashMap<FactionUpgrade, Integer> u = factionUpgrades.get(f);
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            final String W = ChatColor.translateAlternateColorCodes('&', config.getString("gui.tier")), L = ChatColor.translateAlternateColorCodes('&', config.getString("gui.locked.tier"));
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null) {
                    final ItemStack upgrade = getUpgrade(u, i, W, L);
                    if(upgrade != null) {
                        top.setItem(i, upgrade);
                    }
                }
            }
            player.updateInventory();
        }
    }
    public HashMap<String, String> getValues(String factionName, FactionUpgrade upgrade) {
        final HashMap<FactionUpgrade, Integer> upgrades = factionUpgrades.getOrDefault(factionName, null);
        final HashMap<String, String> values = new HashMap<>();
        final List<String> attributes = new ArrayList<>(Arrays.asList(
                "allowfactionflight", "allowsmeltable", "increasemaxfactionwarps", "increasemaxfactionsize", "increasefactionpower", "reducemcmmocooldown", "setmobspawnrate", "setmobxpmultiplier", "setteleportdelaymultiplier",
                "sethungermultiplier", "setcropgrowthmultiplier", "setenemydamagemultiplier", "decreaseraritygemcost", "lmsmultiplier", "setoutpostcapmultiplier", "setconquestmobdamagemultiplier", "setbossdamagemultiplier",
                "reducecombattagtimer", "reduceenderpearlcooldown", "setdungeonportalappearancetime", "reducedungeonlootredeemable", "increasevkitlevelingchance", "setdungeonlootbagbonus", "setarmorsetdamagemultiplier")
        );
        if(upgrades != null) {
            final int tier = upgrades.getOrDefault(upgrade, 0);
            for(String s : upgrade.getPerks()) {
                final int targetTier = getRemainingInt(s.split(";")[0]);
                if(targetTier == tier) {
                    for(String at : attributes)
                        if(s.toLowerCase().contains(at))
                            values.put(at, s.toLowerCase().split(at + "\\{")[1].split("};")[0]);
                }
            }
        }
        return values;
    }
    public void tryToUpgrade(Player player, FactionUpgrade fu) {
        final String f = regions.getFactionTag(player.getUniqueId());
        final HashMap<FactionUpgrade, Integer> upgrades = factionUpgrades.get(f);
        final int ti = upgrades.getOrDefault(fu, 0);
        if(ti >= fu.getMaxLevel()) return;
        BigDecimal requiredCash = BigDecimal.ZERO, requiredSpawnerValue = BigDecimal.ZERO;
        ItemStack requiredItem = null;
        final HashMap<String, String> replacements = new HashMap<>();
        for(String req : fu.getRequirements()) {
            final int tier = getRemainingInt(req.split(";")[0]);
            if(tier == ti+1) {
                String target = req.toLowerCase().split("tier" + (ti+1) + ";")[1].split("}")[0];
                if(target.startsWith("cash{")) {
                    final double amount = getRemainingDouble(target.split("\\{")[1]);
                    requiredCash = BigDecimal.valueOf(amount);
                    if(eco.getBalance(player) < amount) {
                        replacements.put("{COST}", formatDouble(requiredCash.doubleValue()).split("E")[0]);
                        sendStringListMessage(player, config.getStringList("messages.dont have enough cash"), replacements);
                        return;
                    }
                } else if(target.startsWith("item{")) {
                    final ItemStack a = givedpitem.valueOf(target.split("\\{")[1].split(";")[0]);
                    requiredItem = a;
                    a.setAmount(Integer.parseInt(target.split("\\{")[1].split("amount=")[1]));
                    if(!player.getInventory().containsAtLeast(a, a.getAmount())) {
                        replacements.put("{ITEM}", req.split("};")[1]);
                        sendStringListMessage(player, config.getStringList("messages.dont have item"), replacements);
                        return;
                    }
                } else if(target.startsWith("spawnervalue{")) {
                    replacements.put("{COST}", formatBigDecimal(BigDecimal.valueOf(Double.parseDouble(target.split("\\{")[1].split("}")[0]))));
                    sendStringListMessage(player, config.getStringList("messages.dont have enough spawner value"), replacements);
                    return;
                } else if(target.startsWith("factionupgrade{")) {
                    final String p = target.split("\\{")[1].split("}")[0];
                    final FactionUpgrade fuu = getFactionUpgrade(p.split(":")[0]);
                    final int lvl = Integer.parseInt(p.split(":")[1].split("=")[1]);
                    if(!upgrades.containsKey(fuu)) {
                        final String di = ChatColor.stripColor(fuu.getItem().getItemMeta().getDisplayName());
                        replacements.put("{UPGRADE}", di + " " + toRoman(lvl));
                        sendStringListMessage(player, config.getStringList("messages.dont have f upgrade"), replacements);
                        return;
                    }
                }
            }
        }
        final FactionUpgradeLevelupEvent e = new FactionUpgradeLevelupEvent(player, fu, ti);
        pluginmanager.callEvent(e);
        if(e.isCancelled()) return;
        if(!requiredCash.equals(BigDecimal.ZERO)) eco.withdrawPlayer(player, requiredCash.doubleValue());
        if(requiredItem != null) removeItem(player, requiredItem, requiredItem.getAmount());
        final int tier = ti+1;
        upgrades.put(fu, tier);
        final HashMap<String, String> values = getValues(f, fu);
        for(String key : values.keySet()) {
            final String v = values.get(key);
            try {
                final double value = Double.parseDouble(v);
                if(key.equals("increasefactionpower")) {
                    //fapi.increasePowerBoost(f, value);
                } else if(key.equals("setteleportdelaymultiplier"))
                    setTeleportDelayMultiplier(f, fu.getTeleportDelayMultiplier(tier));
                else if(key.equals("setcropgrowthmultiplier"))
                    setCropGrowthMultiplier(f, fu.getCropGrowMultiplier(tier));
                else if(key.equals("setenemydamagemultiplier"))
                    setEnemyDamageMultiplier(f, fu.getEnemyDamageMultiplier(tier));
                else if(key.equals("setbossdamagemultiplier"))
                    setBossDamageMultiplier(f, value);
				/*
				else if(key.equals("sethungermultiplier"))
					fapi.setHungerMultiplier(f, value);
				else if(key.equals("setoutpostcapmultiplier"))
					fapi.setOutpostCapMultiplier(f, value);
				else if(key.equals("setconquestmobdamagemulitplier"))
					fapi.setConquestMobDamageMultiplier(f, value);
				else if(key.equals("setmobspawnrate"))
					fapi.setMobSpawnRate(f, value);
				else if(key.equals("setmobxpmultiplier"))
					fapi.setMobXPMultiplier(f, value);
				else if(key.equals("reducecombattagmultiplier"))
					fapi.reduceCombatTagMultiplier(f, value);
				else if(key.equals("reduceenderpearlcooldown"))
					fapi.reduceEnderpearlCooldown(f, value);
				else if(key.equals("setdungeonpportalappearancetime"))
					fapi.setDungeonPortalAppearanceTime(f, value);
				else if(key.equals("increasevkitlevelingchance"))
					fapi.increaseVkitLevelingChance(f, value);
				else if(key.equals("setdungeonlootbagbonus"))
					fapi.setDungeonLootbagBonus(f, value);
				else if(key.equals("setincomingarmorsetdamagemultiplier"))
					fapi.setIncomingArmorSetDamageMultiplier(f, value);*/
            } catch (Exception ee) {
                if(key.equals("allowfactionflight")) {
                } else if(key.equals("allowsmeltable")) {
                } else if(key.equals("reducemcmmocooldown")) {
                } else if(key.equals("reduceraritygemcost")) {
                }
            }
        }
        final int slot = fu.getSlot();
        player.getOpenInventory().getTopInventory().setItem(slot, getUpgrade(upgrades, slot, ChatColor.translateAlternateColorCodes('&', config.getString("gui.tier")), ChatColor.translateAlternateColorCodes('&', config.getString("gui.locked.tier"))));
        player.updateInventory();
    }

    public double getTeleportDelayMultiplier(String faction) {
        return teleportDelayMultipliers.getOrDefault(faction, 1.00);
    }
    public void setTeleportDelayMultiplier(String faction, double multiplier) {
        teleportDelayMultipliers.put(faction, multiplier);
    }
    public void resetTeleportDelayMultiplier(String faction) {
        if(faction != null) {
            teleportDelayMultipliers.put(faction, 1.00);
        }
    }

    public double getCropGrowthMultiplier(String factionName) {
        return cropGrowthMultipliers.getOrDefault(factionName, 1.00);
    }
    public void setCropGrowthMultiplier(String faction, double multiplier) {
        cropGrowthMultipliers.put(faction, multiplier);
    }

    public double getBossDamageMultiplier(String faction) {
        return bossDamageMultipliers.getOrDefault(faction, 1.00);
    }
    public void setBossDamageMultiplier(String faction, double multiplier) {
        bossDamageMultipliers.put(faction, multiplier);
    }

    public double getEnemyDamageMultiplier(String faction) {
        return enemyDamageMultipliers.getOrDefault(faction, 1.00);
    }
    public void setEnemyDamageMultiplier(String faction, double multiplier) {
        enemyDamageMultipliers.put(faction, multiplier);
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


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
        final Entity D = event.getDamager();
        if(D instanceof Player) {
            final Player damager = (Player) D;
            final String fn = regions.getFactionTag(damager.getUniqueId());
            event.setDamage(event.getDamage()*getBossDamageMultiplier(fn));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final UUID d = event.getDamager().getUniqueId(), v = event.getEntity().getUniqueId();
            if(factions.isEnemy(d, v)) {
                final String f = regions.getFactionTag(d);
                event.setDamage(event.getDamage()*getEnemyDamageMultiplier(f));
            }
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
