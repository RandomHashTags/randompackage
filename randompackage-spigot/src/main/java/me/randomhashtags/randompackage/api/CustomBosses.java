package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.CustomBoss;
import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.addon.living.LivingCustomMinion;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.addon.file.FileCustomBoss;
import me.randomhashtags.randompackage.dev.Feature;
import me.randomhashtags.randompackage.util.universal.UMaterial;
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

	public String getIdentifier() { return "CUSTOM_BOSSES"; }
	public void load() {
		final long started = System.currentTimeMillis();
		deadBosses = new HashMap<>();

		if(!otherdata.getBoolean("saved default custom bosses")) {
			final String[] s = new String[] { "BROOD_MOTHER", "KING_SLIME", "PLAGUE_BLOATER", "SOUL_REAPER" };
			for(String o : s) save("custom bosses", o + ".yml");
			otherdata.set("saved default custom bosses", true);
			saveOtherData();
		}
		final List<ItemStack> j = new ArrayList<>();
		final File folder = new File(dataFolder + separator + "custom bosses");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final FileCustomBoss b = new FileCustomBoss(f);
				j.add(b.getSpawnItem());
			}
			addGivedpCategory(j, UMaterial.SPIDER_SPAWN_EGG, "Custom Bosses", "Givedp: Custom Bosses");
		}
		loadBackup();
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.CUSTOM_BOSS).size() + " Custom Bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		backup();
		LivingCustomBoss.living = null;
		LivingCustomMinion.deleteAll();
		unregister(Feature.CUSTOM_BOSS);
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
				a.set(p + "type", b.type.getIdentifier());
				final List<String> m = new ArrayList<>();
				for(LivingCustomMinion lcm : b.minions) m.add(lcm.entity.getUniqueId().toString());
				a.set(p + "minions", m);
				final HashMap<UUID, Double> D = b.damagers;
				for(UUID u : D.keySet()) {
                    a.set(p + "damager." + u.toString(), D.get(u));
                }
			}
		}
		saveOtherData();
	}
	public void loadBackup() {
		final ConfigurationSection bosses = otherdata.getConfigurationSection("custom bosses");
		if(bosses != null) {
			for(String s : bosses.getKeys(false)) {
				final UUID u = UUID.fromString(s);
				final Entity e = getEntity(u);
				if(e != null && !e.isDead()) {
					final String p = "custom bosses." + s + ".", S = otherdata.getString(p + "summoner");
					final LivingEntity summoner = S != null && !S.equals("null") ? (LivingEntity) getEntity(UUID.fromString(S)) : null;
					final LivingCustomBoss l = new LivingCustomBoss(summoner, (LivingEntity) e, getCustomBoss(otherdata.getString(p + "type")));
					final ConfigurationSection d = otherdata.getConfigurationSection(p + "damager");
					if(d != null) {
						for(String aa : d.getKeys(false)) {
                            l.damagers.put(UUID.fromString(aa), otherdata.getDouble(p + "damager." + aa));
                        }
                    }
				}
			}
		}
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack I = event.getItem();
		if(I != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final CustomBoss c = valueOfCustomBoss(I);
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
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		final HashMap<UUID, LivingCustomBoss> L = LivingCustomBoss.living;
		if(L != null) {
			final LivingCustomBoss c = L.getOrDefault(event.getEntity().getUniqueId(), null);
			if(c != null) {
				final LivingEntity entity = (LivingEntity) event.getEntity();
				c.damage(entity, event.getDamager(), event.getFinalDamage());
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	private void slimeSplitEvent(SlimeSplitEvent event) {
		final UUID u = event.getEntity().getUniqueId();
		if(deadBosses.containsKey(u)) {
			for(String a : deadBosses.get(u).type.getAttributes()) {
				if(a.toLowerCase().startsWith("split=false")) {
					event.setCancelled(true);
					break;
				}
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
			final LivingCustomBoss c = L.get(u);
			final HashMap<UUID, LivingCustomMinion> M = LivingCustomMinion.living;
			final LivingCustomMinion m = c == null && M != null ? M.get(u) : null;
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
