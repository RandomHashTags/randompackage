package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.dev.Feature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileRarityGem extends RPAddon implements RarityGem {
	public static HashMap<Integer, String> defaultColors;
	private ItemStack item;
	private List<EnchantRarity> worksFor;
	private HashMap<Integer, String> colors;

	public FileRarityGem(File f) {
		load(f);
		register(Feature.RARITY_GEM, this);
	}
	public String getIdentifier() { return getYamlName(); }

	public ItemStack getItem() {
		if(item == null) item = api.d(yml, "item");
		return getClone(item);
	}
	public ItemStack getItem(int souls) {
		final ItemStack item = getItem();
		final ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SOULS}", getColors(souls) + souls));
		item.setItemMeta(itemMeta);
		return item;
	}
	public List<EnchantRarity> getWorksFor() {
		if(worksFor == null) {
			worksFor = new ArrayList<>();
			for(String s : yml.getString("settings.works for rarities").split(";")) {
				worksFor.add(getCustomEnchantRarity(s));
			}
		}
		return worksFor;
	}
	public List<String> getSplitMsg() { return colorizeListString(yml.getStringList("messages.split")); }
	public long getTimeBetweenSameKills() { return yml.getLong("settings.time between same kills"); }
	public HashMap<Integer, String> getColors() {
		if(colors == null) {
			final ConfigurationSection cs = yml.getConfigurationSection("colors");
			if(cs != null) {
				colors = new HashMap<>();
				colors.put(-1, colorize(yml.getString("colors.else")));
				colors.put(0, colorize(yml.getString("colors.less than 100")));
				for(String s : cs.getKeys(false)) {
					if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
						colors.put(Integer.parseInt(s.split("s")[0]), colorize(yml.getString("colors." + s)));
					}
				}
			}
		}
		return colors;
	}
	public List<String> getToggleOnMsg() { return colorizeListString(yml.getStringList("messages.toggle on")); }
	public List<String> getToggleOffInteractMsg() { return colorizeListString(yml.getStringList("messages.toggle off.interact")); }
	public List<String> getToggleOffDroppedMsg() { return colorizeListString(yml.getStringList("messages.toggle off.dropped")); }
	public List<String> getToggleOffMovedMsg() { return colorizeListString(yml.getStringList("messages.toggle off.moved")); }
	public List<String> getToggleOffRanOutMsg() { return colorizeListString(yml.getStringList("messages.toggle off.ran out")); }
	public String getColors(int soulsCollected) {
		HashMap<Integer, String> colors = getColors();
		if(colors == null) colors = defaultColors;
		if(soulsCollected < 100) return colors.get(0);
		int last = -1;
		for(int i = 100; i <= 1000000; i += 100) {
			if(soulsCollected >= i && soulsCollected < i + 100) {
				final String c = colors.get(i);
				final boolean d = c != null;
				if(d) last += 1;
				return d ? c : colors.get(last);
			}
		}
		return colors.get(-1);
	}
}