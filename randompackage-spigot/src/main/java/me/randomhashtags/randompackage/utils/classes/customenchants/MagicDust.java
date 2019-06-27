package me.randomhashtags.randompackage.utils.classes.customenchants;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class MagicDust {
	public static HashMap<String, MagicDust> dust;
	private static YamlConfiguration dusts;

	private String path;
	private MagicDust upgradesto;
	private ItemStack is;
	private int chance, minpercent, maxpercent, upgradecost;
	private List<EnchantRarity> appliesto;
	public MagicDust(String path) {
		if(dust == null) {
			dust = new HashMap<>();
			dusts = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder() + File.separator + "custom enchants", "dusts.yml"));
		}
		this.path = path;
		chance = dusts.getInt("dusts." + path + ".chance");
		final String[] a = dusts.getString("dusts." + path + ".percents").split(";");
		minpercent = Integer.parseInt(a[0]);
		maxpercent = Integer.parseInt(a[1]);
		upgradecost = dusts.getInt("dusts." + path + ".upgrade cost");
		dust.put(path, this);
	}
	public String getPath() { return path; }
	public ItemStack getPlainItem() {
		if(is == null) is = api.d(dusts, "dusts." + path);
		return is.clone();
	}
	public ItemStack getItem() {
		return getItem(minpercent+(api.random.nextInt(maxpercent-minpercent+1)));
	}
	public ItemStack getItem(int percent) {
		final String p = Integer.toString(percent);
		final ItemStack i = getPlainItem();
		final ItemMeta m = i.getItemMeta();
		final List<String> l = new ArrayList<>();
		for(String s : m.getLore()) {
			l.add(s.replace("{PERCENT}", p));
		}
		m.setLore(l);
		i.setItemMeta(m);
		return i;
	}
	public int getChance() { return chance; }
	public int getMinPercent() { return minpercent; }
	public int getMaxPercent() { return maxpercent; }
	public List<EnchantRarity> getAppliesTo() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : dusts.getString("dusts." + path + ".applies to").split(";")) {
				appliesto.add(EnchantRarity.rarities.get(s));
			}
		}
		return appliesto;
	}
	public MagicDust getUpgradesTo() {
		final Object U = dusts.get("dusts." + path + ".upgrades to");
		if(upgradesto == null && U != null) {
			upgradesto = MagicDust.dust.get(U);
		}
		return upgradesto;
	}
	public int getUpgradeCost() { return upgradecost; }
	
	public static MagicDust valueOf(ItemStack is) {
		if(dust != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final Material m = is.getType();
			final String d = is.getItemMeta().getDisplayName();
			for(MagicDust dust : dust.values()) {
				final ItemStack i = dust.getPlainItem();
				if(i.getType().equals(m) && i.getItemMeta().getDisplayName().equals(d)) return dust;
			}
		}
		return null;
	}

	public static void deleteAll() {
		dust = null;
		dusts = null;
	}
}
