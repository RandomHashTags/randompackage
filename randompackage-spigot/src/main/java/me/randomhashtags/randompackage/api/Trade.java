package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.living.ActiveTrade;
import me.randomhashtags.randompackage.perms.TradePermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum Trade implements RPFeatureSpigot, CommandExecutor {
	INSTANCE;

	public YamlConfiguration config;
	protected int radius, countdown;
	private String title;
	private UInventory tradeInventory;
	public ItemStack divider, accept, accepting;
	private HashMap<UUID, UUID> requests;
	private List<String> blacklistedMaterials;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(args.length == 0 && hasPermission(player, TradePermission.COMMAND, true)) {
			sendStringListMessage(player, getStringList(config, "messages.commands"), null);
		} else if(args.length == 1) {
			sendRequest(player, args[0]);
		}
		return true;
	}

	@Override
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "trade.yml");
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "trade.yml"));

		divider = createItemStack(config, "gui.divider");
		accept = createItemStack(config, "gui.accept");
		accepting = createItemStack(config, "gui.accepting");
		countdown = config.getInt("gui.countdown start", 0);
		radius = config.getInt("radius", 0);
		title = colorize(config.getString("gui.title"));
		tradeInventory = new UInventory(null, 54, title);
		accept.setAmount(countdown);
		accepting.setAmount(countdown);

		final Inventory inv = tradeInventory.getInventory();
		inv.setItem(0, accept);
		inv.setItem(8, accept);
		for(int i = 4; i < 54; i += 9) {
			inv.setItem(i, divider);
		}
		blacklistedMaterials = new ArrayList<>();
		for(String s : config.getStringList("blacklisted materials")) {
			blacklistedMaterials.add(s.toUpperCase());
		}

		requests = new HashMap<>();
		sendConsoleDidLoadFeature("Trade", started);
	}
	@Override
	public void unload() {
		ActiveTrade.ACTIVE_TRADES = null;
	}

	public int getCountdown() {
		return countdown;
	}
	public void sendRequest(@NotNull Player sender, String receiver) {
		final HashMap<String, String> replacements = new HashMap<>();
		if(hasPermission(sender, TradePermission.SEND_REQUEST, true)) {
			if(receiver == null
					|| Bukkit.getPlayer(receiver) == null
					|| Bukkit.getPlayer(receiver) != null && !Bukkit.getPlayer(receiver).isOnline()
					|| radius != -1 && (sender.getWorld() != Bukkit.getPlayer(receiver).getWorld()
					|| radius != -2 && sender.getLocation().distance(Bukkit.getPlayer(receiver).getLocation()) > radius)
			) {
				replacements.put("{TARGET}", receiver);
				sendStringListMessage(sender, getStringList(config, "messages.not within range"), replacements);
			} else if(sender == Bukkit.getPlayer(receiver)) {
				sendStringListMessage(sender, getStringList(config, "messages.send self"), null);
			} else {
				final UUID uuid = sender.getUniqueId();
				final Player target = Bukkit.getPlayer(receiver);
				if(requests.containsKey(target.getUniqueId()) && requests.get(target.getUniqueId()).equals(uuid)) {
					acceptRequest(target, sender);
				} else {
					replacements.put("{TARGET}", target.getName());
					replacements.put("{SENDER}", sender.getName());
					sendStringListMessage(sender, getStringList(config, "messages.send request"), replacements);
					sendStringListMessage(target, getStringList(config, "messages.receive request"), replacements);
					requests.put(uuid, target.getUniqueId());
					SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
						if(requests.containsKey(uuid) && requests.get(uuid).equals(target.getUniqueId())) {
							requests.remove(uuid);
						}
					}, 20*10);
				}
			}
		}
	}
	public void acceptRequest(Player accepter, Player requester) {
		if(hasPermission(accepter, TradePermission.ACCEPT_REQUEST, true)) {
			final Inventory inv1 = Bukkit.createInventory(requester, 54, title.replace("{PLAYER}", requester.getName())), inv2 = Bukkit.createInventory(accepter, 54, title.replace("{PLAYER}", accepter.getName()));
			final ItemStack[] contents = tradeInventory.getInventory().getContents();
			inv1.setContents(contents);
			inv2.setContents(contents);
			accepter.openInventory(inv1);
			requester.openInventory(inv2);
			accepter.updateInventory();
			requester.updateInventory();
			new ActiveTrade(requester, accepter);
		}
	}

	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer(), other;
		final ActiveTrade trade = ActiveTrade.valueOf(player);
		if(trade != null) {
			final Player s = trade.getSender();
			other = s == player ? trade.getReceiver() : s;
			final List<String> msg = getStringList(config, "messages.cancelled");
			sendStringListMessage(player, msg, null);
			sendStringListMessage(other, msg, null);
			trade.cancel();
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final ItemStack currentItem = event.getCurrentItem();
		if(currentItem != null && !currentItem.getType().equals(Material.AIR)) {
			final Player player = (Player) event.getWhoClicked();
			final Inventory top = player.getOpenInventory().getTopInventory();
			final ActiveTrade trade = ActiveTrade.valueOf(player);
			if(trade != null) {
				final int rawSlot = event.getRawSlot();
				final Player sender = trade.getSender();
				final boolean senderIsPlayer = sender == player;
				event.setCancelled(true);
				if(rawSlot == 0 && (currentItem.getItemMeta().equals(accept.getItemMeta()) || currentItem.getItemMeta().equals(accepting.getItemMeta()))) {
					final boolean ready = currentItem.getItemMeta().equals(accept.getItemMeta());
					if(senderIsPlayer) {
						trade.setSenderReady(ready);
					} else {
						trade.setReceiverReady(ready);
					}
				} else if(rawSlot >= top.getSize()) {
					final int slot = trade.getNextEmptySlot(player);
					if(slot != -1) {
						if(blacklistedMaterials.contains(UMaterial.match(currentItem).name())) {
							sendStringListMessage(player, getStringList(config, "messages.cannot trade blacklisted material"), null);
							player.updateInventory();
							return;
						}
						if(senderIsPlayer) {
							trade.getSenderTrade().put(slot, currentItem);
						} else {
							trade.getReceiverTrade().put(slot, currentItem);
						}
						trade.updateTrades();
						event.setCurrentItem(new ItemStack(Material.AIR));
					}
				} else if(trade.isOnSelfSide(rawSlot)) {
					giveItem(player, currentItem);
					(senderIsPlayer ? trade.getSenderTrade() : trade.getReceiverTrade()).remove(rawSlot);
					trade.updateTrades();
					trade.cancelCountdown();
				} else {
					return;
				}
				player.updateInventory();
			}
		}
	}
}
