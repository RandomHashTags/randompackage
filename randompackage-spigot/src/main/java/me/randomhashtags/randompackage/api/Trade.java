package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.addons.living.ActiveTrade;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Trade extends RPFeature implements CommandExecutor {
	private static Trade instance;
	public static Trade getTrade() {
		if(instance == null) instance = new Trade();
		return instance;
	}

	public YamlConfiguration config;
	private int radius = 0, countdown = 0;
	private String title;
	private UInventory q;
	private ItemStack divider, accept, accepting;

	private HashMap<UUID, UUID> requests;
	private List<String> blacklistedMaterials;

	public String getIdentifier() { return "TRADE"; }

	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "trade.yml");

		requests = new HashMap<>();

		config = YamlConfiguration.loadConfiguration(new File(rpd, "trade.yml"));
		divider = d(config, "gui.divider");
		accept = d(config, "gui.accept");
		accepting = d(config, "gui.accepting");
		countdown = config.getInt("gui.countdown start");
		radius = config.getInt("radius");
		title = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title"));
		q = new UInventory(null, 54, title);
		accept.setAmount(countdown); accepting.setAmount(countdown);
		final Inventory qi = q.getInventory();
		qi.setItem(0, accept);
		qi.setItem(8, accept);
		for(int i = 4; i < 54; i += 9) qi.setItem(i, divider);
		blacklistedMaterials = new ArrayList<>();
		for(String s : config.getStringList("blacklisted materials")) {
			blacklistedMaterials.add(s.toUpperCase());
		}
		lore.clear();
		sendConsoleMessage("&6[RandomPackage] &aLoaded Trade &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		config = null;
		radius = 0;
		countdown = 0;
		title = null;
		q = null;
		divider = null;
		accept = null;
		accepting = null;
		requests = null;
		blacklistedMaterials = null;
		ActiveTrade.trades = null;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(args.length == 0 && hasPermission(player, "RandomPackage.trade", true)) {
			sendStringListMessage(player, config.getStringList("messages.commands"), null);
		} else if(args.length == 1) {
			sendRequest(player, args[0]);
		}
		return true;
	}
	
	
	public void sendRequest(Player sender, String receiver) {
		final HashMap<String, String> r = new HashMap<>();
		if(hasPermission(sender, "RandomPackage.trade.request", true)) {
			if(receiver == null || Bukkit.getPlayer(receiver) == null || Bukkit.getPlayer(receiver) != null && !Bukkit.getPlayer(receiver).isOnline()
					|| radius != -1 && (sender.getWorld() != Bukkit.getPlayer(receiver).getWorld() || radius != -2 && sender.getLocation().distance(Bukkit.getPlayer(receiver).getLocation()) > radius)
			) {
				r.put("{TARGET}", receiver);
				sendStringListMessage(sender, config.getStringList("messages.not within range"), r);
			} else if(sender == Bukkit.getPlayer(receiver)) {
				sendStringListMessage(sender, config.getStringList("messages.send self"), null);
			} else {
				final UUID s = sender.getUniqueId();
				final Player target = Bukkit.getPlayer(receiver);
				if(requests.containsKey(target.getUniqueId()) && requests.get(target.getUniqueId()).equals(s)) {
					acceptRequest(target, sender);
				} else {
					r.put("{TARGET}", target.getName());
					r.put("{SENDER}", sender.getName());
					sendStringListMessage(sender, config.getStringList("messages.send request"), r);
					sendStringListMessage(target, config.getStringList("messages.receive request"), r);
					requests.put(s, target.getUniqueId());
					scheduler.scheduleSyncDelayedTask(randompackage, () -> {
						if(requests.containsKey(s) && requests.get(s).equals(target.getUniqueId()))
							requests.remove(s);
					}, 20 * 10);
				}
			}
		}
	}
	public void acceptRequest(Player accepter, Player requester) {
		if(hasPermission(accepter, "RandomPackage.trade.accept", true)) {
			final Inventory inv1 = Bukkit.createInventory(requester, 54, title.replace("{PLAYER}", requester.getName())), inv2 = Bukkit.createInventory(accepter, 54, title.replace("{PLAYER}", accepter.getName()));
			final ItemStack[] c = q.getInventory().getContents();
			inv1.setContents(c);
			inv2.setContents(c);
			accepter.openInventory(inv1);
			requester.openInventory(inv2);
			accepter.updateInventory();
			requester.updateInventory();

			new ActiveTrade(requester, accepter);
		}
	}
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer(), other = null;
		final ActiveTrade a = ActiveTrade.valueOf(player);
		if(a != null) {
			final Player s = a.getSender();
			other = s == player ? a.getReceiver() : s;
			final List<String> msg = config.getStringList("messages.cancelled");
			sendStringListMessage(player, msg, null);
			sendStringListMessage(other, msg, null);
			a.cancel();
		}
	}
	@EventHandler
	private void inventoryClickEvent(InventoryClickEvent event) {
		final ItemStack c = event.getCurrentItem();
		if(!event.isCancelled() && c != null && !c.getType().equals(Material.AIR)) {
			final Player player = (Player) event.getWhoClicked();
			final Inventory top = player.getOpenInventory().getTopInventory();
			final ActiveTrade a = ActiveTrade.valueOf(player);
			if(a != null) {
				final int r = event.getRawSlot();
				final Player se = a.getSender();
				final boolean senderIsPlayer = se == player;
				event.setCancelled(true);
				if(r == 0 && (c.getItemMeta().equals(accept.getItemMeta()) || c.getItemMeta().equals(accepting.getItemMeta()))) {
					final boolean ready = c.getItemMeta().equals(accept.getItemMeta());
					if(senderIsPlayer) {
						a.setSenderReady(ready);
					} else {
						a.setReceiverReady(ready);
					}
				} else if(r >= top.getSize()) {
					final int slot = a.getNextEmptySlot(player);
					if(slot != -1) {
						if(blacklistedMaterials.contains(UMaterial.match(c).name())) {
							sendStringListMessage(player, config.getStringList("messages.cannot trade blacklisted material"), null);
							player.updateInventory();
							return;
						}
						if(senderIsPlayer) {
							a.getSenderTrade().put(slot, c);
						} else {
							a.getReceiverTrade().put(slot, c);
						}
						a.updateTrades();
						event.setCurrentItem(new ItemStack(Material.AIR));
					}
				} else if(a.isOnSelfSide(r)) {
					giveItem(player, c);
					(senderIsPlayer ? a.getSenderTrade() : a.getReceiverTrade()).remove(r);
					a.updateTrades();
					a.setSenderReady(false);
					a.setReceiverReady(false);
					final ItemStack i = accept.clone();
					top.setItem(0, i);
					top.setItem(8, i);
					se.updateInventory();
					final Player re = a.getReceiver();
					final Inventory t = re.getOpenInventory().getTopInventory();
					t.setItem(0, i);
					t.setItem(8, i);
					re.updateInventory();
				} else return;
				player.updateInventory();
			}
		}
	}
}
