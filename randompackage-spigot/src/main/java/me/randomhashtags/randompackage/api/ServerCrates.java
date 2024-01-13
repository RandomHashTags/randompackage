package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.ServerCrate;
import me.randomhashtags.randompackage.addon.file.FileServerCrate;
import me.randomhashtags.randompackage.addon.living.LivingServerCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.ServerCrateCloseEvent;
import me.randomhashtags.randompackage.event.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum ServerCrates implements RPFeatureSpigot {
	INSTANCE;

	private List<UUID> can_reveal_rarities;
	private HashMap<UUID, ServerCrate> revealing_loot;
	private HashMap<UUID, HashMap<Integer, ServerCrate>> selectedSlots;
	private HashMap<UUID, List<ItemStack>> revealingloot;
	private HashMap<UUID, List<Integer>> tasks, revealed_slots;

	@Override
	public @NotNull Feature get_feature() {
		return Feature.SERVER_CRATE;
	}

	@Override
	public void load() {
		can_reveal_rarities = new ArrayList<>();
		revealing_loot = new HashMap<>();
		selectedSlots = new HashMap<>();
		revealingloot = new HashMap<>();
		tasks = new HashMap<>();
		revealed_slots = new HashMap<>();

		if(!OTHER_YML.getBoolean("saved default server crates")) {
			generateDefaultServerCrates();
			OTHER_YML.set("saved default server crates", true);
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
	}
	@Override
	public void unload() {
		for(UUID uuid : new ArrayList<>(revealing_loot.keySet())) {
			final OfflinePlayer o = Bukkit.getOfflinePlayer(uuid);
			if(o.isOnline()) {
				o.getPlayer().closeInventory();
			}
		}
		for(UUID u : tasks.keySet()) {
			stopTasks(u);
		}
		LivingServerCrate.deleteAll(true);
	}

	public void openCrate(@NotNull Player player, @NotNull ServerCrate crate) {
		final ServerCrateOpenEvent event = new ServerCrateOpenEvent(player, crate);
		PLUGIN_MANAGER.callEvent(event);
		if(!event.isCancelled()) {
			final UInventory i = crate.getInventory();
			player.openInventory(Bukkit.createInventory(player, i.getSize(), i.getTitle()));
			player.getOpenInventory().getTopInventory().setContents(i.getInventory().getContents());
			player.updateInventory();
			final UUID uuid = player.getUniqueId();
			revealing_loot.put(uuid, crate);
			selectedSlots.put(uuid, new HashMap<>());
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
	private void revealBackgroundLoot(@NotNull Player player) {
		final UUID uuid = player.getUniqueId();
		final ServerCrate crate = revealing_loot.get(uuid);
		tasks.put(uuid, new ArrayList<>());
		final List<Integer> tasks = this.tasks.get(uuid);
		final List<Integer> background = new ArrayList<>();
		final Inventory top = player.getOpenInventory().getTopInventory();
		final HashMap<Integer, ServerCrate> selectedSlots = this.selectedSlots.get(uuid);
		final ItemStack background1 = crate.getBackground(), background2 = crate.getBackground2(), revealSlotRarity = crate.getRevealSlotRarity();
		for(int i = 0; i < top.getSize(); i++) {
			if(!selectedSlots.containsKey(i) && !background1.equals(top.getItem(i))) {
				background.add(i);
			}
		}
		final int backgroundSize = background.size();
		final List<Integer> backgroundSlots = new ArrayList<>(background);
		for(int i = 1; i <= backgroundSize; i++) {
			final int iteration = i;
			final int task = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
				final int randomSlot = (int) background.toArray()[RANDOM.nextInt(background.size())];
				final ItemStack reward = crate.getRandomReward(crate.getRandomRarity(true).getIdentifier());
				top.setItem(randomSlot, reward);
				player.updateInventory();
				background.remove((Object) randomSlot);
				if(iteration == backgroundSize) {
					for(int n = 1; n <= backgroundSize; n++) {
						final int K = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
							final int targetSlot = (int) backgroundSlots.toArray()[RANDOM.nextInt(backgroundSlots.size())];
							top.setItem(targetSlot, background2);
							backgroundSlots.remove((Object) targetSlot);
							if(backgroundSlots.isEmpty()) {
								for(int selectedSlot : selectedSlots.keySet()) {
									final ItemMeta itemMeta = revealSlotRarity.getItemMeta();
									if(revealSlotRarity.hasItemMeta()) {
										if(itemMeta.hasDisplayName()) {
											final String name = itemMeta.getDisplayName();
											if(name.contains("{SLOT}")) {
												itemMeta.setDisplayName(name.replace("{SLOT}", Integer.toString(getRemainingInt(top.getItem(selectedSlot).getItemMeta().getDisplayName()))));
											}
										}
									}
									revealSlotRarity.setItemMeta(itemMeta);
									top.setItem(selectedSlot, revealSlotRarity);
								}
								can_reveal_rarities.add(uuid);
							}
							player.updateInventory();
						}, n* 5L);
						tasks.add(K);
					}
				}
			}, i * 5L);
			tasks.add(task);
		}
	}
	
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		final UUID uuid = player.getUniqueId();
		if(revealing_loot.containsKey(uuid)) {
			can_reveal_rarities.remove(uuid);
			stopTasks(uuid);
			final ServerCrate crate = revealing_loot.get(uuid);
			revealing_loot.remove(uuid);
			revealingloot.putIfAbsent(uuid, new ArrayList<>());
			final HashMap<String, List<String>> crateRewards = crate.getRewards();
			final List<ItemStack> lootRevealed = revealingloot.get(uuid);
			final int redeemableItems = crate.getRedeemableItems(), s = revealingloot.get(uuid).size();
			for(int i = 1; i <= redeemableItems-s; i++) {
				lootRevealed.add(crate.getRandomReward((String) crateRewards.keySet().toArray()[RANDOM.nextInt(crateRewards.size())]));
			}
			selectedSlots.remove(uuid);
			if(player.isOnline()) {
				final ServerCrateCloseEvent close_event = new ServerCrateCloseEvent(player, crate, lootRevealed);
				PLUGIN_MANAGER.callEvent(close_event);
				for(ItemStack is : lootRevealed) {
					giveItem(player, is);
				}
			}
			revealingloot.remove(uuid);
			revealed_slots.remove(uuid);
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Block block = event.getClickedBlock();
		final Location loc = block != null ? block.getLocation() : null;
		final HashMap<Location, LivingServerCrate> crates = LivingServerCrate.LIVING;
		final LivingServerCrate crate = loc != null && crates != null ? crates.getOrDefault(loc, null) : null;
		final ItemStack is = event.getItem();
		if(crate != null) {
			crate.delete(true);
		} else if(is != null && is.hasItemMeta() && event.getAction().name().contains("RIGHT")) {
			final ServerCrate serverCrate = valueOfServerCrate(is);
			if(serverCrate != null) {
				event.setCancelled(true);
				removeItem(player, is, 1);
				openCrate(player, serverCrate);
			} else if(block != null) {
				final ServerCrate serverCrateFromFlare = valueOfServerCrateFlare(is);
				if(serverCrateFromFlare != null) {
					event.setCancelled(true);
					player.updateInventory();
					new LivingServerCrate(serverCrateFromFlare, serverCrateFromFlare.getFlare().spawn(player, loc));
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
			if(revealing_loot.containsKey(uuid))  {
				final int slot = event.getRawSlot();
				final ServerCrate crate = revealing_loot.get(uuid);
				event.setCancelled(true);
				player.updateInventory();
				final Inventory top = player.getOpenInventory().getTopInventory();
				if(!event.getClick().isLeftClick() && !event.getClick().isRightClick() || current.getType().equals(Material.AIR) || slot >= top.getSize()) {
					return;
				}
				if(crate != null && crate.getSelectableSlots().contains(slot)) {
					final HashMap<Integer, ServerCrate> selectedSlots = this.selectedSlots.get(uuid);
					ItemStack item = null;
					ItemMeta itemMeta = null;
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
					} else if(can_reveal_rarities.contains(uuid) && selectedSlots.containsKey(slot) && selectedSlots.get(slot) == null) {
						final ServerCrate sc = crate.getRandomRarity(true);
						selectedSlots.put(slot, sc);
						item = sc.getDisplay().clone();
						itemMeta = item.getItemMeta();
						final List<String> lore = new ArrayList<>();
						if(item.hasItemMeta()) {
							if(itemMeta.hasLore()) {
								for(String string : itemMeta.getLore()) {
									if(string.contains("{SC_RARITY}")) {
										string = string.replace("{SC_RARITY}", crate.getDisplayRarity());
									}
									if(string.contains("{LOOT_RARITY}")) {
										string = string.replace("{LOOT_RARITY}", sc.getDisplayRarity());
									}
									lore.add(string);
								}
								itemMeta.setLore(lore);
							}
							item.setItemMeta(itemMeta);
						}
						top.setItem(slot, item);
						player.updateInventory();
					} else if(selectedSlots.containsKey(slot) && selectedSlots.get(slot) != null) {
						if(!revealed_slots.containsKey(uuid) || !revealed_slots.get(uuid).contains(slot)) {
							final ItemStack reward = revealing_loot.get(uuid).getRandomReward(selectedSlots.get(slot).getIdentifier());
							top.setItem(slot, reward);
							player.updateInventory();
							revealingloot.putIfAbsent(uuid, new ArrayList<>());
							revealingloot.get(uuid).add(reward);
							revealed_slots.putIfAbsent(uuid, new ArrayList<>());
							revealed_slots.get(uuid).add(slot);
						}
					}
				}
			}
		}
	}
}
