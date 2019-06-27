package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomKit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class EvolutionKit extends AbstractCustomKit {
	public static HashMap<String, EvolutionKit> kits;
	private int upgradeChance;
	private ItemStack item, fallenherospawnitem, fallenherogem, upgradegem;
	public EvolutionKit(File f) {
		if(kits == null) kits = new HashMap<>();
		load(f);
		upgradeChance = yml.getInt("settings.upgrade chance");
		kits.put(getYamlName(), this);
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
	public String getFallenHeroName() { return getFallenHero().getName().replace("{NAME}", getItem().getItemMeta().getDisplayName()); }
	public int getUpgradeChance() { return upgradeChance; }
	public void setUpgradeChance(int chance) { upgradeChance = chance; }
	public ItemStack getItem() {
		if(item == null) item = api.d(yml, "gui settings");
		return item.clone();
	}
	public ItemStack getUpgradeGem() {
		if(upgradegem == null) upgradegem = api.d(yml, "upgrade gem");
		return upgradegem != null ? upgradegem.clone() : null;
	}

	public static EvolutionKit valueOf(int slot) {
		if(kits != null) {
			for(EvolutionKit k : kits.values()) {
				if(k.getSlot() == slot) {
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
	}
}
