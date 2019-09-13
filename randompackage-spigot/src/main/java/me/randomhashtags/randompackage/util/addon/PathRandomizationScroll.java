package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RandomizationScroll;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PathRandomizationScroll extends RPAddon implements RandomizationScroll {
	private String path;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathRandomizationScroll(String path) {
		this.path = path;
		addRandomizationScroll(this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = api.d(getAddonConfig("randomization scrolls.yml"), "randomization scrolls." + path);
		return getClone(is);
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : getAddonConfig("randomization scrolls.yml").getString("randomization scrolls." + path + ".applies to").split(";")) {
				appliesto.add(getEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
