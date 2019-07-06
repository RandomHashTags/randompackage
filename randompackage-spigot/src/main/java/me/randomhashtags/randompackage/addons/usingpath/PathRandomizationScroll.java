package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RandomizationScroll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PathRandomizationScroll extends RandomizationScroll {
	public static YamlConfiguration randomizationscrollsyml;
	private String path;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathRandomizationScroll(String path) {
		this.path = path;
		addRandomizationScroll(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = api.d(randomizationscrollsyml, "scrolls." + path);
		return is.clone();
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : randomizationscrollsyml.getString("scrolls." + path + ".applies to").split(";")) {
				appliesto.add(getEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
