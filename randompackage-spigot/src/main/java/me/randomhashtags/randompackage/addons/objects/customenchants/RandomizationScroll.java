package me.randomhashtags.randompackage.addons.objects.customenchants;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class RandomizationScroll {
	public static HashMap<String, RandomizationScroll> scrolls;
	private static YamlConfiguration yml;
	private String path;
	private ItemStack is;
	private List<EnchantRarity> appliesto;
	public RandomizationScroll(String path) {
		if(scrolls == null) {
			scrolls = new HashMap<>();
			yml = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder() + File.separator + "custom enchants", "randomization scrolls.yml"));
		}
		this.path = path;
		scrolls.put(path, this);
	}
	public String getPath() { return path; }
	public ItemStack getItem() {
		if(is == null) is = api.d(yml, "scrolls." + path);
		return is.clone();
	}
	public List<EnchantRarity> getAppliesToRarities() {
		if(appliesto == null) {
			appliesto = new ArrayList<>();
			for(String s : yml.getString("scrolls." + path + ".applies to").split(";")) {
				appliesto.add(EnchantRarity.rarities.get(s));
			}
		}
		return appliesto;
	}

	public static RandomizationScroll valueOf(ItemStack is) {
		if(scrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final ItemMeta m = is.getItemMeta();
			for(RandomizationScroll r : scrolls.values()) {
				if(r.getItem().getItemMeta().equals(m)) {
					return r;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		scrolls = null;
		yml = null;
	}
}
