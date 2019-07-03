package me.randomhashtags.randompackage.addons.objects.customenchants;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class SoulTracker {
	private static YamlConfiguration yml;
	public static HashMap<String, SoulTracker> trackers;

	private String path, tracks, appliedlore;
	private String[] appliesto;
	private RarityGem convertsTo;
	private ItemStack is;
	private List<String> applymsg, splitmsg;
	private double soulsPerKill;

	public SoulTracker(String path) {
		if(trackers == null) {
			trackers = new HashMap<>();
			yml = YamlConfiguration.loadConfiguration(new File(getPlugin.getDataFolder() + File.separator + "custom enchants", "soul trackers.yml"));
		}
		this.path = path;
		soulsPerKill = yml.getDouble("trackers." + path + ".souls per kill");
		trackers.put(path, this);
	}

	public String getPath() { return path; }
	public String getTracks() {
		if(tracks == null) tracks = yml.getString("trackers." + path + ".tracks");
		return tracks;
	}
	public String[] getAppliesTo() {
		if(appliesto == null) appliesto = yml.getString("trackers." + path + ".applies to").split(";");
		return appliesto;
	}
	public RarityGem getConvertsTo() {
		if(convertsTo == null) convertsTo = RarityGem.gems.getOrDefault(yml.getString("trackers." + path + ".converts to gem"), null);
		return convertsTo;
	}
	public ItemStack getItem() {
		if(is == null) is = api.d(yml, "trackers." + path);
		return is.clone();
	}
	public String getAppliedLore() {
		if(appliedlore == null) appliedlore = ChatColor.translateAlternateColorCodes('&', yml.getString("trackers." + path + ".apply"));
		return appliedlore;
	}
	public List<String> getApplyMessage() {
		if(applymsg == null) applymsg = api.colorizeListString(yml.getStringList("trackers." + path + ".apply msg"));
		return applymsg;
	}
	public double getSoulsPerKill() { return soulsPerKill; }
	public List<String> getSplitMessage() {
		if(splitmsg == null) splitmsg = api.colorizeListString(yml.getStringList("trackers." + path + ".split msg"));
		return splitmsg;
	}

	public static SoulTracker valueOf(RarityGem gem) {
		if(trackers != null)
			for(SoulTracker st : trackers.values())
				if(st.getConvertsTo().equals(gem))
					return st;
		return null;
	}
	public static SoulTracker valueOf(ItemStack is) {
		if(trackers != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final ItemMeta m = is.getItemMeta();
			for(SoulTracker s : trackers.values()) {
				if(s.getItem().getItemMeta().equals(m)) {
					return s;
				}
			}
		}
		return null;
	}
	public static SoulTracker valueOf(String appliedlore) {
		if(trackers != null) {
			for(SoulTracker st : trackers.values()) {
				if(appliedlore.startsWith(st.getAppliedLore().replace("{SOULS}", ""))) {
					return st;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		yml = null;
		trackers = null;
	}
}
