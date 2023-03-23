package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class FileFactionUpgradeType extends RPAddonSpigot implements FactionUpgradeType {
	private static YamlConfiguration yml;

	private final String path;
	private List<String> unlock, upgrade, maxed, format;
	public FileFactionUpgradeType(String path) {
		super(null);
		if(yml == null) {
			yml = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "faction upgrades", "_settings.yml"));
		}
		this.path = path;
		register(Feature.FACTION_UPGRADE_TYPE, this);
	}
	public @NotNull String getIdentifier() { return path; }

	public String getPerkAchievedPrefix() { return colorize(yml.getString("types." + path + ".perk achieved prefix")); }
	public String getPerkUnachievedPrefix() { return colorize(yml.getString("types." + path + ".perk unachieved prefix")); }
	public String getRequirementsPrefix() { return colorize(yml.getString("types." + path + ".requirements prefix")); }
	public List<String> getUnlock() {
		if(unlock == null) unlock = colorizeListString(yml.getStringList("types." + path + ".unlock"));
		return unlock;
	}
	public List<String> getUpgrade() {
		if(upgrade == null) upgrade = colorizeListString(yml.getStringList("types." + path + ".upgrade"));
		return upgrade;
	}
	public List<String> getMaxed() {
		if(maxed == null) maxed = colorizeListString(yml.getStringList("types." + path + ".maxed"));
		return maxed;
	}
	public List<String> getFormat() {
		if(format == null) format = colorizeListString(yml.getStringList("types." + path + ".format"));
		return format;
	}
}
