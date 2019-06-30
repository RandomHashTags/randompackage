package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEnchantRarity;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class Fireball {
	public static HashMap<String, Fireball> fireballs;
	private static Random random;
	private static YamlConfiguration config;
	
	private String path;
	private ItemStack is;
	private List<AbstractEnchantRarity> exchangeablerarities;
	private List<String> reveals;

	public Fireball(String path) {
		if(fireballs == null) {
			fireballs = new HashMap<>();
			random = api.random;
			config = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder() + File.separator + "custom enchants", "fireballs.yml"));
		}
		this.path = path;
		fireballs.put(path, this);
	}
	public String getPath() { return path; }
	public ItemStack getItem() {
		if(is == null) is = api.d(config, "fireballs." + path);
		return is.clone();
	}
	public ItemStack getRevealedItem(boolean usesChances) {
		getReveals();
		String reward = usesChances ? null : reveals.get(random.nextInt(reveals.size())).split(";")[1];
		if(reward != null) return api.d(null, reward);
		for(String s : reveals) {
			final String[] a = s.split(";");
			final boolean ELSE = a[0].toLowerCase().startsWith("else");
			final int chance = ELSE ? 100 : api.getRemainingInt(a[0]);
			if(random.nextInt(100) <= chance) {
				final int R = ((ELSE ? "else" : "chance=" + chance) + ";").length();
				return api.d(null, s.substring(R));
			}
		}
		return null;
	}
	public List<AbstractEnchantRarity> getExchangeableRarities() {
		if(exchangeablerarities == null) {
			exchangeablerarities = new ArrayList<>();
			final String e = config.getString("fireballs." + path + ".exchangeable rarities");
			for(String s : e.split(";")) {
				exchangeablerarities.add(AbstractEnchantRarity.rarities.get(s));
			}
		}
		return exchangeablerarities;
	}
	public List<String> getReveals() {
		if(reveals == null) reveals = config.getStringList("fireballs." + path + ".reveals");
		return reveals;
	}
	
	public static Fireball valueOf(ItemStack is) {
		if(fireballs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			for(Fireball f : fireballs.values())
				if(is.isSimilar(f.getItem()))
					return f;
		}
		return null;
	}
	public static Fireball valueOf(List<AbstractEnchantRarity> exchangeablerarities) {
		if(fireballs != null) {
			for(Fireball f : fireballs.values()) {
				if(f.getExchangeableRarities().equals(exchangeablerarities)) {
					return f;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		fireballs = null;
		random = null;
		config = null;
	}
}
