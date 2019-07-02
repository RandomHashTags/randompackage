package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class GlobalKit extends CustomKit {
	public static HashMap<String, GlobalKit> kits;
	public static String heroicprefix;

	private boolean isHeroic;
	private ItemStack item, fallenherospawnitem, fallenherogem;

	public GlobalKit(File f) {
		if(kits == null) kits = new HashMap<>();
		load(f);
		isHeroic = yml.getBoolean("settings.heroic");
		kits.put(getYamlName(), this);
		created(new NamespacedKey(getPlugin, "GLOBAL_" + getYamlName()));
	}
	public boolean isHeroic() { return isHeroic; }
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
	public String getFallenHeroName() { return getFallenHero().getName().replace("{NAME}", getItem().getItemMeta().getDisplayName()); }
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

	public static GlobalKit valueOf(int slot) {
		if(kits != null) {
			for(GlobalKit k : kits.values()) {
				if(k.getSlot() == slot) {
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
		heroicprefix = null;
	}
}
