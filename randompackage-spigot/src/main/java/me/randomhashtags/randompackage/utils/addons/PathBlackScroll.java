package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.BlackScroll;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PathBlackScroll extends RPAddon implements BlackScroll {
	private String path;
	private int min, max;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathBlackScroll(String path) {
		this.path = path;
		final String[] p = getAddonConfig("black scrolls.yml").getString("black scrolls." + path + ".percents").split(";");
		min = Integer.parseInt(p[0]);
		max = Integer.parseInt(p[1]);
		addBlackScroll(this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = api.d(getAddonConfig("black scrolls.yml"), "black scrolls." + path);
		return is.clone();
	}

	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : getAddonConfig("black scrolls.yml").getString("black scrolls." + path + ".applies to").split(";")) {
				appliesto.add(getEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
