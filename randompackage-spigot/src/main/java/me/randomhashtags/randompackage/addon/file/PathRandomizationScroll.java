package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RandomizationScroll;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PathRandomizationScroll extends RPAddonSpigot implements RandomizationScroll {
	private final String path;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathRandomizationScroll(String path) {
		super(null);
		this.path = path;
		register(Feature.SCROLL_RANDOMIZATION, this);
	}
	public @NotNull String getIdentifier() { return path; }

	public @NotNull ItemStack getItem() {
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
