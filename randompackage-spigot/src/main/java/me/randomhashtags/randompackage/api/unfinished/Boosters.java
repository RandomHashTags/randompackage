package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;

public class Boosters extends RPFeature {
	private static Boosters instance;
	public static Boosters getFactionAdditions() {
	    if(instance == null) instance = new Boosters();
	    return instance;
	}

	public YamlConfiguration config;
	private ItemStack factionMCMMOBooster, factionXPBooster;

	public void load() {
		final long started = System.currentTimeMillis();

		config = YamlConfiguration.loadConfiguration(new File(rpd, "faction additions.yml"));

		factionMCMMOBooster = d(config, "items.faction mcmmo booster");
		factionXPBooster = d(config, "items.faction xp booster");
		addGivedpCategory(Arrays.asList(factionMCMMOBooster, factionXPBooster), UMaterial.DIAMOND_SWORD, "Faction Items", "Givedp: Faction Items");

		sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Upgrades &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {

	}

	public ItemStack getBooster(double multiplier, long time, boolean xp) {
		final String t = getRemainingTime(time), m = formatDouble(multiplier);
		item = (xp ? factionXPBooster : factionMCMMOBooster).clone();
		itemMeta = item.getItemMeta(); lore.clear();
		for(String s : itemMeta.getLore()) {
			lore.add(s.replace("{MULTIPLIER}", m).replace("{TIME}", t));
		}
		itemMeta.setLore(lore); lore.clear();
		item.setItemMeta(itemMeta);
		return item;
	}
}
