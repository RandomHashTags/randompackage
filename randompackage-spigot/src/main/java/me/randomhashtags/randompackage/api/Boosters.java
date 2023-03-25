package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.addon.file.FileBooster;
import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.attributesys.EventAttributeListener;
import me.randomhashtags.randompackage.attributesys.EventExecutor;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.booster.BoosterActivateEvent;
import me.randomhashtags.randompackage.event.booster.BoosterExpireEvent;
import me.randomhashtags.randompackage.event.booster.BoosterPreActivateEvent;
import me.randomhashtags.randompackage.event.booster.BoosterTriggerEvent;
import me.randomhashtags.randompackage.event.regional.RegionDisbandEvent;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.obj.TObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum Boosters implements RPFeatureSpigot, EventAttributeListener, EventExecutor {
	INSTANCE;

	public static HashMap<String, List<ActiveBooster>> ACTIVE_REGIONAL_BOOSTERS;
	public static HashMap<UUID, List<ActiveBooster>> ACTIVE_PLAYER_BOOSTERS;

	public File dataF;
	public YamlConfiguration data;
	private MCMMOBoosterEvents mcmmo_booster_events;

	@Override
	public @NotNull Feature get_feature() {
		return Feature.BOOSTER;
	}

	@Override
	public void load() {
		save("_Data", "boosters.yml");
		dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "boosters.yml");
		data = YamlConfiguration.loadConfiguration(dataF);
		if(!OTHER_YML.getBoolean("saved default boosters")) {
			generateDefaultBoosters();
			OTHER_YML.set("saved default boosters", true);
			saveOtherData();
		}

		if(RPFeatureSpigot.mcmmoIsEnabled()) {
			mcmmo_booster_events = new MCMMOBoosterEvents();
			PLUGIN_MANAGER.registerEvents(mcmmo_booster_events, RANDOM_PACKAGE);
		}

		ACTIVE_REGIONAL_BOOSTERS = new HashMap<>();
		ACTIVE_PLAYER_BOOSTERS = new HashMap<>();

		final List<ItemStack> b = new ArrayList<>();
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "boosters")) {
			final FileBooster bo = new FileBooster(f);
			b.add(bo.getItem(60*10000, 5.0));
		}
		addGivedpCategory(b, UMaterial.EMERALD, "Boosters", "Givedp: Boosters");
		loadBackup();
	}
	@Override
	public void unload() {
		backup();
		if(RPFeatureSpigot.mcmmoIsEnabled()) {
			HandlerList.unregisterAll(mcmmo_booster_events);
			mcmmo_booster_events = null;
		}
		ACTIVE_REGIONAL_BOOSTERS = null;
		ACTIVE_PLAYER_BOOSTERS = null;
	}

	public void backup() {
		data.set("factions", null);
		if(ACTIVE_REGIONAL_BOOSTERS != null) {
			for(String factionUUID : ACTIVE_REGIONAL_BOOSTERS.keySet()) {
				final List<ActiveBooster> boosters = ACTIVE_REGIONAL_BOOSTERS.get(factionUUID);
				for(ActiveBooster booster : boosters) {
					final String key = "factions." + factionUUID + "." + booster.getBooster().getIdentifier() + ".";
					data.set(key + "activator", booster.getActivator().getUniqueId().toString());
					data.set(key + "expiration", booster.getExpiration());
					data.set(key + "duration", booster.getDuration());
					data.set(key + "multiplier", booster.getMultiplier());
				}
			}
		}
		save();
	}
	public void loadBackup() {
		SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> {
			int loaded = 0, expired = 0;
			final ConfigurationSection configuration = data.getConfigurationSection("factions");
			if(configuration != null) {
				if(ACTIVE_REGIONAL_BOOSTERS == null) {
					ACTIVE_REGIONAL_BOOSTERS = new HashMap<>();
				}
				for(String factionUUID : configuration.getKeys(false)) {
					ACTIVE_REGIONAL_BOOSTERS.putIfAbsent(factionUUID, new ArrayList<>());
					for(String key : getConfigurationSectionKeys(data, "factions." + factionUUID, false)) {
						final String path = "factions." + factionUUID + "." + key + ".";
						final ActiveBooster booster = new ActiveBooster(Bukkit.getOfflinePlayer(UUID.fromString(data.getString(path + "activator"))), getBooster(key), data.getDouble(path + "multiplier"), data.getLong(path + "duration"), data.getLong(path + "expiration"));
						if(booster.getRemainingTime() > 0) {
							ACTIVE_REGIONAL_BOOSTERS.get(factionUUID).add(booster);
							loaded++;
						} else {
							expired++;
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
			data = YamlConfiguration.loadConfiguration(dataF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean activateBooster(Player player, Booster booster, double multiplier, long duration) {
		final BoosterPreActivateEvent e = new BoosterPreActivateEvent(player, booster, multiplier, duration);
		PLUGIN_MANAGER.callEvent(e);
		final boolean cancelled = e.isCancelled();
		if(!cancelled) {
			final BoosterActivateEvent ee = new BoosterActivateEvent(player, booster, multiplier, duration);
			PLUGIN_MANAGER.callEvent(ee);
			if(!ee.isCancelled()) {
				return finish(ee);
			}
		}
		return !cancelled;
	}
	private boolean finish(@NotNull BoosterActivateEvent event) {
		final Booster booster = event.booster;
		if(booster instanceof FileBooster) {
			final OfflinePlayer player = event.activator;
			final UUID uuid = player.getUniqueId();
			final double multiplier = event.multiplier;
			final long duration = event.duration;
			final HashMap<String, String> replacements = new HashMap<>();
			replacements.put("{PLAYER}", player.getName());
			replacements.put("{MULTIPLIER}", Double.toString(multiplier));

			final RegionalAPI regions = RegionalAPI.INSTANCE;
			switch (booster.getRecipients()) {
				case FACTION_MEMBERS:
					if(regions.hookedFactionsUUID()) {
						final FactionsUUID factions = FactionsUUID.INSTANCE;
						final String faction = regions.getFactionTag(uuid);
						if(faction != null) {
							final ActiveBooster activeBooster = new ActiveBooster(event, faction, System.currentTimeMillis()+duration);
							replacements.put("{TIME}", getRemainingTime(activeBooster.getRemainingTime()));
							final List<Player> members = factions.getOnlineAssociates(uuid);
							if(members != null) {
								final List<String> msg = booster.getActivateMsg();
								for(Player p : members) {
									sendStringListMessage(p, msg, replacements);
								}
							}
							ACTIVE_REGIONAL_BOOSTERS.putIfAbsent(faction, new ArrayList<>());
							ACTIVE_REGIONAL_BOOSTERS.get(faction).add(activeBooster);
						}
					} else {
						return false;
					}
					return true;
				case SELF:
					final ActiveBooster activeBooster = new ActiveBooster(event, System.currentTimeMillis()+duration);
					replacements.put("{TIME}", getRemainingTime(activeBooster.getRemainingTime()));
					if(player.isOnline()) {
						sendStringListMessage(player.getPlayer(), booster.getActivateMsg(), replacements);
					}
					if(!ACTIVE_PLAYER_BOOSTERS.containsKey(uuid)) {
						ACTIVE_PLAYER_BOOSTERS.put(uuid, new ArrayList<>());
					}
					ACTIVE_PLAYER_BOOSTERS.get(uuid).add(activeBooster);
					return true;
				default:
					return false;
			}
		}
		return false;
	}
	private List<ActiveBooster> getRegionalBoosters(String identifier) {
		return ACTIVE_REGIONAL_BOOSTERS != null ? ACTIVE_REGIONAL_BOOSTERS.getOrDefault(identifier, new ArrayList<>()) : new ArrayList<>();
	}
	private List<ActiveBooster> getSelfBoosters(UUID player) {
		return ACTIVE_PLAYER_BOOSTERS != null ? ACTIVE_PLAYER_BOOSTERS.getOrDefault(player, new ArrayList<>()) : new ArrayList<>();
	}
	private List<ActiveBooster> getFactionBoosters(UUID player) {
		final RegionalAPI regions = RegionalAPI.INSTANCE;
		return regions.hookedFactionsUUID() ? getRegionalBoosters(regions.getFactionTag(player)) : new ArrayList<>();
	}
	private void sendNotify(Player player, ActiveBooster b, HashMap<String, String> replacements) {
		if(player != null && b != null) {
			sendStringListMessage(player, b.getBooster().getNotifyMsg(), replacements);
		}
	}
	private void triggerBoosters(@NotNull Player player, Event event) {
		final UUID uuid = player.getUniqueId();
		final HashMap<String, String> replacements = new HashMap<>();
		final List<ActiveBooster> boosters = new ArrayList<>();
		boosters.addAll(getFactionBoosters(uuid));
		boosters.addAll(getSelfBoosters(uuid));
		for(ActiveBooster booster : boosters) {
			final BoosterTriggerEvent trigger_event = new BoosterTriggerEvent(event, player, booster);
			PLUGIN_MANAGER.callEvent(trigger_event);
			final String multiplier = Double.toString(booster.getMultiplier()), duration = Long.toString(booster.getDuration());
			if(trigger(event, booster.getBooster().getAttributes(), "multiplier", multiplier, "duration", duration)) {
				replacements.put("multiplier", multiplier);
				replacements.put("duration", duration);
				replacements.put("{MULTIPLIER}", multiplier);
				replacements.put("{PLAYER}", booster.getActivator().getName());
				replacements.put("{TIME}", getRemainingTime(booster.getRemainingTime()));
				sendNotify(player, booster, replacements);
			}
		}
	}
	@Nullable
	public TObject valueOfBooster(@NotNull ItemStack is) {
		if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final ItemMeta m = is.getItemMeta();
			final String d = m.getDisplayName();
			final List<String> l = m.getLore();
			for(Booster b : getAllBoosters().values()) {
				final ItemStack i = b.getItem();
				if(d.equals(i.getItemMeta().getDisplayName())) {
					double multiplier = getRemainingDouble(ChatColor.stripColor(l.get(b.getMultiplierLoreSlot())));
					long duration = parseTime(ChatColor.stripColor(l.get(b.getTimeLoreSlot())));
					return new TObject(b, multiplier, duration);
				}
			}
		}
		return null;
	}
	public void called(@NotNull Event event) {
		if(event instanceof PlayerEvent) {
			triggerBoosters(((PlayerEvent) event).getPlayer(), event);
		} else {
			switch (event.getEventName().toLowerCase().split("event")[0]) {
				case "entitydeath":
					final EntityDeathEvent e = (EntityDeathEvent) event;
					final Player k = e.getEntity().getKiller();
					if(k != null) {
						triggerBoosters(k, e);
					}
					break;
				default:
					break;
			}
		}
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		if(is != null && !is.getType().equals(Material.AIR)) {
			final TObject booster = valueOfBooster(is);
			if(booster != null) {
				event.setCancelled(true);
				final Player player = event.getPlayer();
				player.updateInventory();
				if(activateBooster(player, (Booster) booster.getFirst(), (double) booster.getSecond(), (long) booster.getThird())) {
					removeItem(player, is, 1);
				}
			}
		}
	}
	@EventHandler
	private void boosterExpireEvent(BoosterExpireEvent event) {
		final ActiveBooster booster = event.booster;
		final Booster booster_type = booster.getBooster();
		final List<String> expire = booster_type.getExpireMsg();
		final OfflinePlayer activator = booster.getActivator();
		final UUID activator_uuid = activator.getUniqueId();
		final HashMap<String, String> replacements = new HashMap<>();
		replacements.put("{PLAYER}", activator.getName());
		replacements.put("{MULTIPLIER}", Double.toString(booster.getMultiplier()));
		switch (booster_type.getRecipients()) {
			case FACTION_MEMBERS:
				boolean did = false;
				final String faction = booster.getFaction();
				if(faction != null && ACTIVE_REGIONAL_BOOSTERS.containsKey(faction)) {
					final List<ActiveBooster> boosters = ACTIVE_REGIONAL_BOOSTERS.get(faction);
					if(boosters.contains(booster)) {
						did = true;
						boosters.remove(booster);
						for(Player p : FactionsUUID.INSTANCE.getOnlineAssociates(activator_uuid)) {
							sendStringListMessage(p, expire, replacements);
						}
					}
				}
				if(!did && activator.isOnline()) {
					sendStringListMessage(activator.getPlayer(), expire, replacements);
				}
				break;
			case SELF:
			default:
				if(ACTIVE_PLAYER_BOOSTERS.containsKey(activator_uuid) && activator.isOnline()) {
					sendStringListMessage(activator.getPlayer(), expire, replacements);
				}
				break;
		}
	}
	@EventHandler
	private void regionDisbandEvent(RegionDisbandEvent event) {
		final String identifier = event.identifier;
		if(ACTIVE_REGIONAL_BOOSTERS.containsKey(identifier)) {
			for(ActiveBooster activeBooster : ACTIVE_REGIONAL_BOOSTERS.get(identifier)) {
				activeBooster.expire(false);
			}
			ACTIVE_REGIONAL_BOOSTERS.remove(identifier);
		}
	}

	private class MCMMOBoosterEvents implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST)
		private void mcmmoPlayerXpGainEvent(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event) {
			final Player player = event.getPlayer();
			triggerBoosters(player, event);
		}
	}
}
