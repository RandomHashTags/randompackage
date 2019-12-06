package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.MagicDust;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PathMagicDust extends RPAddon implements MagicDust {
	private String path;
	private MagicDust upgradesto;
	private ItemStack is;
	private int chance, min, max;
	private BigDecimal upgradecost;
	private List<EnchantRarity> appliesto;
	public PathMagicDust(String path) {
		this.path = path;
		final YamlConfiguration config = getAddonConfig("fireballs.yml");
		chance = config.getInt("dusts." + path + ".chance");
		final String[] a = config.getString("dusts." + path + ".percents").split(";");
		min = Integer.parseInt(a[0]);
		max = Integer.parseInt(a[1]);
		upgradecost = BigDecimal.valueOf(config.getInt("dusts." + path + ".upgrade cost"));
		register(Feature.MAGIC_DUST, this);
	}
	public String getIdentifier() { return path; }

	public int getChance() { return chance; }
	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	public ItemStack getItem() {
		if(is == null) is = api.d(getAddonConfig("fireballs.yml"), "dusts." + path);
		return getClone(is);
	}

	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : getAddonConfig("fireballs.yml").getString("dusts." + path + ".applies to").split(";")) {
				appliesto.add(getCustomEnchantRarity(s));
			}
		}
		return appliesto;
	}
	public MagicDust getUpgradesTo() {
		if(upgradesto != null) return upgradesto;
		final String U = getAddonConfig("fireballs.yml").getString("dusts." + path + ".upgrades to", null);
		if(U != null) upgradesto = getMagicDust(U);
		return upgradesto;
	}
	public BigDecimal getUpgradeCost() { return upgradecost; }
}
