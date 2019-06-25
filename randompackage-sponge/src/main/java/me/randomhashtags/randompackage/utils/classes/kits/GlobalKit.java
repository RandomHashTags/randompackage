package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalKit {
	public static HashMap<String, GlobalKit> kits;
	private static RandomPackageAPI api;
	public static String heroicprefix;
	private YamlConfiguration yml;
	private String ymlName, fallenheroname;
	private FallenHero fallenhero;
	private int slot, maxTier;
	private long cooldown;
	private ItemStack item, fallenherospawnitem, fallenherogem;
	private boolean isHeroic;
	private List<KitItem> items;

	public GlobalKit(File f) {
		if(kits == null) {
			kits = new HashMap<>();
			api = RandomPackageAPI.getAPI();
		}
		this.yml = YamlConfiguration.loadConfiguration(f);
		this.ymlName = f.getName().split("\\.yml")[0];
		slot = yml.getInt("gui settings.slot");
		isHeroic = yml.getBoolean("settings.heroic");
		maxTier = yml.getInt("settings.max tier");
		cooldown = yml.getLong("settings.cooldown");
		kits.put(ymlName, this);
	}
	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return ymlName; }
	public FallenHero getFallenHero() {
		if(fallenhero == null) fallenhero = FallenHero.heroes.getOrDefault(yml.getString("settings.fallen hero"), null);;
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
			fallenherogem = getFallenHero().getGem();
			final ItemMeta m = fallenherogem.getItemMeta();
			m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
			final List<String> l = new ArrayList<>();
			for(String s : m.getLore()) {
				l.add(s.replace("{NAME}", n));
			}
			m.setLore(l);
			fallenherogem.setItemMeta(m);
		}
		return fallenherogem.clone();
	}
	public String getFallenHeroName() {
		if(fallenheroname == null) fallenheroname = getFallenHero().getName().replace("{NAME}", getItem().getItemMeta().getDisplayName());
		return fallenheroname;
	}
	public int getSlot() { return slot; }
	public int getMaxTier() { return maxTier; }
	public long getCooldown() { return cooldown; }
	public ItemStack getItem() {
		if(item == null) {
		    item = api.d(yml, "gui settings");
		    if(isHeroic) {
                final ItemMeta m = item.getItemMeta();
                m.setDisplayName(heroicprefix.replace("{NAME}", m.hasDisplayName() ? ChatColor.stripColor(m.getDisplayName()) : item.getType().name()));
                item.setItemMeta(m);
            }
        }
		return item.clone();
	}
	public boolean isHeroic() { return isHeroic; }
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

	public static GlobalKit valueOf(int slot) {
		if(kits != null) {
			for(GlobalKit k : kits.values()) {
				if(k.slot == slot) {
					return k;
				}
			}
		}

		return null;
	}
	public static GlobalKit valueOfFallenHeroSpawnItem(ItemStack is) {
		if(kits != null) {
			for(GlobalKit g : kits.values()) {
				if(g.getFallenHeroSpawnItem().isSimilar(is)) {
					return g;
				}
			}
		}
		return null;
	}
	public static GlobalKit valueOfFallenHeroGem(ItemStack is) {
		if(kits != null) {
			for(GlobalKit g : kits.values()) {
				if(g.getFallenHeroGem().isSimilar(is)) {
					return g;
				}
			}
		}
		return null;
	}

	public static void deleteAll() {
		kits = null;
		api = null;
		heroicprefix = null;
	}
}
