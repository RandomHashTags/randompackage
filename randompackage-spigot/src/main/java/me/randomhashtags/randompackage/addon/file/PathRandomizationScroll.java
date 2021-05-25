package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RandomizationScroll;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class PathRandomizationScroll extends RPAddon implements RandomizationScroll {
	private final String path;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathRandomizationScroll(String path) {
		this.path = path;
		register(Feature.SCROLL_RANDOMIZATION, this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = createItemStack(getAddonConfig("scrolls.yml"), "randomization scrolls." + path);
		return getClone(is);
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : getAddonConfig("scrolls.yml").getString("randomization scrolls." + path + ".applies to").split(";")) {
				appliesto.add(getCustomEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
