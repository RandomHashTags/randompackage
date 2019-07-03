package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.ServerCrate;
import me.randomhashtags.randompackage.addons.objects.ServerCrateFlare;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class FileServerCrate extends ServerCrate {
	private int redeemableItems;
	private UInventory inv;
	private LinkedHashMap<String, Integer> revealChances;
	private ItemStack physicalItem, display, opengui, selected, revealSlotRarity, background, background2;
	private HashMap<String, List<String>> rewards;
	private List<Integer> selectableslots;
	private ServerCrateFlare flare;
	public FileServerCrate(File f) {
		load(f);
		redeemableItems = yml.getInt("settings.redeemable items");
		initilize();
	}
	public void initilize() { addServerCrate(getYamlName(), this); }

	public String getBossReward() { return ChatColor.translateAlternateColorCodes('&', yml.getString("boss reward")); }
	public int getRedeemableItems() { return redeemableItems; }
	public String getDisplayRarity() { return ChatColor.translateAlternateColorCodes('&', yml.getString("display.rarity")); }
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
		if(physicalItem == null) physicalItem = api.d(yml, "item");
		return physicalItem;
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
}
