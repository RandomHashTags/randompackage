package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EvolutionKit {
	public static HashMap<String, EvolutionKit> kits;
	private static RandomPackageAPI api;
	private YamlConfiguration yml;
	private String ymlName, fallenheroname;
	private FallenHero fallenhero;
	private int slot, maxLevel, upgradeChance;
	private long cooldown;
	private ItemStack item, fallenherospawnitem, fallenherogem, upgradegem;
	private List<KitItem> items;
	public EvolutionKit(File f) {
		if(kits == null) {
			kits = new HashMap<>();
			api = RandomPackageAPI.getAPI();
		}
		this.yml = YamlConfiguration.loadConfiguration(f);
		this.ymlName = f.getName().split("\\.yml")[0];
		slot = yml.getInt("gui settings.slot");
		maxLevel = yml.getInt("settings.max level");
		upgradeChance = yml.getInt("settings.upgrade chance");
		cooldown = yml.getLong("settings.cooldown");
		kits.put(ymlName, this);
	}

	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return ymlName; }
	public FallenHero getFallenHero() {
		if(fallenhero == null) fallenhero = FallenHero.heroes.getOrDefault(yml.getString("settings.fallen hero"), null);
		return fallenhero;
	}
	public ItemStack getFallenHeroSpawnItem() {
		if(fallenherospawnitem == null) {
			final String n = getItem().getItemMeta().getDisplayName();
			final ItemStack is = getFallenHero().getSpawnItem();
			final ItemMeta m = is.getItemMeta();
			m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
			final List<String> l = new ArrayList<>();
			for(String s : m.getLore()) {
				l.add(s.replace("{NAME}", n));
			}
			m.setLore(l);
			is.setItemMeta(m);
			fallenherospawnitem = is;
		}
		return fallenherospawnitem.clone();
	}
	public ItemStack getFallenHeroGem() {
		if(fallenherogem == null) {
			final String n = getItem().getItemMeta().getDisplayName();
			final ItemStack is = getFallenHero().getGem();
			final ItemMeta m = is.getItemMeta();
			m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
			final List<String> l = new ArrayList<>();
			for(String s : m.getLore()) {
				l.add(s.replace("{NAME}", n));
			}
			m.setLore(l);
			is.setItemMeta(m);
			fallenherogem = is;
		}
		return fallenherogem.clone();
	}
	public String getFallenHeroName() {
		if(fallenheroname == null) fallenheroname = getFallenHero().getName().replace("{NAME}", getItem().getItemMeta().getDisplayName());
		return fallenheroname;
	}
	public int getSlot() { return slot; }
	public int getMaxLevel() { return maxLevel; }
	public int getUpgradeChance() { return upgradeChance; }
	public long getCooldown() { return cooldown; }
	public ItemStack getItem() {
		if(item == null) item = api.d(yml, "gui settings");
		return item.clone();
	}
	public ItemStack getUpgradeGem() {
		if(upgradegem == null) upgradegem = api.d(yml, "upgrade gem");
		return upgradegem != null ? upgradegem.clone() : null;
	}
	public List<KitItem> getItems() {
		if(items == null) {
			items = new ArrayList<>();
			for(String i : yml.getConfigurationSection("items").getKeys(false)) {
				final String t = yml.getString("items." + i + ".item");
				if(t != null) {
					final int chance = yml.get("items." + i + ".chance") != null ? yml.getInt("items." + i + ".chance") : 100;
					items.add(new KitItem(this, i, yml.getString("items." + i + ".item"), yml.getString("items." + i + ".name"), yml.getStringList("items." + i + ".lore"), chance, "1", false, yml.getInt("items." + i + ".reqlevel")));
				}
			}
		}
		return items;
	}

	public static EvolutionKit valueOf(int slot) {
		if(kits != null) {
			for(EvolutionKit k : kits.values()) {
				if(k.slot == slot) {
					return k;
				}
			}
		}
		return null;
	}
	public static EvolutionKit valueOfFallenHeroSpawnItem(ItemStack is) {
		if(is != null && kits != null) {
			for(EvolutionKit g : kits.values()) {
				if(g.getFallenHeroSpawnItem().isSimilar(is)) {
					return g;
				}
			}
		}
		return null;
	}
	public static EvolutionKit valueOfFallenHeroGem(ItemStack is) {
		if(is != null && kits != null) {
			for(EvolutionKit e : kits.values()) {
				final ItemStack i = e.getFallenHeroGem();
				if(i.isSimilar(is)) {
					return e;
				}
			}
		}
		return null;
	}
	public static EvolutionKit valueOfUpgradeGem(ItemStack is) {
		if(is != null && kits != null) {
			for(EvolutionKit e : kits.values()) {
				final ItemStack i = e.getUpgradeGem();
				if(i != null && i.isSimilar(is)) {
					return e;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		kits = null;
		api = null;
	}
}
