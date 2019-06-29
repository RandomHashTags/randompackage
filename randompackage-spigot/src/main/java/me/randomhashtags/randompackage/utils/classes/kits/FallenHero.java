package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomKit;
import me.randomhashtags.randompackage.utils.abstraction.AbstractFallenHero;
import me.randomhashtags.randompackage.utils.classes.living.LivingFallenHero;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class FallenHero extends AbstractFallenHero {
	public static HashMap<String, FallenHero> heroes;

	public FallenHero(File f) {
		if(heroes == null) heroes = new HashMap<>();
		load(f);
		heroes.put(getYamlName(), this);
	}

	public void spawn(LivingEntity summoner, Location loc, AbstractCustomKit kit) {
		if(loc != null && kit != null) {
			new LivingFallenHero(kit, this, summoner != null ? summoner.getUniqueId() : null, loc);
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
