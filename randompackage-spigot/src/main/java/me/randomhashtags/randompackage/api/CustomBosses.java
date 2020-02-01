package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.CustomBoss;
import me.randomhashtags.randompackage.addon.file.FileCustomBoss;
import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.addon.living.LivingCustomMinion;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomBosses extends EventAttributes {
	private static CustomBosses instance;
	public static CustomBosses getCustomBosses() {
		if(instance == null) instance = new CustomBosses();
		return instance;
	}

	private HashMap<UUID, LivingCustomBoss> deadBosses;

	public String getIdentifier() {
		return "CUSTOM_BOSSES";
	}
	public void load() {
		final long started = System.currentTimeMillis();
		deadBosses = new HashMap<>();

		if(!otherdata.getBoolean("saved default custom bosses")) {
			generateDefaultCustomBosses();
			otherdata.set("saved default custom bosses", true);
			saveOtherData();
		}
		final List<ItemStack> list = new ArrayList<>();
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "custom bosses")) {
			final FileCustomBoss b = new FileCustomBoss(f);
			list.add(b.getSpawnItem());
		}
		addGivedpCategory(list, UMaterial.SPIDER_SPAWN_EGG, "Custom Bosses", "Givedp: Custom Bosses");
		loadBackup();
		sendConsoleDidLoadFeature(getAll(Feature.CUSTOM_BOSS).size() + " Custom Bosses", started);
	}
	public void unload() {
		backup();
		LivingCustomBoss.living = null;
		LivingCustomMinion.deleteAll();
		unregister(Feature.CUSTOM_BOSS);
	}

	public void backup() {
		otherdata.set("custom bosses", null);
		final HashMap<UUID, LivingCustomBoss> living = LivingCustomBoss.living;
		if(living != null) {
			for(LivingCustomBoss boss : living.values()) {
				final String path = "custom bosses." + boss.entity.getUniqueId().toString() + ".";
				final LivingEntity summoner = boss.summoner;
				otherdata.set(path + "summoner", summoner != null ? summoner.getUniqueId().toString() : "null");
				otherdata.set(path + "type", boss.type.getIdentifier());
				final List<String> minions = new ArrayList<>();
				for(LivingCustomMinion lcm : boss.minions) {
					minions.add(lcm.entity.getUniqueId().toString());
				}
				otherdata.set(path + "minions", minions);
				final HashMap<UUID, Double> damagers = boss.damagers;
				for(UUID u : damagers.keySet()) {
                    otherdata.set(path + "damager." + u.toString(), damagers.get(u));
                }
			}
		}
		saveOtherData();
	}
	public void loadBackup() {
		for(String s : getConfigurationSectionKeys(otherdata, "custom bosses", false)) {
			final Entity entity = getEntity(UUID.fromString(s));
			if(entity != null && !entity.isDead()) {
				final String path = "custom bosses." + s + ".", summonerUUID = otherdata.getString(path + "summoner");
				final LivingEntity summoner = summonerUUID != null && !summonerUUID.equals("null") ? (LivingEntity) getEntity(UUID.fromString(summonerUUID)) : null;
				final LivingCustomBoss boss = new LivingCustomBoss(summoner, (LivingEntity) entity, getCustomBoss(otherdata.getString(path + "type")));
				for(String uuid : getConfigurationSectionKeys(otherdata, path + "damager", false)) {
					boss.damagers.put(UUID.fromString(uuid), otherdata.getDouble(path + "damager." + uuid));
				}
			}
		}
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		if(is != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final CustomBoss boss = valueOfCustomBoss(is);
			if(boss != null) {
				final Player player = event.getPlayer();
				event.setCancelled(true);
				player.updateInventory();
				final Location l = event.getClickedBlock().getLocation();
				if(boss.canSpawnAt(l)) {
					removeItem(player, is, 1);
					boss.spawn(player, new Location(l.getWorld(), l.getX(), l.getY()+1, l.getZ()));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		final HashMap<UUID, LivingCustomBoss> living = LivingCustomBoss.living;
		if(living != null) {
			final LivingCustomBoss boss = living.getOrDefault(event.getEntity().getUniqueId(), null);
			if(boss != null) {
				final LivingEntity entity = (LivingEntity) event.getEntity();
				boss.damage(entity, event.getDamager(), event.getFinalDamage());
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	private void slimeSplitEvent(SlimeSplitEvent event) {
		final UUID uuid = event.getEntity().getUniqueId();
		if(deadBosses.containsKey(uuid)) {
			for(String attribute : deadBosses.get(uuid).type.getAttributes()) {
				if(attribute.toLowerCase().startsWith("split=false")) {
					event.setCancelled(true);
					break;
				}
			}
			deadBosses.remove(uuid);
		}
	}
	@EventHandler
	private void entityDeathEvent(EntityDeathEvent event) {
		final LivingEntity entity = event.getEntity();
		final UUID uuid = entity.getUniqueId();
		final HashMap<UUID, LivingCustomBoss> living = LivingCustomBoss.living;
		if(living != null) {
			final LivingCustomBoss boss = living.get(uuid);
			final HashMap<UUID, LivingCustomMinion> minions = LivingCustomMinion.living;
			final LivingCustomMinion minion = boss == null && minions != null ? minions.get(uuid) : null;
			if(boss != null || minion != null) {
				final EntityDamageEvent ede = entity.getLastDamageCause();
				event.setDroppedExp(0);
				event.getDrops().clear();
				if(boss != null) {
					deadBosses.put(uuid, boss);
					boss.kill(entity, ede);
				} else {
					minion.kill(event);
				}
			}
		}
	}
}
