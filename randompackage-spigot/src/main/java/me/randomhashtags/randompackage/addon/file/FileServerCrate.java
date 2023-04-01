package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ServerCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

public final class FileServerCrate extends RPAddonSpigot implements ServerCrate {
	private final String boss_reward;
	private final UInventory inventory;
	private final LinkedHashMap<String, Integer> revealChances;
	private final ItemStack physicalItem, display, opengui, selected, revealSlotRarity, background, background2;
	private final int redeemable_items;
	private final List<String> format;
	private final String display_rarity;
	private final HashMap<String, List<String>> rewards;
	private final List<Integer> selectable_slots;
	private final FileServerCrateFlareObj flare;
	public FileServerCrate(File f) {
		super(f);
		final JSONObject json = parse_json_from_file(f);

		boss_reward = parse_string_in_json(json, "boss reward");

		physicalItem = create_item_stack(json, "item");
		display = create_item_stack(json, "display");
		opengui = create_item_stack(json, "open gui");
		selected = create_item_stack(json, "selected");
		revealSlotRarity = create_item_stack(json, "reveal slot rarity");

		final JSONObject display_json = json.getJSONObject("display");
		display_rarity = parse_string_in_json(display_json, "rarity");

		final JSONObject settings_json = json.getJSONObject("settings");
		redeemable_items = parse_int_in_json(settings_json, "redeemable items");
		format = parse_list_string_in_json(settings_json, "format");
		background = create_item_stack(settings_json, "background");
		background2 = create_item_stack(settings_json, "background 2");

		inventory = new UInventory(null, settings_json.getInt("size"), colorize(settings_json.getString("title")));
		final Inventory inventory_inv = inventory.getInventory();
		final ItemStack background = this.background, air = new ItemStack(Material.AIR), opengui = this.opengui.clone();
		selectable_slots = new ArrayList<>();
		for(int i = 0; i < format.size(); i++) {
			final String s = format.get(i);
			for(int o = 0; o < s.length(); o++) {
				final int slot = i*9+o;
				final String t = s.substring(o, o+1);
				final boolean plus = t.equals("+");
				final ItemStack item = plus ? opengui : t.equals("-") ? background : air;
				if(plus) {
					selectable_slots.add(slot);
					final ItemMeta m = item.getItemMeta();
					if(m.hasDisplayName()) {
						m.setDisplayName(m.getDisplayName().replace("{SLOT}", Integer.toString(selectable_slots.size())));
					}
					item.setItemMeta(m);
				}
				inventory_inv.setItem(slot, item);
			}
		}

		revealChances = new LinkedHashMap<>();
		final JSONObject reveal_chances_json = json.getJSONObject("reveal chances");
		for(String s : reveal_chances_json.keySet()) {
			revealChances.put(s, parse_int_in_json(reveal_chances_json, s));
		}

		flare = new FileServerCrateFlareObj(this, json);

		rewards = new HashMap<>();
		final JSONObject rewards_json = json.getJSONObject("rewards");
		for(String s : rewards_json.keySet()) {
			rewards.put(s, parse_list_string_in_json(rewards_json, s));
		}

		register(Feature.SERVER_CRATE, this);
	}

	public String getBossReward() {
		return boss_reward;
	}
	@Override
	public int getRedeemableItems() {
		return redeemable_items;
	}
	@Override
	public String getDisplayRarity() {
		return display_rarity;
	}
	@Override
	public @NotNull List<Integer> getSelectableSlots() {
		return selectable_slots;
	}
	@Override
	public UInventory getInventory() {
		return inventory;
	}
	public @NotNull List<String> getFormat() {
		return format;
	}
	@Override
	public @NotNull LinkedHashMap<String, Integer> getRevealChances() {
		return revealChances;
	}
	@NotNull
	@Override
	public ItemStack getItem() {
		return getClone(physicalItem);
	}
	public @NotNull ItemStack getDisplay() {
		return getClone(display);
	}
	public @NotNull ItemStack getOpenGui() {
		return getClone(opengui);
	}
	public @NotNull ItemStack getSelected() {
		return getClone(selected);
	}
	public @NotNull ItemStack getRevealSlotRarity() {
		return getClone(revealSlotRarity);
	}
	@Override
	public @NotNull HashMap<String, List<String>> getRewards() {
		return rewards;
	}
	@Override
	public @NotNull ItemStack getBackground() {
		return getClone(background);
	}
	@Override
	public @NotNull ItemStack getBackground2() {
		return getClone(background2);
	}
	@Override
	public @NotNull FileServerCrateFlareObj getFlare() {
		return flare;
	}
	@Override
	public ServerCrate getRandomRarity(boolean useChances) {
		String rarity = null;
		final Collection<String> key = getRewards().keySet();
		if(!useChances) {
			rarity = (String) key.toArray()[RANDOM.nextInt(key.size())];
		} else {
			final LinkedHashMap<String, Integer> reveal_chances = getRevealChances();
			for(String s : key) {
				if(RANDOM.nextInt(100) <= reveal_chances.get(s)) {
					rarity = s;
				}
			}
			if(rarity == null) {
				rarity = (String) reveal_chances.keySet().toArray()[reveal_chances.keySet().size()-1];
			}
		}
		return getServerCrate(rarity);
	}
	@Override
	public ItemStack getRandomReward(@NotNull String rarity) {
		final List<String> rewards = getRewards().get(rarity);
		final String reward = rewards.get(RANDOM.nextInt(rewards.size()));
		return createItemStack(null, reward);
	}
}
