package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ServerCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class FileServerCrate extends RPAddon implements ServerCrate {
	private UInventory inv;
	private LinkedHashMap<String, Integer> revealChances;
	private ItemStack physicalItem, display, opengui, selected, revealSlotRarity, background, background2;
	private HashMap<String, List<String>> rewards;
	private List<Integer> selectableslots;
	private FileServerCrateFlareObj flare;
	public FileServerCrate(File f) {
		load(f);
		register(Feature.SERVER_CRATE, this);
	}
	public String getIdentifier() { return getYamlName(); }

	public String getBossReward() { return colorize(yml.getString("boss reward")); }
	public int getRedeemableItems() { return yml.getInt("settings.redeemable items"); }
	public String getDisplayRarity() { return colorize(yml.getString("display.rarity")); }
	public List<Integer> getSelectableSlots() { return selectableslots; }
	public UInventory getInventory() {
		if(inv == null) {
			inv = new UInventory(null, yml.getInt("settings.size"), colorize(yml.getString("settings.title")));
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
					final ItemStack item = plus ? O.clone() : t.equals("-") ? BG : air;
					if(plus) {
						selectableslots.add(slot);
						final ItemMeta m = item.getItemMeta();
						if(m.hasDisplayName()) m.setDisplayName(m.getDisplayName().replace("{SLOT}", Integer.toString(selectableslots.size())));
						item.setItemMeta(m);
					}
					ii.setItem(slot, item);
				}
			}
		}
		return inv;
	}
	public List<String> getFormat() { return yml.getStringList("settings.format"); }
	public LinkedHashMap<String, Integer> getRevealChances() {
		if(revealChances == null) {
			revealChances = new LinkedHashMap<>();
			for(String s : yml.getConfigurationSection("reveal chances").getKeys(false)) {
				revealChances.put(s, yml.getInt("reveal chances." + s));
			}
		}
		return revealChances;
	}
	public ItemStack getItem() {
		if(physicalItem == null) physicalItem = API.d(yml, "item");
		return getClone(physicalItem);
	}
	public ItemStack getDisplay() {
		if(display == null) display = API.d(yml, "display");;
		return getClone(display);
	}
	public ItemStack getOpenGui() {
		if(opengui == null) opengui = API.d(yml, "open gui");
		return getClone(opengui);
	}
	public ItemStack getSelected() {
		if(selected == null) selected = API.d(yml, "selected");
		return getClone(selected);
	}
	public ItemStack getRevealSlotRarity() {
		if(revealSlotRarity == null) revealSlotRarity = API.d(yml, "reveal slot rarity");
		return getClone(revealSlotRarity);
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
		if(background == null) background = API.d(yml, "settings.background");
		return getClone(background);
	}
	public ItemStack getBackground2() {
		if(background2 == null) background2 = API.d(yml, "settings.background 2");
		return getClone(background2);
	}
	public FileServerCrateFlareObj getFlare() {
		if(flare == null) flare = new FileServerCrateFlareObj(this);
		return flare;
	}
	public ServerCrate getRandomRarity(boolean useChances) {
		String rarity = null;
		final Collection<String> key = getRewards().keySet();
		if(!useChances) {
			rarity = (String) key.toArray()[random.nextInt(key.size())];
		} else {
			final LinkedHashMap<String, Integer> r = getRevealChances();
			for(String s : key) if(random.nextInt(100) <= r.get(s)) rarity = s;
			if(rarity == null) rarity = (String) r.keySet().toArray()[r.keySet().size()-1];
		}
		return getServerCrate(rarity);
	}
	public ItemStack getRandomReward(String rarity) {
		final List<String> r = getRewards().get(rarity);
		final String reward = r.get(random.nextInt(r.size()));
		return API.d(null, reward);
	}
}
