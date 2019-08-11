package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.ServerCrate;
import me.randomhashtags.randompackage.addons.objects.ServerCrateFlare;
import me.randomhashtags.randompackage.events.ServerCrateCloseEvent;
import me.randomhashtags.randompackage.events.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.utils.objects.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.addons.active.LivingServerCrate;
import me.randomhashtags.randompackage.addons.usingfile.FileServerCrate;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

	public String getIdentifier() { return "SERVER_CRATES"; }
	public void load() {
	    final long started = System.currentTimeMillis();
		canRevealRarities = new ArrayList<>();
		revealingLoot = new HashMap<>();
		selectedSlots = new HashMap<>();
		revealingloot = new HashMap<>();
		tasks = new HashMap<>();
		revealedslots = new HashMap<>();

		final YamlConfiguration a = otherdata;
		if(!a.getBoolean("saved default server crates")) {
			final String[] s = new String[] {"ELITE", "GODLY", "LEGENDARY", "SIMPLE", "ULTIMATE", "UNIQUE"};
			for(String c : s) save("server crates", c + ".yml");
			a.set("saved default server crates", true);
			saveOtherData();
		}
		final List<ItemStack> flares = new ArrayList<>(), crates = new ArrayList<>();
		final File folder = new File(rpd + separator + "server crates");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				final FileServerCrate sc = new FileServerCrate(f);
				crates.add(sc.getItem());
				flares.add(sc.getFlare().getItem());
			}
		}
		addGivedpCategory(crates, UMaterial.CHEST, "Server Crates", "Givedp: Server Crates");
		addGivedpCategory(flares, UMaterial.TORCH, "Server Crate Flares", "Givedp: Server Crate Flares");
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (servercrates != null ? servercrates.size() : 0) + " server crates and flares &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		canRevealRarities = null;
		for(UUID uuid : revealingLoot.keySet()) {
			final OfflinePlayer o = Bukkit.getOfflinePlayer(uuid);
			if(o != null && o.isOnline()) o.getPlayer().closeInventory();
		}
		revealingLoot = null;
		selectedSlots = null;
		revealingloot = null;
		for(UUID u : tasks.keySet()) stopTasks(u);
		tasks = null;
		revealedslots = null;
		deleteAll(Feature.SERVER_CRATES);
		LivingServerCrate.deleteAll(true);
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
			final ServerCrate c = valueOf(is);
			if(c != null) {
				event.setCancelled(true);
				removeItem(player, is, 1);
				openCrate(player, c);
			} else if(b != null) {
				final ServerCrate f = valueOfFlare(is);
				if(f != null) {
					event.setCancelled(true);
					player.updateInventory();
					new LivingServerCrate(f, f.getFlare().spawn(player, loc));
					removeItem(player, is, 1);
				}
			}
		}
	}
	@EventHandler
	private void inventoryClickEvent(InventoryClickEvent event) {
		if(event.getCurrentItem() != null && event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() == event.getWhoClicked()) {
			final Player player = (Player) event.getWhoClicked();
			final UUID uuid = player.getUniqueId();
			if(revealingLoot.containsKey(uuid))  {
				final int r = event.getRawSlot();
				final ServerCrate c = revealingLoot.get(uuid);
				event.setCancelled(true);
				player.updateInventory();
				final Inventory top = player.getOpenInventory().getTopInventory();
				if(!event.getClick().isLeftClick() && !event.getClick().isRightClick() || event.getCurrentItem().getType().equals(Material.AIR) || r >= top.getSize()) return;
				if(c != null && c.getSelectableSlots().contains(r)) {
					if(selectedSlots.get(uuid).keySet().size() != c.getRedeemableItems()) {
						if(!selectedSlots.get(uuid).containsKey(r)) {
							selectedSlots.get(uuid).put(r, null);
							item = c.getSelected().clone(); itemMeta = item.getItemMeta(); lore.clear();
							if(item.hasItemMeta()) {
								if(itemMeta.hasDisplayName()) {
									if(itemMeta.getDisplayName().contains("{SLOT}")) itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(getRemainingInt(event.getCurrentItem().getItemMeta().getDisplayName()))));
								}
								if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
								itemMeta.setLore(lore); lore.clear();
								item.setItemMeta(itemMeta);
							}
							top.setItem(r, item);
						} else {
							item = c.getOpenGui().clone(); itemMeta = item.getItemMeta();
							if(itemMeta.hasDisplayName() && itemMeta.getDisplayName().contains("{SLOT}")) itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(getRemainingInt(event.getCurrentItem().getItemMeta().getDisplayName()))));
							item.setItemMeta(itemMeta);
							selectedSlots.get(uuid).remove(r);
							top.setItem(r, item);
						}
						player.updateInventory();
						if(!tasks.containsKey(uuid) && selectedSlots.get(uuid).keySet().size() == c.getRedeemableItems()) {
							revealBackgroundLoot(player);
						}
					} else if(canRevealRarities.contains(uuid) && selectedSlots.get(uuid).containsKey(r) && selectedSlots.get(uuid).get(r) == null) {
						final ServerCrate sc = c.getRandomRarity(true);
						selectedSlots.get(uuid).put(r, sc);
						item = sc.getDisplay().clone(); itemMeta = item.getItemMeta(); lore.clear();
						if(item.hasItemMeta()) {
							if(itemMeta.hasLore()) {
								for(String s : itemMeta.getLore()) {
									if(s.contains("{SC_RARITY}")) s = s.replace("{SC_RARITY}", c.getDisplayRarity());
									if(s.contains("{LOOT_RARITY}")) s = s.replace("{LOOT_RARITY}", sc.getDisplayRarity());
									lore.add(s);
								}
								itemMeta.setLore(lore); lore.clear();
							}
							item.setItemMeta(itemMeta);
						}
						top.setItem(event.getRawSlot(), item);
						player.updateInventory();
					} else if(selectedSlots.get(uuid).containsKey(r) && selectedSlots.get(uuid).get(r) != null) {
						if(!revealedslots.keySet().contains(uuid) || !revealedslots.get(uuid).contains(r)) {
							final ItemStack reward = revealingLoot.get(uuid).getRandomReward(selectedSlots.get(uuid).get(r).getIdentifier());
							top.setItem(r, reward);
							player.updateInventory();
							if(!revealingloot.containsKey(uuid)) revealingloot.put(uuid, new ArrayList<>());
							revealingloot.get(uuid).add(reward);
							if(!revealedslots.containsKey(uuid)) revealedslots.put(uuid, new ArrayList<>());
							revealedslots.get(uuid).add(r);
						}
					}
				}
			}
		}
	}

	public void openCrate(Player player, ServerCrate crate) {
		final ServerCrateOpenEvent e = new ServerCrateOpenEvent(player, crate);
		pluginmanager.callEvent(e);
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
			for(int i : tasks.get(uuid))
				scheduler.cancelTask(i);
			tasks.remove(uuid);
		}
	}
	private void revealBackgroundLoot(Player player) {
		final UUID uuid = player.getUniqueId();
		final ServerCrate c = revealingLoot.get(uuid);
		tasks.put(player.getUniqueId(), new ArrayList<>());
		List<Integer> background = new ArrayList<>();
		final Inventory top = player.getOpenInventory().getTopInventory();
		for(int i = 0; i < top.getSize(); i++) {
			item = top.getItem(i);
			if(item != null && !item.equals(c.getBackground()) && !selectedSlots.get(uuid).keySet().contains(i)) background.add(i);
		}
		final int bgSize = background.size();
		final List<Integer> bg = new ArrayList<>(background);
		for(int i = 1; i <= bgSize; i++) {
			final int I = i;
			int k = scheduler.scheduleSyncDelayedTask(randompackage, () -> {
				final int randomSlot = (int) background.toArray()[random.nextInt(background.size())];
				ItemStack reward = c.getRandomReward(c.getRandomRarity(true).getIdentifier());
				top.setItem(randomSlot, reward);
				player.updateInventory();
				background.remove((Object) randomSlot);
				if(I == bgSize) {
					for(int n = 1; n <= bgSize; n++) {
						int K = scheduler.scheduleSyncDelayedTask(randompackage, () -> {
							final int R = (int) bg.toArray()[random.nextInt(bg.size())];
							top.setItem(R, c.getBackground2());
							player.updateInventory();
							bg.remove((Object) R);
							if(bg.isEmpty()) {
								for(int P : selectedSlots.get(uuid).keySet()) {
									item = c.getRevealSlotRarity().clone(); itemMeta = item.getItemMeta(); lore.clear();
									if(item.hasItemMeta()) {
										if(itemMeta.hasDisplayName())
											if(itemMeta.getDisplayName().contains("{SLOT}")) itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOT}", Integer.toString(getRemainingInt(top.getItem(P).getItemMeta().getDisplayName()))));
										if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
									}
									itemMeta.setLore(lore); lore.clear();
									item.setItemMeta(itemMeta);
									top.setItem(P, item);
								}
								player.updateInventory();
								canRevealRarities.add(uuid);
							}
						}, n*5);
						tasks.get(player.getUniqueId()).add(K);
					}
				}
			}, i*5);
			tasks.get(player.getUniqueId()).add(k);
		}
	}
	
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		final UUID uuid = player.getUniqueId();
		if(revealingLoot.containsKey(uuid)) {
			canRevealRarities.remove(uuid);
			stopTasks(uuid);
			final ServerCrate c = revealingLoot.get(uuid);
			revealingLoot.remove(uuid);
			if(!revealingloot.containsKey(uuid)) revealingloot.put(uuid, new ArrayList<>());
			final HashMap<String, List<String>> cr = c.getRewards();
			final List<ItemStack> l = revealingloot.get(uuid);
			final int ri = c.getRedeemableItems(), s = revealingloot.get(uuid).size();
			for(int i = 1; i <= ri-s; i++) {
				l.add(c.getRandomReward((String) cr.keySet().toArray()[random.nextInt(cr.size())]));
			}
			selectedSlots.remove(uuid);
			if(player.isOnline()) {
				final ServerCrateCloseEvent e = new ServerCrateCloseEvent(player, c, l);
				pluginmanager.callEvent(e);
				for(ItemStack is : l) {
					giveItem(player, is);
				}
			}
			revealingloot.remove(uuid);
			revealedslots.remove(uuid);
		}
	}

	public ServerCrate valueOf(ItemStack item) {
		if(servercrates != null) {
			for(ServerCrate crate : servercrates.values()) {
				if(crate.getItem().isSimilar(item)) {
					return crate;
				}
			}
		}
		return null;
	}
	public ServerCrate valueOfFlare(ItemStack flare) {
		if(servercrates != null) {
			for(ServerCrate s : servercrates.values()) {
				final ServerCrateFlare f = s.getFlare();
				if(f != null && f.getItem().isSimilar(flare)) {
					return s;
				}
			}
		}
		return null;
	}
}
