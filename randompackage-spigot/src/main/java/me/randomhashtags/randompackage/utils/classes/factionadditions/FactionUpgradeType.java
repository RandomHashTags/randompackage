package me.randomhashtags.randompackage.utils.classes.factionadditions;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.utils.classes.factionadditions.FactionUpgrade.yml;

public class FactionUpgradeType {
	public static HashMap<String, FactionUpgradeType> types;
	private static RandomPackageAPI api;

	private String path, perkAchievedPrefix, perkUnachievedPrefix, requirementsPrefix;
	private List<String> unlock, upgrade, maxed, format;
	public boolean itemAmountEqualsTier;
	public FactionUpgradeType(String path, boolean itemAmountEqualsTier) {
		if(types == null) {
			types = new HashMap<>();
			api = RandomPackageAPI.getAPI();
		}
		this.path = path;
		this.itemAmountEqualsTier = itemAmountEqualsTier;
		types.put(path, this);
	}

	public String getPerkAchievedPrefix() {
		if(perkAchievedPrefix == null) perkAchievedPrefix = ChatColor.translateAlternateColorCodes('&', yml.getString("upgrades.types." + path + ".perk achieved prefix"));
		return perkAchievedPrefix;
	}
	public String getPerkUnachievedPrefix() {
		if(perkUnachievedPrefix == null) perkUnachievedPrefix = ChatColor.translateAlternateColorCodes('&', yml.getString("upgrades.types." + path + ".perk unachieved prefix"));
		return perkUnachievedPrefix;
	}
	public String getRequirementsPrefix() {
		if(requirementsPrefix == null) requirementsPrefix = ChatColor.translateAlternateColorCodes('&', yml.getString("upgrades.types." + path + ".requirements prefix"));
		return requirementsPrefix;
	}
	public List<String> getUnlock() {
		if(unlock == null) unlock = api.colorizeListString(yml.getStringList("upgrades.types." + path + ".unlock"));
		return unlock;
	}
	public List<String> getUpgrade() {
		if(upgrade == null) upgrade = api.colorizeListString(yml.getStringList("upgrades.types." + path + ".upgrade"));
		return upgrade;
	}
	public List<String> getMaxed() {
		if(maxed == null) maxed = api.colorizeListString(yml.getStringList("upgrades.types." + path + ".maxed"));
		return maxed;
	}
	public List<String> getFormat() {
		if(format == null) format = api.colorizeListString(yml.getStringList("upgrades.types." + path + ".format"));
		return format;
	}

	public static void deleteAll() {
		types = null;
		api = null;
	}
}
