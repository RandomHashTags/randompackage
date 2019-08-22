package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.Booster;
import me.randomhashtags.randompackage.addons.living.ActiveBooster;
import me.randomhashtags.randompackage.addons.objects.ExecutedEventAttributes;
import me.randomhashtags.randompackage.events.regional.FactionDisbandEvent;
import me.randomhashtags.randompackage.utils.addons.FileBooster;
import me.randomhashtags.randompackage.events.*;
import me.randomhashtags.randompackage.utils.newEventAttributes;
import me.randomhashtags.randompackage.utils.objects.TObject;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
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

public class Boosters extends newEventAttributes {
	private static Boosters instance;
	public static Boosters getBoosters() {
	    if(instance == null) instance = new Boosters();
	    return instance;
	}

	public File dataF;
	public YamlConfiguration data;
	public static HashMap<String, List<ActiveBooster>> activeFactionBoosters;
	public static HashMap<UUID, List<ActiveBooster>> activePlayerBoosters;

	private MCMMOBoosterEvents mcmmoboosters;

	public String getIdentifier() { return "BOOSTERS"; }
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

		if(mcmmoIsEnabled()) {
			mcmmoboosters = new MCMMOBoosterEvents();
			pluginmanager.registerEvents(mcmmoboosters, randompackage);
		}

		activeFactionBoosters = new HashMap<>();
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
		activeFactionBoosters = null;
		activePlayerBoosters = null;
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
							if(!activeFactionBoosters.containsKey(faction)) activeFactionBoosters.put(faction, new ArrayList<>());
							activeFactionBoosters.get(faction).add(booster);
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
				if(faction != null && activeFactionBoosters.containsKey(faction)) {
					final List<ActiveBooster> boosters = activeFactionBoosters.get(faction);
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
			default: // SELF
				if(activePlayerBoosters.containsKey(u) && a.isOnline()) {
					sendStringListMessage(a.getPlayer(), expire, replacements);
				}
				break;
		}
	}
	@EventHandler
	private void factionDisbandEvent(FactionDisbandEvent event) {
		final String s = event.factionName;
		if(activeFactionBoosters.containsKey(s)) {
			for(ActiveBooster b : activeFactionBoosters.get(s)) {
				b.expire(false);
			}
			activeFactionBoosters.remove(s);
		}
	}

	private List<ActiveBooster> getFactionBoosters(String faction) { return activeFactionBoosters != null ? activeFactionBoosters.getOrDefault(faction, new ArrayList<>()) : new ArrayList<>(); }
	private List<ActiveBooster> getSelfBoosters(UUID player) { return activePlayerBoosters != null ? activePlayerBoosters.getOrDefault(player, new ArrayList<>()) : new ArrayList<>(); }
	private List<ActiveBooster> getFactionBoosters(UUID player) { return hookedFactionsUUID() ? getFactionBoosters(getFactionTag(player)) : new ArrayList<>(); }

	private void sendNotify(Player player, ActiveBooster b, HashMap<String, String> replacements) {
		if(player != null && b != null) {
			sendStringListMessage(player, b.getBooster().getNotifyMsg(), replacements);
		}
	}

	@EventHandler
	private void entityDeathEvent(EntityDeathEvent event) {
		final Player k = event.getEntity().getKiller();
		if(k != null) {
			final UUID u = k.getUniqueId();
			final HashMap<String, String> replacements = new HashMap<>();
			final List<ActiveBooster> boosters = new ArrayList<>();
			boosters.addAll(getFactionBoosters(u));
			boosters.addAll(getSelfBoosters(u));
			for(ActiveBooster ab : boosters) {
				final double multiplier = ab.getMultiplier();
				final long duration = ab.getDuration();
				final String M = Double.toString(multiplier), D = Long.toString(duration);
				replacements.put("multiplier", M);
				replacements.put("duration", D);
				final BoosterPreTriggerEvent pre = new BoosterPreTriggerEvent(event, k, ab);
				pluginmanager.callEvent(pre);
				if(!pre.isCancelled()) {
					final List<ExecutedEventAttributes> e = executeAttributes(event, ab.getBooster().getAttributes(), replacements);
					if(e != null) {
						final BoosterTriggerEvent t = new BoosterTriggerEvent(event, k, ab, e);
						pluginmanager.callEvent(t);
						replacements.put("{MULTIPLIER}", M);
						replacements.put("{PLAYER}", ab.getActivator().getName());
						replacements.put("{TIME}", getRemainingTime(ab.getRemainingTime()));
						sendNotify(k, ab, replacements);
					}
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
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		private void mcmmoPlayerXpGainEvent(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event) {
			final Player player = event.getPlayer();
			final List<ActiveBooster> boosters = new ArrayList<>();
			if(hookedFactionsUUID()) {
				final String f = factions.getFactionTag(player.getUniqueId());
				boosters.addAll(getFactionBoosters(f));
			}
			boosters.addAll(getSelfBoosters(player.getUniqueId()));
			for(ActiveBooster ab : boosters) {
				final HashMap<String, String> replacements = new HashMap<>();
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
					sendNotify(player, ab, replacements);
				}
			}
		}
		private ExecutedEventAttributes doAttribute(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event, Player player, String attribute, String wholeAttribute) {
			String attributeLowercase = attribute.toLowerCase();
			final float xp = event.getRawXpGained();
			final TreeMap<String, Entity> entities = getEntities("Player", player);
			if(attributeLowercase.startsWith("gainedxp=")) {
				final String s = attributeLowercase.split("=")[1].replace("xp", Float.toString(xp));
				event.setRawXpGained((float) eval(s));
				final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
				attributes.put(wholeAttribute, attributeLowercase);
				return new ExecutedEventAttributes(event, attributes);
			} else {
				if(player != null && attributeLowercase.contains("playerlocation")) attributeLowercase = attributeLowercase.replace("playerlocation", Boosters.this.toString(player.getLocation()));
				return doGenericAttribute(event, entities, attribute, attributeLowercase);
			}
		}
		private List<ExecutedEventAttributes> executeAttributes(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event, List<String> attributes, HashMap<String, String> attributeReplacements) {
			if(success(event, attributes, attributeReplacements)) {
				attributes = replace(attributes, attributeReplacements);
				final Player player = event.getPlayer();
				final List<ExecutedEventAttributes> e = new ArrayList<>();
				final String skill = event.getSkill().name();
				final TreeMap<String, Entity> entities = getEntities("Player", player);
				for(String s : attributes) {
					if(s.toLowerCase().startsWith("mcmmoxpgained;")) {
						boolean did = true;
						for(String a : s.split(s.split(";")[0] + ";")[1].split(";")) {
							final String al = a.toLowerCase();
							final boolean passed = passedIfs(event, entities, a);
							if(!passed) return null;
							else if(al.startsWith("skill=")) {
								did = skill.equals(al.split("=")[1].toUpperCase());
							} else if(did) {
								final ExecutedEventAttributes att = doAttribute(event, player, a, s);
								if(att != null) e.add(att);
							}
						}
					}
				}
				if(player != null) player.updateInventory();
				if(!e.isEmpty()) return e;
			}
			return null;
		}
	}
}
