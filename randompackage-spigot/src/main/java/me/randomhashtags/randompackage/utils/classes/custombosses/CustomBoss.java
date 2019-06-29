package me.randomhashtags.randompackage.utils.classes.custombosses;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomBoss;
import me.randomhashtags.randompackage.utils.classes.living.LivingCustomBoss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class CustomBoss extends AbstractCustomBoss {
	public static HashMap<String, CustomBoss> bosses;

	public CustomBoss(File f) {
		if(bosses == null) bosses = new HashMap<>();
		load(f);
		bosses.put(getYamlName(), this);
	}
	public LivingCustomBoss spawn(LivingEntity summoner, Location location) {
		return new LivingCustomBoss(summoner, api.getEntity(getType(), location, true), this);
	}

	public static CustomBoss valueOf(ItemStack spawnitem) {
		if(bosses != null && spawnitem != null && spawnitem.hasItemMeta())
			for(CustomBoss c : bosses.values())
				if(c.getSpawnItem().getItemMeta().equals(spawnitem.getItemMeta()))
					return c;
		return null;
	}
	public static void deleteAll() {
		bosses = null;
	}
}
