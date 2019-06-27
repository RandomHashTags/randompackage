package me.randomhashtags.randompackage.utils.classes.servercrate;

import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class ServerCrate {
	public static HashMap<String, ServerCrate> crates;
	private static Random random;

	private YamlConfiguration yml;
	private String ymlName, displayrarity, bossReward;
	private int redeemableItems;
	private UInventory inv;
	private LinkedHashMap<String, Integer> revealChances;
	private ItemStack physicalItem, display, opengui, selected, revealSlotRarity, background, background2;
	private HashMap<String, List<String>> rewards;
	private List<Integer> selectableslots;
	private List<String> format;
	private ServerCrateFlare flare;
	public ServerCrate(File f) {
		if(crates == null) {
			crates = new HashMap<>();
			random = api.random;
		}
		yml = YamlConfiguration.loadConfiguration(f);
		ymlName = f.getName().split("\\.yml")[0];
		redeemableItems = yml.getInt("settings.redeemable items");
		crates.put(ymlName, this);
	}
	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return ymlName; }
	public int getRedeemableItems() { return redeemableItems; }
	public String getDisplayRarity() {
		if(displayrarity == null) displayrarity = ChatColor.translateAlternateColorCodes('&', yml.getString("display.rarity"));
		return displayrarity;
	}
	public List<Integer> getSelectableSlots() {
		return selectableslots;
	}
	public UInventory getInventory() {
		if(inv == null) {
			inv = new UInventory(null, yml.getInt("settings.size"), ChatColor.translateAlternateColorCodes('&', yml.getString("settings.title")));
			final Inventory ii = inv.getInventory();
			final List<String> f = getFormat();
			final ItemStack BG = getBackground(), air = new ItemStack(Material.AIR), O = getOpenGui();
			selectableslots = new ArrayList<>();
			for(int i = 0; i < f.size(); i++) {
				final String s = f.get(i);
				for(int o = 0; o < s.length(); o++) {
					final int slot = i*9+o;
					final String t = s.substring(o, o+1);
					final boolean plus = t.equals("+");
					final ItemStack item = t.equals("-") ? BG : plus ? O : air;
					if(plus) {
						final ItemMeta m = item.getItemMeta();
						if(m.hasDisplayName()) m.setDisplayName(m.getDisplayName().replace("{SLOT}", Integer.toString(slot)));
						item.setItemMeta(m);
						selectableslots.add(slot);
					}
					ii.setItem(slot, item);
				}
			}
		}
		return inv;
	}
	public List<String> getFormat() {
		if(format == null) {
			format = yml.getStringList("settings.format");
		}
		return format;
	}
	public LinkedHashMap<String, Integer> getRevealChances() {
		if(revealChances == null) {
			revealChances = new LinkedHashMap<>();
			for(String s : yml.getConfigurationSection("reveal chances").getKeys(false)) {
				revealChances.put(s, yml.getInt("reveal chances." + s));
			}
		}
		return revealChances;
	}
	public ItemStack getPhyiscalItem() {
		if(physicalItem == null) physicalItem = api.d(yml, "item");
		return physicalItem;
	}
	public String getBossReward() {
		if(bossReward == null) bossReward = ChatColor.translateAlternateColorCodes('&', yml.getString("boss reward"));
		return bossReward;
	}
	public ItemStack getDisplay() {
		if(display == null) display = api.d(yml, "display");;
		return display.clone();
	}
	public ItemStack getOpenGui() {
		if(opengui == null) opengui = api.d(yml, "open gui");
		return opengui.clone();
	}
	public ItemStack getSelected() {
		if(selected == null) selected = api.d(yml, "selected");
		return selected.clone();
	}
	public ItemStack getRevealSlotRarity() {
		if(revealSlotRarity == null) revealSlotRarity = api.d(yml, "reveal slot rarity");
		return revealSlotRarity.clone();
	}
	public HashMap<String, List<String>> getRewards() {
		if(rewards == null) {
			rewards = new HashMap<>();
			for(String s : yml.getConfigurationSection("rewards").getKeys(false)) {
				rewards.put(s, yml.getStringList("rewards." + s));
			}
		}
		return rewards;
	}
	public ItemStack getBackground() {
		if(background == null) background = api.d(yml, "settings.background");
		return background.clone();
	}
	public ItemStack getBackground2() {
		if(background2 == null) background2 = api.d(yml, "settings.background 2");
		return background2.clone();
	}
	public ServerCrateFlare getFlare() {
		if(flare == null) flare = new ServerCrateFlare(api.d(yml, "flare"), yml.getStringList("flare.request msg"), yml.getInt("flare.settings.spawn radius"), yml.getInt("flare.settings.spawn in delay"), yml.getInt("flare.settings.nearby radius"), yml.getStringList("flare.nearby spawn msg"));
		return flare;
	}
	
	public ServerCrate getRandomRarity(boolean useChances) {
		String rarity = null;
		final Collection<String> key = getRewards().keySet();
		if(!useChances) {
			rarity = (String)key.toArray()[random.nextInt(key.size())];
		} else {
			final LinkedHashMap<String, Integer> r = getRevealChances();
			for(String s : key) if(random.nextInt(100) <= r.get(s)) rarity = s;
			if(rarity == null) rarity = (String) r.keySet().toArray()[r.keySet().size()-1];
		}
		return crates.getOrDefault(rarity, null);
	}
	public ItemStack getRandomReward(String rarity) {
		final String reward = getRewards().get(rarity).get(random.nextInt(rewards.get(rarity).size()));
		return api.d(null, reward);
	}
	public static ServerCrate valueOf(ItemStack serverCrateItem) {
		if(crates != null) {
			for(ServerCrate crate : crates.values()) {
				if(crate.getPhyiscalItem().isSimilar(serverCrateItem)) {
					return crate;
				}
			}
		}
		return null;
	}
	public static ServerCrate valueOfFlare(ItemStack flare) {
		if(crates != null) {
			for(ServerCrate s : crates.values()) {
				final ServerCrateFlare f = s.flare;
				if(f != null && f.getItem().isSimilar(flare)) {
					return s;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		crates = null;
		random = null;
	}
}
