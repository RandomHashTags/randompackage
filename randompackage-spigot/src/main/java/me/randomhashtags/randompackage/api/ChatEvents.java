package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.TitleData;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPItemStack;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum ChatEvents implements RPFeatureSpigot, CommandExecutor, RPItemStack {
	INSTANCE;

	private String bragDisplay, itemDisplay, chatformat;
	private HashMap<UUID, PlayerInventory> bragInventories;
	private List<UUID> viewingBrag;

	@Override
	public void load() {
		final long started = System.currentTimeMillis();
		bragDisplay = colorize(RP_CONFIG.getString("chat cmds.brag.display"));
		itemDisplay = colorize(RP_CONFIG.getString("chat cmds.item.display"));
		viewingBrag = new ArrayList<>();
		bragInventories = new HashMap<>();
		chatformat = RP_CONFIG.getString("chat cmds.format");
		sendConsoleDidLoadFeature("ChatEvents", started);
	}
	@Override
	public void unload() {
		for(UUID id : new ArrayList<>(viewingBrag)) {
			Bukkit.getPlayer(id).closeInventory();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void playerChatEvent(AsyncPlayerChatEvent event) {
		final String message = colorize(event.getMessage());
		final boolean brag = message.contains("[brag]"), item = message.contains("[item]");
		if(brag || item) {
			final Player player = event.getPlayer();
			final List<Player> recipients = new ArrayList<>(Bukkit.getOnlinePlayers());

			final TitleData data = FileRPPlayer.get(player.getUniqueId()).getTitleData();
			final Title title = data.getActive();
			String format = colorize(chatformat.replace("{DISPLAYNAME}", player.getDisplayName()).replace("{TITLE}", title != null ? " " + title.getChatTitle() : ""));
			if(RANDOM_PACKAGE.placeholderapi) {
				format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
			}
			final TextComponent prefix = new TextComponent(format.replace("{MESSAGE}", message.split("\\[").length > 0 ? message.split("\\[")[0] : "")), suffix = new TextComponent(message.split("]").length > 1 ? message.split("]")[1] : "");
			event.setCancelled(true);
			if(brag) {
				if(hasPermission(player, "RandomPackage.chat.brag", false)) {
					sendBragMessage(player, bragDisplay.replace("{PLAYER}", player.getName()), prefix, suffix, recipients);
				} else {
					sendStringListMessage(player, getStringList(RP_CONFIG, "chat cmds.brag.no perm"), null);
				}
			} else {
				if(hasPermission(player, "RandomPackage.chat.item", false)) {
					final ItemStack i = getItemInHand(player);
					if(!i.getType().equals(Material.AIR)) {
						String name = i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : i.getType().name();
						sendItemMessage(player, itemDisplay.replace("{ITEM_NAME}", name).replace("{ITEM_AMOUNT}", Integer.toString(i.getAmount())), prefix, suffix, recipients);
					}
				} else {
					sendStringListMessage(player, getStringList(RP_CONFIG, "chat cmds.item.no perm"), null);
				}
			}
		}
	}
	public void sendBragMessage(Player player, String message, TextComponent prefix, TextComponent suffix, List<Player> recipients) {
		bragInventories.put(player.getUniqueId(), player.getInventory());
		final TextComponent m = new TextComponent(message);
		m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(colorize(RP_CONFIG.getString("chat cmds.brag.hover message").replace("{PLAYER}", player.getName()))).create()));
		m.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/brag " + player.getUniqueId()));
		for(Player p : recipients) {
			send(player, p, prefix, m, suffix);
		}
		sendConsoleMessage(prefix.getText().replace("{F_TAG}", "") + m.getText() + suffix.getText());
	}
	public void sendItemMessage(Player player, String message, TextComponent prefix, TextComponent suffix, List<Player> recipients) {
		final TextComponent m = new TextComponent(message);
		final ItemStack i = getItemInHand(player);
		final String u = asNMSCopy(i);
		if(u == null || u.isEmpty()) return;
		m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(u).create()));
		for(Player p : recipients) {
			send(player, p, prefix, m, suffix);
		}
		sendConsoleMessage(prefix.getText().replace("{F_TAG}", "") + m.getText() + suffix.getText());
	}
	public void sendHoverMessage(@NotNull Player player, String message, @NotNull List<String> hoverMessage, @NotNull HashMap<String, List<String>> replacements) {
	    String hover = "";
	    for(int i = 0; i < hoverMessage.size(); i++) {
	        hover = hover + (i != 0 ? "\n" : "") + hoverMessage.get(i);
        }
        for(int i = 0; i < replacements.size(); i++) {
            final String s = (String) replacements.keySet().toArray()[i];
            final StringBuilder b = new StringBuilder();
            for(int j = 0; j < replacements.get(s).size(); j++) {
                final String r = replacements.get(s).get(j);
                b.append(j != 0 ? "\n" : "").append(r);
            }
            hover = hover.replace(s, b.toString());
        }
        final TextComponent m = new TextComponent(message);
	    m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(colorize(hover)).create()));
	    player.spigot().sendMessage(m);
    }
	
	private void send(Player sender, Player recipient, TextComponent prefix, TextComponent m, TextComponent suffix) {
		final UUID uuid = sender.getUniqueId();
		final String tag = RegionalAPI.INSTANCE.getFactionTag(uuid);
		String text = prefix.toPlainText();
		if(text.contains("{F_TAG}")) {
			final FactionsUUID factions = FactionsUUID.INSTANCE;
			text = prefix.toLegacyText().replace("{F_TAG}", tag != null && !ChatColor.stripColor(tag).toLowerCase().contains("wilderness") ? factions.getRelationColor(sender, recipient) + factions.getRole(uuid) + tag + " " : "");
		}
		final TextComponent base = text.isEmpty() ? prefix : new TextComponent(text);
		recipient.spigot().sendMessage(base, m, suffix);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
		if(args.length != 1) {
			return true;
		}
		final Player player = sender instanceof Player ? (Player) sender : null;
		UUID uuid;
		try {
			uuid = UUID.fromString(args[0]);
		} catch (Exception e) {
			sendStringListMessage(sender, getStringList(RP_CONFIG, "chat cmds.brag.invalid"), null);
			return true;
		}
		if(player != null && hasPermission(sender, "RandomPackage.brag", true) && bragInventories.containsKey(uuid)) {
			player.openInventory(Bukkit.createInventory(player, 45, ChatColor.stripColor(bragDisplay.replace("{PLAYER}", Bukkit.getOfflinePlayer(uuid).getName()))));
			final Inventory top = player.getOpenInventory().getTopInventory();
			final PlayerInventory bragInv = bragInventories.get(uuid);
			final ItemStack[] con = bragInv.getContents();
			for(int i = 0; i < top.getSize(); i++) {
				if(i < con.length && con[i] != null) {
					top.setItem(i, con[i]);
				}
			}
			final ItemStack stainedGlass = UMaterial.BLACK_STAINED_GLASS_PANE.getItemStack();
			if(stainedGlass != null) {
				final ItemMeta itemMeta = stainedGlass.getItemMeta();
				itemMeta.setDisplayName(" ");
				stainedGlass.setItemMeta(itemMeta);
				top.setItem(36, stainedGlass);
				top.setItem(37, stainedGlass);
				top.setItem(40, stainedGlass);
				top.setItem(43, stainedGlass);
				top.setItem(44, stainedGlass);
				top.setItem(38, bragInv.getHelmet());
				top.setItem(39, bragInv.getChestplate());
				top.setItem(41, bragInv.getLeggings());
				top.setItem(42, bragInv.getBoots());
			}
			viewingBrag.add(player.getUniqueId());
		} else {
			sendStringListMessage(sender, getStringList(RP_CONFIG, "chat cmds.brag.invalid"), null);
		}
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
}
