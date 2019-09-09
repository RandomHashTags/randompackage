package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.ArmorSet;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileArmorSet extends RPAddon implements ArmorSet {
	private ItemStack helmet, chestplate, leggings, boots, weapon;

	public FileArmorSet(File f) {
		load(f);
		addArmorSet(this);
	}
	public String getIdentifier() { return getYamlName(); }

	public String getName() {
		final String s = yml.getString("crystal.name");
		return s != null ? ChatColor.translateAlternateColorCodes('&', s) : getIdentifier() + " crystal doesn't exist!";
	}
	public ItemStack getHelmet() {
		if(helmet == null) helmet = api.d(yml, "helmet");
		return helmet.clone();
	}
	public ItemStack getChestplate() {
		if(chestplate == null) chestplate = api.d(yml, "chestplate");
		return chestplate.clone();
	}
	public ItemStack getLeggings() {
		if(leggings == null) leggings = api.d(yml, "leggings");
		return leggings.clone();
	}
	public ItemStack getBoots() {
		if(boots == null) boots = api.d(yml, "boots");
		return boots.clone();
	}
	public ItemStack getWeapon() {
		if(weapon == null) weapon = api.d(yml, "weapon");
		return weapon != null ? weapon.clone() : null;
	}
	public List<String> getArmorLore() { return colorizeListString(yml.getStringList("armor lore")); }
	public List<String> getWeaponLore() { return colorizeListString(yml.getStringList("weapon lore")); }
	public List<String> getCrystalPerks() { return colorizeListString(yml.getStringList("crystal perks")); }
	public List<String> getArmorAttributes() { return yml.getStringList("attributes.armor"); }
	public List<String> getWeaponAttributes() { return yml.getStringList("attributes.weapon"); }
	public List<String> getCrystalAttributes() { return yml.getStringList("attributes.crystal"); }
	public List<String> getActivateMessage() { return colorizeListString(yml.getStringList("activate message")); }
}
