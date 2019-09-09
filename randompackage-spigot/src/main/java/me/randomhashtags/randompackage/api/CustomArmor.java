package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.ArmorSet;
import me.randomhashtags.randompackage.event.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.event.armor.ArmorEvent;
import me.randomhashtags.randompackage.event.armor.ArmorPieceBreakEvent;
import me.randomhashtags.randompackage.event.armor.ArmorUnequipEvent;
import me.randomhashtags.randompackage.event.customenchant.isDamagedEvent;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.event.ArmorSetEquipEvent;
import me.randomhashtags.randompackage.event.ArmorSetUnequipEvent;
import me.randomhashtags.randompackage.event.customenchant.PvAnyEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.FileArmorSet;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class CustomArmor extends EventAttributes {
	private static CustomArmor instance;
	public static CustomArmor getCustomArmor() {
		if(instance == null) instance = new CustomArmor();
		return instance;
	}
	
	public YamlConfiguration config;
	public ItemStack equipmentLootbox, crystal;
	public int percentSlot;
	public String crystalAddedLore;
	private List<Player> inEquipmentLootbox;

	public String getIdentifier() { return "CUSTOM_ARMOR"; }
	protected RPFeature getFeature() { return getCustomArmor(); }
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "custom armor.yml");

		inEquipmentLootbox = new ArrayList<>();
		config = YamlConfiguration.loadConfiguration(new File(rpd, "custom armor.yml"));
		equipmentLootbox = d(config, "items.equipment lootbox");
		crystal = d(config, "items.crystal");
		crystalAddedLore = ChatColor.translateAlternateColorCodes('&', config.getString("items.crystal.applied lore"));

		givedpitem.items.put("equipmentlootbox", equipmentLootbox);

		final YamlConfiguration a = otherdata;
		if(!a.getBoolean("saved default custom armor")) {
			final String[] c = new String[] {"DRAGON", "ENGINEER", "KOTH", "PHANTOM", "RANGER", "SUPREME", "TRAVELER", "YETI", "YIJKI"};
			for(String s : c) save("custom armor", s + ".yml");
			a.set("saved default custom armor", true);
			saveOtherData();
		}
		final File folder = new File(rpd + separator + "custom armor");
		final List<ItemStack> crystals = new ArrayList<>();
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final ItemStack is = getCrystal(new FileArmorSet(f), 100);
				if(is != null) {
					crystals.add(is);
				}
			}
		}
		addGivedpCategory(crystals, UMaterial.NETHER_STAR, "Armor Set Crystals", "Givedp: ArmorSet Crystals");
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (armorsets != null ? armorsets.size() : 0) + " Armor Sets &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		armorsets = null;
	}
	public ItemStack getCrystal(ArmorSet set, int percent) {
		final String p = Integer.toString(percent), n = set.getName();
		item = null;
		if(n != null) {
			final List<String> perks = set.getCrystalPerks();
			item = crystal.clone();
			itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", n));
			lore.clear();
			for(String s : itemMeta.getLore()) {
				if(s.equals("{PERKS}")) {
					lore.addAll(perks);
				} else {
					lore.add(s.replace("{PERCENT}", p).replace("{NAME}", n));
				}
			}
			itemMeta.setLore(lore); lore.clear();
			item.setItemMeta(itemMeta);
		}
		return item;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorEquipEvent(ArmorEquipEvent event) {
		final ItemStack i = event.getItem();
		final Player player = event.getPlayer();
		final ArmorSet crystal = getArmorCrystalOnItem(i);
		if(crystal != null) {
			trigger(event, crystal.getCrystalAttributes());
		}
		scheduler.scheduleSyncDelayedTask(randompackage, () -> {
			final ArmorSet W = valueOfArmorSet(player);
			if(W != null) {
				sendStringListMessage(player, W.getActivateMessage(), null);
				final ArmorSetEquipEvent e = new ArmorSetEquipEvent(player, W);
				pluginmanager.callEvent(e);
				trigger(e, W.getArmorAttributes());
			}
		}, 0);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorUnequipEvent(ArmorUnequipEvent event) {
		final Player player = event.getPlayer();
		tryCrystal(player, event);
		final ArmorSet W = valueOfArmorSet(player);
		if(W != null) {
			final ArmorSetUnequipEvent e = new ArmorSetUnequipEvent(player, W);
			pluginmanager.callEvent(e);
			trigger(e, W.getArmorAttributes());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorPieceBreakEvent(ArmorPieceBreakEvent event) {
		final Player player = event.getPlayer();
		final ArmorSet crystal = getArmorCrystalOnItem(event.getItem());
		if(crystal != null) {
			trigger(event, crystal.getCrystalAttributes());
		}
		final ArmorSet W = valueOfArmorSet(player);
		if(W != null) {
			trigger(event, W.getArmorAttributes());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void pvAnyEvent(PvAnyEvent event) {
		final Player d = event.getDamager();
		tryCrystal(d, event);
		final ArmorSet a = valueOfArmorSet(d);
		if(a != null) {
			trigger(event, a.getArmorAttributes());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void isDamagedEvent(isDamagedEvent event) {
		final Player player = event.getEntity();
		tryCrystal(player, event);
		final ArmorSet a = valueOfArmorSet(player);
		if(a != null) {
			trigger(event, a.getArmorAttributes());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageEvent(EntityDamageEvent event) {
		final Entity e = event.getEntity();
		if(e instanceof Player) {
			final Player player = (Player) e;
			tryCrystal(player, event);
			final ArmorSet a = valueOfArmorSet(player);
			if(a != null) {
				trigger(event, a.getArmorAttributes());
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
		final HumanEntity e = event.getEntity();
		if(e instanceof Player) {
			final Player player = (Player) e;
			tryCrystal(player, event);
			final ArmorSet a = valueOfArmorSet(player);
			if(a != null) {
				trigger(event, a.getArmorAttributes());
			}
		}
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null) {
			final Player player = event.getPlayer();
			if(i.isSimilar(equipmentLootbox)) {
				removeItem(player, i, 1);
				final ItemStack is = getRandomEquipmentLootboxLoot();
				giveItem(player, is);

				final String p = player.getName(), it = is.getItemMeta().getDisplayName();
				for(String s : config.getStringList("messages.receive loot from Equipment Lootbox")) {
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", p).replace("{ITEM}", it)));
				}
			} else if(valueOfArmorCrystal(i) != null) {
			} else return;
			event.setCancelled(true);
			player.updateInventory();
		}

	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final ItemStack cur = event.getCurrentItem(), curs = event.getCursor();
		final ArmorSet crystal = valueOfArmorCrystal(curs);
		if(crystal != null && cur != null && curs != null && tryApplyingCrystal(crystal, getRemainingInt(curs.getItemMeta().getLore().get(percentSlot)), cur) >= 1) {
			final Player player = (Player) event.getWhoClicked();
			event.setCancelled(true);
			player.updateInventory();
			final int a = curs.getAmount();
			if(a == 1) item = new ItemStack(Material.AIR);
			else {
				curs.setAmount(a-1);
				item = curs;
			}
			event.setCursor(item);
			player.updateInventory();
		}
	}

	public byte tryApplyingCrystal(ArmorSet type, int percent, ItemStack is) {
		final String mat = is != null ? is.getType().name() : null;
		if(mat != null && valueOfArmorSet(is) == null && getArmorCrystalOnItem(is) == null && (mat.endsWith("HELMET") || mat.endsWith("CHESTPLATE") || mat.endsWith("LEGGINGS") || mat.endsWith("BOOTS"))) {
			if(random.nextInt(100) < percent) {
				itemMeta = is.getItemMeta(); lore.clear();
				if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
				lore.add(crystalAddedLore.replace("{NAME}", type.getName()));
				itemMeta.setLore(lore); lore.clear();
				is.setItemMeta(itemMeta);
				return 2;
			}
			return 1;
		}
		return 0;
	}

	public ItemStack getRandomEquipmentLootboxLoot() {
		final List<String> r = config.getStringList("items.equipment lootbox.rewards");
		String l = r.get(random.nextInt(r.size()));
		if(l.contains("||")) l = l.split("\\|\\|")[random.nextInt(l.split("\\|\\|").length)];
		return givedpitem.valueOf(l);
	}

	private List<ItemStack> getItems(Player player) {
		final List<ItemStack> i = new ArrayList<>();
		for(ItemStack is : player.getInventory().getArmorContents()) {
			if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
				i.add(is);
			}
		}
		return i;
	}


	private void tryCrystal(Player player, ArmorEvent event) {
		for(ItemStack is : getItems(player)) {
			final ArmorSet crystal = getArmorCrystalOnItem(is);
			if(crystal != null) {
				trigger(event, crystal.getCrystalAttributes());
			}
		}
	}
	private void tryCrystal(Player player, EntityDamageEvent event) {
		for(ItemStack is : getItems(player)) {
			final ArmorSet crystal = getArmorCrystalOnItem(is);
			if(crystal != null) {
				trigger(event, crystal.getCrystalAttributes());
			}
		}
	}
	private void tryCrystal(Player player, FoodLevelChangeEvent event) {
		for(ItemStack is : getItems(player)) {
			final ArmorSet crystal = getArmorCrystalOnItem(is);
			if(crystal != null) {
				trigger(event, crystal.getCrystalAttributes());
			}
		}
	}
	private void tryCrystal(Player player, isDamagedEvent event) {
		for(ItemStack is : getItems(player)) {
			final ArmorSet crystal = getArmorCrystalOnItem(is);
			if(crystal != null) {
				trigger(event, crystal.getCrystalAttributes());
			}
		}
	}
	private void tryCrystal(Player player, PvAnyEvent event) {
		for(ItemStack is : getItems(player)) {
			final ArmorSet crystal = getArmorCrystalOnItem(is);
			if(crystal != null) {
				trigger(event, crystal.getCrystalAttributes());
			}
		}
	}
}
