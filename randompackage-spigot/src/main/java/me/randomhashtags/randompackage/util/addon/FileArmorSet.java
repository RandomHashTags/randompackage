package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.ArmorSet;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileArmorSet extends RPAddon implements ArmorSet {
	private ItemStack helmet, chestplate, leggings, boots;
	private List<ArmorSetWeaponInfo> weapons;

	public FileArmorSet(File f) {
		load(f);
		addArmorSet(this);
	}
	public String getIdentifier() { return getYamlName(); }

	public String getName() {
		final String s = yml.getString("name");
		return s != null ? ChatColor.translateAlternateColorCodes('&', s) : getIdentifier() + " name doesn't exist!";
	}
	public ItemStack getHelmet() {
		if(helmet == null) helmet = api.d(yml, "helmet");
		return getClone(helmet);
	}
	public ItemStack getChestplate() {
		if(chestplate == null) chestplate = api.d(yml, "chestplate");
		return getClone(chestplate);
	}
	public ItemStack getLeggings() {
		if(leggings == null) leggings = api.d(yml, "leggings");
		return getClone(leggings);
	}
	public ItemStack getBoots() {
		if(boots == null) boots = api.d(yml, "boots");
		return getClone(boots);
	}
	public List<ArmorSetWeaponInfo> getWeapons() {
		if(weapons == null) {
			weapons = new ArrayList<>();
			final ConfigurationSection c = yml.getConfigurationSection("weapons");
			if(c != null) {
				for(String s : c.getKeys(false)) {
					weapons.add(new ArmorSetWeaponInfo(s, api.d(yml, "weapons." + s), colorizeListString(yml.getStringList("weapons." + s + ".set lore")), yml.getStringList("weapons." + s + ".attributes")));
				}
			}
		}
		return weapons;
	}
	public List<String> getArmorLore() { return colorizeListString(yml.getStringList("armor lore")); }
	public List<String> getCrystalPerks() { return colorizeListString(yml.getStringList("crystal perks")); }
	public List<String> getArmorAttributes() { return yml.getStringList("attributes.armor"); }
	public List<String> getCrystalAttributes() { return yml.getStringList("attributes.crystal"); }
	public List<String> getActivateMessage() { return colorizeListString(yml.getStringList("activate message")); }
}
