package me.randomhashtags.randompackage.utils.classes.envoy;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEnvoyCrate;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class EnvoyCrate extends AbstractEnvoyCrate {
	public static HashMap<String, EnvoyCrate> crates;
	public static String defaultTier;
	public EnvoyCrate(File f) {
		if(crates == null) crates = new HashMap<>();
		load(f);
		crates.put(getYamlName(), this);
	}
	public static EnvoyCrate valueOf(ItemStack is) {
		if(crates != null && is != null && is.hasItemMeta())
			for(EnvoyCrate c : crates.values())
				if(is.isSimilar(c.getItem()))
					return c;
		return null;
	}
	public static EnvoyCrate getRandomCrate(boolean useChances) {
		if(crates != null) {
			final Random random = new Random();
			if(useChances) {
				for(EnvoyCrate c : crates.values())
					if(random.nextInt(100) <= c.getChance())
						return c;
			} else {
				return crates.get(defaultTier);
			}
			return crates.get(defaultTier);
		}
		return null;
	}
	public static void deleteAll() {
		crates = null;
		defaultTier = null;
	}
}
