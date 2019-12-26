package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randompackage.event.FundDepositEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
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
import java.math.BigDecimal;
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
	private HashMap<String, BigDecimal> needed_unlocks;
	private HashMap<UUID, BigDecimal> deposits;
	
	public BigDecimal maxfund, total;

	public String getIdentifier() { return "FUND"; }
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(args.length == 0) {
			view(sender);
		} else {
			final String a = args[0];
			switch (a) {
				case "reset":
					reset(sender);
					break;
				case "deposit":
					if(args.length >= 2 && sender instanceof Player) {
						deposit((Player) sender, args[1]);
					}
					break;
				default:
					viewHelp(sender);
					break;
			}
		}
		return true;
	}

	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "fund.yml");
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "fund.yml"));

		unlockstring = new HashMap<>();
		needed_unlocks = new HashMap<>();
		deposits = new HashMap<>();
		maxfund = BigDecimal.ZERO;

		for(String s : config.getStringList("unlock")) {
			final String[] a = s.split(";");
			final BigDecimal v = BigDecimal.valueOf(Double.parseDouble(a[1]));
			unlockstring.put(a[0], s);
			needed_unlocks.put(a[2], v);
			if(v.doubleValue() > maxfund.doubleValue()) {
				maxfund = v;
			}
		}

		total = BigDecimal.valueOf(otherdata.getDouble("fund.total"));
		final ConfigurationSection cs = otherdata.getConfigurationSection("fund.depositors");
		if(cs != null) {
			for(String s : cs.getKeys(false)) {
				deposits.put(UUID.fromString(s), BigDecimal.valueOf(otherdata.getDouble("fund.depositors." + s)));
			}
		}
		sendConsoleMessage("&6[RandomPackage] &aLoaded Server Fund &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		otherdata.set("fund.total", total);
		for(UUID u : deposits.keySet()) {
			otherdata.set("fund.depositors." + u.toString(), deposits.get(u));
		}
		saveOtherData();
	}
	
	public void deposit(@NotNull Player player, @NotNull String arg) {
		if(hasPermission(player, "RandomPackage.fund.deposit", true)) {
			if(total.doubleValue() >= maxfund.doubleValue()) {
				sendStringListMessage(player, getStringList(config, "messages.already complete"), null);
			} else if(arg.contains(".") && !config.getBoolean("allows decimals")) {
				sendStringListMessage(player, getStringList(config, "messages.cannot include decimals"), null);
			} else {
				BigDecimal amount = valueOfBigDecimal(arg);
				final double min = config.getDouble("min deposit");
				final double d = amount.doubleValue();
				final String a = d < config.getDouble("min deposit") ? "less than min" : d > eco.getBalance(player) ? "need more money" : null;
				if(a != null) {
					sendMessage(player, a, null, a.equals("less than min") ? min : d, false);
					return;
				}
				final FundDepositEvent e = new FundDepositEvent(player, amount);
				PLUGIN_MANAGER.callEvent(e);
				if(!e.isCancelled()) {
					amount = e.amount;
					final UUID u = player.getUniqueId();
					deposits.put(u, deposits.getOrDefault(u, BigDecimal.ZERO).add(amount));
					eco.withdrawPlayer(player, d);
					total = total.add(amount);
					sendMessage(player, "deposited", null, d, true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		final String msg = event.getMessage(), cmd;
		if(msg.contains(":")) {
			final String[] values = msg.split(":");
			cmd = values.length > 1 ? values[1].split(" ")[0].toLowerCase() : null;
			if(cmd == null) return;
		} else {
			cmd = msg.substring(1).split(" ")[0].toLowerCase();
		}
		final PluginCommand p = Bukkit.getPluginCommand(cmd);
		if(p != null) {
			final String pl = p.getName(), m = msg.toLowerCase();
			final List<String> a = p.getAliases();
			for(String s : unlockstring.keySet()) {
				final String us = unlockstring.get(s);
				if(total.doubleValue() < Double.parseDouble(us.split(";")[1])) {
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
		replacements.put("{FACTION}", sender instanceof Player ? getFactionTag(((Player) sender).getUniqueId()) : "");
		replacements.put("{PLAYER}", sender.getName());
		replacements.put("{AMOUNT}", formatDouble(q).split("\\.")[0]);

		for(String ss : getStringList(config, "messages." + path)) {
			for(String r : replacements.keySet()) {
				final String R = replacements.get(r);
				if(r != null && R != null) {
					ss = ss.replace(r, R);
				}
			}
			if(broadcasted) Bukkit.broadcastMessage(colorize(ss));
			else            sender.sendMessage(colorize(ss));
			return;
		}
	}

	public void reset(@Nullable CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.fund.reset", true)) {
			Bukkit.broadcastMessage(colorize("&c&l(!)&r &e" + (sender != null ? sender.getName() : "CONSOLE") + " &chas reset the server fund!"));
			total = BigDecimal.ZERO;
			deposits.clear();
		}
	}
	public void view(@NotNull CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.fund", true)) {
			final int length = config.getInt("messages.progress bar.length"), pdigits = config.getInt("messages.unlock percent digits");
			final String symbol = config.getString("messages.progress bar.symbol"), achieved = colorize(config.getString("messages.progress bar.achieved")), notachieved = colorize(config.getString("messages.progress bar.not achieved"));
			for(String s : getStringList(config, "messages.view")) {
				if(s.contains("{BALANCE}")) s = s.replace("{BALANCE}", formatBigDecimal(total).split("\\.")[0]);
				if(s.equals("{CONTENT}")) {
					for(String i : config.getStringList("unlock")) {
						final BigDecimal req = needed_unlocks.get(i.split(";")[2]);
						final double d = req.doubleValue(), t = total.doubleValue();
						final int q = req.intValue();
						final String percent = roundDoubleString((t/d) * 100 > 100.000 ? 100 : (t/d) * 100, pdigits), abb = getAbbreviation(d), qq = formatInt(q);
						for(String k : getStringList(config, "messages.content")) {
							if(k.contains("{COMPLETED}")) k = k.replace("{COMPLETED}", t >= d ? colorize(config.getString("messages.completed")) : "");
							if(k.contains("{UNLOCK}")) k = k.replace("{UNLOCK}", i.split(";")[2]);
							if(k.contains("{UNLOCK%}")) k = k.replace("{UNLOCK%}", percent);
							if(k.contains("{PROGRESS_BAR}")) {
								StringBuilder u = new StringBuilder();
								for(int a = 1; a <= length; a++) u.append(Double.parseDouble(percent) >= a ? achieved : notachieved).append(symbol);
								k = k.replace("{PROGRESS_BAR}", u.toString());
							}
							if(k.contains("{REQ}")) k = k.replace("{REQ}", abb);
							if(k.contains("{REQ$}")) k = k.replace("{REQ$}", qq);
							sender.sendMessage(colorize(k));
						}
					}
				}
				if(!s.equals("{CONTENT}")) sender.sendMessage(colorize(s));
			}
		}
	}
	public void viewHelp(@NotNull CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.fund.help", true)) {
			sendStringListMessage(sender, getStringList(config, "messages.help"), null);
		}
	}
	private String getAbbreviation(double input) {
		final int l = Integer.toString((int) input).length();
		String ll = formatDouble(input);
		if(ll.contains(",")) ll = ll.split(",")[0] + "." + ll.split(",")[1];
		String d = Double.toString(Double.parseDouble(ll));
		if(d.endsWith(".0") && d.split("\\.")[1].length() == 1) d = d.split("\\.")[0];
		return d + colorize(config.getString("messages." + (l >= 13 && l <= 15 ? "trillion" : l >= 10 && l <= 12 ? "billion" : l >= 7 && l <= 9 ? "million" : l >= 4 && l <= 6 ? "thousand" : "")));
	}
}
