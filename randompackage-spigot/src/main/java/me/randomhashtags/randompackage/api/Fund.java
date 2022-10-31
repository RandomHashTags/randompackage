package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.event.FundDepositEvent;
import me.randomhashtags.randompackage.perms.FundPermission;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum Fund implements RPFeatureSpigot, CommandExecutor {
	INSTANCE;

	public YamlConfiguration config;

	private HashMap<String, String> unlockstring;
	private HashMap<String, BigDecimal> needed_unlocks;
	private HashMap<UUID, BigDecimal> deposits;
	
	public BigDecimal maxfund, total;

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
		if(args.length == 0) {
			view(sender);
		} else {
			final String key = args[0];
			switch (key) {
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

	@Override
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "fund.yml");
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "fund.yml"));

		unlockstring = new HashMap<>();
		needed_unlocks = new HashMap<>();
		deposits = new HashMap<>();
		maxfund = BigDecimal.ZERO;

		for(String s : config.getStringList("unlock")) {
			final String[] values = s.split(";");
			final BigDecimal v = BigDecimal.valueOf(Double.parseDouble(values[1]));
			unlockstring.put(values[0], s);
			needed_unlocks.put(values[2], v);
			if(v.doubleValue() > maxfund.doubleValue()) {
				maxfund = v;
			}
		}

		total = BigDecimal.valueOf(OTHER_YML.getDouble("fund.total"));
		for(String uuid : getConfigurationSectionKeys(OTHER_YML, "fund.depositors", false)) {
			deposits.put(UUID.fromString(uuid), BigDecimal.valueOf(OTHER_YML.getDouble("fund.depositors." + uuid)));
		}
		sendConsoleDidLoadFeature("Server Fund", started);
	}
	@Override
	public void unload() {
		OTHER_YML.set("fund.total", total);
		for(UUID u : deposits.keySet()) {
			OTHER_YML.set("fund.depositors." + u.toString(), deposits.get(u));
		}
		saveOtherData();
	}
	
	public void deposit(@NotNull Player player, @NotNull String arg) {
		if(hasPermission(player, FundPermission.DEPOSIT, true)) {
			if(total.doubleValue() >= maxfund.doubleValue()) {
				sendStringListMessage(player, getStringList(config, "messages.already complete"), null);
			} else if(arg.contains(".") && !config.getBoolean("allows decimals")) {
				sendStringListMessage(player, getStringList(config, "messages.cannot include decimals"), null);
			} else {
				BigDecimal amount = valueOfBigDecimal(arg);
				final double min = config.getDouble("min deposit"), amountValue = amount.doubleValue();
				final String msg = amountValue < config.getDouble("min deposit") ? "less than min" : amountValue > ECONOMY.getBalance(player) ? "need more money" : null;
				if(msg != null) {
					sendMessage(player, msg, null, msg.equals("less than min") ? min : amountValue, false);
					return;
				}
				final FundDepositEvent depositEvent = new FundDepositEvent(player, amount);
				PLUGIN_MANAGER.callEvent(depositEvent);
				if(!depositEvent.isCancelled()) {
					amount = depositEvent.amount;
					final UUID uuid = player.getUniqueId();
					deposits.put(uuid, deposits.getOrDefault(uuid, BigDecimal.ZERO).add(amount));
					ECONOMY.withdrawPlayer(player, amountValue);
					total = total.add(amount);
					sendMessage(player, "deposited", null, amountValue, true);
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
		final PluginCommand pCmd = Bukkit.getPluginCommand(cmd);
		if(pCmd != null) {
			final String pl = pCmd.getName(), m = msg.toLowerCase();
			final List<String> a = pCmd.getAliases();
			for(String s : unlockstring.keySet()) {
				final String us = unlockstring.get(s);
				if(total.doubleValue() < Double.parseDouble(us.split(";")[1])) {
					if(s.startsWith("/" + pl) && (a.contains(cmd) && !s.contains(" ") || pl.equals(cmd) && !s.contains(" "))
							|| m.equals(s.toLowerCase())
							|| m.equals(s.replace(pl, cmd).toLowerCase())) {
						if(hasPermission(player, FundPermission.BYPASS_LOCKED, false)) {
							return;
						}
						event.setCancelled(true);
						sendMessage(player, "needs to reach", us, 0, false);
						return;
					}
				}
			}
		}
	}
	private void sendMessage(CommandSender sender, String path, String unlockString, double amount, boolean broadcasted) {
		final HashMap<String, String> replacements = new HashMap<>();
		if(unlockString != null) {
			final String[] values = unlockString.split(";");
			final String value = values[1];
			final String arg1 = values[3], arg2 = values[0], req = getAbbreviation(Double.parseDouble(value)), req$ = formatInt(Integer.parseInt(value));
			replacements.put("{ARG1}", arg1);
			replacements.put("{ARG2}", arg2);
			replacements.put("{REQ}", req);
			replacements.put("{REQ$}", req$);
		}
		replacements.put("{FACTION}", sender instanceof Player ? RegionalAPI.INSTANCE.getFactionTag(((Player) sender).getUniqueId()) : "");
		replacements.put("{PLAYER}", sender.getName());
		replacements.put("{AMOUNT}", formatDouble(amount).split("\\.")[0]);

		for(String string : getStringList(config, "messages." + path)) {
			for(String replacementKey : replacements.keySet()) {
				final String replacement = replacements.get(replacementKey);
				if(replacementKey != null && replacement != null) {
					string = string.replace(replacementKey, replacement);
				}
			}
			if(broadcasted) {
				Bukkit.broadcastMessage(string);
			} else {
				sender.sendMessage(string);
			}
			return;
		}
	}

	public void reset(@Nullable CommandSender sender) {
		if(hasPermission(sender, FundPermission.RESET, true)) {
			Bukkit.broadcastMessage(colorize("&c&l(!)&r &e" + (sender != null ? sender.getName() : "CONSOLE") + " &chas reset the server fund!"));
			total = BigDecimal.ZERO;
			deposits.clear();
		}
	}
	public void view(@NotNull CommandSender sender) {
		if(hasPermission(sender, FundPermission.VIEW, true)) {
			final int length = config.getInt("messages.progress bar.length"), pdigits = config.getInt("messages.unlock percent digits");
			final String symbol = getString(config, "messages.progress bar.symbol"), achieved = getString(config, "messages.progress bar.achieved"), notachieved = getString(config, "messages.progress bar.not achieved");
			final String completed = getString(config, "messages.completed");
			for(String string : getStringList(config, "messages.view")) {
				if(string.contains("{BALANCE}")) {
					string = string.replace("{BALANCE}", formatBigDecimal(total).split("\\.")[0]);
				}
				if(string.equals("{CONTENT}")) {
					final List<String> content = getStringList(config, "messages.content");
					for(String i : getStringList(config, "unlock")) {
						final String[] values = i.split(";");
						final BigDecimal req = needed_unlocks.get(values[2]);
						final double required = req.doubleValue(), total = this.total.doubleValue();
						final int requiredInt = req.intValue();
						final String percent = roundDoubleString((total/required) * 100 > 100.000 ? 100 : (total/required) * 100, pdigits);

						final HashMap<String, String> replacements = new HashMap<>();
						replacements.put("{COMPLETED}", total >= required ? completed : "");
						replacements.put("{UNLOCK}", values[2]);
						replacements.put("{UNLOCK%}", percent);
						replacements.put("{PROGRESS_BAR}", getProgressBar(length, percent, achieved, notachieved, symbol));
						replacements.put("{REQ}", getAbbreviation(required));
						replacements.put("{REQ$}", formatInt(requiredInt));
						sendStringListMessage(sender, content, replacements);
					}
				} else {
					sender.sendMessage(string);
				}
			}
		}
	}
	private String getProgressBar(int length, String percent, String achieved, String notachieved, String symbol) {
		final StringBuilder builder = new StringBuilder();
		final double percentDouble = Double.parseDouble(percent);
		for(int i = 1; i <= length; i++) {
			builder.append(percentDouble >= i ? achieved : notachieved).append(symbol);
		}
		return builder.toString();
	}
	public void viewHelp(@NotNull CommandSender sender) {
		if(hasPermission(sender, FundPermission.VIEW_HELP, true)) {
			sendStringListMessage(sender, getStringList(config, "messages.help"), null);
		}
	}
	private String getAbbreviation(double input) {
		final int l = Integer.toString((int) input).length();
		String ll = formatDouble(input);
		if(ll.contains(",")) {
			ll = ll.split(",")[0] + "." + ll.split(",")[1];
		}
		String d = Double.toString(Double.parseDouble(ll));
		if(d.endsWith(".0") && d.split("\\.")[1].length() == 1) {
			d = d.split("\\.")[0];
		}
		return d + colorize(config.getString("messages." + (l >= 13 && l <= 15 ? "trillion" : l >= 10 && l <= 12 ? "billion" : l >= 7 && l <= 9 ? "million" : l >= 4 && l <= 6 ? "thousand" : "")));
	}
}
