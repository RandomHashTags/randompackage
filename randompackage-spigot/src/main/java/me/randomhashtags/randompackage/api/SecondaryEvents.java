package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
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

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

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

    public String getIdentifier() { return "SECONDARY_EVENTS"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String n = cmd.getName();
        if(n.equals("balance")) {
            String q, qq = "", bal = player != null ? formatBigDecimal(BigDecimal.valueOf(eco.getBalance(player))) : "0.00";
            if(player != null && args.length == 0 && hasPermission(sender, "RandomPackage.balance", true)) {
                q = "self";
            } else if(args.length >= 1 && hasPermission(sender, "RandomPackage.balance-other", true) && Bukkit.getOfflinePlayer(args[0]) != null) {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                if(op.equals(player)) {
                    q = "self";
                } else {
                    q = "other";
                    qq = eco.getBalance(player) >= eco.getBalance(op) ? "other" : "self";
                    bal = formatBigDecimal(BigDecimal.valueOf(eco.getBalance(op)));
                }
            } else return true;
            for(String s : getStringList(config, "balance.view " + q)) {
                if(s.contains("{INT}")) s = s.replace("{INT}", bal.contains(".") ? bal.split("\\.")[0] : bal);
                if(s.contains("{DECIMALS}")) s = s.replace("{DECIMALS}", bal.contains(".") ? "." + (bal.split("\\.")[1].length() > 2 ? bal.split("\\.")[1].substring(0, 2) : bal.split("\\.")[1]) : "");
                if(s.equals("{RICHER}") && player != null) s = config.getString("balance.richer than " + qq);
                if(s.contains("{TARGET}")) s = s.replace("{TARGET}", Bukkit.getOfflinePlayer(args[0]).getName());
                if(!s.equals("{RICHER}")) sender.sendMessage(colorize(s));
            }
        } else if(player != null) {
            if(n.equals("bless") && hasPermission(player, "RandomPackage.bless", true)) {
                bless(player);
            } else if(n.equals("bump") && hasPermission(sender, "RandomPackage.bump", true)) {
                player.damage(1.0);
            } else if(n.equals("confirm") && hasPermission(sender, "RandomPackage.confirm", true)) {
                final RPPlayer pdata = RPPlayer.get(args.length == 0 ? player.getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                if(pdata == null) {

                } else {
                    if(pdata.getUnclaimedPurchases().isEmpty()) {
                        sendStringListMessage(player, getStringList(config, "confirm." + (pdata.getOfflinePlayer().equals(player) ? "self " : "other") + "no unclaimed items"), null);
                    } else {
                        confirm(player, pdata);
                    }
                }
            } else if(n.equals("roll") && hasPermission(sender, "RandomPackage.roll", true)) {
                roll(player, args.toString());
            } else if(n.equals("withdraw") && hasPermission(player, "RandomPackage.withdraw", true)) {
                if(args.length == 0) {
                    sendStringListMessage(player, getStringList(config, "withdraw.argument 0"), null);
                } else {
                    BigDecimal amount = BigDecimal.valueOf(getRemainingDouble(args[0]));
                    final double a = amount.doubleValue();
                    String m = null, formattedAmount = formatBigDecimal(amount);
                    formattedAmount = formattedAmount.contains("E") ? formattedAmount.split("E")[0] : formattedAmount;
                    if(eco == null) { player.sendMessage("[RandomPackage] You need an Economy plugin installed and enabled to use this feature!"); return true; }
                    else if(a <= 0.00)                  m = "withdraw.cannot withdraw zero";
                    else if(eco.getBalance(player) < a) m = "withdraw.cannot withdraw more than balance";
                    else if(eco.withdrawPlayer(player, a).transactionSuccess()) {
                        item = givedpitem.getBanknote(amount, player.getName());
                        giveItem(player, item);
                        m = "withdraw.success";
                    } else return true;
                    for(String string : getStringList(config, m)) {
                        if(string.contains("{VALUE}")) string = string.replace("{VALUE}", formattedAmount);
                        if(string.contains("{BALANCE}")) string = string.replace("{BALANCE}", formatDouble(eco.getBalance(player)));
                        player.sendMessage(colorize(string));
                    }
                }
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "secondary.yml");

        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "secondary.yml"));
        confirm = colorize(config.getString("confirm.title"));

        int i = 0;
        banknote = givedpitem.items.get("banknote");
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

        sendConsoleMessage("&6[RandomPackage] &aLoaded Secondary Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
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
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{MAX}", formatInt(maxroll));
            replacements.put("{PLAYER}", player.getName());
            replacements.put("{ROLLED}", formatInt(roll));
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
        final ItemStack c = event.getCurrentItem();
        if(c != null && !c.getType().equals(Material.AIR)) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory top = player.getOpenInventory().getTopInventory();
            final String t = event.getView().getTitle();
            final int r = event.getRawSlot();
            if(t.equals(confirm)) {
                event.setCancelled(true);
                player.updateInventory();
                if(r >= top.getSize()) return;
                giveItem(player, c);
                RPPlayer.get(player.getUniqueId()).removeUnclaimedPurchase(c);
                top.setItem(r, new ItemStack(Material.AIR));
            } else return;
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final Player player = event.getPlayer();
            final String d = is.getItemMeta().getDisplayName(), b = banknote.getItemMeta().getDisplayName();
            if(d.equals(b)) {
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
