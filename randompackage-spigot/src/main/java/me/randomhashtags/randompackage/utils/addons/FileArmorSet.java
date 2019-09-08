package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.ArmorSet;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileArmorSet extends RPAddon implements ArmorSet {
	private ItemStack helmet, chestplate, leggings, boots;

	public FileArmorSet(File f) {
		load(f);
		addArmorSet(this);
	}
	public String getIdentifier() { return getYamlName(); }

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
	public List<String> getArmorLore() { return colorizeListString(yml.getStringList("armor lore")); }
	public List<String> getWeaponLore() { return colorizeListString(yml.getStringList("weapon lore")); }
	public List<String> getAttributes() { return yml.getStringList("attributes"); }
	public List<String> getActivateMessage() { return colorizeListString(yml.getStringList("activate message")); }
}
