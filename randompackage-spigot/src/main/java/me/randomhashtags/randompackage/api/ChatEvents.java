package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.Title;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatEvents extends RPFeature implements CommandExecutor {
	private static ChatEvents instance;
	public static ChatEvents getChatEvents() {
		if(instance == null) instance = new ChatEvents();
		return instance;
	}

	private String bragDisplay, itemDisplay, chatformat;
	private HashMap<UUID, PlayerInventory> bragInventories;
	private ArrayList<UUID> viewingBrag;

	public void load() {
		final long started = System.currentTimeMillis();
		bragDisplay = ChatColor.translateAlternateColorCodes('&', randompackage.getConfig().getString("chat cmds.brag.display"));
		itemDisplay = ChatColor.translateAlternateColorCodes('&', randompackage.getConfig().getString("chat cmds.item.display"));
		viewingBrag = new ArrayList<>();
		bragInventories = new HashMap<>();
		chatformat = randompackage.getConfig().getString("chat cmds.format");
		sendConsoleMessage("&6[RandomPackage] &aLoaded ChatEvents &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		bragDisplay = null;
		itemDisplay = null;
		for(UUID id : viewingBrag) Bukkit.getPlayer(id).closeInventory();
		bragInventories = null;
		viewingBrag = null;
	}
	@EventHandler
	private void playerChatEvent(AsyncPlayerChatEvent event) {
		final String message = event.getMessage();
		final Player player = event.getPlayer();
		if(message.contains("[brag]") || message.contains("[item]")) {
			final ArrayList<Player> recipients = new ArrayList<>(Bukkit.getOnlinePlayers());
			final Title ac = RPPlayer.get(player.getUniqueId()).getActiveTitle();
			final String format = ChatColor.translateAlternateColorCodes('&', chatformat.replace("{DISPLAYNAME}", player.getDisplayName()).replace("{TITLE}", ac != null ? " " + ac.getChatTitle() : ""));
			final TextComponent prefix = new TextComponent(format.replace("{MESSAGE}", event.getMessage().split("\\[").length > 0 ? event.getMessage().split("\\[")[0] : "")), suffix = new TextComponent(event.getMessage().split("]").length > 1 ? event.getMessage().split("]")[1] : "");
			if(message.contains("[brag]")) {
				event.setCancelled(true);
				if(hasPermission(player, "RandomPackage.chat.brag", false)) {
					sendBragMessage(player, bragDisplay.replace("{PLAYER}", player.getName()), prefix, suffix, recipients);
				} else {
					sendStringListMessage(player, randompackage.getConfig().getStringList("chat cmds.brag.no perm"), null);
				}
			}
			if(message.contains("[item]")) {
				event.setCancelled(true);
				if(hasPermission(player, "RandomPackage.chat.item", false)) {
					ItemStack i = getItemInHand(event.getPlayer());
					if(i != null && !i.getType().equals(Material.AIR)) {
						String name = i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : i.getType().name();
						sendItemMessage(player, itemDisplay.replace("{ITEM_NAME}", name).replace("{ITEM_AMOUNT}", Integer.toString(i.getAmount())), prefix, suffix, recipients);
					}
				} else sendStringListMessage(player, randompackage.getConfig().getStringList("chat cmds.item.no perm"), null);
			}
		}
	}
	public void sendBragMessage(Player player, String message, TextComponent prefix, TextComponent suffix, ArrayList<Player> recipients) {
		bragInventories.put(player.getUniqueId(), player.getInventory());
		final TextComponent m = new TextComponent(message);
		m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', randompackage.getConfig().getString("chat cmds.brag.hover message").replace("{PLAYER}", player.getName()))).create()));
		m.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/brag " + player.getUniqueId().toString()));
		for(Player p : recipients)
			send(player, p, prefix, m, suffix);
		sendConsoleMessage(prefix.getText().replace("{F_TAG}", "") + m.getText() + suffix.getText());
	}
	public void sendItemMessage(Player player, String message, TextComponent prefix, TextComponent suffix, ArrayList<Player> recipients) {
		final TextComponent m = new TextComponent(message);
		final ItemStack i = getItemInHand(player);
		final String u = asNMSCopy(i);
		if(u == null || u.isEmpty()) return;
		m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(u).create()));
		for(Player p : recipients)
			send(player, p, prefix, m, suffix);
		sendConsoleMessage(prefix.getText().replace("{F_TAG}", "") + m.getText() + suffix.getText());
	}
	public void sendHoverMessage(Player player, String message, List<String> hoverMessage, HashMap<String, List<String>> replacements) {
	    String hover = "";
	    for(int i = 0; i < hoverMessage.size(); i++) {
	        hover = hover + (i != 0 ? "\n" : "") + hoverMessage.get(i);
        }
        for(int i = 0; i < replacements.keySet().size(); i++) {
            final String s = (String) replacements.keySet().toArray()[i];
            String replacement = "";
            for(int j = 0; j < replacements.get(s).size(); j++) {
                final String r = replacements.get(s).get(j);
                replacement = replacement + (j != 0 ? "\n" : "") + r;
            }
            hover = hover.replace(s, replacement);
        }
        TextComponent m = new TextComponent(message);
	    m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hover)).create()));
	    player.spigot().sendMessage(m);
    }
	
	private void send(Player sender, Player recipient, TextComponent prefix, TextComponent m, TextComponent suffix) {
		final String f = fapi.getFaction(sender);
		TextComponent ftag = new TextComponent(prefix.toPlainText().contains("{F_TAG}") ?
				prefix.toLegacyText().replace("{F_TAG}", f != null && !ChatColor.stripColor(f).toLowerCase().contains("wilderness") ? fapi.getRelationColor(sender, recipient) + fapi.getRole(sender) + f + " " : "")
			: "");
		TextComponent p = ftag.toPlainText().equals("") ? prefix : ftag;
		if(version.contains("1.8")) {
			recipient.spigot().sendMessage(p, m, suffix);
		} else {
			recipient.spigot().sendMessage(p, m, suffix);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(args.length != 1) return true;
		final Player player = sender instanceof Player ? (Player) sender : null;
		UUID v;
		try {
			v = UUID.fromString(args[0]);
		} catch (Exception e) {
			sendStringListMessage(sender, randompackage.getConfig().getStringList("chat cmds.brag.invalid"), null);
			return true;
		}
		if(player != null && hasPermission(sender, "RandomPackage.brag", true) && bragInventories.keySet().contains(v)) {
			player.openInventory(Bukkit.createInventory(player, 45, ChatColor.stripColor(bragDisplay.replace("{PLAYER}", Bukkit.getOfflinePlayer(v).getName()))));
			final Inventory top = player.getOpenInventory().getTopInventory();
			final PlayerInventory bi = bragInventories.get(v);
			final ItemStack[] con = bi.getContents();
			for(int i = 0; i < top.getSize(); i++) {
				if(i < con.length && con[i] != null) {
					top.setItem(i, con[i]);
				}
			}
			final ItemStack b = UMaterial.BLACK_STAINED_GLASS_PANE.getItemStack();
			if(b != null) {
				itemMeta = b.getItemMeta();
				itemMeta.setDisplayName(" ");
				b.setItemMeta(itemMeta);
				top.setItem(36, b);
				top.setItem(37, b);
				top.setItem(40, b);
				top.setItem(43, b);
				top.setItem(44, b);
				top.setItem(38, bi.getHelmet());
				top.setItem(39, bi.getChestplate());
				top.setItem(41, bi.getLeggings());
				top.setItem(42, bi.getBoots());
			}
			viewingBrag.add(player.getUniqueId());
		} else {
			sendStringListMessage(sender, randompackage.getConfig().getStringList("chat cmds.brag.invalid"), null);
		}
		return true;
	}
	
	@EventHandler
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if(viewingBrag.contains(player.getUniqueId())) {
			event.setCancelled(true);
			player.updateInventory();
		}
	}
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		viewingBrag.remove(event.getPlayer().getUniqueId());
	}
	@EventHandler
	private void playerQuitEvent(PlayerQuitEvent event) {
		viewingBrag.remove(event.getPlayer().getUniqueId());
	}

	private String asNMSCopy(ItemStack itemstack) {
		if(version.contains("1.8")) {
			return org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_8_R3.NBTTagCompound()).toString();
		} else if(version.contains("1.9")) {
			return org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_9_R2.NBTTagCompound()).toString();
		} else if(version.contains("1.10")) {
			return org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_10_R1.NBTTagCompound()).toString();
		} else if(version.contains("1.11")) {
			return org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_11_R1.NBTTagCompound()).toString();
		} else if(version.contains("1.12")) {
			return org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_12_R1.NBTTagCompound()).toString();
		} else if(version.contains("1.13")) {
			return org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_13_R2.NBTTagCompound()).toString();
		} else if(version.contains("1.14")) {
			return org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(itemstack).save(new net.minecraft.server.v1_14_R1.NBTTagCompound()).toString();
		} else {
			return null;
		}
	}
}
