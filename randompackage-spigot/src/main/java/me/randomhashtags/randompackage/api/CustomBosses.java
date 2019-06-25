package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.classes.custombosses.*;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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

public class CustomBosses extends RandomPackageAPI implements Listener {

	private static CustomBosses instance;
	public static final CustomBosses getCustomBosses() {
		if(instance == null) instance = new CustomBosses();
		return instance;
	}

	public boolean isEnabled = false;
	private HashMap<UUID, LivingCustomBoss> deadBosses;

	public void enable() {
		final long started = System.currentTimeMillis();
		if(isEnabled) return;
		pluginmanager.registerEvents(this, randompackage);
		isEnabled = true;

		deadBosses = new HashMap<>();

		final YamlConfiguration a = otherdata;
		if(!a.getBoolean("saved default custom bosses")) {
			final String[] s = new String[] { "BROOD_MOTHER", "KING_SLIME", "PLAGUE_BLOATER", "SOUL_REAPER" };
			for(String o : s) save("custom bosses", o + ".yml");
			a.set("saved default custom bosses", true);
			saveOtherData();
		}
		final List<ItemStack> j = new ArrayList<>();
		for(File f : new File(rpd + separator + "custom bosses").listFiles()) {
			final CustomBoss b = new CustomBoss(f);
			j.add(b.getSpawnItem());
		}
		loadBackup();
		addGivedpCategory(j, UMaterial.SPIDER_SPAWN_EGG, "Custom Bosses", "Givedp: Custom Bosses");
		final HashMap<String, CustomBoss> C = CustomBoss.bosses;
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (C != null ? C.size() : 0) + " Custom Bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void disable() {
		if(!isEnabled) return;
		backup();
		LivingCustomBoss.deleteAll();
		LivingCustomMinion.deleteAll();
		CustomBoss.deleteAll();
		deadBosses = null;
		isEnabled = false;
		HandlerList.unregisterAll(this);
	}

	public void backup() {
		final YamlConfiguration a = otherdata;
		a.set("custom bosses", null);
		final HashMap<UUID, LivingCustomBoss> j = LivingCustomBoss.living;
		if(j != null) {
			for(LivingCustomBoss b : j.values()) {
				final String p = "custom bosses." + b.entity.getUniqueId().toString() + ".";
				final LivingEntity s = b.summoner;
				a.set(p + "summoner", s != null ? s.getUniqueId().toString() : "null");
				a.set(p + "type", b.type.getYamlName());
				final List<String> m = new ArrayList<>();
				for(LivingCustomMinion lcm : b.minions) m.add(lcm.entity.getUniqueId().toString());
				a.set(p + "minions", m);
				final HashMap<UUID, Double> D = b.damagers;
				for(UUID u : D.keySet())
					a.set(p + "damager." + u.toString(), D.get(u));
			}
		}
		saveOtherData();
	}
	public void loadBackup() {
		final YamlConfiguration a = otherdata;
		final ConfigurationSection bosses = a.getConfigurationSection("custom bosses");
		if(bosses != null) {
			for(String s : bosses.getKeys(false)) {
				final UUID u = UUID.fromString(s);
				final Entity e = getEntity(u);
				if(e != null && !e.isDead()) {
					final String p = "custom bosses." + s + ".", S = a.getString(p + "summoner");
					final LivingEntity summoner = S != null && !S.equals("null") ? (LivingEntity) getEntity(UUID.fromString(S)) : null;
					final LivingCustomBoss l = new LivingCustomBoss(summoner, (LivingEntity) e, CustomBoss.bosses.get(a.getString(p + "type")));
					final ConfigurationSection d = a.getConfigurationSection(p + "damager");
					if(d != null)
						for(String aa : d.getKeys(false))
							l.damagers.put(UUID.fromString(aa), a.getDouble(p + "damager." + a));
				}
			}
		}
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack I = event.getItem();
		if(I != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final CustomBoss c = CustomBoss.valueOf(I);
			if(c != null) {
				final Player player = event.getPlayer();
				event.setCancelled(true);
				player.updateInventory();
				final Location l = event.getClickedBlock().getLocation();
				final String s = c.getSpawnableRegion();
				if(s.equals("ANYWHERE") || s.equals("WARZONE") && fapi.getWarZoneChunks().contains(l.getChunk())) {
					removeItem(player, I, 1);
					c.spawn(player, new Location(l.getWorld(), l.getX(), l.getY()+1, l.getZ()));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if(!event.isCancelled()) {
			final HashMap<UUID, LivingCustomBoss> L = LivingCustomBoss.living;
			if(L != null) {
				final LivingCustomBoss c = L.getOrDefault(event.getEntity().getUniqueId(), null);
				if(c != null) {
					final LivingEntity entity = (LivingEntity) event.getEntity();
					c.damage(entity, event.getDamager(), event.getFinalDamage());
				}
			}
		}
	}
	@EventHandler
	private void slimeSplitEvent(SlimeSplitEvent event) {
		final UUID u = event.getEntity().getUniqueId();
		if(deadBosses.containsKey(u)) {
			for(String a : deadBosses.get(u).type.getAttributes()) {
				if(a.toLowerCase().startsWith("split=false"))
					event.setCancelled(true);
			}
			deadBosses.remove(u);
		}
	}
	@EventHandler
	private void entityDeathEvent(EntityDeathEvent event) {
		final LivingEntity e = event.getEntity();
		final UUID u = e.getUniqueId();
		final HashMap<UUID, LivingCustomBoss> L = LivingCustomBoss.living;
		if(L != null) {
			final LivingCustomBoss c = L.getOrDefault(u, null);
			final HashMap<UUID, LivingCustomMinion> M = LivingCustomMinion.living;
			final LivingCustomMinion m = c == null && M != null ? M.getOrDefault(u, null) : null;
			if(c != null || m != null) {
				final EntityDamageEvent ede = e.getLastDamageCause();
				event.setDroppedExp(0);
				event.getDrops().clear();
				if(c != null) {
					deadBosses.put(u, c);
					c.kill(e, ede);
				} else {
					m.kill(event);
				}
			}
		}
	}
}
