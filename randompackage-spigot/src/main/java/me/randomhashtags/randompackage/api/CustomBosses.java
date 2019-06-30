package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomBoss;
import me.randomhashtags.randompackage.utils.classes.custombosses.*;
import me.randomhashtags.randompackage.utils.classes.living.LivingCustomBoss;
import me.randomhashtags.randompackage.utils.classes.living.LivingCustomMinion;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.util.*;

public class CustomBosses extends RPFeature {
	private static CustomBosses instance;
	public static CustomBosses getCustomBosses() {
		if(instance == null) instance = new CustomBosses();
		return instance;
	}

	private HashMap<UUID, LivingCustomBoss> deadBosses;

	public void load() {
		final long started = System.currentTimeMillis();
		deadBosses = new HashMap<>();

		final YamlConfiguration a = otherdata;
		if(!a.getBoolean("saved default custom bosses")) {
			final String[] s = new String[] { "BROOD_MOTHER", "KING_SLIME", "PLAGUE_BLOATER", "SOUL_REAPER" };
			for(String o : s) save("custom bosses", o + ".yml");
			a.set("saved default custom bosses", true);
			saveOtherData();
		}
		final List<ItemStack> j = new ArrayList<>();
		final File folder = new File(rpd + separator + "custom bosses");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final CustomBoss b = new CustomBoss(f);
				j.add(b.getSpawnItem());
			}
		}
		loadBackup();
		addGivedpCategory(j, UMaterial.SPIDER_SPAWN_EGG, "Custom Bosses", "Givedp: Custom Bosses");
		final HashMap<NamespacedKey, AbstractCustomBoss> C = AbstractCustomBoss.bosses;
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (C != null ? C.size() : 0) + " Custom Bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		backup();
		LivingCustomBoss.living = null;
		LivingCustomMinion.deleteAll();
		AbstractCustomBoss.bosses = null;
		AbstractCustomBoss.spawnType = null;
		deadBosses = null;
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
			final AbstractCustomBoss c = AbstractCustomBoss.valueOf(I);
			if(c != null) {
				final Player player = event.getPlayer();
				event.setCancelled(true);
				player.updateInventory();
				final Location l = event.getClickedBlock().getLocation();
				if(c.canSpawnAt(l)) {
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
