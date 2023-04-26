package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class FileRarityGem extends RPAddonSpigot implements RarityGem {
	public static HashMap<Integer, String> DEFAULT_COLORS;
	private final long time_between_same_kills;
	private final ItemStack item;
	private final List<EnchantRarity> worksFor;
	private final List<String> split_msg, toggle_on, toggle_off_interact, toggle_off_dropped, toggle_off_moved, toggle_off_ran_out;
	private final HashMap<Integer, String> colors;

	public FileRarityGem(File f) {
		super(f);

		final JSONObject json = parse_json_from_file(f);
		final JSONObject messages_json = json.getJSONObject("messages");
		split_msg = parse_list_string_in_json(messages_json, "split");
		toggle_on = parse_list_string_in_json(messages_json, "toggle on");
		final JSONObject toggle_off_messages = messages_json.getJSONObject("toggle off");
		toggle_off_interact = parse_list_string_in_json(toggle_off_messages, "interact");
		toggle_off_dropped = parse_list_string_in_json(toggle_off_messages, "dropped");
		toggle_off_moved = parse_list_string_in_json(toggle_off_messages, "moved");
		toggle_off_ran_out = parse_list_string_in_json(toggle_off_messages, "ran out");

		item = create_item_stack(json, "item");

		final JSONObject settings_json = json.getJSONObject("settings");
		time_between_same_kills = parse_long_in_json(settings_json, "time between same kills", 18000);
		worksFor = new ArrayList<>();
		final String[] works_for_rarities = parse_string_in_json(settings_json, "works for rarities").split(";");
		for(String s : works_for_rarities) {
			worksFor.add(getCustomEnchantRarity(s));
		}
		final JSONObject colors_json = json.optJSONObject("colors");
		if(colors_json != null) {
			colors = new HashMap<>();
			final String _else = parse_string_in_json(colors_json, "else"), less_than_100 = parse_string_in_json(colors_json, "less than 100");
			colors.put(-1, _else);
			colors.put(0, less_than_100);
			final Iterator<String> keys = colors_json.keys();
			for (Iterator<String> it = keys; it.hasNext(); ) {
				String s = it.next();
				if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
					final String value = parse_string_in_json(colors_json, s);
					colors.put(Integer.parseInt(s.split("s")[0]), value);
				}
			}
		} else {
			colors = null;
		}
		register(Feature.RARITY_GEM, this);
	}
	public @NotNull String getIdentifier() {
		return identifier;
	}

	public @NotNull ItemStack getItem() {
		return getClone(item);
	}
	public ItemStack getItem(int souls) {
		final ItemStack item = getItem();
		final ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SOULS}", getColors(souls) + souls));
		item.setItemMeta(itemMeta);
		return item;
	}
	@Override
	public List<EnchantRarity> getWorksFor() {
		return worksFor;
	}
	@Override
	public List<String> getSplitMsg() {
		return split_msg;
	}
	@Override
	public long getTimeBetweenSameKills() {
		return time_between_same_kills;
	}
	@Override
	public HashMap<Integer, String> getColors() {
		return colors;
	}
	@Override
	public List<String> getToggleOnMsg() {
		return toggle_on;
	}
	@Override
	public List<String> getToggleOffInteractMsg() {
		return toggle_off_interact;
	}
	@Override
	public List<String> getToggleOffDroppedMsg() {
		return toggle_off_dropped;
	}
	@Override
	public List<String> getToggleOffMovedMsg() {
		return toggle_off_moved;
	}
	@Override
	public List<String> getToggleOffRanOutMsg() {
		return toggle_off_ran_out;
	}
	@Override
	public String getColors(int soulsCollected) {
		HashMap<Integer, String> colors = getColors();
		if(colors == null) {
			colors = DEFAULT_COLORS;
		}
		if(soulsCollected < 100) {
			return colors.get(0);
		}
		int last = -1;
		for(int i = 100; i <= 1000000; i += 100) {
			if(soulsCollected >= i && soulsCollected < i + 100) {
				final String c = colors.get(i);
				final boolean d = c != null;
				if(d) {
					last += 1;
				}
				return d ? c : colors.get(last);
			}
		}
		return colors.get(-1);
	}
}