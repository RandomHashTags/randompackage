package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.BlackScroll;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PathBlackScroll extends RPAddonSpigot implements BlackScroll {
	private final String identifier;
	private final int min;
    private final int max;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public PathBlackScroll(@NotNull String identifier) {
		this.identifier = identifier;
		final String[] p = getAddonConfig("scrolls.yml").getString("black scrolls." + identifier + ".percents").split(";");
		min = Integer.parseInt(p[0]);
		max = Integer.parseInt(p[1]);
		register(Feature.SCROLL_BLACK, this);
	}
	@NotNull
	@Override
	public String getIdentifier() { return identifier; }

	@NotNull
	@Override
	public ItemStack getItem() {
		if(is == null) is = createItemStack(getAddonConfig("scrolls.yml"), "black scrolls." + identifier);
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
