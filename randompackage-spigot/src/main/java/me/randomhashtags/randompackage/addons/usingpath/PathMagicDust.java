package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.MagicDust;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.utils.CustomEnchantUtils.addons;

public class PathMagicDust extends MagicDust {
	private String path;
	private PathMagicDust upgradesto;
	private ItemStack is;
	private int chance, min, max;
	private double upgradecost;
	private List<EnchantRarity> appliesto;
	public PathMagicDust(String path) {
		this.path = path;
		chance = addons.getInt("dusts." + path + ".chance");
		final String[] a = addons.getString("dusts." + path + ".percents").split(";");
		min = Integer.parseInt(a[0]);
		max = Integer.parseInt(a[1]);
		upgradecost = addons.getInt("dusts." + path + ".upgrade cost");
		addDust(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public int getChance() { return chance; }
	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	public ItemStack getItem() {
		if(is == null) is = api.d(addons, "dusts." + path);
		return is.clone();
	}

	public List<EnchantRarity> getAppliesTo() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : addons.getString("dusts." + path + ".applies to").split(";")) {
				appliesto.add(EnchantRarity.rarities.get(s));
			}
		}
		return appliesto;
	}
	public MagicDust getUpgradesTo() {
		if(upgradesto != null) return upgradesto;
		final String U = addons.getString("dusts." + path + ".upgrades to", null);
		if(U != null) upgradesto = getDust(U);
		return upgradesto;
	}
	public double getUpgradeCost() { return upgradecost; }
}
