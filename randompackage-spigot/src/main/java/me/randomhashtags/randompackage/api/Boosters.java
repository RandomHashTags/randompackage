package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.Booster;
import me.randomhashtags.randompackage.addons.active.ActiveBooster;
import me.randomhashtags.randompackage.addons.objects.ExecutedEventAttributes;
import me.randomhashtags.randompackage.addons.usingfile.FileBooster;
import me.randomhashtags.randompackage.api.events.BoosterActivateEvent;
import me.randomhashtags.randompackage.api.events.BoosterExpireEvent;
import me.randomhashtags.randompackage.api.events.BoosterPreActivateEvent;
import me.randomhashtags.randompackage.api.events.FactionDisbandEvent;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.newEventAttributes;
import me.randomhashtags.randompackage.utils.objects.TObject;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Boosters extends newEventAttributes {
	private static Boosters instance;
	public static Boosters getBoosters() {
	    if(instance == null) instance = new Boosters();
	    return instance;
	}

	public File dataF;
	public YamlConfiguration config, data;
	public static HashMap<String, List<ActiveBooster>> activeFactionBoosters;


	public void load() {
		loadUtils();
		final long started = System.currentTimeMillis();
		save("_Data", "boosters.yml");
		dataF = new File(rpd + separator + "_Data", "boosters.yml");
		data = YamlConfiguration.loadConfiguration(dataF);
		if(!otherdata.getBoolean("saved default boosters")) {
			final String[] a = new String[] {"FACTION_MCMMO", "FACTION_XP"};
			for(String s : a) save("boosters", s + ".yml");
			otherdata.set("saved default boosters", true);
			saveOtherData();
		}

		config = YamlConfiguration.loadConfiguration(new File(rpd, "faction additions.yml"));
		activeFactionBoosters = new HashMap<>();

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
		loadBackup();
	}
	public void unload() {
		backup();
		instance = null;
		activeFactionBoosters = null;
		deleteAll(Feature.BOOSTERS);
	}

	public void backup() {
		data.set("factions", null);
		if(activeFactionBoosters != null) {
			for(String s : activeFactionBoosters.keySet()) {
				final List<ActiveBooster> boosters = activeFactionBoosters.get(s);
				for(ActiveBooster ab : boosters) {
					final String p = "factions." + s + "." + ab.getBooster().getIdentifier() + ".";
					data.set(p + "activator", ab.getActivator().getUniqueId().toString());
					data.set(p + "expiration", ab.getExpiration());
					data.set(p + "duration", ab.getDuration());
					data.set(p + "multiplier", ab.getMultiplier());
				}
			}
		}
		save();
	}
	public void loadBackup() {
		scheduler.runTaskAsynchronously(randompackage, () -> {
			int loaded = 0, expired = 0;
			final ConfigurationSection c = data.getConfigurationSection("factions");
			if(c != null) {
				if(activeFactionBoosters == null) activeFactionBoosters = new HashMap<>();
				for(String s : c.getKeys(false)) {
					if(!activeFactionBoosters.containsKey(s)) activeFactionBoosters.put(s, new ArrayList<>());
					final ConfigurationSection boosters = data.getConfigurationSection("factions." + s);
					if(boosters != null) {
						for(String b : boosters.getKeys(false)) {
							final String p = "factions." + s + "." + b + ".";
							final ActiveBooster a = new ActiveBooster(Bukkit.getOfflinePlayer(UUID.fromString(data.getString(p + "activator"))), getBooster(b), data.getDouble(p + "multiplier"), data.getLong(p + "duration"), data.getLong(p + "expiration"));
							if(a.getRemainingTime() > 0) {
								activeFactionBoosters.get(s).add(a);
								loaded++;
							} else {
								expired++;
							}
						}
					}
				}
			}
			sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " and expired " + expired + " existing Boosters &e[async]");
		});
	}
	private void save() {
		try {
			data.save(dataF);
			dataF = new File(rpd + separator + "_Data", "boosters.yml");
			data = YamlConfiguration.loadConfiguration(dataF);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		final boolean cancelled = e.isCancelled();
		if(!cancelled) {
			final BoosterActivateEvent ee = new BoosterActivateEvent(player, booster, multiplier, duration);
			pluginmanager.callEvent(ee);
		}
		return !cancelled;
	}
	private List<Player> getFactionMembers(String faction) {
		final List<UUID> m = fapi != null ? fapi.getMembers(faction) : null;
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
	@EventHandler(priority = EventPriority.LOWEST)
	private void boosterActivateEvent(BoosterActivateEvent event) {
		final Booster b = event.booster;
		if(b instanceof FileBooster) {
			switch(b.getRecipients()) {
				case "FACTION_MEMBERS":
					final OfflinePlayer player = event.activator;
					final String faction = fapi != null ? fapi.getFaction(player) : null;
					if(faction != null) {
						final long duration = event.duration;
						final ActiveBooster booster = new ActiveBooster(event, faction, System.currentTimeMillis()+duration);
						final double multiplier = event.multiplier;
						final HashMap<String, String> replacements = new HashMap<>();
						replacements.put("{PLAYER}", player.getName());
						replacements.put("{MULTIPLIER}", Double.toString(multiplier));
						replacements.put("{TIME}", getRemainingTime(booster.getRemainingTime()));
						final List<Player> members = getFactionMembers(faction);
						if(members != null) {
							for(Player p : members) {
								sendStringListMessage(p, b.getActivateMsg(), replacements);
							}
						}
						if(!activeFactionBoosters.containsKey(faction)) activeFactionBoosters.put(faction, new ArrayList<>());
						activeFactionBoosters.get(faction).add(booster);
					}
					break;
				case "SELF":
					break;
			}
		}
	}
	@EventHandler
	private void boosterExpireEvent(BoosterExpireEvent event) {
		final ActiveBooster b = event.booster;
		final Booster booster = b.getBooster();
		switch(booster.getRecipients()) {
			case "FACTION_MEMBERS":
				final List<String> expire = booster.getExpireMsg();
				final OfflinePlayer a = b.getActivator();
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{PLAYER}", a.getName());
				replacements.put("{MULTIPLIER}", Double.toString(b.getMultiplier()));
				boolean did = false;
				final String faction = b.getFaction();
				if(faction != null && activeFactionBoosters.containsKey(faction)) {
					final List<ActiveBooster> boosters = activeFactionBoosters.get(faction);
					if(boosters.contains(b)) {
						did = true;
						boosters.remove(b);
						for(Player p : getFactionMembers(faction)) {
							sendStringListMessage(p, expire, replacements);
						}
					}
				}
				if(!did && a.isOnline()) {
					sendStringListMessage(a.getPlayer(), expire, replacements);
				}
				break;
			case "SELF":
		}
	}
	@EventHandler
	private void factionDisbandEvent(FactionDisbandEvent event) {
		activeFactionBoosters.remove(event.factionName);
	}

	private List<ActiveBooster> getFactionBoosters(String faction) {
		return activeFactionBoosters != null ? activeFactionBoosters.getOrDefault(faction, new ArrayList<>()) : new ArrayList<>();
	}
	private List<ActiveBooster> getFactionBoosters(Player player) {
		return fapi != null ? getFactionBoosters(fapi.getFaction(player)) : new ArrayList<>();
	}
	private void sendNotify(Player player, ActiveBooster b, HashMap<String, String> replacements) {
		if(player != null && b != null) {
			sendStringListMessage(player, b.getBooster().getNotifyMsg(), replacements);
		}
	}

	@EventHandler
	private void entityDeathEvent(EntityDeathEvent event) {
		final Player k = event.getEntity().getKiller();
		if(k != null) {
			final HashMap<String, String> replacements = new HashMap<>();
			final List<ActiveBooster> boosters = getFactionBoosters(k);
			for(ActiveBooster ab : boosters) {
				final double multiplier = ab.getMultiplier();
				final long duration = ab.getDuration();
				final String M = Double.toString(multiplier), D = Long.toString(duration);
				replacements.put("multiplier", M);
				replacements.put("duration", D);
				final List<ExecutedEventAttributes> e = executeAttributes(event, ab.getBooster().getAttributes(), replacements);
				if(e != null) {
					replacements.put("{MULTIPLIER}", M);
					replacements.put("{PLAYER}", ab.getActivator().getName());
					replacements.put("{TIME}", getRemainingTime(ab.getRemainingTime()));
					sendNotify(k, ab, replacements);
				}
			}
		}
	}
}
