package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.MagicDust;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PathMagicDust extends MagicDust {
	private String path;
	private PathMagicDust upgradesto;
	private ItemStack is;
	private int chance, min, max;
	private double upgradecost;
	private List<EnchantRarity> appliesto;
	public PathMagicDust(String path) {
		this.path = path;
		final YamlConfiguration config = getAddonConfig("fireballs.yml");
		chance = config.getInt("dusts." + path + ".chance");
		final String[] a = config.getString("dusts." + path + ".percents").split(";");
		min = Integer.parseInt(a[0]);
		max = Integer.parseInt(a[1]);
		upgradecost = config.getInt("dusts." + path + ".upgrade cost");
		addDust(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public int getChance() { return chance; }
	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	public ItemStack getItem() {
		if(is == null) is = api.d(getAddonConfig("fireballs.yml"), "dusts." + path);
		return is.clone();
	}

	public List<EnchantRarity> getAppliesTo() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : getAddonConfig("fireballs.yml").getString("dusts." + path + ".applies to").split(";")) {
				appliesto.add(rarities.get(s));
			}
		}
		return appliesto;
	}
	public MagicDust getUpgradesTo() {
		if(upgradesto != null) return upgradesto;
		final String U = getAddonConfig("fireballs.yml").getString("dusts." + path + ".upgrades to", null);
		if(U != null) upgradesto = getDust(U);
		return upgradesto;
	}
	public double getUpgradeCost() { return upgradecost; }
}
