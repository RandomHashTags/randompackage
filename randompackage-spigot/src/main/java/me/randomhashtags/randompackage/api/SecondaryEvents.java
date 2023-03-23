package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.perms.SecondaryPermission;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum SecondaryEvents implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private List<PotionEffectType> removedPotionEffects;
    private String confirm;
    private ItemStack banknote;
    private int banknoteValueSlot;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String cmdName = cmd.getName();
        final int length = args.length;
        if(cmdName.equals("balance")) {
            String type, qq = "", bal = player != null ? formatNumber(BigDecimal.valueOf(ECONOMY.getBalance(player)), true) : "0.00";
            if(player != null && length == 0 && hasPermission(sender, SecondaryPermission.BALANCE, true)) {
                type = "self";
            } else if(length >= 1 && hasPermission(sender, SecondaryPermission.BALANCE_OTHER, true)) {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                if(op.equals(player)) {
                    type = "self";
                } else {
                    type = "other";
                    qq = ECONOMY.getBalance(player) >= ECONOMY.getBalance(op) ? "other" : "self";
                    bal = formatBigDecimal(BigDecimal.valueOf(ECONOMY.getBalance(op)));
                }
            } else {
                return true;
            }
            for(String string : getStringList(config, "balance.view " + type)) {
                if(string.contains("{INT}")) {
                    string = string.replace("{INT}", bal.contains(".") ? bal.split("\\.")[0] : bal);
                }
                if(string.contains("{DECIMALS}")) {
                    string = string.replace("{DECIMALS}", bal.contains(".") ? "." + (bal.split("\\.")[1].length() > 2 ? bal.split("\\.")[1].substring(0, 2) : bal.split("\\.")[1]) : "");
                }
                if(string.equals("{RICHER}") && player != null) {
                    string = config.getString("balance.richer than " + qq);
                }
                if(string.contains("{TARGET}")) {
                    string = string.replace("{TARGET}", Bukkit.getOfflinePlayer(args[0]).getName());
                }
                if(!string.equals("{RICHER}")) {
                    sender.sendMessage(colorize(string));
                }
            }
        } else if(player != null) {
            if(cmdName.equals("bless") && hasPermission(player, SecondaryPermission.BLESS, true)) {
                bless(player);
            } else if(cmdName.equals("bump") && hasPermission(sender, SecondaryPermission.BUMP, true)) {
                player.damage(1.0);
            } else if(cmdName.equals("confirm") && hasPermission(sender, SecondaryPermission.CONFIRM, true)) {
                final RPPlayer pdata = FileRPPlayer.get(length == 0 ? player.getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                if(pdata != null) {
                    final List<ItemStack> unclaimedPurchases = pdata.getUnclaimedPurchases();
                    if(unclaimedPurchases == null || unclaimedPurchases.isEmpty()) {
                        sendStringListMessage(player, getStringList(config, "confirm." + (pdata.getOfflinePlayer().equals(player) ? "self " : "other") + "no unclaimed items"), null);
                    } else {
                        confirm(player, pdata);
                    }
                }
            } else if(cmdName.equals("roll") && hasPermission(sender, SecondaryPermission.ROLL, true)) {
                roll(player, args.toString());
            } else if(cmdName.equals("withdraw") && hasPermission(player, SecondaryPermission.WITHDRAW, true)) {
                if(length == 0) {
                    sendStringListMessage(player, getStringList(config, "withdraw.argument 0"), null);
                } else {
                    BigDecimal amount = BigDecimal.valueOf(getRemainingDouble(args[0]));
                    final double amountDouble = amount.doubleValue();
                    String msg = null, formattedAmount = formatNumber(amount, true);
                    formattedAmount = formattedAmount.contains("E") ? formattedAmount.split("E")[0] : formattedAmount;
                    if(ECONOMY == null) {
                        player.sendMessage("[RandomPackage] You need an Economy plugin installed and enabled to use this feature!");
                        return true;
                    } else if(amountDouble <= 0.00) {
                        msg = "withdraw.cannot withdraw zero";
                    } else if(ECONOMY.getBalance(player) < amountDouble) {
                        msg = "withdraw.cannot withdraw more than balance";
                    } else if(ECONOMY.withdrawPlayer(player, amountDouble).transactionSuccess()) {
                        final ItemStack item = getBanknote(amount, player.getName());
                        giveItem(player, item);
                        msg = "withdraw.success";
                    } else {
                        return true;
                    }
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{VALUE}", formattedAmount);
                    replacements.put("{BALANCE}", formatDouble(ECONOMY.getBalance(player)));
                    sendStringListMessage(player, getStringList(config, msg), replacements);
                }
            }
        }
        return true;
    }
    @Override
    public void load() {
        save(null, "secondary.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "secondary.yml"));
        confirm = colorize(config.getString("confirm.title"));

        int i = 0;
        banknote = GivedpItem.INSTANCE.items.get("banknote");
        for(String s : banknote.getItemMeta().getLore()) {
            if(s.contains("{VALUE}")) banknoteValueSlot = i;
            i++;
        }

        final List<String> pe = getStringList(config, "bless.removed potion effects");
        removedPotionEffects = new ArrayList<>();
        for(String string : pe) {
            final PotionEffectType type = get_potion_effect_type(string);
            if(type != null) {
                removedPotionEffects.add(type);
            }
        }
    }
    @Override
    public void unload() {
    }

    public ItemStack getBanknote(@NotNull BigDecimal value, @Nullable String signer) {
        final ItemStack item = GivedpItem.INSTANCE.items.get("banknote").clone();
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String string : itemMeta.getLore()) {
            if(string.contains("{SIGNER}")) {
                string = signer != null ? string.replace("{SIGNER}", signer) : null;
            }
            if(string != null) {
                lore.add(string.replace("{VALUE}", formatBigDecimal(value)));
            }
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public void bless(@NotNull Player player) {
        for(PotionEffectType type : removedPotionEffects) {
            player.removePotionEffect(type);
        }
        sendStringListMessage(player, getStringList(config, "bless.msg"), null);
    }
    public void confirm(@NotNull Player opener, @NotNull RPPlayer target) {
        final List<ItemStack> u = target.getUnclaimedPurchases();
        final int size = ((u.size()+9)/9)*9;
        opener.openInventory(Bukkit.createInventory(opener, size, confirm));
        final Inventory top = opener.getOpenInventory().getTopInventory();
        for(ItemStack is : target.getUnclaimedPurchases()) {
            top.setItem(top.firstEmpty(), is);
        }
        opener.updateInventory();
    }
    public void roll(@NotNull Player player, @NotNull String arg0) {
        final int radius = config.getInt("roll.block radius"), maxroll = getRemainingInt(arg0), roll = RANDOM.nextInt(maxroll);
        final List<Entity> nearby = player.getNearbyEntities(radius, radius, radius);
        final List<Player> players = new ArrayList<>();
        for(Entity entity : nearby) {
            if(entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        if(!players.isEmpty()) {
            final HashMap<String, String> replacements = getReplacements("{MAX}", formatInt(maxroll), "{PLAYER}", player.getName(), "{ROLLED}", formatInt(roll));
            final List<String> msg = getStringList(config, "roll.message");
            for(Player target : players) {
                sendStringListMessage(target, msg, replacements);
            }
            sendStringListMessage(player, msg, replacements);
        } else {
            sendStringListMessage(player, getStringList(config, "roll.nobody heard roll"), null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR)) {
            final String title = event.getView().getTitle();
            final Player player = (Player) event.getWhoClicked();
            if(title.equals(confirm)) {
                final Inventory top = player.getOpenInventory().getTopInventory();
                event.setCancelled(true);
                player.updateInventory();
                final int slot = event.getRawSlot();
                if(slot >= top.getSize()) {
                    return;
                }
                giveItem(player, current);
                FileRPPlayer.get(player.getUniqueId()).getUnclaimedPurchases().remove(current);
                top.setItem(slot, new ItemStack(Material.AIR));
            } else {
                return;
            }
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final Player player = event.getPlayer();
            final String name = is.getItemMeta().getDisplayName();
            if(name.equals(banknote.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final double amount = getRemainingDouble(ChatColor.stripColor(is.getItemMeta().getLore().get(banknoteValueSlot)));
                ECONOMY.depositPlayer(player, amount);
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{VALUE}", formatDouble(amount));
                sendStringListMessage(player, getStringList(config, "withdraw.deposit"), replacements);
                player.updateInventory();
            }
        }
    }
}
