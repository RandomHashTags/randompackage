package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.event.regional.RegionDisbandEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.FileBooster;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.util.obj.TObject;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class Boosters extends EventAttributes {
	private static Boosters instance;
	public static Boosters getBoosters() {
	    if(instance == null) instance = new Boosters();
	    return instance;
	}

	public File dataF;
	public YamlConfiguration data;
	public static HashMap<String, List<ActiveBooster>> activeRegionalBoosters;
	public static HashMap<UUID, List<ActiveBooster>> activePlayerBoosters;

	private MCMMOBoosterEvents mcmmoboosters;

	public String getIdentifier() { return "BOOSTERS"; }
	protected RPFeature getFeature() { return getBoosters(); }
	public void load() {
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

		if(mcmmoIsEnabled()) {
			mcmmoboosters = new MCMMOBoosterEvents();
			pluginmanager.registerEvents(mcmmoboosters, randompackage);
		}

		activeRegionalBoosters = new HashMap<>();
		activePlayerBoosters = new HashMap<>();

		final List<ItemStack> b = new ArrayList<>();
		final File folder = new File(rpd + separator + "boosters");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final FileBooster bo = new FileBooster(f);
				b.add(bo.getItem(60*10000, 5.0));
			}
			addGivedpCategory(b, UMaterial.EMERALD, "Boosters", "Givedp: Boosters");
		}
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (boosters != null ? boosters.size() : 0) + " Boosters &e(took " + (System.currentTimeMillis()-started) + "ms)");
		loadBackup();
	}
	public void unload() {
		backup();
		if(mcmmoIsEnabled()) {
			HandlerList.unregisterAll(mcmmoboosters);
			mcmmoboosters = null;
		}
		boosters = null;
		activeRegionalBoosters = null;
		activePlayerBoosters = null;
	}

	public void backup() {
		data.set("factions", null);
		if(activeRegionalBoosters != null) {
			for(String s : activeRegionalBoosters.keySet()) {
				final List<ActiveBooster> boosters = activeRegionalBoosters.get(s);
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
				if(activeRegionalBoosters == null) activeRegionalBoosters = new HashMap<>();
				for(String s : c.getKeys(false)) {
					if(!activeRegionalBoosters.containsKey(s)) activeRegionalBoosters.put(s, new ArrayList<>());
					final ConfigurationSection boosters = data.getConfigurationSection("factions." + s);
					if(boosters != null) {
						for(String b : boosters.getKeys(false)) {
							final String p = "factions." + s + "." + b + ".";
							final ActiveBooster a = new ActiveBooster(Bukkit.getOfflinePlayer(UUID.fromString(data.getString(p + "activator"))), getBooster(b), data.getDouble(p + "multiplier"), data.getLong(p + "duration"), data.getLong(p + "expiration"));
							if(a.getRemainingTime() > 0) {
								activeRegionalBoosters.get(s).add(a);
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
			final TObject m = valueOf(is);
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
			if(!ee.isCancelled()) finish(ee);
		}
		return !cancelled;
	}
	private void finish(BoosterActivateEvent event) {
		final Booster b = event.booster;
		if(b instanceof FileBooster) {
			final OfflinePlayer player = event.activator;
			final UUID u = player.getUniqueId();
			final double multiplier = event.multiplier;
			final long duration = event.duration;
			final HashMap<String, String> replacements = new HashMap<>();
			replacements.put("{PLAYER}", player.getName());
			replacements.put("{MULTIPLIER}", Double.toString(multiplier));

			switch(b.getRecipients()) {
				case "FACTION_MEMBERS":
					if(hookedFactionsUUID()) {
						final String faction = getFactionTag(u);
						if(faction != null) {
							final ActiveBooster booster = new ActiveBooster(event, faction, System.currentTimeMillis()+duration);
							replacements.put("{TIME}", getRemainingTime(booster.getRemainingTime()));
							final List<Player> members = factions.getOnlineAssociates(u);
							if(members != null) {
								for(Player p : members) {
									sendStringListMessage(p, b.getActivateMsg(), replacements);
								}
							}
							if(!activeRegionalBoosters.containsKey(faction)) activeRegionalBoosters.put(faction, new ArrayList<>());
							activeRegionalBoosters.get(faction).add(booster);
						}
					}
					break;
				case "SELF":
					final ActiveBooster booster = new ActiveBooster(event, System.currentTimeMillis()+duration);
					replacements.put("{TIME}", getRemainingTime(booster.getRemainingTime()));
					if(player.isOnline()) {
						sendStringListMessage(player.getPlayer(), b.getActivateMsg(), replacements);
					}
					if(!activePlayerBoosters.containsKey(u)) activePlayerBoosters.put(u, new ArrayList<>());
					activePlayerBoosters.get(u).add(booster);
					break;
			}
		}
	}
	@EventHandler
	private void boosterExpireEvent(BoosterExpireEvent event) {
		final ActiveBooster b = event.booster;
		final Booster booster = b.getBooster();
		final List<String> expire = booster.getExpireMsg();
		final OfflinePlayer a = b.getActivator();
		final UUID u = a.getUniqueId();
		final HashMap<String, String> replacements = new HashMap<>();
		replacements.put("{PLAYER}", a.getName());
		replacements.put("{MULTIPLIER}", Double.toString(b.getMultiplier()));
		switch(booster.getRecipients()) {
			case "FACTION_MEMBERS":
				boolean did = false;
				final String faction = b.getFaction();
				if(faction != null && activeRegionalBoosters.containsKey(faction)) {
					final List<ActiveBooster> boosters = activeRegionalBoosters.get(faction);
					if(boosters.contains(b)) {
						did = true;
						boosters.remove(b);
						for(Player p : factions.getOnlineAssociates(u)) {
							sendStringListMessage(p, expire, replacements);
						}
					}
				}
				if(!did && a.isOnline()) {
					sendStringListMessage(a.getPlayer(), expire, replacements);
				}
				break;
			case "SELF":
			default:
				if(activePlayerBoosters.containsKey(u) && a.isOnline()) {
					sendStringListMessage(a.getPlayer(), expire, replacements);
				}
				break;
		}
	}
	@EventHandler
	private void regionDisbandEvent(RegionDisbandEvent event) {
		final String s = event.identifier;
		if(activeRegionalBoosters.containsKey(s)) {
			for(ActiveBooster b : activeRegionalBoosters.get(s)) {
				b.expire(false);
			}
			activeRegionalBoosters.remove(s);
		}
	}

	private List<ActiveBooster> getRegionalBoosters(String identifier) { return activeRegionalBoosters != null ? activeRegionalBoosters.getOrDefault(identifier, new ArrayList<>()) : new ArrayList<>(); }
	private List<ActiveBooster> getSelfBoosters(UUID player) { return activePlayerBoosters != null ? activePlayerBoosters.getOrDefault(player, new ArrayList<>()) : new ArrayList<>(); }
	private List<ActiveBooster> getFactionBoosters(UUID player) { return hookedFactionsUUID() ? getRegionalBoosters(getFactionTag(player)) : new ArrayList<>(); }

	private void sendNotify(Player player, ActiveBooster b, HashMap<String, String> replacements) {
		if(player != null && b != null) {
			sendStringListMessage(player, b.getBooster().getNotifyMsg(), replacements);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDeathEvent(EntityDeathEvent event) {
		final Player k = event.getEntity().getKiller();
		if(k != null) {
			final UUID u = k.getUniqueId();
			final HashMap<String, String> replacements = new HashMap<>();
			final List<ActiveBooster> boosters = new ArrayList<>();
			boosters.addAll(getFactionBoosters(u));
			boosters.addAll(getSelfBoosters(u));
			for(ActiveBooster ab : boosters) {
				final String M = Double.toString(ab.getMultiplier()), D = Long.toString(ab.getDuration());
				final BoosterTriggerEvent e = new BoosterTriggerEvent(event, k, ab);
				pluginmanager.callEvent(e);
				if(trigger(event, ab.getBooster().getAttributes(), "multiplier", M, "duration", D)) {
					replacements.put("multiplier", M);
					replacements.put("duration", D);
					replacements.put("{MULTIPLIER}", M);
					replacements.put("{PLAYER}", ab.getActivator().getName());
					replacements.put("{TIME}", getRemainingTime(ab.getRemainingTime()));
					sendNotify(k, ab, replacements);
				}
			}
		}
	}

	public TObject valueOf(ItemStack is) {
		if(boosters != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final ItemMeta m = is.getItemMeta();
			final String d = m.getDisplayName();
			final List<String> l = m.getLore();
			for(Booster b : boosters.values()) {
				final ItemStack i = b.getItem();
				if(d.equals(i.getItemMeta().getDisplayName())) {
					double multiplier = getRemainingDouble(ChatColor.stripColor(l.get(b.getMultiplierLoreSlot())));
					long duration = getTime(ChatColor.stripColor(l.get(b.getTimeLoreSlot())));
					return new TObject(b, multiplier, duration);
				}
			}
		}
		return null;
	}

	private class MCMMOBoosterEvents implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST)
		private void mcmmoPlayerXpGainEvent(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event) {
			final Player player = event.getPlayer();
			final List<ActiveBooster> boosters = new ArrayList<>();
			if(hookedFactionsUUID()) {
				final String f = factions.getFactionTag(player.getUniqueId());
				boosters.addAll(getRegionalBoosters(f));
			}
			boosters.addAll(getSelfBoosters(player.getUniqueId()));
			for(ActiveBooster ab : boosters) {
				final HashMap<String, String> replacements = new HashMap<>();
				final String M = Double.toString(ab.getMultiplier()), D = Long.toString(ab.getDuration());
				final BoosterTriggerEvent e = new BoosterTriggerEvent(event, player, ab);
				pluginmanager.callEvent(e);
				if(trigger(event, ab.getBooster().getAttributes(), "multiplier", M, "duration", D)) {
					replacements.put("{MULTIPLIER}", M);
					replacements.put("{PLAYER}", ab.getActivator().getName());
					replacements.put("{TIME}", getRemainingTime(ab.getRemainingTime()));
					sendNotify(player, ab, replacements);
				}
			}
		}
	}
}
