package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.api.events.fund.FundDepositEvent;
import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Fund extends RPFeature implements CommandExecutor {
	private static Fund instance;
	public static Fund getFund() {
		if(instance == null) instance = new Fund();
		return instance;
	}
	public YamlConfiguration config;

	private HashMap<String, String> unlockstring;
	private HashMap<String, Long> needed_unlocks;
	private HashMap<UUID, Long> deposits;
	
	public long maxfund = 0, total = 0;
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(args.length == 0) {
			view(sender);
		} else {
			final String a = args[0];
			if(a.equals("help")) viewHelp(sender);
			else if(a.equals("reset")) reset(sender);
			else if(sender instanceof Player && args.length >= 2 && a.equals("deposit")) deposit((Player) sender, args[1]);
		}
		return true;
	}

	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "fund.yml");
		config = YamlConfiguration.loadConfiguration(new File(rpd, "fund.yml"));

		unlockstring = new HashMap<>();
		needed_unlocks = new HashMap<>();
		deposits = new HashMap<>();

		for(String s : config.getStringList("unlock")) {
			final String[] a = s.split(";");
			final long v = Long.parseLong(a[1]);
			unlockstring.put(a[0], s);
			needed_unlocks.put(a[2], v);
			if(v > maxfund) maxfund = v;
		}

		total = otherdata.getLong("fund.total");
		final ConfigurationSection cs = otherdata.getConfigurationSection("fund.depositors");
		if(cs != null) {
			for(String s : cs.getKeys(false)) {
				deposits.put(UUID.fromString(s), otherdata.getLong("fund.depositors." + s));
			}
		}
		sendConsoleMessage("&6[RandomPackage] &aLoaded Server Fund &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		final YamlConfiguration a = otherdata;
		a.set("fund.total", total);
		for(UUID u : deposits.keySet()) {
			a.set("fund.depositors." + u.toString(), deposits.get(u));
		}
		saveOtherData();
		config = null;
		unlockstring = null;
		needed_unlocks = null;
		maxfund = 0;
		total = 0;
	}
	
	public void deposit(Player player, String arg) {
		if(hasPermission(player, "RandomPackage.fund.deposit", true)) {
			if(total >= maxfund) {
				sendStringListMessage(player, config.getStringList("messages.already complete"), null);
			} else if(arg.contains(".") && !config.getBoolean("allows decimals")) {
				sendStringListMessage(player, config.getStringList("messages.cannot include decimals"), null);
			} else {
				final double q = getRemainingDouble(arg), min = config.getDouble("min deposit");
				if(q == -1) return;
				long Q = (long) q;
				final String a = Q < config.getDouble("min deposit") ? "less than min" : Q > eco.getBalance(player) ? "need more money" : null;
				if(a != null) {
					sendMessage(player, a, null, a.equals("less than min") ? min : Q, false);
					return;
				}
				final FundDepositEvent e = new FundDepositEvent(player, Q);
				pluginmanager.callEvent(e);
				if(!e.isCancelled()) {
					Q = e.amount;
					final UUID u = player.getUniqueId();
					deposits.put(u, deposits.getOrDefault(u, 0l)+Q);
					eco.withdrawPlayer(player, Q);
					total += Q;
					sendMessage(player, "deposited", null, Q, true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if(!event.isCancelled()) {
			final Player player = event.getPlayer();
			final String me = event.getMessage(), cmd = (me.contains(":") ? me.split(":")[1].split(" ")[0] : me.substring(1).split(" ")[0]).toLowerCase();
			final PluginCommand p = Bukkit.getPluginCommand(cmd);
			if(p != null) {
				final String pl = p.getName(), m = me.toLowerCase();
				final List<String> a = p.getAliases();
				for(String s : unlockstring.keySet()) {
					final String us = unlockstring.get(s);
					if(total < Double.parseDouble(us.split(";")[1])) {
						if(s.startsWith("/" + pl) && (a.contains(cmd) && !s.contains(" ") || pl.equals(cmd) && !s.contains(" "))
								|| m.equals(s.toLowerCase())
								|| m.equals(s.replace(pl, cmd).toLowerCase())) {
							if(hasPermission(player, "RandomPackage.fund.bypass", false)) return;
							event.setCancelled(true);
							sendMessage(player, "needs to reach", us, 0, false);
							return;
						}
					}
				}
			}
		}
	}
	private void sendMessage(CommandSender sender, String path, String unlockstring, double q, boolean broadcasted) {
		final HashMap<String, String> replacements = new HashMap<>();
		if(unlockstring != null) {
			final String[] b = unlockstring.split(";");
			final String arg1 = b[3], arg2 = b[0], req = getAbbreviation(Double.parseDouble(unlockstring.split(";")[1])), req$ = formatInt(Integer.parseInt(unlockstring.split(";")[1]));
			replacements.put("{ARG1}", arg1);
			replacements.put("{ARG2}", arg2);
			replacements.put("{REQ}", req);
			replacements.put("{REQ$}", req$);
		}
		replacements.put("{FACTION}", sender instanceof Player ? fapi.getFaction((Player) sender) : "");
		replacements.put("{PLAYER}", sender.getName());
		replacements.put("{AMOUNT}", formatDouble(q).split("\\.")[0]);

		for(String ss : config.getStringList("messages." + path)) {
			for(String r : replacements.keySet()) {
				final String R = replacements.get(r);
				if(r != null && R != null) {
					ss = ss.replace(r, R);
				}
			}
			if(broadcasted) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ss));
			else            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ss));
			return;
		}
	}

	public void reset(CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.fund.reset", true)) {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!)&r &e" + (sender != null ? sender.getName() : "CONSOLE") + " &chas reset the server fund!"));
			total = 0;
			deposits.clear();
		}
	}
	public void view(CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.fund", true)) {
			final int length = config.getInt("messages.progress bar.length"), pdigits = config.getInt("messages.unlock percent digits");
			final String symbol = config.getString("messages.progress bar.symbol"), achieved = ChatColor.translateAlternateColorCodes('&', config.getString("messages.progress bar.achieved")), notachieved = ChatColor.translateAlternateColorCodes('&', config.getString("messages.progress bar.not achieved"));
			for(String s : config.getStringList("messages.view")) {
				if(s.contains("{BALANCE}")) s = s.replace("{BALANCE}", formatDouble(total).split("\\.")[0]);
				if(s.equals("{CONTENT}")) {
					for(String i : config.getStringList("unlock")) {
						final double req = needed_unlocks.get(i.split(";")[2]);
						final int q = (int) req;
						final String percent = roundDoubleString((total / req) * 100 > 100.000 ? 100 : (total / req) * 100, pdigits), abb = getAbbreviation(req), qq = formatInt(q);
						for(String k : config.getStringList("messages.content")) {
							if(k.contains("{COMPLETED}")) k = k.replace("{COMPLETED}", total >= req ? ChatColor.translateAlternateColorCodes('&', config.getString("messages.completed")) : "");
							if(k.contains("{UNLOCK}")) k = k.replace("{UNLOCK}", i.split(";")[2]);
							if(k.contains("{UNLOCK%}")) k = k.replace("{UNLOCK%}", percent);
							if(k.contains("{PROGRESS_BAR}")) {
								String u = "";
								for(int a = 1; a <= length; a++) u = u + (Double.parseDouble(percent) >= a ? achieved : notachieved) + symbol;
								k = k.replace("{PROGRESS_BAR}", u);
							}
							if(k.contains("{REQ}")) k = k.replace("{REQ}", abb);
							if(k.contains("{REQ$}")) k = k.replace("{REQ$}", qq);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', k));
						}
					}
				}
				if(!s.equals("{CONTENT}")) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
		}
	}
	public void viewHelp(CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.fund.help", true))
			sendStringListMessage(sender, config.getStringList("messages.help"), null);
	}
	private String getAbbreviation(double input) {
		final int l = Integer.toString((int) input).length();
		String ll = formatDouble(input);
		if(ll.contains(",")) ll = ll.split(",")[0] + "." + ll.split(",")[1];
		String d = Double.toString(Double.parseDouble(ll));
		if(d.endsWith(".0") && d.split("\\.")[1].length() == 1) d = d.split("\\.")[0];
		return d + ChatColor.translateAlternateColorCodes('&', config.getString("messages." + (l >= 13 && l <= 15 ? "trillion" : l >= 10 && l <= 12 ? "billion" : l >= 7 && l <= 9 ? "million" : l >= 4 && l <= 6 ? "thousand" : "")));
	}
}
