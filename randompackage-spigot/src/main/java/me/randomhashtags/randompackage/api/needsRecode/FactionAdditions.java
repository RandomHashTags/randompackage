package me.randomhashtags.randompackage.api.needsRecode;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.api.events.customboss.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.factionadditions.FactionUpgrade;
import me.randomhashtags.randompackage.utils.classes.factionadditions.FactionUpgradeType;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FactionAdditions extends RandomPackageAPI implements Listener {
	public boolean isEnabled = false;
	private static FactionAdditions instance;
	public static final FactionAdditions getFactionAdditions() {
	    if(instance == null) instance = new FactionAdditions();
	    return instance;
	}

	public YamlConfiguration config;

	private File fadditionsF;
	private YamlConfiguration fadditions;

	private UInventory factionUpgrades;
	private ItemStack background, locked;
	private ItemStack heroicFactionCrystal, factionCrystal, factionMCMMOBooster, factionXPBooster;
	private List<String> aliases;
	private List<String> attributes;
	public HashMap<String, HashMap<Location, Double>> cropGrowthRate;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if(!event.isCancelled()) {
			final Player player = event.getPlayer();
			final String a = event.getMessage().toLowerCase().substring(1);
			for(String s : aliases) {
				if(a.startsWith(s)) {
					event.setCancelled(true);
					if(a.contains("reset")) {
						if(hasPermission(player, "RandomPackage.fupgrade.reset", true)) {
							RPPlayer.getFactionUpgrades(player).clear();
						}
					} else if(hasPermission(player, "RandomPackage.fupgrade", true)) {
						viewFactionUpgrades(player);
					}
					return;
				}
			}
		}
	}

	private void backup() {
		final HashMap<String, HashMap<FactionUpgrade, Integer>> upgrades = RPPlayer.getFactionUpgrades();
		for(String F : upgrades.keySet()) {
			for(FactionUpgrade u : upgrades.get(F).keySet()) {
				fadditions.set("factions." + F, null);
				fadditions.set("factions." + F + "." + u.getPath(), upgrades.get(F).get(u));
			}
		}
		try {
			fadditions.save(fadditionsF);
			fadditionsF = new File(rpd + separator + "_Data", "faction additions.yml");
			fadditions = YamlConfiguration.loadConfiguration(fadditionsF);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	private void loadBackup() {
		final ConfigurationSection c = fadditions.getConfigurationSection("factions");
		if(c != null) {
			final HashMap<String, HashMap<FactionUpgrade, Integer>> L = RPPlayer.getFactionUpgrades();
			for(String s : c.getKeys(false)) {
				final ConfigurationSection f = fadditions.getConfigurationSection("factions." + s);
				final HashMap<FactionUpgrade, Integer> b = new HashMap<>();
				for(String a : f.getKeys(false)) {
					final FactionUpgrade u = FactionUpgrade.upgrades.getOrDefault(a, null);
					if(u != null) {
						b.put(u, fadditions.getInt("factions." + s + "." + a));
					}
				}
				L.put(s, b);
			}
		}
	}

	public void enable() {
		final long started = System.currentTimeMillis();
		if(isEnabled) return;
		save(null, "faction additions.yml");
		save("_Data", "faction additions.yml");

		attributes = new ArrayList<>(Arrays.asList(
				"allowfactionflight", "allowsmeltable", "increasemaxfactionwarps", "increasemaxfactionsize", "increasefactionpower", "reducemcmmocooldown", "setmobspawnrate", "setmobxpmultiplier", "setteleportdelaymultiplier",
				"sethungermultiplier", "setcropgrowthmultiplier", "setenemydamagemultiplier", "decreaseraritygemcost", "lmsmultiplier", "setoutpostcapmultiplier", "setconquestmobdamagemultiplier", "setbossdamagemultiplier",
				"reducecombattagtimer", "reduceenderpearlcooldown", "setdungeonportalappearancetime", "reducedungeonlootredeemable", "increasevkitlevelingchance", "setdungeonlootbagbonus", "setarmorsetdamagemultiplier")
		);

		fadditionsF = new File(rpd + separator + "_Data", "faction additions.yml");
		fadditions = YamlConfiguration.loadConfiguration(fadditionsF);

		pluginmanager.registerEvents(this, randompackage);
		isEnabled = true;
		aliases = randompackage.getConfig().getStringList("faction additions.aliases");
		config = YamlConfiguration.loadConfiguration(new File(rpd, "faction additions.yml"));
		FactionUpgrade.yml = config;

		heroicFactionCrystal = d(config, "items.heroic faction crystal");
		factionCrystal = d(config, "items.faction crystal");
		factionMCMMOBooster = d(config, "items.faction mcmmo booster");
		factionXPBooster = d(config, "items.faction xp booster");
		factionUpgrades = new UInventory(null, config.getInt("f upgrades.size"), ChatColor.translateAlternateColorCodes('&', config.getString("f upgrades.title")));
		background = d(config, "f upgrades.background");
		locked = d(config, "f upgrades.locked");
		addGivedpCategory(Arrays.asList(factionCrystal, heroicFactionCrystal, factionMCMMOBooster, factionXPBooster), UMaterial.DIAMOND_SWORD, "Faction Items", "Givedp: Faction Items");

		FactionUpgradeType defaultType = null;
		cropGrowthRate = new HashMap<>();

		givedpitem.items.put("heroicfactioncrystal", heroicFactionCrystal);
		givedpitem.items.put("factioncrystal", factionCrystal);

		final Inventory fi = factionUpgrades.getInventory();
		final int defaultMaxTier = config.getInt("upgrades.default settings.max tier");
		final boolean itemAmountEqualsTier = config.getBoolean("upgrades.default settings.item amount equals tier");
		for(String s : config.getConfigurationSection("upgrades").getKeys(false)) {
			if(s.equals("types")) {
				for(String S : config.getConfigurationSection("upgrades.types").getKeys(false)) {
					final boolean a = config.get("upgrades." + S + ".item amount equals tier") != null ? config.getBoolean("upgrades." + S + ".item amount equals tier") : itemAmountEqualsTier;
					final FactionUpgradeType u = new FactionUpgradeType(S, a);
					if(S.equals("DEFAULT")) defaultType = u;
				}
			} else if(!s.equals("default settings")) {
				final FactionUpgradeType type = config.get("upgrades." + s + ".type") == null ? defaultType : FactionUpgradeType.types.get(config.getString("upgrades." + s + ".type"));
				final FactionUpgrade u = new FactionUpgrade(s, type);
				fi.setItem(u.getSlot(), u.getItem());
			}
		}
		for(int i = 0; i < factionUpgrades.getSize(); i++) {
			if(fi.getItem(i) == null) {
				fi.setItem(i, background);
			}
		}
		loadBackup();
		sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Upgrades &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void disable() {
		if(!isEnabled) return;
		backup();
		isEnabled = false;
		aliases = null;
		fapi.vkitLevelingChances = null;
		fapi.decreaseRarityGemCost = null;
		fapi.bossDamageMultipliers = null;
		fapi.cropGrowthMultipliers = null;
		fapi.enemyDamageMultipliers = null;
		fapi.teleportDelayMultipliers = null;
		cropGrowthRate = null;
		FactionUpgradeType.deleteAll();
		FactionUpgrade.deleteAll();
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Inventory top = player.getOpenInventory().getTopInventory();
		if(!event.isCancelled() && top.getHolder() == player) {
			final String t = event.getView().getTitle();
			if(t.equals(factionUpgrades.getTitle())) {
				final int r = event.getRawSlot();
				event.setCancelled(true);
				player.updateInventory();
				final String c = event.getClick().name();
				if(r < 0 || r >= top.getSize() || !c.contains("LEFT") && !c.contains("RIGHT") || event.getCurrentItem() == null) return;
				final FactionUpgrade f = FactionUpgrade.valueOf(r);
				if(f != null) tryToUpgrade(player, f);
			}
		}
	}

	private ItemStack getUpgrade(HashMap<FactionUpgrade, Integer> upgrades, int slot, String W, String L) {
		final FactionUpgrade f = FactionUpgrade.valueOf(slot);
		if(f != null) {
			final int tier = upgrades != null ? upgrades.getOrDefault(f, 0) : 0;
			item = f.getItem();
			itemMeta = item.getItemMeta(); lore.clear();
			final FactionUpgradeType type = f.getUpgradeType();
			final String perkAchived = type.getPerkAchievedPrefix(), perkUnachived = type.getPerkUnachievedPrefix(), requirementsPrefix = type.getRequirementsPrefix();
			if(item.hasItemMeta() && itemMeta.hasLore()) {
				for(String s : itemMeta.getLore()) {
					if(s.equals("{TIER}")) {
						lore.add(tier == 0 ? L : W.replace("{MAX_TIER}", Integer.toString(f.getMaxTier())).replace("{TIER}", Integer.toString(tier)));
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
									for(int g = a + 1; g <= f.getMaxTier(); g++) {
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
				if(type.itemAmountEqualsTier) item.setAmount(tier == 0 ? 1 : tier);
			}
			return item;
		} else {
			return null;
		}
	}
	public void viewFactionUpgrades(Player player) {
		if(fapi != null && fapi.getFaction(player) != null) {
			player.closeInventory();
			final HashMap<FactionUpgrade, Integer> u = RPPlayer.getFactionUpgrades(player);
			player.openInventory(Bukkit.createInventory(player, factionUpgrades.getSize(), factionUpgrades.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			top.setContents(factionUpgrades.getInventory().getContents());
			final String W = ChatColor.translateAlternateColorCodes('&', config.getString("f upgrades.tier")), L = ChatColor.translateAlternateColorCodes('&', config.getString("f upgrades.locked.tier"));
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
	public void tryToUpgrade(Player player, FactionUpgrade fu) {
		final String f = fapi.getFaction(player);
		final HashMap<String, HashMap<FactionUpgrade, Integer>> U = RPPlayer.getFactionUpgrades();
		if(!U.containsKey(f)) U.put(f, new HashMap<>());
		final HashMap<FactionUpgrade, Integer> upgrades = RPPlayer.getFactionUpgrades(player);
		final int ti = upgrades.getOrDefault(fu, 0);
		if(ti >= fu.getMaxTier()) return;
		double requiredCash = 0.00, requiredSpawnerValue = 0.00;
		ItemStack requiredItem = null;
		final HashMap<String, String> replacements = new HashMap<>();
		for(String req : fu.getRequirements()) {
			final int tier = getRemainingInt(req.split(";")[0]);
			if(tier == ti+1) {
				String target = req.toLowerCase().split("tier" + (ti+1) + ";")[1].split("}")[0];
				if(target.startsWith("cash{")) {
					final double amount = getRemainingDouble(target.split("\\{")[1]);
					requiredCash = amount;
					if(eco.getBalance(player) < amount) {
						replacements.put("{COST}", formatDouble(amount));
						sendStringListMessage(player, config.getStringList("messages.dont have cash"), replacements);
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
					return;
				} else if(target.startsWith("factionupgrade{")) {
					final String p = target.split("\\{")[1].split("}")[0];
					final FactionUpgrade fuu = FactionUpgrade.upgrades.get(p.split(":")[0]);
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
		if(requiredCash != 0.00)
			eco.withdrawPlayer(player, requiredCash);
		if(requiredItem != null)
			removeItem(player, requiredItem, requiredItem.getAmount());
        upgrades.put(fu, ti+1);
        final HashMap<String, String> values = getValues(f, fu);
		for(String key : values.keySet()) {
			final String v = values.get(key);
			try {
				final double value = Double.parseDouble(v);
				if(key.equals("increasefactionpower"))
					fapi.increasePowerBoost(f, value);
				else if(key.equals("setteleportdelaymultiplier"))
					fapi.setTeleportDelayMultiplier(f, value);
				else if(key.equals("setcropgrowthmultiplier"))
					fapi.setCropGrowthMultiplier(f, value);
				else if(key.equals("setenemydamagemultiplier"))
					fapi.setEnemyDamageMultiplier(f, value);
				else if(key.equals("setbossdamagemultiplier"))
					fapi.setBossDamageMultiplier(f, value);
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
			} catch (Exception e) {
				if(key.equals("allowfactionflight")) {
				} else if(key.equals("allowsmeltable")) {
				} else if(key.equals("reducemcmmocooldown")) {
				} else if(key.equals("reduceraritygemcost")) {
				}
			}
		}
		final int slot = fu.getSlot();
		player.getOpenInventory().getTopInventory().setItem(slot, getUpgrade(upgrades, slot, ChatColor.translateAlternateColorCodes('&', config.getString("f upgrades.tier")), ChatColor.translateAlternateColorCodes('&', config.getString("f upgrades.locked.tier"))));
		player.updateInventory();
	}

	public ItemStack getBooster(double multiplier, long time, boolean xp) {
		final String t = getRemainingTime(time), m = formatDouble(multiplier);
		item = (xp ? factionXPBooster : factionMCMMOBooster).clone();
		itemMeta = item.getItemMeta(); lore.clear();
		for(String s : itemMeta.getLore()) {
			lore.add(s.replace("{MULTIPLIER}", m).replace("{TIME}", t));
		}
		itemMeta.setLore(lore); lore.clear();
		item.setItemMeta(itemMeta);
		return item;
	}

	public HashMap<String, String> getValues(String factionName, FactionUpgrade upgrade) {
		final HashMap<FactionUpgrade, Integer> upgrades = RPPlayer.getFactionUpgrades().getOrDefault(factionName, null);
		final HashMap<String, String> values = new HashMap<>();
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

	@EventHandler(priority = EventPriority.LOWEST)
	private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
		final Entity D = event.damager;
		if(!event.isCancelled() && D instanceof Player) {
			final Player damager = (Player) D;
			final String fn = fapi.getFaction(damager);
			event.damage = event.damage*fapi.getBossDamageMultiplier(fn);
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if(!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			final Player damager = (Player) event.getDamager(), victim = (Player) event.getEntity();
			if(fapi.relationIsEnemyOrNull(damager, victim)) {
				final String f = fapi.getFaction(damager);
				event.setDamage(event.getDamage()*fapi.getEnemyDamageMultiplier(f));
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockGrowEvent(BlockGrowEvent event) {
		if(!event.isCancelled()) {
			final Location l = event.getBlock().getLocation();
			final String f = fapi.getFactionAt(l);
			final double cgm = f != null ? fapi.getCropGrowthMultiplier(f) : 1.00;
			if(cgm != 1.00) {
				final Material m = l.getBlock().getType();
				if(!cropGrowthRate.containsKey(f)) {
					cropGrowthRate.put(f, new HashMap<>());
					cropGrowthRate.get(f).put(l, 0.00);
				} else if(!cropGrowthRate.get(f).containsKey(l)) {
					cropGrowthRate.get(f).put(l, 0.00);
				}
				cropGrowthRate.get(f).put(l, cropGrowthRate.get(f).get(l) + fapi.getCropGrowthMultiplier(f));
				byte d = (byte) (Math.floor(cropGrowthRate.get(f).get(l))), max = (byte) (m.name().equals("CROPS") || m.name().equals("CARROT") || m.name().equals("POTATO") ? 7 : 7);
				if(d > max) {
					d = max;
				}
				event.getNewState().setRawData(d);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockBreakEvent(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			final Location l = event.getBlock().getLocation();
			final String f = fapi.getFactionAt(l);
			if(f != null && cropGrowthRate.containsKey(f)) {
				cropGrowthRate.get(f).remove(l);
			}
		}
	}
}
