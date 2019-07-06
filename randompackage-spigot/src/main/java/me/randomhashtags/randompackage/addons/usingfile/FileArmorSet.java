package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.ArmorSet;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileArmorSet extends ArmorSet {
	private ItemStack helmet, chestplate, leggings, boots;
	private List<String> armorLore, weaponLore, attributes, activateMessage;

	public FileArmorSet(File f) {
		load(f);
		addArmorSet(getIdentifier(), this);
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
	public List<String> getArmorLore() {
		if(armorLore == null) armorLore = api.colorizeListString(yml.getStringList("armor lore"));
		return armorLore;
	}
	public List<String> getWeaponLore() {
		if(weaponLore == null) weaponLore = api.colorizeListString(yml.getStringList("weapon lore"));
		return weaponLore;
	}
	public List<String> getAttributes() {
		if(attributes == null) attributes = yml.getStringList("attributes");
		return attributes;
	}
	public List<String> getActivateMessage() {
		if(activateMessage == null) activateMessage = api.colorizeListString(yml.getStringList("activate message"));
		return activateMessage;
	}
}
