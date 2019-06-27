package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.utils.abstraction.AbstractFallenHero;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class FallenHero extends AbstractFallenHero {
	public static HashMap<String, FallenHero> heroes;

	public FallenHero(File f) {
		if(heroes == null) heroes = new HashMap<>();
		load(f);
		heroes.put(getYamlName(), this);
	}

	public void spawn(LivingEntity summoner, Location loc, GlobalKit kit) {
		if(loc != null && kit != null) {
			final boolean s = summoner != null;
			new LivingFallenHero(kit, this, s ? summoner.getUniqueId() : null, loc);
			if(s) {
				final HashMap<String, String> r = new HashMap<>();
				r.put("{NAME}", getName());
				api.sendStringListMessage(summoner, getSummonMsg(), r);
			}
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

	public static void deleteAll() {
		heroes = null;
	}
}
