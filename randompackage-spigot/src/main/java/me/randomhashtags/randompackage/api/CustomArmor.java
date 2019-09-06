package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.ArmorSet;
import me.randomhashtags.randompackage.events.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.events.armor.ArmorPieceBreakEvent;
import me.randomhashtags.randompackage.events.armor.ArmorUnequipEvent;
import me.randomhashtags.randompackage.utils.EventAttributes;
import me.randomhashtags.randompackage.events.ArmorSetEquipEvent;
import me.randomhashtags.randompackage.events.ArmorSetUnequipEvent;
import me.randomhashtags.randompackage.events.customenchant.PvAnyEvent;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.addons.FileArmorSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.utils.listeners.GivedpItem.givedpitem;

public class CustomArmor extends EventAttributes {
	private static CustomArmor instance;
	public static CustomArmor getCustomArmor() {
		if(instance == null) instance = new CustomArmor();
		return instance;
	}
	
	public YamlConfiguration config;
	public ItemStack equipmentLootbox;
	private List<Player> inEquipmentLootbox;

	public String getIdentifier() { return "CUSTOM_ARMOR"; }
	protected RPFeature getFeature() { return getCustomArmor(); }
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "custom armor.yml");

		inEquipmentLootbox = new ArrayList<>();
		config = YamlConfiguration.loadConfiguration(new File(rpd, "custom armor.yml"));
		equipmentLootbox = d(config, "items.equipment lootbox");

		givedpitem.items.put("equipmentlootbox", equipmentLootbox);

		final YamlConfiguration a = otherdata;
		if(!a.getBoolean("saved default custom armor")) {
			final String[] c = new String[] {"KOTH", "PHANTOM", "RANGER", "SUPREME", "TRAVELER", "YETI", "YIJKI"};
			for(String s : c) save("custom armor", s + ".yml");
			a.set("saved default custom armor", true);
			saveOtherData();
		}
		final File folder = new File(rpd + separator + "custom armor");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				new FileArmorSet(f);
			}
		}
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (armorsets != null ? armorsets.size() : 0) + " Armor Sets &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		armorsets = null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorEquipEvent(ArmorEquipEvent event) {
		final Player player = event.getPlayer();
		scheduler.scheduleSyncDelayedTask(randompackage, () -> {
			final ArmorSet W = valueOfArmorSet(player);
			if(W != null) {
				sendStringListMessage(player, W.getActivateMessage(), null);
				final ArmorSetEquipEvent e = new ArmorSetEquipEvent(player, W);
				pluginmanager.callEvent(e);
				trigger(e, W.getAttributes());
			}
		}, 0);
	}
	@EventHandler
	private void armorUnequipEvent(ArmorUnequipEvent event) {
		final Player player = event.getPlayer();
		final ArmorSet W = valueOfArmorSet(player);
		if(W != null) {
			final ArmorSetUnequipEvent e = new ArmorSetUnequipEvent(player, W);
			pluginmanager.callEvent(e);
			trigger(e, W.getAttributes());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorPieceBreakEvent(ArmorPieceBreakEvent event) {
		final ArmorSet W = valueOfArmorSet(event.getPlayer());
		if(W != null) {
			trigger(event, W.getAttributes());
		}
	}

	@EventHandler
	private void pvAnyEvent(PvAnyEvent event) {
		final LivingEntity victim = event.getEntity();
		final Player d = event.getDamager(), v = victim instanceof Player ? (Player) victim : null;
		final ArmorSet a = valueOfArmorSet(d), b = valueOfArmorSet(v);
		if(a != null) trigger(event, a.getAttributes());
		if(b != null) trigger(event, b.getAttributes());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageEvent(EntityDamageEvent event) {
		final Entity e = event.getEntity();
		if(e instanceof Player) {
			final ArmorSet a = valueOfArmorSet((Player) e);
			if(a != null) {
				trigger(event, a.getAttributes());
			}
			if(event instanceof EntityDamageByEntityEvent) {
				final EntityDamageByEntityEvent E = (EntityDamageByEntityEvent) event;
				final Entity d = E.getDamager();
				if(d instanceof Player) {
					final ArmorSet aa = valueOfArmorSet((Player) d);
					if(aa != null) {
						trigger(event, aa.getAttributes());
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
		final HumanEntity e = event.getEntity();
		if(e instanceof Player) {
			final ArmorSet a = valueOfArmorSet((Player) e);
			if(a != null) {
				trigger(event, a.getAttributes());
			}
		}
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null && i.isSimilar(equipmentLootbox)) {
			final Player player = event.getPlayer();
			event.setCancelled(true);
			player.updateInventory();
			removeItem(player, i, 1);
			final ItemStack is = getRandomEquipmentLootboxLoot();
			giveItem(player, is);

			final String p = player.getName(), it = is.getItemMeta().getDisplayName();
			for(String s : config.getStringList("messages.receive loot from Equipment Lootbox")) {
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", p).replace("{ITEM}", it)));
			}
		}
	}

	public ItemStack getRandomEquipmentLootboxLoot() {
		final List<String> r = config.getStringList("items.equipment lootbox.rewards");
		String l = r.get(random.nextInt(r.size()));
		if(l.contains("||")) l = l.split("\\|\\|")[random.nextInt(l.split("\\|\\|").length)];
		return givedpitem.valueOf(l);
	}
}
