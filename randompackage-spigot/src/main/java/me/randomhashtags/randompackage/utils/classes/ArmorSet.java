package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ArmorSet {
	public static HashMap<String, ArmorSet> sets;
	private static RandomPackageAPI api;

	private YamlConfiguration yml;
	private String ymlName;
	private ItemStack helmet, chestplate, leggings, boots;
	private List<String> armorLore, weaponLore, attributes, activateMessage;

	public ArmorSet(File f) {
		if(sets == null) {
			sets = new HashMap<>();
			api = RandomPackageAPI.getAPI();
		}
		yml = YamlConfiguration.loadConfiguration(f);
		ymlName = f.getName().split("\\.yml")[0];
		sets.put(ymlName, this);
	}

	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return ymlName; }
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
		if(activateMessage == null )activateMessage = api.colorizeListString(yml.getStringList("activate message"));
		return activateMessage;
	}

	public static ArmorSet valueOf(Player player) {
		if(sets != null && player != null) {
			final PlayerInventory pi = player.getInventory();
			final ItemStack h = pi.getHelmet(), c = pi.getChestplate(), l = pi.getLeggings(), b = pi.getBoots();
			for(ArmorSet set : sets.values()) {
				final List<String> a = set.armorLore;
				if(a != null &&
						(h != null && h.hasItemMeta() && h.getItemMeta().hasLore() && h.getItemMeta().getLore().containsAll(a)
								&& c != null && c.hasItemMeta() && c.getItemMeta().hasLore() && c.getItemMeta().getLore().containsAll(a)
								&& l != null && l.hasItemMeta() && l.getItemMeta().hasLore() && l.getItemMeta().getLore().containsAll(a)
								&& b != null && b.hasItemMeta() && b.getItemMeta().hasLore() && b.getItemMeta().getLore().containsAll(a))) {
					return set;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		sets = null;
		api = null;
	}
}
