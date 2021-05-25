package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ArmorSet;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileArmorSet extends RPAddon implements ArmorSet {
	private ItemStack helmet, chestplate, leggings, boots;
	private List<ArmorSetWeaponInfo> weapons;

	public FileArmorSet(File f) {
		load(f);
		register(Feature.ARMOR_SET, this);
	}

	@Override
	public String getIdentifier() {
		return getYamlName();
	}

	public String getName() {
		final String s = yml.getString("name");
		return s != null ? colorize(s) : getIdentifier() + " name doesn't exist!";
	}
	public ItemStack getHelmet() {
		if(helmet == null) helmet = createItemStack(yml, "helmet");
		return getClone(helmet);
	}
	public ItemStack getChestplate() {
		if(chestplate == null) chestplate = createItemStack(yml, "chestplate");
		return getClone(chestplate);
	}
	public ItemStack getLeggings() {
		if(leggings == null) leggings = createItemStack(yml, "leggings");
		return getClone(leggings);
	}
	public ItemStack getBoots() {
		if(boots == null) boots = createItemStack(yml, "boots");
		return getClone(boots);
	}
	public List<ArmorSetWeaponInfo> getWeapons() {
		if(weapons == null) {
			weapons = new ArrayList<>();
			final ConfigurationSection c = yml.getConfigurationSection("weapons");
			if(c != null) {
				for(String s : c.getKeys(false)) {
					weapons.add(new ArmorSetWeaponInfo(s, createItemStack(yml, "weapons." + s), colorizeListString(yml.getStringList("weapons." + s + ".set lore")), yml.getStringList("attributes." + s)));
				}
			}
		}
		return weapons;
	}
	public List<String> getArmorLore() {
		return getStringList(yml, "armor lore");
	}
	public List<String> getCrystalPerks() {
		return getStringList(yml, "crystal perks");
	}
	public List<String> getArmorAttributes() {
		return getStringList(yml, "attributes.armor");
	}
	public List<String> getCrystalAttributes() {
		return getStringList(yml, "attributes.crystal");
	}
	public List<String> getActivateMessage() {
		return getStringList(yml, "activate message");
	}
	public List<String> getCrystalAppliedMsg() {
		return getStringList(yml, "crystal applied msg");
	}
}
