package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.addons.Booster;
import me.randomhashtags.randompackage.addons.active.ActiveBooster;
import me.randomhashtags.randompackage.addons.usingfile.FileBooster;
import me.randomhashtags.randompackage.api.events.BoosterActivateEvent;
import me.randomhashtags.randompackage.api.events.BoosterPreActivateEvent;
import me.randomhashtags.randompackage.utils.EventAttributes;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.objects.TObject;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Boosters extends EventAttributes {
	private static Boosters instance;
	public static Boosters getBoosters() {
	    if(instance == null) instance = new Boosters();
	    return instance;
	}

	public YamlConfiguration config;
	public static HashMap<String, List<ActiveBooster>> activeBoosters;

	public void load() {
		final long started = System.currentTimeMillis();

		if(!otherdata.getBoolean("saved default boosters")) {
			final String[] a = new String[] {"FACTION_MCMMO", "FACTION_XP"};
			for(String s : a) save("boosters", s + ".yml");
			otherdata.set("saved default boosters", false);
			saveOtherData();
		}

		config = YamlConfiguration.loadConfiguration(new File(rpd, "faction additions.yml"));
		activeBoosters = new HashMap<>();

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
		instance = null;
		deleteAll(Feature.BOOSTERS);
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		if(is != null && !is.getType().equals(Material.AIR)) {
			final Player player = event.getPlayer();
			final TObject m = Booster.valueOf(is);
			if(m != null) {
				event.setCancelled(true);
				player.updateInventory();
				if(activateBooster(player, (Booster) m.first(), (double) m.second(), (long) m.third())) removeItem(player, is, 1);
			}
		}
	}

	private boolean activateBooster(Player player, Booster booster, double multiplier, long duration) {
		final BoosterPreActivateEvent e = new BoosterPreActivateEvent(player, booster, multiplier, duration);
		pluginmanager.callEvent(e);
		if(!e.isCancelled()) {
			final double m = e.multiplier;
			final long d = e.duration;
			final BoosterActivateEvent ee = new BoosterActivateEvent(player, booster, m, d);
			pluginmanager.callEvent(ee);
			executeAttributes(player, ee, booster.getAttributes());
			new ActiveBooster(player, booster, multiplier, System.currentTimeMillis()+duration);

			final String type = booster.getRecipients();
			final boolean fm = type.equals("FACTION_MCMMO"), fx = !fm && type.equals("FACTION_XP");
			if(fm || fx) {
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{PLAYER}", player.getName());
				final List<Player> members = getFactionMembers(player);
				if(members != null) {
					for(Player p : members) {
						sendStringListMessage(p, booster.getActivateMsg(), replacements);
					}
				}
			}
			return true;
		} else return false;
	}
	private List<Player> getFactionMembers(Player player) {
		final String f = fapi != null ? fapi.getFaction(player) : null;
		final List<UUID> m = f != null ? fapi.getMembers(f) : null;
		if(m != null) {
			final List<Player> a = new ArrayList<>();
			for(UUID u : m) {
				final Player p = Bukkit.getPlayer(u);
				if(p != null && p.isOnline()) {
					a.add(p);
				}
			}
			return a;
		}
		return null;
	}
	@EventHandler
	private void boosterActivateEvent(BoosterActivateEvent event) {
		final Booster b = event.booster;
		if(b instanceof FileBooster) {
			final Player player = event.player;
			final double multiplier = event.multiplier;
			final long duration = event.duration;
			final String recipients = b.getRecipients();
			for(String s : b.getAttributes()) {
				if(s.toLowerCase().startsWith("activatebooster;")) {

				}
			}
		}
	}
}
