package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.MagicDust;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PathMagicDust extends RPAddonSpigot implements MagicDust {
	private final String path;
	private MagicDust upgradesto;
	private ItemStack is;
	private final int chance;
    private final int min;
    private final int max;
	private final BigDecimal upgradecost;
	private List<EnchantRarity> appliesto;
	public PathMagicDust(String path) {
		super(null);
		this.path = path;
		final YamlConfiguration config = getAddonConfig("fireballs.yml");
		chance = config.getInt("dusts." + path + ".chance");
		final String[] a = config.getString("dusts." + path + ".percents").split(";");
		min = Integer.parseInt(a[0]);
		max = Integer.parseInt(a[1]);
		upgradecost = BigDecimal.valueOf(config.getInt("dusts." + path + ".upgrade cost"));
		register(Feature.MAGIC_DUST, this);
	}
	public @NotNull String getIdentifier() { return path; }

	public int getChance() { return chance; }
	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	@NotNull
	@Override
	public ItemStack getItem() {
		if(is == null) is = createItemStack(getAddonConfig("fireballs.yml"), "dusts." + path);
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
