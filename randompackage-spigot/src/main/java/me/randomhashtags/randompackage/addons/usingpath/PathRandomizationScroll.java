package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RandomizationScroll;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.utils.CustomEnchantUtils.addons;

public class PathRandomizationScroll extends RandomizationScroll {
	private String path;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathRandomizationScroll(String path) {
		this.path = path;
		addRandomizationScroll(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(is == null) is = api.d(addons, "randomization scrolls." + path);
		return is.clone();
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : addons.getString("randomization scrolls." + path + ".applies to").split(";")) {
				appliesto.add(getEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
