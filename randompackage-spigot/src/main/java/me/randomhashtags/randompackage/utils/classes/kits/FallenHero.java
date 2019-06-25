package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FallenHero {
	private static RandomPackageAPI api;
	public static HashMap<String, FallenHero> heroes;

	private File f;
	private YamlConfiguration yml;
	private String ymlName, type, name, spawnable;
	private List<String> summonMsg, receiveKitMsg, potioneffects;
	private ItemStack spawnitem, gem;
	private int gemDropChance;

	public FallenHero(File f) {
		if(heroes == null) {
			heroes = new HashMap<>();
			api = RandomPackageAPI.getAPI();
		}
		this.f = f;
		yml = YamlConfiguration.loadConfiguration(f);
		ymlName = f.getName().split("\\.yml")[0];
		gemDropChance = yml.getInt("gem.chance");
		heroes.put(ymlName, this);
	}
	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return ymlName; }
	public List<String> getSummonMsg() {
		if(summonMsg == null) summonMsg = api.colorizeListString(yml.getStringList("messages.summon"));
		return summonMsg;
	}
	public List<String> getReceiveKitMsg() {
		if(receiveKitMsg == null) receiveKitMsg = api.colorizeListString(yml.getStringList("messages.receive kit"));
		return receiveKitMsg;
	}
	public String getType() {
		if(type == null) type = yml.getString("settings.type").toUpperCase();
		return type;
	}
	public String getName() {
		if(name == null) name = ChatColor.translateAlternateColorCodes('&', yml.getString("settings.name"));
		return name;
	}
	public String getSpawnable() {
		if(spawnable == null) spawnable = yml.getString("settings.spawnable").toUpperCase();
		return spawnable;
	}
	public List<PotionEffect> getPotionEffects() {
		final List<PotionEffect> e = new ArrayList<>();
		if(potioneffects == null) potioneffects = yml.getStringList("potion effects");
		for(String s : potioneffects) {

		}
		return e;
	}
	public ItemStack getSpawnItem() {
		if(spawnitem == null) spawnitem = api.d(yml, "spawn item");
		return spawnitem.clone();
	}
	public ItemStack getGem() {
		if(gem == null) gem = api.d(yml, "gem");
		return gem.clone();
	}
	public int getGemDropChance() { return gemDropChance; }

	public void spawn(LivingEntity summoner, Location loc, GlobalKit kit) {
		if(loc != null && kit != null) {
			final boolean s = summoner != null;
			new LivingFallenHero(kit, this, s ? summoner.getUniqueId() : null, loc);
			if(s) {
				final HashMap<String, String> r = new HashMap<>();
				r.put("{NAME}", name);
				api.sendStringListMessage(summoner, summonMsg, r);
			}
		}
	}

	public void delete() {
		heroes.remove(ymlName);
		f = null;
		yml = null;
		ymlName = null;
		type = null;
		name = null;
		spawnable = null;
		summonMsg = null;
		receiveKitMsg = null;
		potioneffects = null;
		spawnitem = null;
		gem = null;
		gemDropChance = 0;
		if(heroes.isEmpty()) {
			heroes = null;
			api = null;
		}
	}


	public static FallenHero valueOf(ItemStack spawnitem) {
		if(heroes != null && spawnitem != null && spawnitem.hasItemMeta())
			for(FallenHero h : heroes.values())
				if(h.getSpawnItem().isSimilar(spawnitem)) return h;
		return null;
	}
	public static FallenHero valueOF(ItemStack gem) {
		if(heroes != null && gem != null && gem.hasItemMeta())
			for(FallenHero h : heroes.values())
				if(h.getGem().isSimilar(gem)) return h;
		return null;
	}
}
