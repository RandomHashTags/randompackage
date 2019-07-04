package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RandomizationScroll;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileRandomizationScroll extends RandomizationScroll {
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public FileRandomizationScroll(File f) {
		load(f);
		initilize();
	}
	public void initilize() { addRandomizationScroll(getYamlName(), this); }

	public ItemStack getItem() {
		if(is == null) is = api.d(yml, "item");
		return is.clone();
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : yml.getString("applies to").split(";")) {
				appliesto.add(getEnchantRarity(s));
			}
		}
		return appliesto;
	}
}
