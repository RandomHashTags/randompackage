package me.randomhashtags.randompackage.utils.classes;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class FactionUpgradeType {
	public static HashMap<String, FactionUpgradeType> types;
	private static YamlConfiguration yml;

	private String path, perkAchievedPrefix, perkUnachievedPrefix, requirementsPrefix;
	private List<String> unlock, upgrade, maxed, format;
	public FactionUpgradeType(String path) {
		if(types == null) {
			types = new HashMap<>();
			yml = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder(), "faction additions.yml"));
		}
		this.path = path;
		types.put(path, this);
	}

	public String getPerkAchievedPrefix() {
		if(perkAchievedPrefix == null) perkAchievedPrefix = ChatColor.translateAlternateColorCodes('&', yml.getString("types." + path + ".perk achieved prefix"));
		return perkAchievedPrefix;
	}
	public String getPerkUnachievedPrefix() {
		if(perkUnachievedPrefix == null) perkUnachievedPrefix = ChatColor.translateAlternateColorCodes('&', yml.getString("types." + path + ".perk unachieved prefix"));
		return perkUnachievedPrefix;
	}
	public String getRequirementsPrefix() {
		if(requirementsPrefix == null) requirementsPrefix = ChatColor.translateAlternateColorCodes('&', yml.getString("types." + path + ".requirements prefix"));
		return requirementsPrefix;
	}
	public List<String> getUnlock() {
		if(unlock == null) unlock = api.colorizeListString(yml.getStringList("types." + path + ".unlock"));
		return unlock;
	}
	public List<String> getUpgrade() {
		if(upgrade == null) upgrade = api.colorizeListString(yml.getStringList("types." + path + ".upgrade"));
		return upgrade;
	}
	public List<String> getMaxed() {
		if(maxed == null) maxed = api.colorizeListString(yml.getStringList("types." + path + ".maxed"));
		return maxed;
	}
	public List<String> getFormat() {
		if(format == null) format = api.colorizeListString(yml.getStringList("types." + path + ".format"));
		return format;
	}

	public static void deleteAll() {
		types = null;
		yml = null;
	}
}
