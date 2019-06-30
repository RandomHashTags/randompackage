package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.abstraction.AbstractBooster;
import me.randomhashtags.randompackage.utils.classes.Booster;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Boosters extends RPFeature {
	private static Boosters instance;
	public static Boosters getBoosters() {
	    if(instance == null) instance = new Boosters();
	    return instance;
	}

	public YamlConfiguration config;

	public void load() {
		final long started = System.currentTimeMillis();

		if(!otherdata.getBoolean("saved default boosters")) {
			final String[] a = new String[] {"FACTION_MCMMO", "FACTION_XP"};
			for(String s : a) save("boosters", s + ".yml");
			otherdata.set("saved default boosters", false);
			saveOtherData();
		}

		config = YamlConfiguration.loadConfiguration(new File(rpd, "faction additions.yml"));

		final List<ItemStack> boosters = new ArrayList<>();
		final File folder = new File(rpd + separator + "boosters");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final Booster b = new Booster(f);
				boosters.add(b.getItem(600, 5.0));
			}
		}
		addGivedpCategory(boosters, UMaterial.EMERALD, "Boosters", "Givedp: Boosters");
		final TreeMap<NamespacedKey, AbstractBooster> b = AbstractBooster.boosters;
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (b != null ? b.size() : 0) + " Boosters &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		config = null;
		AbstractBooster.boosters = null;
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack is = event.getItem();
		if(is != null && !is.getType().equals(Material.AIR)) {

		}
	}
}
