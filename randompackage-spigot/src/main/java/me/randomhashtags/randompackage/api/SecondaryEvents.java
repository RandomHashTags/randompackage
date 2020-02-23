package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.perms.SecondaryPermission;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
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
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

public class SecondaryEvents extends RPFeature implements CommandExecutor {
    private static SecondaryEvents instance;
    public static SecondaryEvents getSecondaryEvents() {
        if(instance == null) instance = new SecondaryEvents();
        return instance;
    }

    public YamlConfiguration config;
    private List<PotionEffectType> removedPotionEffects;
    private String confirm;
    private ItemStack banknote;
    private int banknoteValueSlot;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String cmdName = cmd.getName();
        final int length = args.length;
        if(cmdName.equals("balance")) {
            String type, qq = "", bal = player != null ? formatBigDecimal(BigDecimal.valueOf(eco.getBalance(player)), true) : "0.00";
            if(player != null && length == 0 && hasPermission(sender, SecondaryPermission.BALANCE, true)) {
                type = "self";
            } else if(length >= 1 && hasPermission(sender, SecondaryPermission.BALANCE_OTHER, true)) {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                if(op.equals(player)) {
                    type = "self";
                } else {
                    type = "other";
                    qq = eco.getBalance(player) >= eco.getBalance(op) ? "other" : "self";
                    bal = formatBigDecimal(BigDecimal.valueOf(eco.getBalance(op)));
                }
            } else {
                return true;
            }
            for(String s : getStringList(config, "balance.view " + type)) {
                if(s.contains("{INT}")) s = s.replace("{INT}", bal.contains(".") ? bal.split("\\.")[0] : bal);
                if(s.contains("{DECIMALS}")) s = s.replace("{DECIMALS}", bal.contains(".") ? "." + (bal.split("\\.")[1].length() > 2 ? bal.split("\\.")[1].substring(0, 2) : bal.split("\\.")[1]) : "");
                if(s.equals("{RICHER}") && player != null) s = config.getString("balance.richer than " + qq);
                if(s.contains("{TARGET}")) s = s.replace("{TARGET}", Bukkit.getOfflinePlayer(args[0]).getName());
                if(!s.equals("{RICHER}")) {
                    sender.sendMessage(colorize(s));
                }
            }
        } else if(player != null) {
            if(cmdName.equals("bless") && hasPermission(player, SecondaryPermission.BLESS, true)) {
                bless(player);
            } else if(cmdName.equals("bump") && hasPermission(sender, SecondaryPermission.BUMP, true)) {
                player.damage(1.0);
            } else if(cmdName.equals("confirm") && hasPermission(sender, SecondaryPermission.CONFIRM, true)) {
                final RPPlayer pdata = RPPlayer.get(length == 0 ? player.getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                if(pdata != null) {
                    if(pdata.getUnclaimedPurchases().isEmpty()) {
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
                    String msg = null, formattedAmount = formatBigDecimal(amount, true);
                    formattedAmount = formattedAmount.contains("E") ? formattedAmount.split("E")[0] : formattedAmount;
                    if(eco == null) {
                        player.sendMessage("[RandomPackage] You need an Economy plugin installed and enabled to use this feature!");
                        return true;
                    } else if(amountDouble <= 0.00) {
                        msg = "withdraw.cannot withdraw zero";
                    } else if(eco.getBalance(player) < amountDouble) {
                        msg = "withdraw.cannot withdraw more than balance";
                    } else if(eco.withdrawPlayer(player, amountDouble).transactionSuccess()) {
                        item = getBanknote(amount, player.getName());
                        giveItem(player, item);
                        msg = "withdraw.success";
                    } else {
                        return true;
                    }
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{VALUE}", formattedAmount);
                    replacements.put("{BALANCE}", formatDouble(eco.getBalance(player)));
                    sendStringListMessage(player, getStringList(config, msg), replacements);
                }
            }
        }
        return true;
    }

    public String getIdentifier() {
        return "SECONDARY_EVENTS";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "secondary.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "secondary.yml"));
        confirm = colorize(config.getString("confirm.title"));

        int i = 0;
        banknote = GIVEDP_ITEM.items.get("banknote");
        for(String s : banknote.getItemMeta().getLore()) {
            if(s.contains("{VALUE}")) banknoteValueSlot = i;
            i++;
        }

        final List<String> pe = getStringList(config, "bless.removed potion effects");
        removedPotionEffects = new ArrayList<>();
        for(String s : pe) {
            final PotionEffectType a = getPotionEffectType(s);
            if(a != null) {
                removedPotionEffects.add(a);
            }
        }

        sendConsoleDidLoadFeature("Secondary Events", started);
    }
    public void unload() {
    }

    public ItemStack getBanknote(BigDecimal value, String signer) {
        item = GIVEDP_ITEM.items.get("banknote").clone();
        itemMeta = item.getItemMeta(); lore.clear();
        for(String s : itemMeta.getLore()) {
            if(s.contains("{SIGNER}")) {
                s = signer != null ? s.replace("{SIGNER}", signer) : null;
            }
            if(s != null) {
                lore.add(s.replace("{VALUE}", formatBigDecimal(value)));
            }
        }
        itemMeta.setLore(lore); lore.clear();
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
                RPPlayer.get(player.getUniqueId()).removeUnclaimedPurchase(current);
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
                eco.depositPlayer(player, amount);
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{VALUE}", formatDouble(amount));
                sendStringListMessage(player, getStringList(config, "withdraw.deposit"), replacements);
                player.updateInventory();
            }
        }
    }
}
