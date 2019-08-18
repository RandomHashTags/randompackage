package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.MagicDust;
import me.randomhashtags.randompackage.addons.RarityFireball;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PathFireball extends RPAddon implements RarityFireball {
	private String path;
	private ItemStack is;
	private List<EnchantRarity> exchangeablerarities;
	private List<String> reveals;

	public PathFireball(String path) {
		this.path = path;
		addFireball(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = api.d(getAddonConfig("fireballs.yml"), "fireballs." + path);
		return is.clone();
	}
	public ItemStack getRevealedItem(boolean usesChances) {
		getReveals();
		String reward = usesChances ? null : reveals.get(random.nextInt(reveals.size())).split(";")[1];
		if(reward != null) return api.d(null, reward);
		for(String s : reveals) {
			final String[] a = s.split(";");
			final int chance = api.getRemainingInt(a[0]);
			if(random.nextInt(100) <= chance) {
				final int R = ("chance=" + chance + ";").length();
				final String r = s.substring(R);
				final MagicDust d = getDust(r);
				return d.getRandomPercentItem(random);
			}
		}
		return null;
	}
	public List<EnchantRarity> getExchangeableRarities() {
		if(exchangeablerarities == null) {
			exchangeablerarities = new ArrayList<>();
			final String e = getAddonConfig("fireballs.yml").getString("fireballs." + path + ".exchangeable rarities");
			for(String s : e.split(";")) {
				exchangeablerarities.add(getEnchantRarity(s));
			}
		}
		return exchangeablerarities;
	}
	public List<String> getReveals() {
		if(reveals == null) reveals = getAddonConfig("fireballs.yml").getStringList("fireballs." + path + ".reveals");
		return reveals;
	}
}
