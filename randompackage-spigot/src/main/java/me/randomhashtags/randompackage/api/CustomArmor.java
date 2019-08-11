package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.ArmorSet;
import me.randomhashtags.randompackage.addons.usingfile.FileArmorSet;
import me.randomhashtags.randompackage.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.events.ArmorSetEquipEvent;
import me.randomhashtags.randompackage.events.ArmorSetUnequipEvent;
import me.randomhashtags.randompackage.events.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.events.customenchant.CEAApplyPotionEffectEvent;
import me.randomhashtags.randompackage.events.customenchant.CustomEnchantEntityDamageByEntityEvent;
import me.randomhashtags.randompackage.events.customenchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.events.MobStackDepleteEvent;
import me.randomhashtags.randompackage.utils.objects.Feature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.utils.GivedpItem.givedpitem;

public class CustomArmor extends CustomEnchants implements Listener {
	public boolean isEnabled = false;
	private static CustomArmor instance;
	public static CustomArmor getCustomArmor() {
		if(instance == null) instance = new CustomArmor();
		return instance;
	}
	
	public YamlConfiguration config;
	public ItemStack equipmentLootbox;
	private List<Player> inEquipmentLootbox;

	public String getIdentifier() { return "CUSTOM_ARMOR"; }
	public void load() {
		isEnabled = true;
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
		isEnabled = false;
		config = null;
		equipmentLootbox = null;
		inEquipmentLootbox = null;
		deleteAll(Feature.CUSTOM_ARMOR);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerArmorEvent(PlayerArmorEvent event) {
		if(!event.isCancelled()) {
			final Player player = event.player;
			final String n = event.reason.name();
			if(n.contains("_EQUIP")) {
				scheduler.scheduleSyncDelayedTask(randompackage, () -> {
					final ArmorSet W = valueOf(player);
					if(W != null) {
						final ArmorSetEquipEvent e = new ArmorSetEquipEvent(player, W);
						pluginmanager.callEvent(e);
						sendStringListMessage(player, W.getActivateMessage(), null);
						procCustomArmor(e, W);
					}
				}, 0);
			} else if(n.contains("_UNEQUIP")) {
				final ArmorSet W = valueOf(player);
				if(W != null) {
					final ArmorSetUnequipEvent e = new ArmorSetUnequipEvent(player, W);
					pluginmanager.callEvent(e);
					procCustomArmor(e, W);
				}
			} else if(n.equals("BREAK")) {
				final ArmorSet W = valueOf(player);
				if(W != null) procCustomArmor(event, W);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		final Entity e = event.getEntity(), d = event.getDamager();
		if(e instanceof Player) procCustomArmor(event, valueOf((Player) e));
		if(d instanceof Player) procCustomArmor(event, valueOf((Player) d));
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageEvent(EntityDamageEvent event) {
		final Entity e = event.getEntity();
		if(e instanceof Player) procCustomArmor(event, valueOf((Player) e));
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
		final HumanEntity e = event.getEntity();
		if(e instanceof Player) procCustomArmor(event, valueOf((Player) e));
	}

	public void procCustomArmor(Event event, ArmorSet set) {
		if(set == null) return;
		for(String attr : set.getAttributes()) {
            final String A = attr.split(";")[0].toLowerCase();
            final boolean isPlayer = event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player || event instanceof EntityDamageEvent && ((EntityDamageEvent) event).getEntity() instanceof Player;
            if(event instanceof PlayerArmorEvent && A.equals("armorequip") && ((PlayerArmorEvent) event).reason.name().contains("_EQUIP")
                || event instanceof PlayerArmorEvent && A.equals("armorunequip") && ((PlayerArmorEvent) event).reason.name().contains("_UNEQUIP")
                || event instanceof PlayerArmorEvent && A.equals("armorpiecebreak") && ((PlayerArmorEvent) event).reason.equals(PlayerArmorEvent.ArmorEventReason.BREAK)

				|| event instanceof ArmorSetEquipEvent && A.equals("armorsetequip")
				|| event instanceof ArmorSetUnequipEvent && A.equals("armorsetunequip")

                || event instanceof EntityDamageByEntityEvent && A.equals("damagedealt") && isPlayer
                || event instanceof EntityDamageByEntityEvent && A.equals("pva") && isPlayer
                || event instanceof EntityDamageByEntityEvent && A.equals("pvp") && isPlayer && ((EntityDamageByEntityEvent) event).getEntity() instanceof Player
                || event instanceof EntityDamageByEntityEvent && A.equals("pve") && isPlayer && ((EntityDamageByEntityEvent) event).getEntity() instanceof LivingEntity && !(((EntityDamageByEntityEvent) event).getEntity() instanceof Player)
                || event instanceof EntityDamageByEntityEvent && A.equals("isdamaged") && ((EntityDamageByEntityEvent) event).getEntity() instanceof Player
                || event instanceof EntityDamageByEntityEvent && A.equals("hitbyarrow") && ((EntityDamageByEntityEvent) event).getEntity() instanceof Player && ((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow
                || event instanceof EntityDamageByEntityEvent && A.equals("arrowhit") && ((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow && ((Arrow) ((EntityDamageByEntityEvent) event).getDamager()).getShooter() instanceof Player && shotbows.keySet().contains(((EntityDamageByEntityEvent) event).getDamager().getUniqueId())
                || event instanceof EntityDamageEvent && A.startsWith("damagedby(") && isPlayer && A.toUpperCase().contains(((EntityDamageEvent) event).getCause().name())
                || event instanceof EntityDamageByEntityEvent && A.startsWith("damagedby(") && ((EntityDamageByEntityEvent) event).getEntity() instanceof Player && A.toUpperCase().contains(((EntityDamageByEntityEvent) event).getCause().name())

                || event instanceof CustomEnchantEntityDamageByEntityEvent && A.startsWith("ceentityisdamaged")
                || event instanceof CustomBossDamageByEntityEvent && A.startsWith("custombossisdamaged")

                || event instanceof BlockPlaceEvent && A.equals("blockplace")
                || event instanceof BlockBreakEvent && A.equals("blockbreak")

                || event instanceof FoodLevelChangeEvent && A.equals("foodlevelgained") && ((FoodLevelChangeEvent) event).getEntity() instanceof Player && ((FoodLevelChangeEvent) event).getFoodLevel() > ((Player) ((FoodLevelChangeEvent) event).getEntity()).getFoodLevel()
                || event instanceof FoodLevelChangeEvent && A.equals("foodlevellost") && ((FoodLevelChangeEvent) event).getEntity() instanceof Player && ((FoodLevelChangeEvent) event).getFoodLevel() < ((Player) ((FoodLevelChangeEvent) event).getEntity()).getFoodLevel()

                || event instanceof PlayerItemDamageEvent && A.equals("isdurabilitydamaged")

                || event instanceof PlayerInteractEvent && A.equals("playerinteract")
                || event instanceof ProjectileHitEvent && A.equals("arrowland") && ((ProjectileHitEvent) event).getEntity() instanceof Arrow && getHitEntity((ProjectileHitEvent) event) != null
                || event instanceof EntityShootBowEvent && A.equals("shootbow")

                || event instanceof PlayerDeathEvent && A.equals("playerdeath")
                || event instanceof PlayerDeathEvent && A.equals("killedplayer")
                || event instanceof EntityDeathEvent && A.equals("killedentity") && !(((EntityDeathEvent) event).getEntity() instanceof Player)

                || event instanceof CustomEnchantProcEvent && A.equals("enchantproc")
                || event instanceof CEAApplyPotionEffectEvent && A.equals("ceapplypotioneffect")

                || event instanceof MobStackDepleteEvent && A.equals("mobstackdeplete")

                || event instanceof PluginEnableEvent && A.startsWith("timer(")

                || mcmmoIsEnabled() && event instanceof com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent && A.equals("mcmmoxpgained")
                || mcmmoIsEnabled() && event instanceof com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent && A.equals("mcmmoxpgained:" + ((com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event).getSkill().name().toLowerCase())
            ) {
                executeAttributes(event, attr);
            }
		}
	}
	
	public void executeAttributes(Event event, String attribute) {
		for(String a : attribute.substring(attribute.split(";")[0].length()).split(";")) {
			if(event != null && a.toLowerCase().startsWith("cancel")) {
				((Cancellable) event).setCancelled(true);
				if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow) {
					((EntityDamageByEntityEvent) event).getDamager().remove();
				}
				return;
			} else {
				w(null, event, null, getRecipients(event, a.contains("[") ? a.split("\\[")[1].split("]")[0] : a, null), a, attribute, -1, null);
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


	public ArmorSet valueOf(Player player) {
		if(armorsets != null && player != null) {
			final PlayerInventory pi = player.getInventory();
			final ItemStack h = pi.getHelmet(), c = pi.getChestplate(), l = pi.getLeggings(), b = pi.getBoots();
			for(ArmorSet set : armorsets.values()) {
				final List<String> a = set.getArmorLore();
				if(a != null &&
						(h != null && h.hasItemMeta() && h.getItemMeta().hasLore() && h.getItemMeta().getLore().containsAll(a)
								&& c != null && c.hasItemMeta() && c.getItemMeta().hasLore() && c.getItemMeta().getLore().containsAll(a)
								&& l != null && l.hasItemMeta() && l.getItemMeta().hasLore() && l.getItemMeta().getLore().containsAll(a)
								&& b != null && b.hasItemMeta() && b.getItemMeta().hasLore() && b.getItemMeta().getLore().containsAll(a))) {
					return set;
				}
			}
		}
		return null;
	}
}
