package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ArmorSet;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileArmorSet extends RPAddonSpigot implements ArmorSet {
	private final MultilingualString name;
	private final ItemStack helmet, chestplate, leggings, boots;
	private final List<ArmorSetWeaponInfo> weapons;
	private final List<String> armor_lore, crystal_perks, armor_attributes, crystal_attributes, activate_message, crystal_applied_message;

	public FileArmorSet(File f) {
		super(f);
		final JSONObject json = parse_json_from_file(f);
		name = parse_multilingual_string_in_json(json, "name");
		armor_lore = parse_list_string_in_json(json, "armor lore");
		crystal_perks = parse_list_string_in_json(json, "crystal perks");
		final JSONObject attributes_json = parse_json_in_json(json, "attributes");
		armor_attributes = parse_list_string_in_json(attributes_json, "armor");
		crystal_attributes = parse_list_string_in_json(attributes_json, "crystal");
		activate_message = parse_list_string_in_json(json, "activate msg");
		crystal_applied_message = parse_list_string_in_json(json, "crystal applied msg");

		helmet = create_item_stack(json, "helmet");
		chestplate = create_item_stack(json, "chestplate");
		leggings = create_item_stack(json, "leggings");
		boots = create_item_stack(json, "boots");

		weapons = new ArrayList<>();
		final JSONObject weapons_json = parse_json_in_json(json, "weapons");
		if(weapons_json != null) {
			for(String s : weapons_json.keySet()) {
				final JSONObject weapon_json = weapons_json.getJSONObject(s);
				final List<String> weapon_set_lore = parse_list_string_in_json(weapon_json, "set lore"), weapon_attributes = parse_list_string_in_json(attributes_json, s);
				weapons.add(new ArmorSetWeaponInfo(s, create_item_stack(weapons_json, s),  weapon_set_lore, weapon_attributes));
			}
		}
		register(Feature.ARMOR_SET, this);
	}

	public @NotNull MultilingualString getName() {
		return name;
	}
	@NotNull
	public ItemStack getHelmet() {
		return getClone(helmet);
	}
	@NotNull
	public ItemStack getChestplate() {
		return getClone(chestplate);
	}
	@NotNull
	public ItemStack getLeggings() {
		return getClone(leggings);
	}
	@NotNull
	public ItemStack getBoots() {
		return getClone(boots);
	}
	@NotNull
	public List<ArmorSetWeaponInfo> getWeapons() {
		return weapons;
	}
	public @NotNull List<String> getArmorLore() {
		return armor_lore;
	}
	public @NotNull List<String> getCrystalPerks() {
		return crystal_perks;
	}
	public @NotNull List<String> getArmorAttributes() {
		return armor_attributes;
	}
	public @NotNull List<String> getCrystalAttributes() {
		return crystal_attributes;
	}
	public @NotNull List<String> getActivateMessage() {
		return activate_message;
	}
	public @NotNull List<String> getCrystalAppliedMsg() {
		return crystal_applied_message;
	}
}
