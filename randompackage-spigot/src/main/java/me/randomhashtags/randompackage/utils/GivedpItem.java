package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.CollectionFilter;
import me.randomhashtags.randompackage.api.events.customenchant.ItemNameTagUseEvent;
import me.randomhashtags.randompackage.api.events.customenchant.MysteryMobSpawnerOpenEvent;
import me.randomhashtags.randompackage.api.nearFinished.Boosters;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileArmorSet;
import me.randomhashtags.randompackage.recode.api.addons.EnchantRarity;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileEnchantRarity;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileTitle;
import me.randomhashtags.randompackage.utils.classes.customenchants.*;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileServerCrate;
import me.randomhashtags.randompackage.utils.supported.MCMMOAPI;
import me.randomhashtags.randompackage.utils.supported.plugins.MCMMOOverhaul;
import me.randomhashtags.randompackage.utils.supported.plugins.MCMMOClassic;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GivedpItem extends RPFeature implements CommandExecutor {
    public static final GivedpItem givedpitem = new GivedpItem();

    public YamlConfiguration itemsConfig;
    public HashMap<String, ItemStack> customitems;

    public HashMap<String, ItemStack> items;
    private ItemStack air;
    private List<Player> itemnametag, itemlorecrystal, explosivesnowball;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(hasPermission(sender, "RandomPackage.givedp", true)) {
            if(args.length == 0 && player != null)
                viewGivedp(player);
            else if(args.length >= 2) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                final String i = args[1];
                if(i.startsWith("gkit:") || i.startsWith("vkit:")) {
                    final String kit = i.split(":")[1];
                    final int tier = Integer.parseInt(i.split(":")[2]);
                    final Player pl = p.getPlayer();
                    final GlobalKit gkit = GlobalKit.kits.getOrDefault(kit, null);
                    final EvolutionKit vkit = gkit == null ? EvolutionKit.kits.getOrDefault(kit, null) : null;
                    final Kits kits = Kits.getKits();
                    if(gkit != null || vkit != null) {
                        kits.give(pl, gkit != null ? gkit : vkit, tier, false, false);
                    }
                } else {
                    item = d(null, i);
                    if(item != null) {
                        if(args.length >= 3) item.setAmount(getRemainingInt(args[2]));
                        if(p.isOnline()) {
                            p.getPlayer().getInventory().addItem(item);
                        } else {
                            final RPPlayer pdata = RPPlayer.get(p.getUniqueId());
                            pdata.load();
                            pdata.getUnclaimedPurchases().add(item);
                            pdata.unload();
                        }
                    }
                }
            }
        }
        return true;
    }
    public void load() {
        save(null, "items.yml");
        itemsConfig = YamlConfiguration.loadConfiguration(new File(rpd, "items.yml"));
        customitems = new HashMap<>();
        final ConfigurationSection cs = itemsConfig.getConfigurationSection("custom items");
        if(cs != null) {
            for(String s : cs.getKeys(false)) {
                customitems.put(s, d(itemsConfig, "custom items." + s));
            }
        }
        items = new HashMap<>();
        items.put("banknote", d(itemsConfig, "banknote"));
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
    }
    public void unload() {
        itemsConfig = null;
        items = null;
        customitems = null;
        air = null;
    }

    private int getInt(String input, int max) {
        input = input.toLowerCase();
        return input.equalsIgnoreCase("random") ? 1+random.nextInt(max) : Integer.parseInt(input);
    }
    private int getInt(String input) {
        input = input.toLowerCase();
        return input.equalsIgnoreCase("random") ? random.nextInt(101) : Integer.parseInt(input);
    }

    public ItemStack valueOf(String input) {
        final String Q = input.split(";")[0];
        input = input.toLowerCase();
        if(input.startsWith("banknote:")) {
            final String[] a = Q.split(":");
            return getBanknote(Integer.parseInt(a[1]), a.length == 3 ? a[2] : null);
        } else if(input.startsWith("blackscroll:")) {
            final String[] a = Q.split(":");
            final BlackScroll b = BlackScroll.scrolls != null ? BlackScroll.scrolls.getOrDefault(a[1], null) : null;
            int amount = 0;
            if(b != null) {
                if(a.length == 3) {
                    final String A = a[2];
                    final boolean m = A.contains("-");
                    final int min = m ? Integer.parseInt(A.split("-")[0]) : b.getMinPercent();
                    amount = m ? min+random.nextInt(Integer.parseInt(A.split("-")[1])-min+1) : Integer.parseInt(A);
                } else {
                    amount = b.getRandomPercent();
                }
            }
            return b != null ? b.getItem(amount) : air;
        } else if(input.equals("collectionchest")) {
            final CollectionFilter cf = CollectionFilter.getCollectionFilter();
            return cf.isEnabled() ? cf.getCollectionChest("all") : air;
        } else if(input.startsWith("customarmor:")) {
            if(CustomArmor.getCustomArmor().isEnabled) {
                final String[] b = input.split(":");
                String type = b.length == 2 ? "random" : b[2];
                final HashMap<String, FileArmorSet> L = FileArmorSet.sets;
                final FileArmorSet a = L != null ? L.get(Q.split(":")[1]) : null;
                if(a != null) {
                    final int R = random.nextInt(4);
                    type = type.equals("random") ? R == 0 ? "helmet" : R == 1 ? "chestplate" : R == 2 ? "leggings" : R == 3 ? "boots" : null : type;
                }
                item = a != null && type != null ? type.equals("helmet") ? a.getHelmet() : type.equals("chestplate") ? a.getChestplate() : type.equals("leggings") ? a.getLeggings() : a.getBoots() : null;
                if(item != null) {
                    itemMeta = item.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                    lore.addAll(a.getArmorLore());
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
                return item;
            }
            return air;
        } else if(input.startsWith("customboss")) {
            final String[] k = Q.split(":");
            final int l = k.length;
            final String plugin = l == 3 ? k[1] : null, key = l >= 2 ? l == 2 ? k[1] : k[2] : null;
            final AbstractCustomBoss b = getCustomBoss(plugin, key);
            return b != null ? b.getSpawnItem() : air;
        } else if(input.startsWith("customexplosion")) {
            final String[] k = Q.split(":");
            final int l = k.length;
            final String plugin = l == 3 ? k[1] : null, key = l >= 2 ? l == 2 ? k[1] : k[2] : null;
            final AbstractCustomExplosion e = getCustomExplosion(plugin, key);
            return e != null ? e.getItem() : air;
        } else if(input.startsWith("customenchant:") || input.startsWith("ce:")) {
            // ce:<enchant>:<level>:<success>:<destroy>
            // ce:<plugin>:<enchant>:<level>:<success>:<destroy>
            final String[] k = Q.split(":");
            final int l = k.length;
            if(l < 5) return air;
            final String plugin = l == 6 ? k[1] : null, key = l == 6 ? k[2] : k[1];
            AbstractCustomEnchant e = getCustomEnchant(plugin, key.replace("_", " "));
            final EnchantRarity r = e == null ? getEnchantRarity(plugin, key) : null;
            if(r != null) {
                final List<AbstractCustomEnchant> a = r.getEnchants();
                e = a.get(random.nextInt(a.size()));
            }
            final int level = e != null ? l == 6 ? getInt(k[3], e.getMaxLevel()) : getInt(k[2]) : 0;
            final int success = level != 0 ? l == 6 ? getInt(k[4]) : getInt(k[3]) : 0;
            final int destroy = level != 0 ? l == 6 ? getInt(k[5]) : getInt(k[4]) : 0;
            return e != null ? CustomEnchants.getCustomEnchants().getRevealedItem(e, level, success, destroy, true, true) : air;
        } else if(input.startsWith("dust:")) {
            final HashMap<String, MagicDust> m = MagicDust.dust;
            final MagicDust d = m != null ? m.get(Q.split(":")[1]) : null;
            return d != null ? d.getItem() : air;
        } else if(input.startsWith("enchantmentorb:")) {
            final HashMap<String, EnchantmentOrb> L = EnchantmentOrb.orbs;
            final String[] a = Q.split(":");
            String p = a[1], percent = a.length == 3 ? a[2] : Integer.toString(random.nextInt(101));
            EnchantmentOrb o = L != null && L.containsKey(p) ? L.get(p) : null;
            if(o != null) {
                final List<String> paths = new ArrayList<>();
                for(String s : L.keySet()) {
                    if(s.startsWith(p)) {
                        paths.add(s);
                    }
                }
                o = L.get(paths.get(random.nextInt(paths.size())));
            }
            final boolean h = percent.contains("-");
            final int min = h ? Integer.parseInt(percent.split("-")[0]) : Integer.parseInt(percent), P = h ? min+random.nextInt(Integer.parseInt(percent.split("-")[1])-min+1) : min;
            return o != null ? o.getItem(P) : air;
        } else if(input.startsWith("booster:")) {
            final Boosters f = Boosters.getBoosters();
            if(f.isEnabled()) {
                final String[] a = Q.split(":");
                return AbstractBooster.valueOf(a[0]).getItem(Long.parseLong(a[2])*1000, Double.parseDouble(a[1]));
            }
            return air;
        } else if(input.equals("gkitfallenhero") || input.startsWith("gkitfallenhero:")) {
            final HashMap<String, GlobalKit> L = GlobalKit.kits;
            final GlobalKit g = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return g != null ? g.getFallenHeroSpawnItem() : air;
        } else if(input.startsWith("gkitgem")) {
            final HashMap<String, GlobalKit> L = GlobalKit.kits;
            final GlobalKit g = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return g != null ? g.getFallenHeroGem() : air;
        } else if(input.startsWith("lootbox:")) {
            final String[] k = Q.split(":");
            final int l = k.length;
            final String plugin = l == 3 ? k[1] : null, key = l >= 2 ? l == 2 ? k[1] : k[2] : null;
            final AbstractLootbox L = key != null ? key.equalsIgnoreCase("latest") ? AbstractLootbox.latest() : getLootbox(plugin, key) : null;
            return L != null ? L.getItem() : air;
        } else if(input.startsWith("mask") && !input.equals("maskgenerator")) {
            final HashMap<String, Mask> L = Mask.masks;
            final Mask m = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return m != null ? m.getItem() : air;
        } else if(input.startsWith("mcmmocreditvoucher") || input.startsWith("mcmmolevelvoucher") || input.startsWith("mcmmoxpvoucher")) {
            if(mcmmoIsEnabled()) {
                final MCMMOAPI m = MCMMOAPI.getMCMMOAPI();
                if(m.isEnabled()) {
                    final boolean lvl = input.startsWith("mcmmolevelvoucher"), xp = input.startsWith("mcmmoxpvoucher");
                    final ItemStack i = lvl ? items.get("mcmmolevelvoucher").clone() : xp ? items.get("mcmmoxpvoucher").clone() : items.get("mcmmocreditvoucher").clone();
                    final String[] a = input.split(":");
                    final String sk = a[1];
                    final String skill = m.isClassic ? MCMMOClassic.getMCMMOClassic().getSkill(sk.equals("random") ? MCMMOClassic.getMCMMOClassic().getRandomSkill().name() : sk).name() : MCMMOOverhaul.getMCMMOOverhaul().getSkill(sk.equals("random") ? MCMMOOverhaul.getMCMMOOverhaul().getRandomSkill().name() : sk).name();
                    final boolean r = a[2].contains("-");
                    final int min = r ? Integer.parseInt(a[2].split("-")[0]) : Integer.parseInt(a[2]), amt = r ? min+random.nextInt(Integer.parseInt(a[2].split("-")[1])-min+1) : min;
                    final String n = ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("mcmmo vouchers.skill names." + skill.toLowerCase()));
                    itemMeta = i.getItemMeta();
                    lore.clear();
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{AMOUNT}", Integer.toString(amt)).replace("{SKILL}", n));
                    }
                    itemMeta.setLore(lore); lore.clear();
                    i.setItemMeta(itemMeta);
                    return i;
                }
            }
            return air;
        } else if(input.startsWith("mkitgem")) {
            final HashMap<String, MasteryKit> L = MasteryKit.kits;
            final MasteryKit m = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return m != null ? m.getRedeem() : air;
        } else if(input.startsWith("monthlycrate:")) {
            final HashMap<String, MonthlyCrate> L = MonthlyCrate.crates;
            final MonthlyCrate mc = L != null ? L.getOrDefault(Q.split(":")[1], null) : null;
            return mc != null ? mc.getItem() : air;
        } else if(input.startsWith("randomizationscroll")) {
            final HashMap<String, RandomizationScroll> L = RandomizationScroll.scrolls;
            final RandomizationScroll R = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return R != null ? R.getItem() : air;
        } else if(input.startsWith("raritybook:")) {
            final EnchantRarity r = FileEnchantRarity.rarities != null ? EnchantRarity.rarities.getOrDefault(Q.split(":")[1], null) : null;
            return r != null ? r.getRevealItem() : air;
        } else if(input.startsWith("rarityfireball")) {
            final HashMap<String, Fireball> L = Fireball.fireballs;
            final Fireball f = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return f != null ? f.getItem() : air;
        } else if(input.startsWith("raritygem")) {
            final HashMap<String, RarityGem> L = RarityGem.gems;
            final RarityGem g = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())])  : L.getOrDefault(Q.split(":")[1], null) : null;
            final String three = input.split(":").length == 3 ? input.split(":")[2] : null;
            final int min = three != null ? Integer.parseInt(three.contains("-") ? three.split("-")[0] : three) : 0;
            final int amount = three != null && three.contains("-") ? min+random.nextInt(Integer.parseInt(three.split("-")[1])-min+1) : min;
            return g != null ? g.getItem(amount) : air;
        } else if(input.startsWith("servercrate") || input.startsWith("spacecrate") || input.startsWith("spacechest")) {
            final HashMap<String, FileServerCrate> L = FileServerCrate.crates;
            final FileServerCrate s = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return s != null ? s.getPhyiscalItem() : air;
        } else if(input.startsWith("serverflare") || input.startsWith("spaceflare")) {
            final HashMap<String, FileServerCrate> L = FileServerCrate.crates;
            final FileServerCrate s = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return s != null ? s.getFlare().getItem() : air;
        } else if(input.startsWith("soultracker")) {
            final HashMap<String, SoulTracker> L = SoulTracker.trackers;
            final SoulTracker st = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return st != null ? st.getItem() : air;
        } else if(input.startsWith("title")) {
            final Titles t = Titles.getTitles();
            final HashMap<Integer, FileTitle> L = FileTitle.numbers;
            final int a = t.isEnabled() && L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? random.nextInt(L.size()) : getRemainingInt(input.split(":")[1]) : -1;
            final FileTitle ti = L != null? L.getOrDefault(a, null) : null;
            return ti != null ? ti.getItem() : air;
        } else if(input.startsWith("vkitfallenhero")) {
            final HashMap<String, EvolutionKit> L = EvolutionKit.kits;
            final EvolutionKit v = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return v != null ? v.getFallenHeroSpawnItem() : air;
        } else if(input.startsWith("vkitgem")) {
            final HashMap<String, EvolutionKit> L = EvolutionKit.kits;
            final EvolutionKit v = L != null ? !input.contains(":") || Q.split(":")[1].equals("random") ? L.get(L.keySet().toArray()[random.nextInt(L.size())]) : L.getOrDefault(Q.split(":")[1], null) : null;
            return v != null ? v.getFallenHeroGem() : air;
        } else if(input.startsWith("xpbottle:")) {
            final String[] a = Q.split(":");
            final boolean r = a[1].contains("-");
            final int min = r ? Integer.parseInt(a[1].split("-")[0]) : Integer.parseInt(a[1]), amt = r ? min+random.nextInt(Integer.parseInt(a[1].split("-")[1])-min+1) : min;
            return getXPBottle(amt, a.length == 3 ? a[2] : null);
        } else if(customitems != null && customitems.containsKey(Q)) {
            return customitems.get(Q).clone();
        } else if(items != null && items.containsKey(input)) {
            return items.get(input).clone();
        }
        return null;
    }

    public ItemStack getBanknote(double value, String signer) {
        item = items.get("banknote").clone(); itemMeta = item.getItemMeta(); lore.clear();
        for(String s : itemMeta.getLore()) {
            if(s.contains("{SIGNER}")) s = signer != null ? s.replace("{SIGNER}", signer) : null;
            if(s != null) lore.add(s.replace("{VALUE}", formatDouble(value)));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack getXPBottle(int value, String enchanter) {
        item = items.get("xpbottle").clone(); itemMeta = item.getItemMeta(); lore.clear();
        for(String s : itemMeta.getLore()) {
            if(s.contains("{ENCHANTER}")) s = enchanter != null ? s.replace("{ENCHANTER}", enchanter) : null;
            if(s != null) lore.add(s.replace("{VALUE}", formatInt(value)));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }


    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null) {
            final Player player = event.getPlayer();
            if(i.isSimilar(items.get("mysterymobspawner"))) {
                event.setCancelled(true);
                final List<String> s = itemsConfig.getStringList("mystery mob spawner.reward"), receivemsg = colorizeListString(itemsConfig.getStringList("mystery mob spawner.receive message"));
                final String spawner = s.get(random.nextInt(s.size()));
                final MysteryMobSpawnerOpenEvent e = new MysteryMobSpawnerOpenEvent(player, spawner);
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    removeItem(player, i, 1);
                    final ItemStack r = d(null, spawner);
                    giveItem(player, r);
                    player.updateInventory();
                    if(!receivemsg.isEmpty()) {
                        final String type = ChatColor.stripColor(r != null && r.hasItemMeta() && r.getItemMeta().hasDisplayName() ? r.getItemMeta().getDisplayName() : "Random Spawner"), n = player.getName();
                        for(String a : receivemsg) Bukkit.broadcastMessage(a.replace("{PLAYER}", n).replace("{TYPE}", type));
                    }
                }
            } else if(i.isSimilar(items.get("itemnametag"))) {
                if(itemnametag.contains(player)) {
                    sendStringListMessage(player, itemsConfig.getStringList("item name tag.already in rename process"), null);
                } else {
                    itemnametag.add(player);
                    sendStringListMessage(player, itemsConfig.getStringList("item name tag.enter rename"), null);
                    removeItem(player, i, 1);
                }
            } else if(i.isSimilar(items.get("itemlorecrystal"))) {
                if(itemlorecrystal.contains(player)) {
                    sendStringListMessage(player, itemsConfig.getStringList("item lore crystal.already in process"), null);
                } else {
                    itemlorecrystal.add(player);
                    sendStringListMessage(player, itemsConfig.getStringList("item lore crystal.enter addlore"), null);
                    removeItem(player, i, 1);
                }
            } else if(i.isSimilar(items.get("explosivesnowball"))) {
                explosivesnowball.add(player);
            } else if(i.isSimilar(items.get("explosivecake"))) {
                final Location l = event.getClickedBlock().getLocation();
                final int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
                removeItem(player, i, 1);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute " + player.getName() + " " + x + " " + y + " " + z + " particle smoke " + x + " " + y + " " + z + " 1 1 1 1 100");
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
        if(itemnametag.contains(player)) {
            itemnametag.remove(player);
            event.setCancelled(true);
            final String message = ChatColor.translateAlternateColorCodes('&', event.getMessage());
            item = getItemInHand(player);
            if(item == null || item.getType().equals(Material.AIR)) {
                sendStringListMessage(player, itemsConfig.getStringList("item name tag.cannot rename air"), null);
                giveItem(player, items.get("itemnametag").clone());
            } else if(item.getType().name().endsWith("BOW") || item.getType().name().endsWith("_AXE") || item.getType().name().endsWith("SWORD") || item.getType().name().endsWith("HELMET") || item.getType().name().endsWith("CHESTPLATE") || item.getType().name().endsWith("LEGGINGS") || item.getType().name().endsWith("BOOTS")) {
                final ItemNameTagUseEvent e = new ItemNameTagUseEvent(player, item, message);
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    itemMeta = item.getItemMeta(); lore.clear();
                    itemMeta.setDisplayName(message);
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                    playSound(itemsConfig, "item name tag.sounds.rename item", player, player.getLocation(), false);
                    for(String string : itemsConfig.getStringList("item name tag.rename item")) {
                        if(string.contains("{NAME}")) string = string.replace("{NAME}", itemMeta.getDisplayName());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                    }
                }
            } else {
                sendStringListMessage(player, itemsConfig.getStringList("item name tag.cannot rename item"), null);
                playSound(itemsConfig, "item name tag.sounds.cannot rename item", player, player.getLocation(), false);
                giveItem(player, items.get("itemnametag").clone());
            }
        } else if(itemlorecrystal.contains(player)) {
            String apply = ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("item lore crystal.apply"));
            item = getItemInHand(player);
            event.setCancelled(true);
            itemlorecrystal.remove(player);
            if(item == null || item.getType().equals(Material.AIR)) {
                sendStringListMessage(player, itemsConfig.getStringList("item lore crystal.cannot addlore air"), null);
                giveItem(player, items.get("itemlorecrystal").clone());
            } else if(item.getType().name().endsWith("BOW") || item.getType().name().endsWith("_AXE") || item.getType().name().endsWith("SWORD") || item.getType().name().endsWith("HELMET") || item.getType().name().endsWith("CHESTPLATE") || item.getType().name().endsWith("LEGGINGS") || item.getType().name().endsWith("BOOTS")) {
                itemMeta = item.getItemMeta(); lore.clear();
                boolean did = false;
                if(itemMeta.hasLore()) {
                    lore.addAll(itemMeta.getLore());
                    for(int i = 0; i < lore.size(); i++) {
                        if(!did && lore.get(i).startsWith(apply)) {
                            did = true;
                            lore.set(i, apply + ChatColor.stripColor(event.getMessage()));
                        }
                    }
                }
                if(!did) lore.add(apply + ChatColor.stripColor(event.getMessage()));
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                player.updateInventory();
                for(String string : itemsConfig.getStringList("item lore crystal.add lore")) {
                    if(string.contains("{LORE}")) string = string.replace("{LORE}", apply.replace("{LORE}", ChatColor.stripColor(event.getMessage())));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                }
            } else {
                sendStringListMessage(player, itemsConfig.getStringList("item lore crystal.cannot addlore item"), null);
                giveItem(player, items.get("itemlorecrystal").clone());
            }
        }
    }
}
