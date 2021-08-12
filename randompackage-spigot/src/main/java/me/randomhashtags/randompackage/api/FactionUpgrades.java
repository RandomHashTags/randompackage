package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;
import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.addon.file.FileFactionUpgrade;
import me.randomhashtags.randompackage.addon.file.FileFactionUpgradeType;
import me.randomhashtags.randompackage.addon.obj.FactionUpgradeInfo;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.FactionUpgradeLevelupEvent;
import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.perms.FactionUpgradePermission;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.GET_PLUGIN;

public final class FactionUpgrades extends EventAttributes {
    private static FactionUpgrades instance;
    public static FactionUpgrades getFactionUpgrades() {
        if(instance == null) instance = new FactionUpgrades();
        return instance;
    }

    public static HashMap<String, List<FactionUpgradeInfo>> FACTION_UPGRADES;

    public YamlConfiguration config;
    private File fupgradesF;
    private YamlConfiguration fupgrades;

    private UInventory gui;
    private ItemStack background, locked;
    public ItemStack heroicFactionCrystal, factionCrystal;
    private List<String> aliases;

    private HashMap<String, HashMap<Location, Double>> cropGrowthRate;

    @Override
    public String getIdentifier() {
        return "FACTION_UPGRADES";
    }

    @Override
    public boolean canBeEnabled() {
        final boolean enabled = RegionalAPI.INSTANCE.hookedFactionsUUID();
        if(!enabled) {
            sendConsoleMessage("&6[RandomPackage] &cDidn't load FactionUpgrades due to no supported Faction plugin installed!");
        }
        return enabled;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("faction upgrades", "_settings.yml");
        save("_Data", "faction upgrades.yml");

        if(!OTHER_YML.getBoolean("saved default faction upgrades")) {
            generateDefaultFactionUpgrades();
            OTHER_YML.set("saved default faction upgrades", true);
        }
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "faction upgrades", "_settings.yml"));
        for(String s : config.getConfigurationSection("types").getKeys(false)) {
            new FileFactionUpgradeType(s);
        }
        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory fi = gui.getInventory();
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "faction upgrades")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileFactionUpgrade fu = new FileFactionUpgrade(f);
                fi.setItem(fu.getSlot(), fu.getItem());
            }
        }

        cropGrowthRate = new HashMap<>();
        fupgradesF = new File(DATA_FOLDER + SEPARATOR + "_Data", "faction upgrades.yml");
        fupgrades = YamlConfiguration.loadConfiguration(fupgradesF);
        aliases = GET_PLUGIN.getConfig().getStringList("faction upgrades.cmds");
        heroicFactionCrystal = createItemStack(config, "items.heroic faction crystal");
        factionCrystal = createItemStack(config, "items.faction crystal");
        background = createItemStack(config, "gui.background");
        locked = createItemStack(config, "gui.locked");
        addGivedpCategory(Arrays.asList(factionCrystal, heroicFactionCrystal), UMaterial.DIAMOND_SWORD, "Faction Items", "Givedp: Faction Items");

        GivedpItem.INSTANCE.items.put("heroicfactioncrystal", heroicFactionCrystal);
        GivedpItem.INSTANCE.items.put("factioncrystal", factionCrystal);

        for(int i = 0; i < gui.getSize(); i++) {
            if(fi.getItem(i) == null) {
                fi.setItem(i, background);
            }
        }

        loadBackup();
        sendConsoleDidLoadFeature(getAll(Feature.FACTION_UPGRADE).size() + " Faction Upgrades", started);
    }
    @Override
    public void unload() {
        backup();
        FACTION_UPGRADES = null;
        unregister(Feature.FACTION_UPGRADE, Feature.FACTION_UPGRADE_TYPE);
    }

    private void backup() {
        if(isEnabled()) {
            for(String faction : FACTION_UPGRADES.keySet()) {
                final List<FactionUpgradeInfo> f = FACTION_UPGRADES.get(faction);
                fupgrades.set("factions." + faction, null);
                for(FactionUpgradeInfo info : f) {
                    fupgrades.set("factions." + faction + "." + info.getType().getIdentifier(), info.getLevel().asInt());
                }
            }
            try {
                fupgrades.save(fupgradesF);
                fupgrades = YamlConfiguration.loadConfiguration(fupgradesF);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void loadBackup() {
        final ConfigurationSection configuration = fupgrades.getConfigurationSection("factions");
        FACTION_UPGRADES = new HashMap<>();
        if(configuration != null) {
            for(String key : configuration.getKeys(false)) {
                final List<FactionUpgradeInfo> upgrades = new ArrayList<>();
                for(String upgrade : getConfigurationSectionKeys(fupgrades, "factions." + key, false)) {
                    final FactionUpgrade factionUpgrade = getFactionUpgrade(upgrade);
                    if(factionUpgrade != null) {
                        upgrades.add(new FactionUpgradeInfo(factionUpgrade, factionUpgrade.getLevels().get(fupgrades.getInt("factions." + key + "." + upgrade))));
                    }
                }
                FACTION_UPGRADES.put(key, upgrades);
            }
        }
    }

    public FactionUpgradeInfo getFactionUpgradeInfo(FactionUpgrade upgrade, String faction) {
        return getFactionUpgradeInfo(upgrade, faction, getDefaultUpgradeInfo(upgrade));
    }
    public FactionUpgradeInfo getFactionUpgradeInfo(FactionUpgrade upgrade, String faction, FactionUpgradeInfo def) {
        if(upgrade != null && faction != null && FACTION_UPGRADES.containsKey(faction)) {
            for(FactionUpgradeInfo info : FACTION_UPGRADES.get(faction)) {
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
    public void tryToUpgrade(@NotNull Player player, @NotNull FactionUpgrade upgrade) {
        final RegionalAPI regions = RegionalAPI.INSTANCE;
        final GivedpItem givedpItem = GivedpItem.INSTANCE;
        final String faction = regions.getFactionTag(player.getUniqueId());
        final FactionUpgradeInfo info = getFactionUpgradeInfo(upgrade, faction);
        if(info != null) {
            final FactionUpgradeLevel level = info.getLevel();
            final int tier = level.asInt();
            if(tier >= upgrade.getMaxLevel()) {
                return;
            }
            final FactionUpgradeLevel nextLevel = upgrade.getLevels().get(tier+1);
            BigDecimal requiredCash = BigDecimal.ZERO, requiredSpawnerValue = BigDecimal.ZERO;
            ItemStack requiredItem = null;
            final HashMap<String, String> replacements = new HashMap<>();
            final List<FactionUpgradeInfo> upgrades = FACTION_UPGRADES.get(faction);
            for(String costString : nextLevel.getCost()) {
                costString = costString.toLowerCase();
                if(costString.startsWith("cash{")) {
                    final double amount = getRemainingDouble(costString.split("\\{")[1]);
                    requiredCash = BigDecimal.valueOf(Double.parseDouble(costString.split("\\{")[1].split("}")[0]));
                    if(ECONOMY.getBalance(player) < amount) {
                        replacements.put("{COST}", formatBigDecimal(requiredCash));
                        sendStringListMessage(player, getStringList(config, "messages.dont have enough cash"), replacements);
                        return;
                    }
                } else if(costString.startsWith("item{")) {
                    final String value = costString.split("\\{")[1];
                    final ItemStack item = givedpItem.valueOfRPItem(value.split(";")[0]);
                    requiredItem = item;
                    item.setAmount(Integer.parseInt(value.split("amount=")[1].split("}")[0]));
                    if(!player.getInventory().containsAtLeast(item, item.getAmount())) {
                        replacements.put("{ITEM}", costString.split("};")[1]);
                        sendStringListMessage(player, getStringList(config, "messages.dont have item"), replacements);
                        return;
                    }
                } else if(costString.startsWith("spawnervalue{")) {
                    replacements.put("{COST}", formatBigDecimal(BigDecimal.valueOf(getRemainingDouble(costString.split("\\{")[1].split("}")[0]))));
                    sendStringListMessage(player, getStringList(config, "messages.dont have enough spawner value"), replacements);
                    return;
                } else if(costString.startsWith("factionupgrade{")) {
                    final String[] values = costString.split("\\{")[1].split("}")[0].split(":");
                    final FactionUpgrade target = getFactionUpgrade(values[0]);
                    final int lvl = Integer.parseInt(values[1].split("=")[1]);
                    if(!upgrades.contains(getFactionUpgradeInfo(target, faction))) {
                        final String name = ChatColor.stripColor(target.getItem().getItemMeta().getDisplayName());
                        replacements.put("{UPGRADE}", name + " " + toRoman(lvl));
                        sendStringListMessage(player, getStringList(config, "messages.dont have f upgrade"), replacements);
                        return;
                    }
                }
            }
            final FactionUpgradeLevelupEvent upgradeEvent = new FactionUpgradeLevelupEvent(player, upgrade, tier);
            PLUGIN_MANAGER.callEvent(upgradeEvent);
            if(upgradeEvent.isCancelled()) {
                return;
            }
            if(!requiredCash.equals(BigDecimal.ZERO)) {
                ECONOMY.withdrawPlayer(player, requiredCash.doubleValue());
            }
            if(requiredItem != null) {
                removeItem(player, requiredItem, requiredItem.getAmount());
            }
            info.setLevel(nextLevel);
            final int slot = upgrade.getSlot();
            player.getOpenInventory().getTopInventory().setItem(slot, getUpgrade(faction, slot, getString(config, "gui.tier"), getString(config, "gui.locked.tier")));
            player.updateInventory();
        }
    }
    private ItemStack getUpgrade(String faction, int slot, String W, String L) {
        final FactionUpgrade upgrade = valueOfFactionUpgrade(slot);
        if(upgrade != null) {
            final FactionUpgradeInfo info = getFactionUpgradeInfo(upgrade, faction);
            if(info != null) {
                final HashMap<Integer, FactionUpgradeLevel> levels = upgrade.getLevels();
                final FactionUpgradeLevel level = info.getLevel();
                final int tier = level.asInt(), max = upgrade.getMaxLevel();
                final boolean isTierZero = tier == 0;
                ItemStack item = upgrade.getItem();
                ItemMeta itemMeta = item.getItemMeta();
                final FactionUpgradeType type = upgrade.getType();
                final String perkAchieved = type.getPerkAchievedPrefix(), perkUnachieved = type.getPerkUnachievedPrefix(), requirementsPrefix = type.getRequirementsPrefix();
                final List<String> achievedPerks = new ArrayList<>(), unachievedPerks = new ArrayList<>();
                for(int i = 1; i <= max; i++) {
                    final boolean achieved = tier >= i;
                    (achieved ? achievedPerks : unachievedPerks).add(colorize((achieved ? perkAchieved : perkUnachieved) + levels.get(i).getString()));
                }
                if(item.hasItemMeta() && itemMeta.hasLore()) {
                    final List<String> lore = new ArrayList<>();
                    for(String string : itemMeta.getLore()) {
                        if(string.equals("{TIER}")) {
                            lore.add(isTierZero ? L : W.replace("{MAX_TIER}", Integer.toString(max)).replace("{TIER}", Integer.toString(tier)));
                        } else {
                            if(string.equals("{PERKS}")) {
                                lore.addAll(achievedPerks);
                                lore.addAll(unachievedPerks);
                            } else if(string.equals("{REQUIREMENTS}")) {
                                final FactionUpgradeLevel next = levels.getOrDefault(tier+1, null);
                                if(next != null) {
                                    for(String r : next.getCost()) {
                                        lore.add(colorize(requirementsPrefix + r.split("};")[1]));
                                    }
                                }
                            } else {
                                lore.add(string);
                            }
                        }
                    }
                    for(String string : isTierZero ? type.getUnlock() : type.getUpgrade()) {
                        lore.add(colorize(string));
                    }
                    itemMeta.setLore(lore);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    item.setItemMeta(itemMeta);
                    if(isTierZero) {
                        final String displayName = itemMeta.getDisplayName();
                        final ItemStack lockedItem = locked.clone();
                        itemMeta = lockedItem.getItemMeta();
                        itemMeta.setDisplayName(displayName);
                        itemMeta.setLore(lore);
                        lockedItem.setItemMeta(itemMeta);
                        item = lockedItem;
                    }
                    if(upgrade.itemAmountEqualsTier()) {
                        item.setAmount(isTierZero ? 1 : tier);
                    }
                }
                return item;
            }
        }
        return null;
    }
    public void viewFactionUpgrades(Player player) {
        final String faction = RegionalAPI.INSTANCE.getFactionTag(player.getUniqueId());
        if(faction != null) {
            player.closeInventory();
            FACTION_UPGRADES.putIfAbsent(faction, new ArrayList<>());
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            final String tierString = colorize(config.getString("gui.tier")), lockedTierName = colorize(config.getString("gui.locked.tier"));
            for(int i = 0; i < top.getSize(); i++) {
                final ItemStack item = top.getItem(i);
                if(item != null) {
                    final ItemStack upgrade = getUpgrade(faction, i, tierString, lockedTierName);
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
        if(aliases != null) {
            final Player player = event.getPlayer();
            final String msg = event.getMessage().toLowerCase().substring(1);
            final RegionalAPI regions = RegionalAPI.INSTANCE;
            for(String alias : aliases) {
                if(msg.startsWith(alias)) {
                    event.setCancelled(true);
                    if(msg.contains("reset")) {
                        if(hasPermission(player, FactionUpgradePermission.COMMAND_RESET, true)) {
                            FACTION_UPGRADES.get(regions.getFactionTag(player.getUniqueId())).clear();
                        }
                    } else if(hasPermission(player, FactionUpgradePermission.COMMAND, true)) {
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
        if(top.getHolder() == player && event.getView().getTitle().equals(gui.getTitle())) {
            final int slot = event.getRawSlot();
            event.setCancelled(true);
            player.updateInventory();
            final String click = event.getClick().name();
            if(slot < 0 || slot >= top.getSize() || !click.contains("LEFT") && !click.contains("RIGHT") || event.getCurrentItem() == null) {
                return;
            }
            final FactionUpgrade f = valueOfFactionUpgrade(slot);
            if(f != null) {
                tryToUpgrade(player, f);
            }
        }
    }

    private void triggerFactionUpgrades(Event event, String faction) {
        if(faction != null && FACTION_UPGRADES.containsKey(faction)) {
            final List<FactionUpgradeInfo> upgrades = FACTION_UPGRADES.get(faction);
            for(FactionUpgradeInfo info : upgrades) {
                trigger(event, info.getType().getAttributes(), "value", Double.toString(info.getLevel().getValue()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        if(damager instanceof Player) {
            triggerFactionUpgrades(event, RegionalAPI.INSTANCE.getFactionTag((Player) damager));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        triggerFactionUpgrades(event, RegionalAPI.INSTANCE.getFactionTag(event.getDamager()));
    }
    @EventHandler
    private void isDamagedEvent(isDamagedEvent event) {
        triggerFactionUpgrades(event, RegionalAPI.INSTANCE.getFactionTag(event.getEntity()));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerTeleportDelayEvent(PlayerTeleportDelayEvent event) {
        triggerFactionUpgrades(event, RegionalAPI.INSTANCE.getFactionTag(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Location location = event.getBlock().getLocation();
        final String factionTag = RegionalAPI.INSTANCE.getFactionTagAt(location);
        if(factionTag != null && cropGrowthRate.containsKey(factionTag)) {
            cropGrowthRate.get(factionTag).remove(location);
        }
    }
}
