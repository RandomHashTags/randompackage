package me.randomhashtags.randompackage.util.listener;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.obj.RandomizedLootItem;
import me.randomhashtags.randompackage.api.RandomizedLoot;
import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.addon.Scrolls;
import me.randomhashtags.randompackage.api.dev.InventoryPets;
import me.randomhashtags.randompackage.api.ItemSkins;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.MysteryMobSpawnerOpenEvent;
import me.randomhashtags.randompackage.event.async.ItemLoreCrystalUseEvent;
import me.randomhashtags.randompackage.event.async.ItemNameTagUseEvent;
import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.Collection;
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
                item = d(null, i);
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
            customitems.put(s, d(itemsConfig, "custom items." + s));
        }

        items = new HashMap<>();
        items.put("banknote", d(itemsConfig, "banknote"));
        items.put("christmascandy", d(itemsConfig, "christmas candy"));
        items.put("christmaseggnog", d(itemsConfig, "christmas eggnog"));
        items.put("commandreward", d(itemsConfig, "command reward"));
        items.put("explosivecake", d(itemsConfig, "explosive cake"));
        items.put("explosivesnowball", d(itemsConfig, "explosive snowball"));
        items.put("halloweencandy", d(itemsConfig, "halloween candy"));
        items.put("itemlorecrystal", d(itemsConfig, "item lore crystal"));
        items.put("itemnametag", d(itemsConfig, "item name tag"));
        items.put("mcmmocreditvoucher", d(itemsConfig, "mcmmo vouchers.credit"));
        items.put("mcmmolevelvoucher", d(itemsConfig, "mcmmo vouchers.level"));
        items.put("mcmmoxpvoucher", d(itemsConfig, "mcmmo vouchers.xp"));
        items.put("mysterymobspawner", d(itemsConfig, "mystery mob spawner"));
        items.put("spacedrink", d(itemsConfig, "space drink"));
        items.put("spacefirework", d(itemsConfig, "space firework"));
        items.put("xpbottle", d(itemsConfig, "xpbottle"));
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

    private int getInteger(String input, int minimum) {
        final boolean m = input.contains("-");
        final String[] values = input.split("-");
        final int min = m ? Integer.parseInt(values[0]) : minimum;
        return m ? min+RANDOM.nextInt(Integer.parseInt(values[1])-min+1) : Integer.parseInt(input);
    }

    public final ItemStack valueOf(String input) {
        final String Q = input.split(";")[0];
        input = input.toLowerCase();
        if(input.startsWith("banknote:")) {
            final String[] a = Q.split(":");
            return getBanknote(BigDecimal.valueOf(getInteger(a[1], 0)), a.length == 3 ? a[2] : null);
        } else if(input.startsWith("blackscroll:")) {
            final String[] a = Q.split(":");
            final BlackScroll b = getBlackScroll(a[1]);
            int amount = 0;
            if(b != null) {
                amount = a.length == 3 ? getInteger(a[2], b.getMinPercent()) : b.getRandomPercent(RANDOM);
            }
            return b != null ? b.getItem(amount) : air;
        } else if(input.equals("collectionchest")) {
            final CollectionFilter cf = CollectionFilter.getCollectionFilter();
            return cf.isEnabled() ? cf.getCollectionChest("all") : air;
        } else if(input.startsWith("customarmor:")) {
            if(CustomArmor.getCustomArmor().isEnabled()) {
                final String[] b = Q.split(":");
                final ArmorSet s = getArmorSet(b[1]);
                if(s != null) {
                    String type = b.length == 2 ? "random" : b[2];
                    final int R = RANDOM.nextInt(4);
                    type = type.equals("random") ? R == 0 ? "helmet" : R == 1 ? "chestplate" : R == 2 ? "leggings" : R == 3 ? "boots" : null : type;
                    final ArmorSetWeaponInfo weapon = s.getWeapon(type);
                    item = type != null ? type.equals("helmet") ? s.getHelmet() : type.equals("chestplate") ? s.getChestplate() : type.equals("leggings") ? s.getLeggings() : type.equals("boots") ? s.getBoots() : weapon != null ? weapon.getItem() : air : air;
                    if(item != air) {
                        itemMeta = item.getItemMeta(); lore.clear();
                        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                        lore.addAll(s.getArmorLore());
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                    }
                    return item;
                }
                return air;
            }
            return air;
        } else if(input.startsWith("customarmorcrystal:")) {
            final CustomArmor ca = CustomArmor.getCustomArmor();
            if(ca.isEnabled()) {
                final String[] values = Q.split(":");
                final ArmorSet a = getArmorSet(values[1]);
                if(a != null) {
                    final int percent = values.length >= 3 && !values[2].equals("random") ? Integer.parseInt(values[2]) : RANDOM.nextInt(101);
                    return ca.getCrystal(a, percent);
                }
            }
            return air;
        } else if(input.startsWith("multicustomarmorcrystal:")) {
            return air;

        } else if(input.startsWith("customboss:")) {
            final CustomBosses bosses = CustomBosses.getCustomBosses();
            if(bosses.isEnabled()) {
                final CustomBoss b = getCustomBoss(Q.split(":")[1]);
                return b != null ? b.getSpawnItem() : air;
            }
            return air;
        } else if(input.startsWith("customexplosion:")) {
            final CustomExplosion e = getCustomExplosion(Q.split(":")[1]);
            return e != null ? e.getItem() : air;
        } else if(input.startsWith("customenchant:") || input.startsWith("ce:")) {
            final CustomEnchants enchants = CustomEnchants.getCustomEnchants();
            if(enchants.isEnabled()) {
                final ItemStack target = enchants.getRevealedItemFromString(Q);
                return target == null ? air : target;
            }
            return air;
        } else if(input.startsWith("dust:")) {
            final String[] a = Q.split(":");
            final MagicDust d = getMagicDust(a[1]);
            final int percent = a.length >= 3 ? Integer.parseInt(a[2]) : -1;
            return d != null ? percent == -1 ? d.getRandomPercentItem(RANDOM) : d.getItem(percent) : air;
        } else if(input.startsWith("enchantmentorb:")) {
            final String[] values = Q.split(":");
            String p = values[1], percent = values.length == 3 ? values[2] : Integer.toString(RANDOM.nextInt(101));
            EnchantmentOrb o = getEnchantmentOrb(p);
            if(o == null) {
                final List<EnchantmentOrb> e = new ArrayList<>();
                for(String s : getAllEnchantmentOrbs().keySet()) {
                    if(s.startsWith(p)) {
                        e.add(getEnchantmentOrb(s));
                    }
                }
                o = !e.isEmpty() ? e.get(RANDOM.nextInt(e.size())) : null;
            }
            final boolean h = percent.contains("-");
            final int min = h ? Integer.parseInt(percent.split("-")[0]) : Integer.parseInt(percent), P = h ? min+RANDOM.nextInt(Integer.parseInt(percent.split("-")[1])-min+1) : min;
            return o != null ? o.getItem(P) : air;

        } else if(input.startsWith("equipmentlootbox:")) {
            return air;

        } else if(input.startsWith("booster:")) {
            final Boosters boosters = Boosters.getBoosters();
            if(boosters.isEnabled()) {
                final String[] a = Q.split(":");
                return getBooster(a[1]).getItem(Long.parseLong(a[3])*1000, Double.parseDouble(a[2]));
            }
            return air;

        } else if(input.startsWith("omnigem:")) {
            return air;
        } else if(input.startsWith("fallenherogem")) {
            final String type = Q.contains(":") ? Q.split(":")[1] : null;
            CustomKit k = type != null ? getCustomKit(type) : null;
            final Collection<CustomKit> kits = getAllCustomKits().values();
            if(type != null && k == null) {
                final List<CustomKit> list = new ArrayList<>();
                for(CustomKit kk : kits) {
                    if(kk.getIdentifier().startsWith(type)) {
                        list.add(kk);
                    }
                }
                final int s = list.size();
                if(s > 0) k = list.get(RANDOM.nextInt(s));
            }
            if(k == null) k = (CustomKit) kits.toArray()[RANDOM.nextInt(kits.size())];
            final FallenHero f = k != null ? k.getFallenHero() : null;
            return f != null ? k.getFallenHeroItem(k, false) : air;
        } else if(input.startsWith("fallenhero")) {
            final String type = Q.contains(":") ? Q.split(":")[1] : null;
            CustomKit k = type != null ? getCustomKit(type) : null;
            final Collection<CustomKit> kits = getAllCustomKits().values();
            if(type != null && k == null) {
                final List<CustomKit> list = new ArrayList<>();
                for(CustomKit kk : kits) {
                    if(kk.getIdentifier().startsWith(type)) {
                        list.add(kk);
                    }
                }
                final int s = list.size();
                if(s > 0) k = list.get(RANDOM.nextInt(s));
            }
            if(k == null) k = (CustomKit) kits.toArray()[RANDOM.nextInt(kits.size())];
            final FallenHero f = k != null ? k.getFallenHero() : null;
            return f != null ? k.getFallenHeroItem(k, true) : air;

        } else if(input.startsWith("fatbucket:")) {
            final String[] values = Q.split(":");
            final FatBucket fb = getFatBucket(values[1]);
            return fb != null ? values.length > 2 ? fb.getItem(Integer.parseInt(values[1])) : fb.getItem(0) : air;
        } else if(input.startsWith("inventorypet:") || input.startsWith("pet:")) {
            final InventoryPets pets = InventoryPets.getInventoryPets();
            if(pets.isEnabled()) {
                final InventoryPet pet = getInventoryPet(Q.split(":")[1]);
                return pet != null ? pet.getItem(1) : air;
            }
            return air;
        } else if(input.startsWith("inventorypetegg:") || input.startsWith("petegg:")) {
            final InventoryPets pets = InventoryPets.getInventoryPets();
            if(pets.isEnabled()) {
                final InventoryPet pet = getInventoryPet(Q.split(":")[1]);
                return pet != null ? pet.getEgg() : air;
            }
            return air;
        } else if(input.startsWith("inventorypetegghatchingkit:")) {
            return air;

        } else if(input.startsWith("itemskin:")) {
            final ItemSkins skins = ItemSkins.getItemSkins();
            if(skins.isEnabled()) {
                final ItemSkin skin = getItemSkin(Q.split(":")[1]);
                return skin != null ? skins.getItemSkinItem(skin, true) : air;
            }
            return air;
        } else if(input.startsWith("lootbox:")) {
            final Lootbox l = getLootbox(Q.split(":")[1]);
            return l != null ? l.getItem() : air;
        } else if(input.startsWith("mask:") && !input.equals("maskgenerator")) {
            final Mask m = getMask(Q.split(":")[1]);
            return m != null ? m.getItem() : air;
        } else if(input.startsWith("multimask:")) {
            return air;
        } else if(input.startsWith("mcmmocreditvoucher") || input.startsWith("mcmmolevelvoucher") || input.startsWith("mcmmoxpvoucher")) {
            if(mcmmoIsEnabled()) {
                final MCMMOAPI mcmmo = MCMMOAPI.getMCMMOAPI();
                if(mcmmo.isEnabled()) {
                    final boolean lvl = input.startsWith("mcmmolevelvoucher"), xp = input.startsWith("mcmmoxpvoucher");
                    final ItemStack i = items.get(lvl ? "mcmmolevelvoucher" : xp ? "mcmmoxpvoucher" : "mcmmocreditvoucher").clone();
                    final String[] a = input.split(":");
                    final String skill = a[1];
                    final boolean r = a[2].contains("-");
                    final int min = r ? Integer.parseInt(a[2].split("-")[0]) : Integer.parseInt(a[2]), amount = r ? min+ RANDOM.nextInt(Integer.parseInt(a[2].split("-")[1])-min+1) : min;
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
        } else if(input.startsWith("monthlycrate:")) {
            final MonthlyCrates crates = MonthlyCrates.getMonthlyCrates();
            if(crates.isEnabled()) {
                final MonthlyCrate m = getMonthlyCrate(Q.split(":")[1]);
                return m != null ? m.getItem() : air;
            }
            return air;
        } else if(input.startsWith("randomizationscroll:")) {
            final RandomizationScroll r = getRandomizationScroll(Q.split(":")[1]);
            return r != null ? r.getItem() : air;
        } else if(input.startsWith("raritybook:")) {
            final EnchantRarity r = getCustomEnchantRarity(Q.split(":")[1]);
            return r != null ? r.getRevealItem() : air;
        } else if(input.startsWith("rarityfireball:")) {
            final RarityFireball f = getRarityFireball(Q.split(":")[1]);
            return f != null ? f.getItem() : air;
        } else if(input.startsWith("raritygem")) {
            final RarityGem g = getRarityGem(Q.split(":")[1]);
            final String three = input.split(":").length == 3 ? input.split(":")[2] : null;
            final int min = three != null ? Integer.parseInt(three.contains("-") ? three.split("-")[0] : three) : 0;
            final int amount = three != null && three.contains("-") ? min+ RANDOM.nextInt(Integer.parseInt(three.split("-")[1])-min+1) : min;
            return g != null ? g.getItem(amount) : air;
        } else if(input.startsWith("servercrate:") || input.startsWith("spacecrate:") || input.startsWith("spacechest:")) {
            final ServerCrate s = getServerCrate(Q.split(":")[1]);
            return s != null ? s.getItem() : air;
        } else if(input.startsWith("servercrateflare:") || input.startsWith("serverflare:") || input.startsWith("spaceflare:")) {
            final ServerCrate c = getServerCrate(Q.split(":")[1]);
            return c != null ? c.getFlare().getItem() : air;
        } else if(input.startsWith("slotbotticket")) {
            final SlotBot bot = SlotBot.getSlotBot();
            return bot.isEnabled() ? getClone(bot.ticket) : air;
        } else if(input.startsWith("soultracker:")) {
            final SoulTracker s = getSoulTracker(Q.split(":")[1]);
            return s != null ? s.getItem() : air;
        } else if(input.startsWith("title")) {
            if(Titles.getTitles().isEnabled()) {
                Title t = getTitle(Q.contains(":") ? Q.split(":")[1] : "random");
                if(t == null) {
                    try {
                        t = getTitle((String) getAll(Feature.TITLE).keySet().toArray()[getRemainingInt(Q.contains(":") ? Q.split(":")[1] : Q)-1]);
                    } catch(Exception e) {
                        System.out.println("[RandomPackage] That title doesn't exist!");
                    }
                }
                return t != null ? t.getItem() : air;
            }
            return air;
        } else if(input.startsWith("transmogscroll")) {
            final Scrolls scrolls = Scrolls.getScrolls();
            if(scrolls.isEnabled() && scrolls.isEnabled(Feature.SCROLL_TRANSMOG)) {
                TransmogScroll t = getTransmogScroll(Q.contains(":") ? Q.split(":")[1] : "REGULAR");
                if(t == null) {
                }
                return t != null ? t.getItem() : air;
            }
            return air;
        } else if(input.startsWith("trinket")) {
            if(Trinkets.getTrinkets().isEnabled()) {
                Trinket t = getTrinket(Q.contains(":") ? Q.split(":")[1] : "random");
                if(t == null) {
                }
                return t != null ? t.getItem() : air;
            }
            return air;
        } else if(input.startsWith("whitescroll")) {
            final Scrolls scrolls = Scrolls.getScrolls();
            if(scrolls.isEnabled() && scrolls.isEnabled(Feature.SCROLL_WHITE)) {
                WhiteScroll w = getWhiteScroll(Q.contains(":") ? Q.split(":")[1] : "REGULAR");
                if(w == null) {
                }
                return w != null ? w.getItem() : air;
            }
            return air;
        } else if(input.startsWith("xpbottle:")) {
            final String[] a = Q.split(":");
            final boolean r = a[1].contains("-");
            final int min = r ? Integer.parseInt(a[1].split("-")[0]) : Integer.parseInt(a[1]), amt = r ? min+ RANDOM.nextInt(Integer.parseInt(a[1].split("-")[1])-min+1) : min;
            return getXPBottle(BigDecimal.valueOf(amt), a.length == 3 ? a[2] : null);
        } else if(input.startsWith("mkitredeem:")) {
            final CustomKit kit = getCustomKit("MKIT_" + Q.split(":")[1]);
            return kit instanceof CustomKitMastery ? ((CustomKitMastery) kit).getRedeem() : air;
        } else if(customitems != null && customitems.containsKey(Q)) {
            return getClone(customitems.get(Q));
        } else if(items != null && items.containsKey(input)) {
            return getClone(items.get(input));
        } else {
            final RandomizedLoot r = RandomizedLoot.getRandomizedLoot();
            final HashMap<String, RandomizedLootItem> items = r.isEnabled() ? r.items : null;
            return items != null && items.containsKey(Q) ? items.get(Q).getItem() : null;
        }
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
                    final ItemStack r = d(null, spawner);
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
