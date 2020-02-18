package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.addon.ArmorSet;
import me.randomhashtags.randompackage.addon.file.FileArmorSet;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.event.armor.ArmorPieceBreakEvent;
import me.randomhashtags.randompackage.event.armor.ArmorUnequipEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

public class CustomArmor extends EventAttributes implements RPItemStack {
	private static CustomArmor instance;
	public static CustomArmor getCustomArmor() {
		if(instance == null) instance = new CustomArmor();
		return instance;
	}
	
	public YamlConfiguration config;
	public ItemStack equipmentLootbox, crystal, heroicUpgrade, omniCrystal;
	public List<String> omniAppliedLore, heroicAddedLore;
	public int percentSlot;
	public String crystalAddedLore;
	private List<Player> inEquipmentLootbox;

	public String getIdentifier() {
		return "CUSTOM_ARMOR";
	}
	public void load() {
		final long started = System.currentTimeMillis();
		save("custom armor", "_settings.yml");

		inEquipmentLootbox = new ArrayList<>();
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "custom armor", "_settings.yml"));
		equipmentLootbox = createItemStack(config, "items.equipment lootbox");
		crystal = createItemStack(config, "items.crystal");
		heroicUpgrade = createItemStack(config, "items.heroic upgrade");
		crystalAddedLore = colorize(config.getString("items.crystal.applied lore"));
		omniCrystal = createItemStack(config, "items.omni crystal");
		omniAppliedLore = colorizeListString(config.getStringList("items.omni crystal.applied lore"));

		heroicAddedLore = colorizeListString(config.getStringList("heroic.added lore"));

		GIVEDP_ITEM.items.put("equipmentlootbox", equipmentLootbox);

		if(!otherdata.getBoolean("saved default custom armor")) {
			generateDefaultCustomArmor();
			otherdata.set("saved default custom armor", true);
			saveOtherData();
		}
		final List<ItemStack> crystals = new ArrayList<>();
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "custom armor")) {
			if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
				final ItemStack is = getCrystal(new FileArmorSet(f), 100);
				if(is != null) {
					crystals.add(is);
				}
			}
		}
		addGivedpCategory(crystals, UMaterial.NETHER_STAR, "Armor Set Crystals", "Givedp: ArmorSet Crystals");
		sendConsoleDidLoadFeature(getAll(Feature.ARMOR_SET).size() + " Armor Sets", started);
	}
	public void unload() {
		unregister(Feature.ARMOR_SET);
	}

	public ItemStack getCrystal(@NotNull ArmorSet set, int percent) {
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
	private void tryTriggeringCustomArmor(Player player, Event event) {
		tryArmorCrystal(player, event);
		final ArmorSet set = valueOfArmorSet(player, true);
		if(set != null) {
			trigger(event, set.getArmorAttributes());
		}
	}
	private List<ItemStack> getArmor(Player player) {
		final List<ItemStack> list = new ArrayList<>();
		for(ItemStack is : player.getInventory().getArmorContents()) {
			if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
				list.add(is);
			}
		}
		return list;
	}
	private void tryArmorCrystal(Player player, Event event) {
		for(ItemStack is : getArmor(player)) {
			final ArmorSet crystal = getArmorCrystalOnItem(is);
			if(crystal != null) {
				trigger(event, crystal.getCrystalAttributes());
			}
		}
	}
	public byte tryApplyingArmorCrystal(Player player, ArmorSet type, int percent, ItemStack is) {
		final String mat = is != null ? is.getType().name() : null;
		final ArmorSet set = valueOfArmorSet(is), crystal = getArmorCrystalOnItem(is);
		if(mat != null && (mat.endsWith("HELMET") || mat.endsWith("CHESTPLATE") || mat.endsWith("LEGGINGS") || mat.endsWith("BOOTS"))) {
			if(set != null) {
				sendStringListMessage(player, getStringList(config, "messages.cannot apply.armor set piece"), null);
				return -1;
			} else if(crystal != null) {
				sendStringListMessage(player, getStringList(config, "messages.cannot apply.already has crystal"), null);
				return -2;
			}
			if(RANDOM.nextInt(100) < percent) {
				itemMeta = is.getItemMeta(); lore.clear();
				if(itemMeta.hasLore()) {
					lore.addAll(itemMeta.getLore());
				}
				lore.add(crystalAddedLore.replace("{NAME}", type.getName()));
				itemMeta.setLore(lore); lore.clear();
				is.setItemMeta(itemMeta);
				sendStringListMessage(player, type.getCrystalAppliedMsg(), null);
				return 2;
			}
			return 1;
		}
		return 0;
	}
	public byte tryApplyingUpgrade(Player player, ArmorSet type, int percent, ItemStack is) {
		return 0;
	}

	public ItemStack getRandomEquipmentLootboxLoot() {
		final List<String> rewards = getStringList(config, "items.equipment lootbox.rewards");
		String l = rewards.get(RANDOM.nextInt(rewards.size()));
		if(l.contains("||")) {
			l = l.split("\\|\\|")[RANDOM.nextInt(l.split("\\|\\|").length)];
		}
		return GIVEDP_ITEM.valueOf(l);
	}

	public ItemStack getHeroicUpgrade(@NotNull ArmorSet set) {
		return getHeroicUpgrade(set, RANDOM.nextInt(101));
	}
	public ItemStack getHeroicUpgrade(@NotNull ArmorSet set, int percent) {
		final String name = set.getName(), percentString = Integer.toString(percent);
		item = heroicUpgrade.clone();
		itemMeta = item.getItemMeta(); lore.clear();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", name));
		for(String s : itemMeta.getLore()) {
			lore.add(s.replace("{NAME}", name).replace("{PERCENT}", percentString));
		}
		itemMeta.setLore(lore); lore.clear();
		item.setItemMeta(itemMeta);
		return item;
	}
	public boolean isHeroic(@Nullable ItemStack is) {
		return getRPItemStackValue(is, "isHeroic") != null;
	}
	public void setHeroic(@NotNull Player player, @NotNull ItemStack is, int durability, int maxDurability) {
		if(!isHeroic(is)) {
			final ArmorSet set = valueOfArmorSet(is);
			if(set != null) {
				addRPItemStackValues(is, new HashMap<String, String>() {{
					put("isHeroic", "true");
					put("CustomDurability", Integer.toString(durability));
					put("CustomMaxDurability", Integer.toString(maxDurability));
				}});
				updateCustomDurability(player, is);
			}
		}
	}

	public boolean hasCustomDurability(@Nullable ItemStack is) {
		return getRPItemStackValue(is, "CustomDurability") != null;
	}
	public ItemStack setCustomDurability(@NotNull ItemStack is, int maxDurability, int armorValueORbonusAttackDmg) {
		if(!hasCustomDurability(is)) {
			final String max = Integer.toString(maxDurability), value = Integer.toString(armorValueORbonusAttackDmg);
			final boolean isArmor = isArmorPiece(is.getType());
			itemMeta = is.getItemMeta(); lore.clear();
			if(itemMeta.hasLore()) {
				lore.addAll(itemMeta.getLore());
			}
			for(String s : heroicAddedLore) {
				final boolean containsArmorValue = s.contains("{ARMOR_VALUE}"), containsBonusAttackDmg = s.contains("{BONUS_ATTACK_DMG}");
				if(containsArmorValue && isArmor || containsBonusAttackDmg && !isArmor || !containsArmorValue && !containsBonusAttackDmg) {
					lore.add(s.replace("{ARMOR_VALUE}", value).replace("{BONUS_ATTACK_DMG}", value).replace("{DURABILITY}", max));
				}
			}
			itemMeta.setLore(lore); lore.clear();
			is.setItemMeta(itemMeta);
			addRPItemStackValues(is, new HashMap<String, String>() {{
				put("CustomDurability", Integer.toString(getCustomDurability(is, maxDurability)));
				put("CustomMaxDurability", max);
			}});
		}
		return is;
	}
	public int getCustomDurability(@NotNull ItemStack is) {
		return hasCustomDurability(is) ? Integer.parseInt(getRPItemStackValue(is, "CustomDurability")) : -1;
	}
	public int getCustomMaxDurability(@NotNull ItemStack is) {
		return hasCustomDurability(is) ? Integer.parseInt(getRPItemStackValue(is, "CustomMaxDurability")) : -1;
	}
	public int getCustomDurability(@NotNull ItemStack is, float max) {
		if(hasCustomDurability(is)) {
			final float current = is.getDurability();
			final float percent = 100-((current/max)*100), maxDurability = is.getType().getMaxDurability();
			return (int) (maxDurability*percent);
		}
		return -1;
	}
	public void updateCustomDurability(@NotNull Player player, @NotNull ItemStack is) {
		if(hasCustomDurability(is)) {
			final float current = getCustomDurability(is), max = getCustomMaxDurability(is);
			final float percent = 100-((current/max)*100), maxDurability = is.getType().getMaxDurability();
			final float durability = maxDurability*percent;
			final short itemDurability = (short) (durability > 0.00 && ((short) durability) == 0 ? maxDurability-1 : durability);
			is.setDurability(itemDurability);
			player.updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorEquipEvent(ArmorEquipEvent event) {
		final ItemStack i = event.getItem();
		final Player player = event.getPlayer();
		final ArmorSet crystal = getArmorCrystalOnItem(i);
		if(crystal != null) {
			trigger(event, crystal.getCrystalAttributes());
		}
		SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
			final ArmorSet set = valueOfArmorSet(player, true);
			if(set != null) {
				sendStringListMessage(player, set.getActivateMessage(), null);
				final ArmorSetEquipEvent e = new ArmorSetEquipEvent(player, set);
				PLUGIN_MANAGER.callEvent(e);
				trigger(e, set.getArmorAttributes());
			}
		}, 0);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorUnequipEvent(ArmorUnequipEvent event) {
		final Player player = event.getPlayer();
		tryArmorCrystal(player, event);
		final ArmorSet set = valueOfArmorSet(player, true);
		if(set != null) {
			final ArmorSetUnequipEvent e = new ArmorSetUnequipEvent(player, set);
			PLUGIN_MANAGER.callEvent(e);
			trigger(e, set.getArmorAttributes());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void armorPieceBreakEvent(ArmorPieceBreakEvent event) {
		final Player player = event.getPlayer();
		final ArmorSet crystal = getArmorCrystalOnItem(event.getItem());
		if(crystal != null) {
			trigger(event, crystal.getCrystalAttributes());
		}
		final ArmorSet set = valueOfArmorSet(player, true);
		if(set != null) {
			trigger(event, set.getArmorAttributes());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void pvAnyEvent(PvAnyEvent event) {
		tryTriggeringCustomArmor(event.getDamager(), event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void isDamagedEvent(isDamagedEvent event) {
		tryTriggeringCustomArmor(event.getEntity(), event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageEvent(EntityDamageEvent event) {
		final Entity entity = event.getEntity();
		if(entity instanceof Player) {
			tryTriggeringCustomArmor((Player) entity, event);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
		final HumanEntity entity = event.getEntity();
		if(entity instanceof Player) {
			tryTriggeringCustomArmor((Player) entity, event);
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		if(is != null) {
			final Player player = event.getPlayer();
			if(is.isSimilar(equipmentLootbox)) {
				final ItemStack reward = getRandomEquipmentLootboxLoot();
				final EquipmentLootboxOpenEvent e = new EquipmentLootboxOpenEvent(player, reward);
				PLUGIN_MANAGER.callEvent(e);
				if(!e.isCancelled()) {
					removeItem(player, is, 1);
					giveItem(player, reward);

					final String p = player.getName(), it = reward.getItemMeta().getDisplayName();
					for(String s : getStringList(config, "messages.receive loot from Equipment Lootbox")) {
						Bukkit.broadcastMessage(colorize(s.replace("{PLAYER}", p).replace("{ITEM}", it)));
					}
				}
			} else if(valueOfArmorCrystal(is) != null) {
			} else {
				return;
			}
			event.setCancelled(true);
			player.updateInventory();
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
		final ArmorSet crystal = valueOfArmorCrystal(cursor);
		if(crystal != null && current != null && cursor != null) {
			final Player player = (Player) event.getWhoClicked();
			if(tryApplyingArmorCrystal(player, crystal, getRemainingInt(cursor.getItemMeta().getLore().get(percentSlot)), current) >= 1) {
				event.setCancelled(true);
				final int amount = cursor.getAmount();
				if(amount == 1) {
					item = new ItemStack(Material.AIR);
				} else {
					cursor.setAmount(amount-1);
					item = cursor;
				}
				event.setCursor(item);
				player.updateInventory();
			}
		}
	}
}
