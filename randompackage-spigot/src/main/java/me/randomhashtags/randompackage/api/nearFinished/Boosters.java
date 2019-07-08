package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.addons.usingfile.FileBooster;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
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

		final List<ItemStack> b = new ArrayList<>();
		final File folder = new File(rpd + separator + "boosters");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final FileBooster bo = new FileBooster(f);
				b.add(bo.getItem(600, 5.0));
			}
		}
		if(boosters != null) addGivedpCategory(b, UMaterial.EMERALD, "Boosters", "Givedp: Boosters");
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (boosters != null ? boosters.size() : 0) + " Boosters &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		config = null;
		deleteAll(Feature.BOOSTERS);
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack is = event.getItem();
		if(is != null && !is.getType().equals(Material.AIR)) {

		}
	}
}
