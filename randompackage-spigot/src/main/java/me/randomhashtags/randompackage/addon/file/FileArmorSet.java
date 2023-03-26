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
	private List<ArmorSetWeaponInfo> weapons;
	private final List<String> armor_lore, crystal_perks, armor_attributes, crystal_attributes, activate_message, crystal_applied_message;

	public FileArmorSet(File f) {
		super(f);
		final JSONObject json = parse_json_from_file(f);
		name = parse_multilingual_string_in_json(json, "name");
		armor_lore = parse_list_string_in_json(json, "armor lore");
		crystal_perks = parse_list_string_in_json(json, "crystal perks");
		armor_attributes = parse_list_string_in_json(json, "attributes.armor");
		crystal_attributes = parse_list_string_in_json(json, "attributes.crystal");
		activate_message = parse_list_string_in_json(json, "activate msg");
		crystal_applied_message = parse_list_string_in_json(json, "crystal applied msg");
		register(Feature.ARMOR_SET, this);
	}

	public @NotNull MultilingualString getName() {
		return name;
	}
	@NotNull
	public ItemStack getHelmet() {
		if(helmet == null) {
			helmet = createItemStack(yml, "helmet");
		}
		return getClone(helmet);
	}
	@NotNull
	public ItemStack getChestplate() {
		if(chestplate == null) {
			chestplate = createItemStack(yml, "chestplate");
		}
		return getClone(chestplate);
	}
	@NotNull
	public ItemStack getLeggings() {
		if(leggings == null) {
			leggings = createItemStack(yml, "leggings");
		}
		return getClone(leggings);
	}
	@NotNull
	public ItemStack getBoots() {
		if(boots == null) {
			boots = createItemStack(yml, "boots");
		}
		return getClone(boots);
	}
	@NotNull
	public List<ArmorSetWeaponInfo> getWeapons() {
		if(weapons == null) {
			weapons = new ArrayList<>();
			final ConfigurationSection c = yml.getConfigurationSection("weapons");
			if(c != null) {
				for(String s : c.getKeys(false)) {
					weapons.add(new ArmorSetWeaponInfo(s, createItemStack(yml, "weapons." + s), colorizeListString(yml.getStringList("weapons." + s + ".set lore")), yml.getStringList("attributes." + s)));
				}
			}
		}
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
