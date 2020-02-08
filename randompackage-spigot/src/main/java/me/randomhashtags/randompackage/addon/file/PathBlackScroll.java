package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.BlackScroll;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PathBlackScroll extends RPAddon implements BlackScroll {
	private String identifier;
	private int min, max;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathBlackScroll(String identifier) {
		this.identifier = identifier;
		final String[] p = getAddonConfig("scrolls.yml").getString("black scrolls." + identifier + ".percents").split(";");
		min = Integer.parseInt(p[0]);
		max = Integer.parseInt(p[1]);
		register(Feature.SCROLL_BLACK, this);
	}
	public String getIdentifier() { return identifier; }

	public ItemStack getItem() {
		if(is == null) is = API.createItemStack(getAddonConfig("scrolls.yml"), "black scrolls." + identifier);
		return getClone(is);
	}

	public int getMinPercent() {
		return min;
	}
	public int getMaxPercent() {
		return max;
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : getAddonConfig("scrolls.yml").getString("black scrolls." + identifier + ".applies to").split(";")) {
				appliesto.add(getCustomEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
