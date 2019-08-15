package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RarityGem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathRarityGem extends RPAddon implements RarityGem {
	public static HashMap<Integer, String> defaultColors;
	private String path;
	private ItemStack item;
	private List<EnchantRarity> worksFor;
	private List<String> splitMessage, toggleon, toggleoffInteract, toggleoffDropped, toggleoffMoved, toggleoffRanOut;
	private HashMap<Integer, String> colors;

	public PathRarityGem(String path) {
		this.path = path;
		addRarityGem(getIdentifier(), this);
	}
	public String getIdentifier() { return path; }

	public ItemStack getItem() {
		if(item == null) item = api.d(getAddonConfig("rarity gems.yml"), "rarity gems." + path);
		return item.clone();
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
			for(String s : getAddonConfig("rarity gems.yml").getString("rarity gems." + path + ".works for rarities").split(";")) {
				worksFor.add(getEnchantRarity(s));
			}
		}
		return worksFor;
	}
	public List<String> getSplitMsg() {
		if(splitMessage == null) splitMessage = api.colorizeListString(getAddonConfig("rarity gems.yml").getStringList("rarity gems." + path + ".split msg"));
		return splitMessage;
	}
	public long getTimeBetweenSameKills() { return getAddonConfig("rarity gems.yml").getLong("rarity gems." + path + ".time between same kills"); }
	public HashMap<Integer, String> getColors() {
		if(colors == null) {
			final YamlConfiguration config = getAddonConfig("rarity gems.yml");
			final ConfigurationSection cs = config.getConfigurationSection("rarity gems." + path + ".colors");
			if(cs == null) {
				colors = defaultColors;
			} else {
				colors = new HashMap<>();
				colors.put(-1, ChatColor.translateAlternateColorCodes('&', config.getString("rarity gems." + path + ".colors.else")));
				colors.put(0, ChatColor.translateAlternateColorCodes('&', config.getString("rarity gems." + path + ".colors.less than 100")));
				for(String s : cs.getKeys(false)) {
					if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
						colors.put(Integer.parseInt(s.split("s")[0]), ChatColor.translateAlternateColorCodes('&', config.getString("rarity gems." + path + ".colors." + s)));
					}
				}
			}
		}
		return colors;
	}
	public List<String> getToggleOnMsg() {
		if(toggleon == null) toggleon = api.colorizeListString(getAddonConfig("rarity gems.yml").getStringList("rarity gems." + path + ".toggle on"));
		return toggleon;
	}
	public List<String> getToggleOffInteractMsg() {
		if(toggleoffInteract == null) toggleoffInteract = api.colorizeListString(getAddonConfig("rarity gems.yml").getStringList("rarity gems." + path + ".toggle off.interact"));
		return toggleoffInteract;
	}
	public List<String> getToggleOffDroppedMsg() {
		if(toggleoffDropped == null) toggleoffDropped = api.colorizeListString(getAddonConfig("rarity gems.yml").getStringList("rarity gems." + path + ".toggle off.dropped"));
		return toggleoffDropped;
	}
	public List<String> getToggleOffMovedMsg() {
		if(toggleoffMoved == null) toggleoffMoved = api.colorizeListString(getAddonConfig("rarity gems.yml").getStringList("rarity gems." + path + ".toggle off.moved"));
		return toggleoffMoved;
	}
	public List<String> getToggleOffRanOutMsg() {
		if(toggleoffRanOut == null) toggleoffRanOut = api.colorizeListString(getAddonConfig("rarity gems.yml").getStringList("rarity gems." + path + ".toggle off.ran out"));
		return toggleoffRanOut;
	}
	public String getColors(int soulsCollected) {
		final HashMap<Integer, String> colors = getColors();
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