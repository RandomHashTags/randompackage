package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEnchantRarity;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class BlackScroll {
	public static HashMap<String, BlackScroll> scrolls;
	private static YamlConfiguration yml;
	private static Random random;
	private String path;
	private int min, max;
	private ItemStack is;
	private List<AbstractEnchantRarity> appliesto;
	public BlackScroll(String path) {
		if(scrolls == null) {
			scrolls = new HashMap<>();
			random = api.random;
			yml = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder() + File.separator + "custom enchants", "black scrolls.yml"));
		}
		this.path = path;
		final String[] p = yml.getString("scrolls." + path + ".percents").split(";");
		min = Integer.parseInt(p[0]);
		max = Integer.parseInt(p[1]);
		scrolls.put(path, this);
	}
	public String getPath() { return path; }
	public ItemStack getItem() {
		if(is == null) is = api.d(yml, "scrolls." + path);
		return is.clone();
	}
	public ItemStack getRandomizedItemPercent() {
		return getItem(getRandomPercent());
	}
	public ItemStack getItem(int min, int max) {
		final ItemStack is = getItem();
		final ItemMeta m = is.getItemMeta();
		final List<String> l = new ArrayList<>();
		final String mi = Integer.toString(min), ma = Integer.toString(max);
		if(m.hasLore()) {
			for(String s : m.getLore()) {
				l.add(s.replace("{MIN}", mi).replace("{MAX}", ma));
			}
			m.setLore(l);
		}
		is.setItemMeta(m);
		return is;
	}
	public ItemStack getItem(int percent) {
		final ItemStack is = getItem();
		final ItemMeta m = is.getItemMeta();
		final List<String> l = new ArrayList<>();
		final String p = Integer.toString(percent > 100 ? 100 : percent);
		if(m.hasLore()) {
			for(String s : m.getLore()) {
				l.add(s.replace("{PERCENT}", p));
			}
			m.setLore(l);
		}
		is.setItemMeta(m);
		return is;
	}
	public int getMinPercent() { return min; }
	public int getMaxPercent() { return max; }
	public int getRandomPercent() { return min + random.nextInt(max-min+1); }
	public List<AbstractEnchantRarity> getAppliesTo() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : yml.getString("scrolls." + path + ".applies to").split(";")) {
				appliesto.add(AbstractEnchantRarity.rarities.get(s));
			}
		}
		return appliesto;
	}

	public static BlackScroll valueOf(ItemStack is) {
		if(scrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final Material m = is.getType();
			final String d = is.getItemMeta().getDisplayName();
			for(BlackScroll b : scrolls.values()) {
				final ItemStack i = b.getItem();
				if(m.equals(i.getType()) && is.getData().getData() == i.getData().getData() && d.equals(i.getItemMeta().getDisplayName())) {
					return b;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		scrolls = null;
		yml = null;
		random = null;
	}
}
