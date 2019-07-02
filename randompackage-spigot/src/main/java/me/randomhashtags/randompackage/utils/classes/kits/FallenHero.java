package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomKit;
import me.randomhashtags.randompackage.utils.abstraction.AbstractFallenHero;
import me.randomhashtags.randompackage.utils.classes.living.LivingFallenHero;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class FallenHero extends AbstractFallenHero {
	private List<String> summonMsg, receiveKitMsg, potioneffects;
	private ItemStack spawnitem, gem;

	public FallenHero(File f) {
		load(f);
		created(new NamespacedKey(getPlugin, getYamlName()));
	}

	public int getGemDropChance() { return yml.getInt("gem.chance"); }
	public List<String> getSummonMsg() {
		if(summonMsg == null) summonMsg = api.colorizeListString(yml.getStringList("messages.summon"));
		return summonMsg;
	}
	public List<String> getReceiveKitMsg() {
		if(receiveKitMsg == null) receiveKitMsg = api.colorizeListString(yml.getStringList("messages.receive kit"));
		return receiveKitMsg;
	}
	public String getType() { return yml.getString("settings.type").toUpperCase(); }
	public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.name")); }
	public List<PotionEffect> getPotionEffects() {
		final List<PotionEffect> e = new ArrayList<>();
		if(potioneffects == null) potioneffects = yml.getStringList("potion effects");
		for(String s : potioneffects) {}
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
	public void spawn(LivingEntity summoner, Location loc, AbstractCustomKit kit) {
		if(loc != null && kit != null) {
			new LivingFallenHero(kit, this, summoner != null ? summoner.getUniqueId() : null, loc);
		}
	}
}
