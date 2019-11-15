package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class SecondaryEvents extends RPFeature implements CommandExecutor {
    private static SecondaryEvents instance;
    public static SecondaryEvents getSecondaryEvents() {
        if(instance == null) instance = new SecondaryEvents();
        return instance;
    }

    public YamlConfiguration config;

    private List<String> combineores;
    private List<PotionEffectType> removedPotionEffects;
    private String confirm;
    private HashMap<Player, String> delayed;

    private ItemStack xpbottle, banknote;
    private int xpbottleValueSlot, banknoteValueSlot;
    private HashMap<String, Integer> minbottles, expexhaustion;
    private HashMap<String, Double> teleportationDelay, teleportMinDelay, teleportationVariable;

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
            for(String s : config.getStringList("balance.view " + q)) {
                if(s.contains("{INT}")) s = s.replace("{INT}", bal.contains(".") ? bal.split("\\.")[0] : bal);
                if(s.contains("{DECIMALS}")) s = s.replace("{DECIMALS}", bal.contains(".") ? "." + (bal.split("\\.")[1].length() > 2 ? bal.split("\\.")[1].substring(0, 2) : bal.split("\\.")[1]) : "");
                if(s.equals("{RICHER}") && player != null) s = config.getString("balance.richer than " + qq);
                if(s.contains("{TARGET}")) s = s.replace("{TARGET}", Bukkit.getOfflinePlayer(args[0]).getName());
                if(!s.equals("{RICHER}")) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else if(player != null) {
            if(n.equals("bless") && hasPermission(player, "RandomPackage.bless", true)) {
                bless(player);
            } else if(n.equals("bump") && hasPermission(sender, "RandomPackage.bump", true)) {
                player.damage(1.0);
            } else if(n.equals("combine") && hasPermission(sender, "RandomPackage.combine", true)) {
                combine(player);
            } else if(n.equals("confirm") && hasPermission(sender, "RandomPackage.confirm", true)) {
                final RPPlayer pdata = RPPlayer.get(args.length == 0 ? player.getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                if(pdata == null) {

                } else {
                    if(pdata.getUnclaimedPurchases().isEmpty()) {
                        sendStringListMessage(player, config.getStringList("confirm." + (pdata.getOfflinePlayer().equals(player) ? "self " : "other") + "no unclaimed items"), null);
                    } else {
                        confirm(player, pdata);
                    }
                }
            } else if(n.equals("roll") && hasPermission(sender, "RandomPackage.roll", true)) {
                roll(player, args.toString());
            } else if(n.equals("withdraw") && hasPermission(player, "RandomPackage.withdraw", true)) {
                if(args.length == 0) {
                    sendStringListMessage(player, config.getStringList("withdraw.argument 0"), null);
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
                    for(String string : config.getStringList(m)) {
                        if(string.contains("{VALUE}")) string = string.replace("{VALUE}", formattedAmount);
                        if(string.contains("{BALANCE}")) string = string.replace("{BALANCE}", formatDouble(eco.getBalance(player)));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                    }
                }
            } else if(n.equals("xpbottle") && hasPermission(sender, "RandomPackage.xpbottle", true)) {
                if(args.length == 0) {
                    sendStringListMessage(player, config.getStringList("xpbottle.argument zero"), null);
                } else {
                    final String a = args[0];
                    final BigDecimal amount = BigDecimal.valueOf(getRemainingInt(a));
                    final int i = amount.intValue();
                    if(i <= 0) {
                        sendStringListMessage(sender, config.getStringList("xpbottle.withdraw at least"), null);
                    } else if(i > getTotalExperience(player)) {
                        sendStringListMessage(player, config.getStringList("xpbottle.not enough to bottle"), null);
                    } else {
                        xpbottle(player, amount);
                    }
                }
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "secondary.yml");

        config = YamlConfiguration.loadConfiguration(new File(rpd, "secondary.yml"));
        xpbottle = givedpitem.items.get("xpbottle");
        int i = 0;
        for(String s : xpbottle.getItemMeta().getLore()) {
            if(s.contains("{VALUE}")) xpbottleValueSlot = i;
            i++;
        }
        minbottles = new HashMap<>();
        for(String s : config.getStringList("xpbottle.min bottle")) {
            final String[] a = s.split("=");
            minbottles.put(a[0], Integer.parseInt(a[1]));
        }
        expexhaustion = new HashMap<>();
        for(String s : config.getStringList("xpbottle.exp exhaustion")) {
            final String[] a = s.split("=");
            expexhaustion.put(a[0], Integer.parseInt(a[1]));
        }
        teleportationDelay = new HashMap<>();
        for(String s : config.getStringList("xpbottle.teleportation delay")) {
            final String[] a = s.split("=");
            teleportationDelay.put(a[0], Double.parseDouble(a[1]));
        }
        teleportMinDelay = new HashMap<>();
        for(String s : config.getStringList("xpbottle.teleport min delay")) {
            final String[] a = s.split("=");
            teleportMinDelay.put(a[0], Double.parseDouble(a[1]));
        }
        teleportationVariable = new HashMap<>();
        for(String s : config.getStringList("xpbottle.teleportation variable")) {
            final String[] a = s.split("=");
            teleportationVariable.put(a[0], Double.parseDouble(a[1]));
        }

        i = 0;
        banknote = givedpitem.items.get("banknote");
        for(String s : banknote.getItemMeta().getLore()) {
            if(s.contains("{VALUE}")) banknoteValueSlot = i;
            i++;
        }

        confirm = ChatColor.translateAlternateColorCodes('&', config.getString("confirm.title"));
        combineores = new ArrayList<>();
        for(String string : config.getStringList("combine.combine ores")) combineores.add(string.toUpperCase());
        delayed = new HashMap<>();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Secondary Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerMoveEvent(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PlayerTeleportDelayEvent tel = PlayerTeleportDelayEvent.teleporting.getOrDefault(player, null);
        if(tel != null) {
            final Location pl = player.getLocation();
            Location L = tel.getFrom();
            if(L.getBlockX() == pl.getBlockX()
                    && L.getBlockY() == pl.getBlockY()
                    && L.getBlockZ() == pl.getBlockZ()) {
                return;
            } else {
                final HashMap<Player, PlayerTeleportDelayEvent> events = PlayerTeleportDelayEvent.teleporting;
                for(String s : config.getStringList("xpbottle.teleport cancelled"))
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                tel.setCancelled(true);
                scheduler.cancelTask(events.get(player).getTask());
                events.remove(player);
            }
        }
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        final PlayerTeleportDelayEvent tel = PlayerTeleportDelayEvent.teleporting.getOrDefault(event.getPlayer(), null);
        if(tel != null) tel.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String m = event.getMessage().toLowerCase();
        for(String s : config.getStringList("xpbottle.delayed commands")) {
            if(m.startsWith(s.toLowerCase())) {
                delayed.put(player, s);
                scheduler.scheduleSyncDelayedTask(randompackage, () -> delayed.remove(player), 1);
                return;
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerTeleportEvent(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if(config.getStringList("xpbottle.teleport causes").contains(event.getCause().name()) && delayed.containsKey(player)) {
            delayed.remove(player);
            final World w = player.getWorld();
            double delay = getTeleportationDelay(w);
            if(hasPermission(player, "RandomPackage.xpbottle.bypass-delay", false) || delay <= 0) return;
            final UUID u = player.getUniqueId();
            final RPPlayer pdata = RPPlayer.get(u);
            if(pdata.isXPExhausted()) {
                final String remaining = getRemainingTime(pdata.xpExhaustionExpiration - System.currentTimeMillis());
                for(String s : config.getStringList("xpbottle.cannot teleport")) {
                    if(s.contains("{TIME}")) s = s.replace("{TIME}", remaining);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            } else {
                final HashMap<Player, PlayerTeleportDelayEvent> events = PlayerTeleportDelayEvent.teleporting;
                final PlayerTeleportDelayEvent previous = events.getOrDefault(player, null);
                final boolean hasPrevious = previous != null;
                final double mindelay = getTeleportMinDelay(w);
                delay -= getTotalExperience(player) / getTeleportationVariable(w);
                delay = round(delay, 3);
                if(delay < mindelay) delay = mindelay;
                if(hasPrevious) {
                    previous.setCancelled(true);
                    scheduler.cancelTask(previous.getTask());
                    events.remove(player);
                }
                final PlayerTeleportDelayEvent e = new PlayerTeleportDelayEvent(player, delay, event.getFrom(), event.getTo());
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    final long de = (long) ((((long) delay * 20)) + (20 * Double.parseDouble("0." + Double.toString(e.getDelay()).split("\\.")[1])));
                    final int t = scheduler.scheduleSyncDelayedTask(getPlugin, () -> {
                        player.teleport(e.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        events.remove(player);
                    }, de);
                    e.setTask(t);
                    events.put(player, e);
                    for(String s : config.getStringList("xpbottle.pending teleport")) {
                        if(s.contains("{SECS}")) s = s.replace("{SECS}", roundDoubleString(e.getDelay(), 3));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                    }
                } else {
                    scheduler.cancelTask(e.getTask());
                    events.remove(player);
                }
            }
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final Player player = event.getPlayer();
            final String d = is.getItemMeta().getDisplayName(), x = xpbottle.getItemMeta().getDisplayName(), b = banknote.getItemMeta().getDisplayName();
            if(d.equals(x)) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final int amount = getRemainingInt(ChatColor.stripColor(is.getItemMeta().getLore().get(xpbottleValueSlot)));
                player.giveExp(amount);
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{VALUE}", formatInt(amount));
                replacements.put("{ENCHANTER}", "Server");
                replacements.put("{PLAYER}", "Server");
                sendStringListMessage(player, config.getStringList("xpbottle.deposit"), replacements);
                playSound(config, "xpbottle.sounds.redeem", player, player.getLocation(), false);
            } else if(d.equals(b)) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final double amount = getRemainingDouble(ChatColor.stripColor(is.getItemMeta().getLore().get(banknoteValueSlot)));
                eco.depositPlayer(player, amount);
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{VALUE}", formatDouble(amount));
                sendStringListMessage(player, config.getStringList("withdraw.deposit"), replacements);
                player.updateInventory();
            }
        }
    }

    public int getMinBottle(World w) {
        return minbottles.getOrDefault(w.getName(), 0);
    }
    public int getExpExhaustion(World w) {
        return expexhaustion.getOrDefault(w.getName(), 0);
    }
    public double getTeleportationDelay(World w) {
        return teleportationDelay.getOrDefault(w.getName(), 0.00);
    }
    public double getTeleportMinDelay(World w) {
        return teleportMinDelay.getOrDefault(w.getName(), 0.00);
    }
    public double getTeleportationVariable(World w) {
        return teleportationVariable.getOrDefault(w.getName(), 0.00);
    }

    public void bless(Player player) {
        final List<String> pe = config.getStringList("bless.removed potion effects");
        if(removedPotionEffects == null) {
            final List<PotionEffectType> t = new ArrayList<>();
            for(String s : pe) {
                final PotionEffectType a = getPotionEffectType(s);
                if(a != null) {
                    t.add(a);
                }
            }
            removedPotionEffects = t;
        }
        for(PotionEffectType T : removedPotionEffects) {
            player.removePotionEffect(T);
        }
        sendStringListMessage(player, config.getStringList("bless.msg"), null);
    }
    public void combine(Player player) {
        final Block tblock = player.getTargetBlock(null, 5);
        Chest chest = null;
        Inventory inventory = player.getInventory();
        if(tblock.getType().equals(Material.CHEST) || tblock.getType().equals(Material.TRAPPED_CHEST)) {
            chest = (Chest) tblock.getState();
            inventory = chest.getBlockInventory();
        }
        final String f = config.getString("combine.format");
        for(String string : config.getStringList("combine.success")) {
            if(string.equals("{SUCCESS}")) {
                for(int i = 0; i < combineores.size(); i++) {
                    int amount = 0, amountb = 0;
                    Material material = Material.valueOf(combineores.get(i).toUpperCase()), block = !material.name().replace("INGOT", "BLOCK").endsWith("BLOCK") ? Material.valueOf(material.name() + "_BLOCK") : Material.valueOf(material.name().replace("INGOT", "BLOCK"));
                    amount = (getTotalAmount(inventory, UMaterial.match(material.name())) / 9) * 9;
                    if(amount != 0) {
                        amountb = amount / 9;
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', f.replace("{AMOUNT_ITEM}", "" + amount).replace("{ITEM_ORE}", material.name()).replace("{AMOUNT_BLOCK}", "" + amountb).replace("{ITEM_BLOCK}", material.name().replace("ORE", "BLOCK"))));
                        for(int z = 1; z <= amount; z++) inventory.removeItem(new ItemStack(material, 1, (byte) 0));
                        inventory.addItem(new ItemStack(block, amountb));
                        if(chest != null) chest.update(); else player.updateInventory();
                    }
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
            }
        }
    }
    private void confirm(Player opener, RPPlayer target) {
        final List<ItemStack> u = target.getUnclaimedPurchases();
        final int size = ((u.size()+9)/9)*9;
        opener.openInventory(Bukkit.createInventory(opener, size, confirm));
        final Inventory top = opener.getOpenInventory().getTopInventory();
        for(ItemStack is : target.getUnclaimedPurchases()) {
            top.setItem(top.firstEmpty(), is);
        }
        opener.updateInventory();
    }
    public void roll(Player player, String arg0) {
        final int radius = config.getInt("roll.block radius"), maxroll = getRemainingInt(arg0), roll = random.nextInt(maxroll);
        final List<Entity> nearby = player.getNearbyEntities(radius, radius, radius);
        final List<Player> p = new ArrayList<>();
        for(Entity entity : nearby) {
            if(entity instanceof Player) {
                p.add((Player) entity);
            }
        }
        if(!p.isEmpty()) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{MAX}", formatInt(maxroll));
            replacements.put("{PLAYER}", player.getName());
            replacements.put("{ROLLED}", formatInt(roll));
            final List<String> msg = config.getStringList("roll.message");
            for(Player P : p) sendStringListMessage(P, msg, replacements);
            sendStringListMessage(player, msg, replacements);
        } else {
            sendStringListMessage(player, config.getStringList("roll.nobody heard roll"), null);
        }
    }
    public void xpbottle(Player player, BigDecimal amount) {
        final int minbottle = getMinBottle(player.getWorld());
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{MIN}", Integer.toString(minbottle));
        replacements.put("{VALUE}", formatBigDecimal(amount));
        final int d = amount.intValue();
        if(d < minbottle) {
            sendStringListMessage(player, config.getStringList("xpbottle.withdraw at least"), replacements);
        } else {
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            if(pdata.isXPExhausted() && !hasPermission(player, "RandomPackage.xpbottle.bypass-exhaustion", false)) {
                final String remaining = getRemainingTime(pdata.xpExhaustionExpiration - System.currentTimeMillis());
                replacements.put("{TIME}", remaining);
                sendStringListMessage(player, config.getStringList("xpbottle.cannot xpbottle"), replacements);
            } else {
                sendStringListMessage(player, config.getStringList("xpbottle.withdraw"), replacements);
                giveItem(player, givedpitem.getXPBottle(amount, player.getName()));

                int xp = Math.round(getTotalExperience(player));
                setTotalExperience(player, xp-d);
                playSound(config, "xpbottle.sounds.withdraw", player, player.getLocation(), false);
                final int exh = getExpExhaustion(player.getWorld());
                if(exh != -1) {
                    pdata.xpExhaustionExpiration = System.currentTimeMillis() + (exh * 1000 * 60);
                    replacements.put("{MIN}", Integer.toString(exh));
                    sendStringListMessage(player, config.getStringList("xpbottle.afflict"), replacements);
                }
            }
        }
    }
}
