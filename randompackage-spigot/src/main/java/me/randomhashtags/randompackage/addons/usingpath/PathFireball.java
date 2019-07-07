package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.RarityFireball;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PathFireball extends RarityFireball {
	public static YamlConfiguration fireballyml;
	
	private String path;
	private ItemStack is;
	private List<EnchantRarity> exchangeablerarities;
	private List<String> reveals;

	public PathFireball(String path) {
		this.path = path;
		addFireball(getIdentifier(), this);
		fireballs.put(path, this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = api.d(fireballyml, "fireballs." + path);
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
	public List<EnchantRarity> getExchangeableRarities() {
		if(exchangeablerarities == null) {
			exchangeablerarities = new ArrayList<>();
			final String e = fireballyml.getString("fireballs." + path + ".exchangeable rarities");
			for(String s : e.split(";")) {
				exchangeablerarities.add(getEnchantRarity(s));
			}
		}
		return exchangeablerarities;
	}
	public List<String> getReveals() {
		if(reveals == null) reveals = fireballyml.getStringList("fireballs." + path + ".reveals");
		return reveals;
	}
}
