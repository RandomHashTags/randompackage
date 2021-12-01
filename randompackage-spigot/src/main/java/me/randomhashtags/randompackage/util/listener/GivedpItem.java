package me.randomhashtags.randompackage.util.listener;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitMastery;
import me.randomhashtags.randompackage.addon.GivedpItemableSpigot;
import me.randomhashtags.randompackage.api.CollectionFilter;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.event.MysteryMobSpawnerOpenEvent;
import me.randomhashtags.randompackage.event.async.ItemLoreCrystalUseEvent;
import me.randomhashtags.randompackage.event.async.ItemNameTagUseEvent;
import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.MCMMO;

public final class GivedpItem extends RPFeatureSpigot implements CommandExecutor {
    public static final GivedpItem INSTANCE = new GivedpItem();

    public YamlConfiguration itemsConfig;
    private HashMap<String, ItemStack> customitems;

    public HashMap<String, ItemStack> items;
    private ItemStack air;
    private List<Player> itemnametag, itemlorecrystal, explosivesnowball;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(hasPermission(sender, "RandomPackage.givedp", true)) {
            if(args.length == 0 && player != null) {
                viewGivedp(player);
            } else if(args.length >= 2) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                final String i = args[1];
                final ItemStack item = createItemStack(null, i);
                if(item != null) {
                    if(args.length >= 3) {
                        item.setAmount(getRemainingInt(args[2]));
                    }
                    if(p.isOnline()) {
                        p.getPlayer().getInventory().addItem(item);
                    } else {
                        final FileRPPlayer pdata = FileRPPlayer.get(p.getUniqueId());
                        pdata.getUnclaimedPurchases().add(item);
                        pdata.unload();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getIdentifier() {
        return "GIVEDP_ITEM";
    }
    @Override
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

        if(MCMMO != null) {
            MCMMOAPI.INSTANCE.enable();
        }
    }
    @Override
    public void unload() {
    }

    public final ItemStack valueOfRPItem(@NotNull String input) {
        final String targetString = input.split(";")[0];
        input = input.toLowerCase();

        if(customitems != null && customitems.containsKey(targetString)) {
            return getClone(customitems.get(targetString));
        } else if(items != null && items.containsKey(targetString)) {
            return getClone(items.get(targetString));
        }

        final HashMap<String, GivedpItemableSpigot> givedpitems = GivedpItemableSpigot.GIVEDP_ITEMS;
        for(String key : givedpitems.keySet()) {
            if(targetString.startsWith(key) || input.startsWith(key)) {
                final ItemStack target = givedpitems.get(key).valueOfInput(targetString, key);
                return target != null ? target : air;
            }
        }

        if(input.startsWith("banknote:")) {
            final String[] values = targetString.split(":");
            return getBanknote(BigDecimal.valueOf(getIntegerFromString(values[1], 0)), values.length == 3 ? values[2] : null);
        } else if(input.equals("collectionchest")) {
            final CollectionFilter cf = CollectionFilter.INSTANCE;
            return cf.isEnabled() ? cf.getCollectionChest("all") : air;

        } else if(input.startsWith("equipmentlootbox:")) {
            return air;

        } else if(input.startsWith("omnigem:")) {
            return air;

        } else if(input.startsWith("inventorypetegghatchingkit:")) {
            return air;

        } else if(input.startsWith("mcmmocreditvoucher") || input.startsWith("mcmmolevelvoucher") || input.startsWith("mcmmoxpvoucher")) {
            if(RPFeatureSpigot.mcmmoIsEnabled()) {
                final MCMMOAPI mcmmo = MCMMOAPI.INSTANCE;
                if(mcmmo.isEnabled()) {
                    final boolean isLevelVoucher = input.startsWith("mcmmolevelvoucher"), isXPVoucher = input.startsWith("mcmmoxpvoucher");
                    final ItemStack item = items.get("mcmmo" + (isLevelVoucher ? "level" : isXPVoucher ? "xp" : "credit") + "voucher").clone();
                    final String[] values = input.split(":");
                    final String skill = values[1];
                    final String amountString = values.length == 2 ? "1" : values[2];
                    final boolean isRandom = amountString.contains("-");
                    final int min = Integer.parseInt(isRandom ? amountString.split("-")[0] : amountString), amount = isRandom ? min+ RANDOM.nextInt(Integer.parseInt(amountString.split("-")[1])-min+1) : min;
                    final String name = itemsConfig.getString("mcmmo vouchers.skill names." + skill.toLowerCase());
                    final String skillName = name != null ? colorize(name) : "UNKNOWN";
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    for(String string : itemMeta.getLore()) {
                        lore.add(string.replace("{AMOUNT}", Integer.toString(amount)).replace("{SKILL}", skillName));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    return item;
                }
            }
            return air;
        } else if(input.startsWith("xpbottle:")) {
            final String[] values = targetString.split(":");
            final boolean hasHyphen = values[1].contains("-");
            final int min = hasHyphen ? Integer.parseInt(values[1].split("-")[0]) : Integer.parseInt(values[1]), amt = hasHyphen ? min+ RANDOM.nextInt(Integer.parseInt(values[1].split("-")[1])-min+1) : min;
            return getXPBottle(BigDecimal.valueOf(amt), values.length == 3 ? values[2] : null);
        } else if(input.startsWith("mkitredeem:")) {
            final CustomKit kit = getCustomKit("MKIT_" + targetString.split(":")[1]);
            return kit instanceof CustomKitMastery ? ((CustomKitMastery) kit).getRedeem() : air;
        }
        return null;
    }

    public ItemStack getBanknote(BigDecimal value, String signer) {
        final ItemStack item = items.get("banknote").clone();
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
    public ItemStack getXPBottle(BigDecimal value, String enchanter) {
        final ItemStack item = items.get("xpbottle").clone();
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String string : itemMeta.getLore()) {
            if(string.contains("{ENCHANTER}")) {
                string = enchanter != null ? string.replace("{ENCHANTER}", enchanter) : null;
            }
            if(string != null) {
                lore.add(string.replace("{VALUE}", formatBigDecimal(value)));
            }
        }
        itemMeta.setLore(lore);
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
            final ItemStack item = getItemInHand(player);
            if(item == null || item.getType().equals(Material.AIR)) {
                sendStringListMessage(player, getStringList(itemsConfig, "item name tag.cannot rename air"), null);
                giveItem(player, items.get("itemnametag").clone());
            } else if(isEquipment(item)) {
                final ItemNameTagUseEvent useEvent = new ItemNameTagUseEvent(player, item, message);
                PLUGIN_MANAGER.callEvent(useEvent);
                if(!useEvent.isCancelled()) {
                    final String name = useEvent.getMessage();
                    final ItemMeta itemMeta = item.getItemMeta();
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
            final ItemStack item = getItemInHand(player);
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
                    final ItemMeta itemMeta = item.getItemMeta();
                    boolean did = false;
                    final List<String> lore = new ArrayList<>();
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
                    itemMeta.setLore(lore);
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
            final String materialName = is.getType().name();
            return materialName.endsWith("BOW") || materialName.endsWith("_AXE") || materialName.endsWith("SWORD") || materialName.endsWith("HELMET") || materialName.endsWith("CHESTPLATE") || materialName.endsWith("LEGGINGS") || materialName.endsWith("BOOTS");
        }
        return false;
    }
}
