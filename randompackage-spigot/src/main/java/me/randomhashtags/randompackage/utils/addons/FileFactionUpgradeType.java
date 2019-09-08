package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.FactionUpgradeType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class FileFactionUpgradeType extends RPAddon implements FactionUpgradeType {
	private static YamlConfiguration yml;

	private String path;
	private List<String> unlock, upgrade, maxed, format;
	public FileFactionUpgradeType(String path) {
		if(yml == null) {
			yml = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder(), "faction upgrades.yml"));
		}
		this.path = path;
		addFactionUpgradeType(this);
	}
	public String getIdentifier() { return path; }

	public String getPerkAchievedPrefix() { return ChatColor.translateAlternateColorCodes('&', yml.getString("types." + path + ".perk achieved prefix")); }
	public String getPerkUnachievedPrefix() { return ChatColor.translateAlternateColorCodes('&', yml.getString("types." + path + ".perk unachieved prefix")); }
	public String getRequirementsPrefix() { return ChatColor.translateAlternateColorCodes('&', yml.getString("types." + path + ".requirements prefix")); }
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
}
