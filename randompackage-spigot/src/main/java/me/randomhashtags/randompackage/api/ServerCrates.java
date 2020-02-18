package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.ServerCrate;
import me.randomhashtags.randompackage.addon.file.FileServerCrate;
import me.randomhashtags.randompackage.addon.living.LivingServerCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.ServerCrateCloseEvent;
import me.randomhashtags.randompackage.event.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerCrates extends RPFeature {
	private static ServerCrates instance;
	public static ServerCrates getServerCrates() {
	    if(instance == null) instance = new ServerCrates();
	    return instance;
	}

	private List<UUID> canRevealRarities;
	private HashMap<UUID, ServerCrate> revealingLoot;
	private HashMap<UUID, HashMap<Integer, ServerCrate>> selectedSlots;
	private HashMap<UUID, List<ItemStack>> revealingloot;
	private HashMap<UUID, List<Integer>> tasks, revealedslots;

	public String getIdentifier() {
		return "SERVER_CRATES";
	}
	public void load() {
	    final long started = System.currentTimeMillis();
		canRevealRarities = new ArrayList<>();
		revealingLoot = new HashMap<>();
		selectedSlots = new HashMap<>();
		revealingloot = new HashMap<>();
		tasks = new HashMap<>();
		revealedslots = new HashMap<>();

		if(!otherdata.getBoolean("saved default server crates")) {
			generateDefaultServerCrates();
			otherdata.set("saved default server crates", true);
			saveOtherData();
		}
		final List<ItemStack> flares = new ArrayList<>(), crates = new ArrayList<>();
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "server crates")) {
			final FileServerCrate crate = new FileServerCrate(f);
			crates.add(crate.getItem());
			flares.add(crate.getFlare().getItem());
		}
		addGivedpCategory(crates, UMaterial.CHEST, "Server Crates", "Givedp: Server Crates");
		addGivedpCategory(flares, UMaterial.TORCH, "Server Crate Flares", "Givedp: Server Crate Flares");
		sendConsoleDidLoadFeature(getAll(Feature.SERVER_CRATE).size() + " Server Crates and flares", started);
	}
	public void unload() {
		for(UUID uuid : new ArrayList<>(revealingLoot.keySet())) {
			final OfflinePlayer o = Bukkit.getOfflinePlayer(uuid);
			if(o.isOnline()) {
				o.getPlayer().closeInventory();
			}
		}
		for(UUID u : tasks.keySet()) {
			stopTasks(u);
		}
		unregister(Feature.SERVER_CRATE);
		LivingServerCrate.deleteAll(true);
	}

	public void openCrate(@NotNull Player player, @NotNull ServerCrate crate) {
		final ServerCrateOpenEvent e = new ServerCrateOpenEvent(player, crate);
		PLUGIN_MANAGER.callEvent(e);
		if(!e.isCancelled()) {
			final UInventory i = crate.getInventory();
			player.openInventory(Bukkit.createInventory(player, i.getSize(), i.getTitle()));
			player.getOpenInventory().getTopInventory().setContents(i.getInventory().getContents());
			player.updateInventory();
			final UUID u = player.getUniqueId();
			revealingLoot.put(u, crate);
			selectedSlots.put(u, new HashMap<>());
		}
	}
	private void stopTasks(UUID uuid) {
		if(tasks.containsKey(uuid)) {
			for(int i : tasks.get(uuid)) {
				SCHEDULER.cancelTask(i);
			}
			tasks.remove(uuid);
		}
	}
	private void revealBackgroundLoot(Player player) {
		final UUID uuid = player.getUniqueId();
		final ServerCrate crate = revealingLoot.get(uuid);
		tasks.put(uuid, new ArrayList<>());
		final List<Integer> tasks = this.tasks.get(uuid);
		final List<Integer> background = new ArrayList<>();
		final Inventory top = player.getOpenInventory().getTopInventory();
		final HashMap<Integer, ServerCrate> selectedSlots = this.selectedSlots.get(uuid);
		final ItemStack background1 = crate.getBackground(), background2 = crate.getBackground2(), revealSlotRarity = crate.getRevealSlotRarity();
		for(int i = 0; i < top.getSize(); i++) {
			if(!selectedSlots.containsKey(i)) {
				if(!background1.equals(top.getItem(i))) {
					background.add(i);
				}
			}
		}
		final int backgroundSize = background.size();
		final List<Integer> bg = new ArrayList<>(background);
		for(int i = 1; i <= backgroundSize; i++) {
			final int I = i;
			int k = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
				final int randomSlot = (int) background.toArray()[RANDOM.nextInt(background.size())];
				final ItemStack reward = crate.getRandomReward(crate.getRandomRarity(true).getIdentifier());
				top.setItem(randomSlot, reward);
				player.updateInventory();
				background.remove((Object) randomSlot);
				if(I == backgroundSize) {
					for(int n = 1; n <= backgroundSize; n++) {
						final int K = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
							final int targetSlot = (int) bg.toArray()[RANDOM.nextInt(bg.size())];
							top.setItem(targetSlot, background2);
							bg.remove((Object) targetSlot);
							if(bg.isEmpty()) {
								for(int selectedSlot : selectedSlots.keySet()) {
									item = revealSlotRarity; itemMeta = item.getItemMeta();
									if(item.hasItemMeta()) {
										if(itemMeta.hasDisplayName()) {
											final String name = itemMeta.getDisplayName();
											if(name.contains("{SLOT}")) {
												itemMeta.setDisplayName(name.replace("{SLOT}", Integer.toString(getRemainingInt(top.getItem(selectedSlot).getItemMeta().getDisplayName()))));
											}
										}
									}
									item.setItemMeta(itemMeta);
									top.setItem(selectedSlot, item);
								}
								canRevealRarities.add(uuid);
							}
							player.updateInventory();
						}, n*5);
						tasks.add(K);
					}
				}
			}, i*5);
			tasks.add(k);
		}
	}
	
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		final UUID uuid = player.getUniqueId();
		if(revealingLoot.containsKey(uuid)) {
			canRevealRarities.remove(uuid);
			stopTasks(uuid);
			final ServerCrate crate = revealingLoot.get(uuid);
			revealingLoot.remove(uuid);
			if(!revealingloot.containsKey(uuid)) revealingloot.put(uuid, new ArrayList<>());
			final HashMap<String, List<String>> cr = crate.getRewards();
			final List<ItemStack> l = revealingloot.get(uuid);
			final int ri = crate.getRedeemableItems(), s = revealingloot.get(uuid).size();
			for(int i = 1; i <= ri-s; i++) {
				l.add(crate.getRandomReward((String) cr.keySet().toArray()[RANDOM.nextInt(cr.size())]));
			}
			selectedSlots.remove(uuid);
			if(player.isOnline()) {
				final ServerCrateCloseEvent e = new ServerCrateCloseEvent(player, crate, l);
				PLUGIN_MANAGER.callEvent(e);
				for(ItemStack is : l) {
					giveItem(player, is);
				}
			}
			revealingloot.remove(uuid);
			revealedslots.remove(uuid);
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Block b = event.getClickedBlock();
		final Location loc = b != null ? b.getLocation() : null;
		final HashMap<Location, LivingServerCrate> L = LivingServerCrate.living;
		final LivingServerCrate l = loc != null && L != null ? L.getOrDefault(loc, null) : null;
		final ItemStack is = event.getItem();
		if(l != null) {
			l.delete(true);
		} else if(is != null && is.hasItemMeta() && event.getAction().name().contains("RIGHT")) {
			final ServerCrate c = valueOfServerCrate(is);
			if(c != null) {
				event.setCancelled(true);
				removeItem(player, is, 1);
				openCrate(player, c);
			} else if(b != null) {
				final ServerCrate f = valueOfServerCrateFlare(is);
				if(f != null) {
					event.setCancelled(true);
					player.updateInventory();
					new LivingServerCrate(f, f.getFlare().spawn(player, loc));
					removeItem(player, is, 1);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem() != null && player.getOpenInventory().getTopInventory().getHolder() == player) {
			final ItemStack current = event.getCurrentItem();
			final UUID uuid = player.getUniqueId();
			if(revealingLoot.containsKey(uuid))  {
				final int slot = event.getRawSlot();
				final ServerCrate crate = revealingLoot.get(uuid);
				event.setCancelled(true);
				player.updateInventory();
				final Inventory top = player.getOpenInventory().getTopInventory();
				if(!event.getClick().isLeftClick() && !event.getClick().isRightClick() || current.getType().equals(Material.AIR) || slot >= top.getSize()) {
					return;
				}
				if(crate != null && crate.getSelectableSlots().contains(slot)) {
					final HashMap<Integer, ServerCrate> selectedSlots = this.selectedSlots.get(uuid);
					if(selectedSlots.size() != crate.getRedeemableItems()) {
						if(!selectedSlots.containsKey(slot)) {
							selectedSlots.put(slot, null);
							item = crate.getSelected().clone();
						} else {
							item = crate.getOpenGui().clone();
							selectedSlots.remove(slot);
						}
						if(item.hasItemMeta()) {
							itemMeta = item.getItemMeta();
							if(itemMeta.hasDisplayName()) {
								final String name = itemMeta.getDisplayName();
								if(name.contains("{SLOT}")) {
									itemMeta.setDisplayName(name.replace("{SLOT}", Integer.toString(getRemainingInt(current.getItemMeta().getDisplayName()))));
								}
							}
							item.setItemMeta(itemMeta);
						}
						top.setItem(slot, item);
						player.updateInventory();
						if(!tasks.containsKey(uuid) && selectedSlots.size() == crate.getRedeemableItems()) {
							revealBackgroundLoot(player);
						}
					} else if(canRevealRarities.contains(uuid) && selectedSlots.containsKey(slot) && selectedSlots.get(slot) == null) {
						final ServerCrate sc = crate.getRandomRarity(true);
						selectedSlots.put(slot, sc);
						item = sc.getDisplay().clone(); itemMeta = item.getItemMeta(); lore.clear();
						if(item.hasItemMeta()) {
							if(itemMeta.hasLore()) {
								for(String s : itemMeta.getLore()) {
									if(s.contains("{SC_RARITY}")) s = s.replace("{SC_RARITY}", crate.getDisplayRarity());
									if(s.contains("{LOOT_RARITY}")) s = s.replace("{LOOT_RARITY}", sc.getDisplayRarity());
									lore.add(s);
								}
								itemMeta.setLore(lore); lore.clear();
							}
							item.setItemMeta(itemMeta);
						}
						top.setItem(slot, item);
						player.updateInventory();
					} else if(selectedSlots.containsKey(slot) && selectedSlots.get(slot) != null) {
						if(!revealedslots.containsKey(uuid) || !revealedslots.get(uuid).contains(slot)) {
							final ItemStack reward = revealingLoot.get(uuid).getRandomReward(selectedSlots.get(slot).getIdentifier());
							top.setItem(slot, reward);
							player.updateInventory();
							if(!revealingloot.containsKey(uuid)) revealingloot.put(uuid, new ArrayList<>());
							revealingloot.get(uuid).add(reward);
							if(!revealedslots.containsKey(uuid)) revealedslots.put(uuid, new ArrayList<>());
							revealedslots.get(uuid).add(slot);
						}
					}
				}
			}
		}
	}
}
