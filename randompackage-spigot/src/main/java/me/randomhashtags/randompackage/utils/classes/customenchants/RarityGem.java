package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.recode.api.addons.EnchantRarity;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class RarityGem {
	public static HashMap<String, RarityGem> gems;
	public static HashMap<Integer, String> defaultColors;

	private YamlConfiguration yml;
	private String path;
	private int timeBetweenSameKills;
	private ItemStack item;
	private List<EnchantRarity> worksFor;
	private List<String> splitMessage, toggleon, toggleoffInteract, toggleoffDropped, toggleoffMoved, toggleoffRanOut;
	private HashMap<Integer, String> colors;

	public RarityGem(YamlConfiguration yml, String path) {
		if(gems == null) {
			gems = new HashMap<>();
		}
		this.yml = yml;
		this.path = path;
		timeBetweenSameKills = -1;
		gems.put(path, this);
	}
	public String getPath() { return path; }
	public ItemStack getItem() {
		if(item == null) item = api.d(yml, "gems." + path);
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
			for(String s : yml.getString("gems." + path + ".works for rarities").split(";")) {
				worksFor.add(EnchantRarity.rarities.get(s));
			}
		}
		return worksFor;
	}
	public List<String> getSplitMessage() {
		if(splitMessage == null) splitMessage = api.colorizeListString(yml.getStringList("gems." + path + ".split msg"));
		return splitMessage;
	}
	public int getTimeBetweenSameKills() {
		if(timeBetweenSameKills == -1) timeBetweenSameKills = yml.getInt("gems." + path + ".time between same kills");
		return timeBetweenSameKills;
	}
	public HashMap<Integer, String> getColors() {
		if(colors == null) {

			final ConfigurationSection cs = yml.getConfigurationSection("gems." + path + ".colors");
			if(cs == null) {
				colors = defaultColors;
			} else {
				colors = new HashMap<>();
				colors.put(-1, ChatColor.translateAlternateColorCodes('&', yml.getString("gems." + path + ".colors.else")));
				colors.put(0, ChatColor.translateAlternateColorCodes('&', yml.getString("gems." + path + ".colors.less than 100")));
				for(String s : cs.getKeys(false)) {
					if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
						colors.put(Integer.parseInt(s.split("s")[0]), ChatColor.translateAlternateColorCodes('&', yml.getString("gems." + s + ".colors." + s)));
					}
				}
			}
		}
		return colors;
	}
	public List<String> getToggleOnMsg() {
		if(toggleon == null) toggleon = api.colorizeListString(yml.getStringList("gems." + path + ".toggle on"));
		return toggleon;
	}
	public List<String> getToggleOffInteractMsg() {
		if(toggleoffInteract == null) toggleoffInteract = api.colorizeListString(yml.getStringList("gems." + path + ".toggle off.interact"));
		return toggleoffInteract;
	}
	public List<String> getToggleOffDroppedMsg() {
		if(toggleoffDropped == null) toggleoffDropped = api.colorizeListString(yml.getStringList("gems." + path + ".toggle off.dropped"));
		return toggleoffDropped;
	}
	public List<String> getToggleOffMovedMsg() {
		if(toggleoffMoved == null) toggleoffMoved = api.colorizeListString(yml.getStringList("gems." + path + ".toggle off.moved"));
		return toggleoffMoved;
	}
	public List<String> getToggleOffRanOutMsg() {
		if(toggleoffRanOut == null) toggleoffRanOut = api.colorizeListString(yml.getStringList("gems." + path + ".toggle off.ran out"));
		return toggleoffRanOut;
	}

	public static RarityGem valueOf(ItemStack item) {
		if(gems != null && item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			final List<String> l = item.getItemMeta().getLore();
			for(RarityGem g : gems.values())
				if(g.getItem().getItemMeta().getLore().equals(l))
					return g;
			}
		return null;
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
	public static String getColors(RarityGem gem, int soulsCollected) {
		return gem.getColors(soulsCollected);
	}

	public static void deleteAll() {
		gems = null;
		defaultColors = null;
	}
}