package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RarityGem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class FileRarityGem extends RarityGem {
	private ItemStack item;
	private List<EnchantRarity> worksFor;
	private List<String> splitMessage, toggleon, toggleoffInteract, toggleoffDropped, toggleoffMoved, toggleoffRanOut;
	private TreeMap<Integer, String> colors;

	public FileRarityGem(File f) {
		load(f);
		initilize();
	}
	public void initilize() { addRarityGem(getYamlName(), this); }

	public ItemStack getItem() {
		if(item == null) item = api.d(yml, "item");
		return item.clone();
	}
	public ItemStack getItem(int souls) {
		final ItemStack item = getItem();
		final ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SOULS}", getColors(this, souls) + souls));
		item.setItemMeta(itemMeta);
		return item;
	}
	public List<EnchantRarity> getWorksFor() {
		if(worksFor == null) {
			worksFor = new ArrayList<>();
			for(String s : yml.getString("works for rarities").split(";")) {
				worksFor.add(getEnchantRarity(s));
			}
		}
		return worksFor;
	}
	public List<String> getSplitMsg() {
		if(splitMessage == null) splitMessage = api.colorizeListString(yml.getStringList("messages.split"));
		return splitMessage;
	}
	public long getTimeBetweenSameKills() { return yml.getLong("time between same kills"); }
	public TreeMap<Integer, String> getColors() {
		if(colors == null) {
			final ConfigurationSection cs = yml.getConfigurationSection("colors");
			if(cs == null) {
				colors = defaultColors;
			} else {
				colors = new TreeMap<>();
				colors.put(-1, ChatColor.translateAlternateColorCodes('&', yml.getString("colors.else")));
				colors.put(0, ChatColor.translateAlternateColorCodes('&', yml.getString("colors.less than 100")));
				for(String s : cs.getKeys(false)) {
					if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
						colors.put(Integer.parseInt(s.split("s")[0]), ChatColor.translateAlternateColorCodes('&', yml.getString("colors." + s)));
					}
				}
			}
		}
		return colors;
	}
	public List<String> getToggleOnMsg() {
		if(toggleon == null) toggleon = api.colorizeListString(yml.getStringList("messages.toggle on"));
		return toggleon;
	}
	public List<String> getToggleOffInteractMsg() {
		if(toggleoffInteract == null) toggleoffInteract = api.colorizeListString(yml.getStringList("messages.toggle off.interact"));
		return toggleoffInteract;
	}
	public List<String> getToggleOffDroppedMsg() {
		if(toggleoffDropped == null) toggleoffDropped = api.colorizeListString(yml.getStringList("messages.toggle off.dropped"));
		return toggleoffDropped;
	}
	public List<String> getToggleOffMovedMsg() {
		if(toggleoffMoved == null) toggleoffMoved = api.colorizeListString(yml.getStringList("messages.toggle off.moved"));
		return toggleoffMoved;
	}
	public List<String> getToggleOffRanOutMsg() {
		if(toggleoffRanOut == null) toggleoffRanOut = api.colorizeListString(yml.getStringList("messages.toggle off.ran out"));
		return toggleoffRanOut;
	}
}