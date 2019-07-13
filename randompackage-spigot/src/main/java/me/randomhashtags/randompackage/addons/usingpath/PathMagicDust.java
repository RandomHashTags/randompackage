package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.MagicDust;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PathMagicDust extends MagicDust {
	public static YamlConfiguration fireballyml;

	private String path;
	private PathMagicDust upgradesto;
	private ItemStack is;
	private int chance, min, max;
	private double upgradecost;
	private List<EnchantRarity> appliesto;
	public PathMagicDust(String path) {
		this.path = path;
		chance = fireballyml.getInt("dusts." + path + ".chance");
		final String[] a = fireballyml.getString("dusts." + path + ".percents").split(";");
		min = Integer.parseInt(a[0]);
		max = Integer.parseInt(a[1]);
		upgradecost = fireballyml.getInt("dusts." + path + ".upgrade cost");
		addDust(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public int getChance() { return chance; }
	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	public ItemStack getItem() {
		if(is == null) is = api.d(fireballyml, "dusts." + path);
		return is.clone();
	}

	public List<EnchantRarity> getAppliesTo() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : fireballyml.getString("dusts." + path + ".applies to").split(";")) {
				appliesto.add(EnchantRarity.rarities.get(s));
			}
		}
		return appliesto;
	}
	public MagicDust getUpgradesTo() {
		if(upgradesto != null) return upgradesto;
		final String U = fireballyml.getString("dusts." + path + ".upgrades to", null);
		if(U != null) upgradesto = getDust(U);
		return upgradesto;
	}
	public double getUpgradeCost() { return upgradecost; }
}
