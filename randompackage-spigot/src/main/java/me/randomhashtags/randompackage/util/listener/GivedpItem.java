package me.randomhashtags.randompackage.util.listener;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitMastery;
import me.randomhashtags.randompackage.addon.GivedpItemable;
import me.randomhashtags.randompackage.api.CollectionFilter;
import me.randomhashtags.randompackage.event.MysteryMobSpawnerOpenEvent;
import me.randomhashtags.randompackage.event.async.ItemLoreCrystalUseEvent;
import me.randomhashtags.randompackage.event.async.ItemNameTagUseEvent;
import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.mcmmo;

public class GivedpItem extends RPFeature implements CommandExecutor {
    public static final GivedpItem GIVEDP_ITEM = new GivedpItem();

    public YamlConfiguration itemsConfig;
    private HashMap<String, ItemStack> customitems;

    public HashMap<String, ItemStack> items;
    private ItemStack air;
    private List<Player> itemnametag, itemlorecrystal, explosivesnowball;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(hasPermission(sender, "RandomPackage.givedp", true)) {
            if(args.length == 0 && player != null) {
                viewGivedp(player);
            } else if(args.length >= 2) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                final String i = args[1];
                item = createItemStack(null, i);
                if(item != null) {
                    if(args.length >= 3) {
                        item.setAmount(getRemainingInt(args[2]));
                    }
                    if(p.isOnline()) {
                        p.getPlayer().getInventory().addItem(item);
                    } else {
                        final RPPlayer pdata = RPPlayer.get(p.getUniqueId());
                        pdata.getUnclaimedPurchases().add(item);
                        pdata.unload();
                    }
                }
            }
        }
        return true;
    }

    public String getIdentifier() {
        return "GIVEDP_ITEM";
    }
    public void load() {
        save(null, "items.yml");
        itemsConfig = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "items.yml"));
        customitems = new HashMap<>();
        for(String s : getConfigurationSectionKeys(itemsConfig, "custom items", false)) {
            customitems.put(s, createItemStack(itemsConfig, "custom items." + s));
        }

        items = new HashMap<>();
        items.put("banknote", createItemStack(itemsConfig, "banknote"));
        items.put("christmascandy", createItemStack(itemsConfig, "christmas candy"));
        items.put("christmaseggnog", createItemStack(itemsConfig, "christmas eggnog"));
        items.put("commandreward", createItemStack(itemsConfig, "command reward"));
        items.put("explosivecake", createItemStack(itemsConfig, "explosive cake"));
        items.put("explosivesnowball", createItemStack(itemsConfig, "explosive snowball"));
        items.put("halloweencandy", createItemStack(itemsConfig, "halloween candy"));
        items.put("itemlorecrystal", createItemStack(itemsConfig, "item lore crystal"));
        items.put("itemnametag", createItemStack(itemsConfig, "item name tag"));
        items.put("mcmmocreditvoucher", createItemStack(itemsConfig, "mcmmo vouchers.credit"));
        items.put("mcmmolevelvoucher", createItemStack(itemsConfig, "mcmmo vouchers.level"));
        items.put("mcmmoxpvoucher", createItemStack(itemsConfig, "mcmmo vouchers.xp"));
        items.put("mysterymobspawner", createItemStack(itemsConfig, "mystery mob spawner"));
        items.put("spacedrink", createItemStack(itemsConfig, "space drink"));
        items.put("spacefirework", createItemStack(itemsConfig, "space firework"));
        items.put("xpbottle", createItemStack(itemsConfig, "xpbottle"));
        air = new ItemStack(Material.AIR);

        itemnametag = new ArrayList<>();
        itemlorecrystal = new ArrayList<>();
        explosivesnowball = new ArrayList<>();

        if(mcmmo != null) {
            MCMMOAPI.getMCMMOAPI().enable();
        }
    }
    public void unload() {
    }

    public final ItemStack valueOf(@NotNull String input) {
        final String Q = input.split(";")[0];
        input = input.toLowerCase();

        if(customitems != null && customitems.containsKey(Q)) {
            return getClone(customitems.get(Q));
        } else if(items != null && items.containsKey(Q)) {
            return getClone(items.get(Q));
        }

        final HashMap<String, GivedpItemable> givedpitems = GivedpItemable.GIVEDP_ITEMS;
        for(String key : givedpitems.keySet()) {
            if(Q.startsWith(key) || input.startsWith(key)) {
                final ItemStack target = givedpitems.get(key).valueOfInput(Q, key);
                return target != null ? target : air;
            }
        }

        if(input.startsWith("banknote:")) {
            final String[] a = Q.split(":");
            return getBanknote(BigDecimal.valueOf(getIntegerFromString(a[1], 0)), a.length == 3 ? a[2] : null);
        } else if(input.equals("collectionchest")) {
            final CollectionFilter cf = CollectionFilter.getCollectionFilter();
            return cf.isEnabled() ? cf.getCollectionChest("all") : air;

        } else if(input.startsWith("equipmentlootbox:")) {
            return air;

        } else if(input.startsWith("omnigem:")) {
            return air;

        } else if(input.startsWith("inventorypetegghatchingkit:")) {
            return air;

        } else if(input.startsWith("mcmmocreditvoucher") || input.startsWith("mcmmolevelvoucher") || input.startsWith("mcmmoxpvoucher")) {
            if(mcmmoIsEnabled()) {
                final MCMMOAPI mcmmo = MCMMOAPI.getMCMMOAPI();
                if(mcmmo.isEnabled()) {
                    final boolean lvl = input.startsWith("mcmmolevelvoucher"), xp = input.startsWith("mcmmoxpvoucher");
                    final ItemStack i = items.get(lvl ? "mcmmolevelvoucher" : xp ? "mcmmoxpvoucher" : "mcmmocreditvoucher").clone();
                    final String[] values = input.split(":");
                    final String skill = values[1];
                    final boolean r = values[2].contains("-");
                    final int min = r ? Integer.parseInt(values[2].split("-")[0]) : Integer.parseInt(values[2]), amount = r ? min+ RANDOM.nextInt(Integer.parseInt(values[2].split("-")[1])-min+1) : min;
                    final String name = itemsConfig.getString("mcmmo vouchers.skill names." + skill.toLowerCase());
                    final String n = name != null ? colorize(name) : "UNKNOWN";
                    itemMeta = i.getItemMeta();
                    lore.clear();
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{AMOUNT}", Integer.toString(amount)).replace("{SKILL}", n));
                    }
                    itemMeta.setLore(lore); lore.clear();
                    i.setItemMeta(itemMeta);
                    return i;
                }
            }
            return air;
        } else if(input.startsWith("xpbottle:")) {
            final String[] values = Q.split(":");
            final boolean hasHyphen = values[1].contains("-");
            final int min = hasHyphen ? Integer.parseInt(values[1].split("-")[0]) : Integer.parseInt(values[1]), amt = hasHyphen ? min+ RANDOM.nextInt(Integer.parseInt(values[1].split("-")[1])-min+1) : min;
            return getXPBottle(BigDecimal.valueOf(amt), values.length == 3 ? values[2] : null);
        } else if(input.startsWith("mkitredeem:")) {
            final CustomKit kit = getCustomKit("MKIT_" + Q.split(":")[1]);
            return kit instanceof CustomKitMastery ? ((CustomKitMastery) kit).getRedeem() : air;
        }
        return null;
    }

    public ItemStack getBanknote(BigDecimal value, String signer) {
        item = items.get("banknote").clone(); itemMeta = item.getItemMeta(); lore.clear();
        for(String s : itemMeta.getLore()) {
            if(s.contains("{SIGNER}")) s = signer != null ? s.replace("{SIGNER}", signer) : null;
            if(s != null) lore.add(s.replace("{VALUE}", formatBigDecimal(value)));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack getXPBottle(BigDecimal value, String enchanter) {
        item = items.get("xpbottle").clone();
        itemMeta = item.getItemMeta(); lore.clear();
        for(String s : itemMeta.getLore()) {
            if(s.contains("{ENCHANTER}")) s = enchanter != null ? s.replace("{ENCHANTER}", enchanter) : null;
            if(s != null) lore.add(s.replace("{VALUE}", formatBigDecimal(value)));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }


    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && !event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            final Player player = event.getPlayer();
            if(is.isSimilar(items.get("mysterymobspawner"))) {
                event.setCancelled(true);
                final List<String> rewards = getStringList(itemsConfig, "mystery mob spawner.reward"), receivemsg = getStringList(itemsConfig, "mystery mob spawner.receive message");
                final String spawner = rewards.get(RANDOM.nextInt(rewards.size()));
                final MysteryMobSpawnerOpenEvent e = new MysteryMobSpawnerOpenEvent(player, spawner);
                PLUGIN_MANAGER.callEvent(e);
                if(!e.isCancelled()) {
                    removeItem(player, is, 1);
                    final ItemStack r = createItemStack(null, spawner);
                    giveItem(player, r);
                    player.updateInventory();
                    if(!receivemsg.isEmpty()) {
                        final String type = ChatColor.stripColor(r != null && r.hasItemMeta() && r.getItemMeta().hasDisplayName() ? r.getItemMeta().getDisplayName() : "Random Spawner"), playerName = player.getName();
                        for(String a : receivemsg) {
                            Bukkit.broadcastMessage(a.replace("{PLAYER}", playerName).replace("{TYPE}", type));
                        }
                    }
                }
            } else if(is.isSimilar(items.get("itemnametag"))) {
                if(itemnametag.contains(player)) {
                    sendStringListMessage(player, getStringList(itemsConfig, "item name tag.already in rename process"), null);
                } else {
                    itemnametag.add(player);
                    sendStringListMessage(player, getStringList(itemsConfig, "item name tag.enter rename"), null);
                    removeItem(player, is, 1);
                }
            } else if(is.isSimilar(items.get("itemlorecrystal"))) {
                if(itemlorecrystal.contains(player)) {
                    sendStringListMessage(player, getStringList(itemsConfig, "item lore crystal.already in process"), null);
                } else {
                    itemlorecrystal.add(player);
                    sendStringListMessage(player, getStringList(itemsConfig, "item lore crystal.enter addlore"), null);
                    removeItem(player, is, 1);
                }
            } else if(is.isSimilar(items.get("explosivesnowball"))) {
                explosivesnowball.add(player);
            } else if(is.isSimilar(items.get("explosivecake"))) {
                event.setCancelled(true);
                final Location l = event.getAction().name().endsWith("_CLICK_BLOCK") ? event.getClickedBlock().getLocation() : player.getLocation();
                final int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
                removeItem(player, is, 1);
                if(EIGHT || NINE || TEN || ELEVEN || TWELVE) {
                    Bukkit.dispatchCommand(CONSOLE, "execute " + player.getName() + " " + x + " " + y + " " + z + " particle smoke " + x + " " + y + " " + z + " 1 1 1 1 100");
                } else {
                    Bukkit.dispatchCommand(CONSOLE, "execute facing " + x + " " + y + " " + z + " run particle smoke " + x + " " + y + " " + z + " 1 1 1 1 100");
                }
                playSound(itemsConfig, "explosive cake.sounds.placed", player, player.getLocation(), false);
            }
        }
    }

    @EventHandler
    private void projectileHitEvent(ProjectileHitEvent event) {
        final ProjectileSource s = event.getEntity().getShooter();
        if(s instanceof Player && explosivesnowball.contains(s)) {
            final Player player = (Player) s;
            final Location l = player.getLocation();
            explosivesnowball.remove(player);
            final int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute " + player.getName() + " " + x + " " + y + " " + z + " particle smoke " + x + " " + y + " " + z + " 1 1 1 1 100");
            playSound(itemsConfig, "explosive snowball.sounds.placed", null, player.getLocation(), false);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerChatEvent(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String msg = event.getMessage();
        if(itemnametag.contains(player)) {
            itemnametag.remove(player);
            event.setCancelled(true);
            final String message = colorize(msg);
            item = getItemInHand(player);
            if(item == null || item.getType().equals(Material.AIR)) {
                sendStringListMessage(player, getStringList(itemsConfig, "item name tag.cannot rename air"), null);
                giveItem(player, items.get("itemnametag").clone());
            } else if(isEquipment(item)) {
                final ItemNameTagUseEvent e = new ItemNameTagUseEvent(player, item, message);
                PLUGIN_MANAGER.callEvent(e);
                if(!e.isCancelled()) {
                    final String name = e.getMessage();
                    itemMeta = item.getItemMeta(); lore.clear();
                    itemMeta.setDisplayName(name);
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                    playSound(itemsConfig, "item name tag.sounds.rename item", player, player.getLocation(), false);
                    for(String string : getStringList(itemsConfig, "item name tag.rename item")) {
                        player.sendMessage(colorize(string.replace("{NAME}", name)));
                    }
                }
            } else {
                sendStringListMessage(player, getStringList(itemsConfig, "item name tag.cannot rename item"), null);
                playSound(itemsConfig, "item name tag.sounds.cannot rename item", player, player.getLocation(), false);
                giveItem(player, items.get("itemnametag").clone());
            }
        } else if(itemlorecrystal.contains(player)) {
            String apply = colorize(itemsConfig.getString("item lore crystal.apply"));
            item = getItemInHand(player);
            event.setCancelled(true);
            itemlorecrystal.remove(player);
            if(item == null || item.getType().equals(Material.AIR)) {
                sendStringListMessage(player, getStringList(itemsConfig, "item lore crystal.cannot addlore air"), null);
                giveItem(player, items.get("itemlorecrystal").clone());
            } else if(isEquipment(item)) {
                final String message = ChatColor.stripColor(msg);
                final ItemLoreCrystalUseEvent e = new ItemLoreCrystalUseEvent(player, item, apply + message);
                PLUGIN_MANAGER.callEvent(e);
                if(!e.isCancelled()) {
                    itemMeta = item.getItemMeta(); lore.clear();
                    boolean did = false;
                    if(itemMeta.hasLore()) {
                        lore.addAll(itemMeta.getLore());
                        for(int i = 0; i < lore.size(); i++) {
                            if(lore.get(i).startsWith(apply)) {
                                did = true;
                                lore.set(i, apply + message);
                                break;
                            }
                        }
                    }
                    if(!did) {
                        lore.add(apply + message);
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                    sendStringListMessage(player, getStringList(itemsConfig, "item lore crystal.add lore"), new HashMap<String, String>(){{ put("{LORE}", message); }});
                }
            } else {
                sendStringListMessage(player, getStringList(itemsConfig, "item lore crystal.cannot addlore item"), null);
                giveItem(player, items.get("itemlorecrystal").clone());
            }
        }
    }
    private boolean isEquipment(ItemStack is) {
        if(is != null) {
            final String m = is.getType().name();
            return m.endsWith("BOW") || m.endsWith("_AXE") || m.endsWith("SWORD") || m.endsWith("HELMET") || m.endsWith("CHESTPLATE") || m.endsWith("LEGGINGS") || m.endsWith("BOOTS");
        }
        return false;
    }
}
