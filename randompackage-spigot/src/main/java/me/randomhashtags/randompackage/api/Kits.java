package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomEnchant;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomKit;
import me.randomhashtags.randompackage.utils.abstraction.AbstractFallenHero;
import me.randomhashtags.randompackage.utils.classes.customenchants.EnchantRarity;
import me.randomhashtags.randompackage.utils.classes.kits.*;
import me.randomhashtags.randompackage.utils.classes.living.LivingFallenHero;
import me.randomhashtags.randompackage.utils.enums.KitType;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.utils.GivedpItem.givedpitem;

public class Kits extends RPFeature implements CommandExecutor, TabCompleter {

    private static Kits instance;
    public static Kits getKits() {
        if(instance == null) instance = new Kits();
        return instance;
    }

    private static boolean isEnabled = false;
    public boolean gkitsAreEnabled = false, vkitsAreEnabled = false, mkitsAreEnabled = false;

    private gkitevents gkitEvents;
    private vkitevents vkitEvents;
    private mkitevents mkitEvents;
    public YamlConfiguration gkits, vkits, mkits;
    private UInventory gkit, vkit, mkit, gkitPreview, vkitPreview, mkitPreview;
    private ArrayList<String> gkitPaths, vkitPaths;

    private ItemStack gkitCooldown, gkitPreviewBackground, vkitCooldown, vkitPreviewBackground, vkitLocked, mkitBackground;
    public boolean heroicEnchantedEffect = false, gkitUsesTiers, tierZeroEnchantEffect;
    public ItemStack gkitFallenHeroBundle;

    private HashMap<EditedKit, String> editing;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String c = cmd.getName();
        if(args.length == 0 && player != null) {
            if(hasPermission(sender, "RandomPackage." + c, true))
                view(player, c.equals("gkit") ? KitType.GLOBAL : c.equals("vkit") ? KitType.EVOLUTION : KitType.MASTERY);
        } else if(args.length == 2 && args[0].equals("reset")) {
            if(hasPermission(sender, "RandomPackage." + c + ".reset", true))
                resetAll(player, args[1], c.equals("gkit") ? KitType.GLOBAL : c.equals("vkit") ? KitType.EVOLUTION : KitType.MASTERY);
        } else if(args.length == 3 && args[0].equals("reset")) {
            if(hasPermission(sender, "RandomPackage." + c + ".reset-kit", true))
                reset(player, args[1], c.equals("gkit") ? KitType.GLOBAL : c.equals("vkit") ? KitType.EVOLUTION : KitType.MASTERY, args[2]);
        }
        if(c.equals("gkit") && args.length == 2) {
            final String arg = args[0], argg = args[1];
            //if(arg.equals("edit") && hasPermission(sender, "RandomPackage.gkit.edit", true))
                //edit(player, GlobalKit.kits.getOrDefault(argg.toUpperCase().split("\\.yml")[0], null), true);
        }
        return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> paths = new ArrayList<>();
        final String n = cmd.getName();
        if((n.equals("gkit") || n.equals("vkit")) && args.length > 0 && args[0].equals("edit")) {
            paths = new ArrayList<>(n.equals("gkit") ? this.gkitPaths : this.vkitPaths);
            if(args.length == 1) {
                paths.clear();
                for(Player p : Bukkit.getOnlinePlayers()) {
                    final String nn = p.getName();
                    if(nn.toLowerCase().startsWith(args[0]))
                        paths.add(nn);
                }
            } else if(args.length == 2) {
                if(!args[1].isEmpty()) {
                    for(int i = 0; i < paths.size(); i++) {
                        final String s = paths.get(i);
                        if(!s.startsWith(args[1].toLowerCase())) {
                            paths.remove(s);
                            i -= 1;
                        }
                    }
                }
            }
        } else if((n.equals("gkit") || n.equals("vkit")) && args.length > 0 && args[0].equals("reset")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                final String pn = p.getName();
                if(args.length == 1 || args.length == 2 && pn.toLowerCase().startsWith(args[1].toLowerCase()))
                    paths.add(p.getName());
            }
        }
        return paths;
    }

    public void load() {
        if(isEnabled) return;
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;
        final long started = System.currentTimeMillis();
        editing = new HashMap<>();
        EditedKit.editing = new HashMap<>();

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default fallen heroes")) {
            final String[] f = new String[] {"GKIT", "VKIT", "MKIT"};
            for(String s : f) save("fallen heroes", s + ".yml");
            a.set("saved default fallen heroes", true);
            saveOtherData();
        }
        final File folder = new File(rpd + separator + "fallen heroes");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                new FallenHero(f);
            }
            final HashMap<NamespacedKey, AbstractFallenHero> f = FallenHero.heroes;
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + (f != null ? f.size() : 0) + " Fallen Heroes &e(took " + (System.currentTimeMillis()-started) + "ms)");
        }
    }
    public void unload() {
        if(!isEnabled) return;
        isEnabled = false;
        disableGkits();
        disableMkits();
        disableVkits();
        FallenHero.deleteAll();
        LivingFallenHero.deleteAll();
        HandlerList.unregisterAll(this);
    }

    public void enableGkits() {
        final long started = System.currentTimeMillis();
        if(gkitsAreEnabled) return;
        save(null, "kits global.yml");
        gkitEvents = new gkitevents();
        pluginmanager.registerEvents(gkitEvents, randompackage);
        load();
        gkitsAreEnabled = true;

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default gkits")) {
            final String[] g = new String[] {
                    "ADMIRAL", "ARCANE", "ARENA_CHAMPION", "BUTCHER", "CANNONER", "COSMONAUT",
                    "DESTROYER", "DIABLO", "ENCHANTMENT", "GLADIATOR", "GRAND_MASTER", "GRIM_REAPER", "GUARDIAN",
                    "HYPERDRIVE", "INDEPENDENCE", "LOKI", "MASTER_BUILDER", "PALADIN", "PARTY_ANIMAL", "SPOOKY",
                    "TEMPLAR", "TINKERMASTER", "TRICKSTER", "VALENTINES", "VIKING", "VOIDWALKER", "WARLOCK",
            };
            for(String s : g) save("gkits", s + ".yml");
            a.set("saved default gkits", true);
            saveOtherData();
        }

        gkits = YamlConfiguration.loadConfiguration(new File(rpd, "kits global.yml"));
        gkitCooldown = d(gkits, "items.cooldown");
        gkitFallenHeroBundle = d(gkits, "items.fallen hero bundle");
        givedpitem.items.put("gkitfallenherobundle", gkitFallenHeroBundle);
        heroicEnchantedEffect = gkits.getBoolean("items.heroic.enchanted effect");
        gkit = new UInventory(null, gkits.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', gkits.getString("gui.title")));
        gkitPreview = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', gkits.getString("items.preview.title")));
        gkitPreviewBackground = d(gkits, "items.preview");
        gkitUsesTiers = gkits.getBoolean("gui.settings.use tiers");
        tierZeroEnchantEffect = gkits.getBoolean("gui.settings.tier zero enchant effect");
        GlobalKit.heroicprefix = ChatColor.translateAlternateColorCodes('&', gkits.getString("items.heroic.prefix"));

        gkitPaths = new ArrayList<>();

        final Inventory gi = gkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        final File folder = new File(rpd + separator + "gkits");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final String n = f.getName().split("\\.yml")[0];
                final GlobalKit g = new GlobalKit(f);
                gi.setItem(g.getSlot(), g.getItem());
                gkitPaths.add(n);
                gems.add(g.getFallenHeroGem());
                fallenheroes.add(g.getFallenHeroSpawnItem());
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Gkit Gems", "Givedp: Gkit Gems");
        addGivedpCategory(fallenheroes, UMaterial.BONE, "Gkit Fallen Heroes", "Givedp: Gkit Fallen Heroes");
        final HashMap<String, GlobalKit> G = GlobalKit.kits;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (G != null ? G.size() : 0) + " Global Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disableGkits() {
        if(!gkitsAreEnabled) return;
        gkitsAreEnabled = false;
        gkitPaths = null;
        for(int i = 0; i < EditedKit.editing.size(); i++) {
            final EditedKit e = EditedKit.editing.get(i);
            final Player p = e.player;
            if(e.original.get(0).kit instanceof GlobalKit) {
                e.delete();
                p.closeInventory();
                i -= 1;
            }
        }
        final HashMap<UUID, LivingFallenHero> f = LivingFallenHero.living;
        if(f != null) {
            for(LivingFallenHero l : new ArrayList<>(f.values())) {
                if(l.getKit() instanceof GlobalKit) {
                    l.delete();
                }
            }
        }
        GlobalKit.deleteAll();
        HandlerList.unregisterAll(gkitEvents);
    }

    public void enableVkits() {
        final long started = System.currentTimeMillis();
        if(vkitsAreEnabled) return;
        save(null, "kits evolution.yml");
        vkitsAreEnabled = true;
        vkits = YamlConfiguration.loadConfiguration(new File(rpd, "kits evolution.yml"));
        vkitEvents = new vkitevents();
        pluginmanager.registerEvents(vkitEvents, randompackage);
        load();

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default vkits")) {
            final String[] v = new String[] {"ALCHEMIST", "JUDGEMENT", "LUCKY", "MIMIC", "OGRE", "PHOENIX", "SLAYER", "TROLL"};
            for(String s : v) save("vkits", s + ".yml");
            a.set("saved default vkits", true);
            saveOtherData();
        }

        vkitCooldown = d(vkits, "items.cooldown");
        vkit = new UInventory(null, vkits.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', vkits.getString("gui.title")));
        vkitPreview = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', vkits.getString("items.preview.title")));
        vkitPreviewBackground = d(vkits, "items.preview");
        vkitLocked = d(vkits, "permissions.locked");

        vkitPaths = new ArrayList<>();
        final Inventory vi = vkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        final File folder = new File(rpd + separator + "vkits");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final String n = f.getName().split("\\.yml")[0];
                final EvolutionKit e = new EvolutionKit(f);
                vi.setItem(e.getSlot(), e.getItem());
                vkitPaths.add(n);
                gems.add(e.getFallenHeroGem());
                fallenheroes.add(e.getFallenHeroSpawnItem());
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Vkit Gems", "Givedp: Vkit Gems");
        addGivedpCategory(fallenheroes, UMaterial.BONE, "Vkit Fallen Heroes", "Givedp: Vkit Fallen Heroes");
        final HashMap<String, EvolutionKit> E = EvolutionKit.kits;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (E != null ? E.size() : 0) + " Evolution Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disableVkits() {
        if(!vkitsAreEnabled) return;
        vkitsAreEnabled = false;
        vkits = null;
        vkitPaths = null;
        HandlerList.unregisterAll(vkitEvents);
        vkitEvents = null;
        for(int i = 0; i < EditedKit.editing.size(); i++) {
            final EditedKit e = (EditedKit) EditedKit.editing.values().toArray()[i];
            final Player p = e.player;
            if(e.original.get(0).kit instanceof EvolutionKit) {
                e.delete();
                p.closeInventory();
                i -= 1;
            }
        }
        final HashMap<UUID, LivingFallenHero> f = LivingFallenHero.living;
        if(f != null) {
            for(LivingFallenHero l : new ArrayList<>(f.values())) {
                if(l.getKit() instanceof EvolutionKit) {
                    l.delete();
                }
            }
        }
        EvolutionKit.deleteAll();
    }

    public void enableMkits() {
        final long started = System.currentTimeMillis();
        if(mkitsAreEnabled) return;
        save(null, "kits mastery.yml");
        mkitsAreEnabled = true;
        mkits = YamlConfiguration.loadConfiguration(new File(rpd, "kits mastery.yml"));
        mkitEvents = new mkitevents();
        pluginmanager.registerEvents(mkitEvents, randompackage);
        load();

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default mkits")) {
            final String[] v = new String[] {"DEATH_KNIGHT", "GHOST", "NECROMANCER"};
            for(String s : v) save("mkits", s + ".yml");
            a.set("saved default mkits", true);
            saveOtherData();
        }

        mkit = new UInventory(null, mkits.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', mkits.getString("gui.title")));
        mkitBackground = d(mkits, "gui.background");
        final Inventory mi = mkit.getInventory();
        final File folder = new File(rpd + separator + "mkits");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final MasteryKit m = new MasteryKit(f);
                mi.setItem(m.getSlot(), m.getItem());
            }
        }
        for(int i = 0; i < mkit.getSize(); i++)
            if(mi.getItem(i) == null)
                mi.setItem(i, mkitBackground);
        final HashMap<String, MasteryKit> M = MasteryKit.kits;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (M != null ? M.size() : 0) + " Mastery Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disableMkits() {
        if(!mkitsAreEnabled) return;
        mkitsAreEnabled = false;
        mkits = null;
        MasteryKit.deleteAll();
        HandlerList.unregisterAll(mkitEvents);
        mkitEvents = null;
    }


    public void view(Player player, KitType type) {
        player.closeInventory();
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        if(type.equals(KitType.EVOLUTION) && vkitsAreEnabled) {
            player.openInventory(Bukkit.createInventory(player, vkit.getSize(), vkit.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(vkit.getInventory().getContents());
            player.updateInventory();
            final HashMap<String, Integer> tiers = pdata.getKitLevels(KitType.EVOLUTION);
            final HashMap<String, Long> cooldowns = pdata.getKitCooldowns(KitType.EVOLUTION);
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null) {
                    item = item.clone();
                    final EvolutionKit v = EvolutionKit.valueOf(i);
                    if(v != null) {
                        final String n = v.getYamlName();
                        final int lvl = tiers.containsKey(n) ? tiers.get(n) : player.hasPermission("RandomPackage.vkit." + n) ? 1 : 0;
                        final boolean hasPerm = hasPermissionToObtain(player, v), cooldown = cooldowns.containsKey(n) && cooldowns.get(n) > System.currentTimeMillis();
                        if(!hasPerm) item = vkitLocked.clone();
                        else if(cooldown) setCooldown(player, v);
                        if(!cooldown) {
                            itemMeta = item.getItemMeta(); lore.clear();
                            if(!hasPerm) {
                                final ItemMeta is = top.getItem(i).getItemMeta();
                                itemMeta.setDisplayName(is.getDisplayName());
                                itemMeta.setLore(is.getLore());
                            }
                            if(itemMeta.hasLore())
                                for(String s : itemMeta.getLore()) {
                                    lore.add(s.replace("{LEVEL}", Integer.toString(lvl)));
                                }
                            if(hasPerm) {
                                if(!cooldown)
                                    for(String s : vkits.getStringList("permissions.unlocked"))
                                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                                for(String s : vkits.getStringList("permissions.preview")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
                            } else {
                                for(String s : vkitLocked.getItemMeta().getLore())
                                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                            }
                            itemMeta.setLore(lore); lore.clear();
                            item.setItemMeta(itemMeta);
                            top.setItem(i, item);
                        }

                    }
                }
            }
        } else if(type.equals(KitType.GLOBAL) && gkitsAreEnabled) {
            player.openInventory(Bukkit.createInventory(player, gkit.getSize(), gkit.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gkit.getInventory().getContents());
            player.updateInventory();
            final HashMap<String, Integer> tiers = pdata.getKitLevels(KitType.GLOBAL);
            final HashMap<String, Long> cooldowns = pdata.getKitCooldowns(KitType.GLOBAL);
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null) {
                    final GlobalKit k = GlobalKit.valueOf(i);
                    if(k != null) {
                        final String n = k.getYamlName();
                        final boolean has = tiers.containsKey(n) || player.hasPermission("RandomPackage.gkit." + n);
                        itemMeta = item.getItemMeta(); lore.clear();
                        if(cooldowns.containsKey(n) && cooldowns.get(n) > System.currentTimeMillis()) {
                            setCooldown(player, k);
                        } else {
                            final int tier = tiers.containsKey(n) ? tiers.get(n) : has ? 1 : 0;
                            final boolean isheroic = k.isHeroic(), q = isheroic && heroicEnchantedEffect && (has || tierZeroEnchantEffect && tiers.containsKey(n) && !(tier < 1));
                            if(gkitUsesTiers)
                                for(String s : gkits.getStringList("gui.settings.pre lore"))
                                    lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{TIER}", tier != 0 ? toRoman(tier) : "0").replace("{MAX_TIER}", toRoman(k.getMaxLevel()))));
                            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                            for(String s : gkits.getStringList("gui.settings." + (has ? "un" : "") + "locked")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
                            for(String s : gkits.getStringList("items.preview.added gui lore")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
                            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                            if(q) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            itemMeta.setLore(lore); lore.clear();
                            item.setItemMeta(itemMeta);
                            if(q) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                        }
                    }
                }
            }
        } else if(type.equals(KitType.MASTERY)) {
            player.openInventory(Bukkit.createInventory(player, mkit.getSize(), mkit.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(mkit.getInventory().getContents());
            player.updateInventory();
            for(int i = 0; i < top.getSize(); i++) {
                final MasteryKit m = MasteryKit.valueOf(i);
                if(m != null) {
                    item = top.getItem(i); itemMeta = item.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            if(s.contains("{") && s.contains("}")) {
                                final String t = s.split("\\{")[1].split("}")[0];
                                final GlobalKit gk = GlobalKit.kits.getOrDefault(t, null);
                                final EvolutionKit vk = gk == null ? EvolutionKit.kits.getOrDefault(t, null) : null;
                                if(gk != null) {
                                    s = s.replace("{" + gk.getYamlName() + "}", gk.getFallenHeroName());
                                }
                                if(vk != null) {
                                    s = s.replace("{" + vk.getYamlName() + "}", vk.getFallenHeroName());
                                }
                            }
                            lore.add(s);
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
            }
        } else return;
        player.updateInventory();
    }

    public void setCooldown(UUID player, Object kit) {
        if(player != null && kit != null) {
            final GlobalKit gkit = kit instanceof GlobalKit ? (GlobalKit) kit : null;
            final EvolutionKit vkit = gkit == null && kit instanceof EvolutionKit ? (EvolutionKit) kit : null;
            final MasteryKit mkit = vkit == null && kit instanceof MasteryKit ? (MasteryKit) kit : null;
            if(gkit == null && vkit == null && mkit == null) return;

            final boolean g = gkit != null, v = vkit != null;
            final RPPlayer pdata = RPPlayer.get(player);
            final long t = System.currentTimeMillis();
            pdata.addKitCooldown(kit, t+(g ? gkit.getCooldown() : v ? vkit.getCooldown() : mkit.getCooldown())*1000);
        }
    }
    private void setCooldown(Player player, Object kit) {
        if(player != null && kit != null) {
            final GlobalKit gkit = kit instanceof GlobalKit ? (GlobalKit) kit : null;
            final EvolutionKit vkit = gkit == null && kit instanceof EvolutionKit ? (EvolutionKit) kit : null;
            final MasteryKit mkit = vkit == null && kit instanceof MasteryKit ? (MasteryKit) kit : null;
            if(gkit == null && vkit == null && mkit == null) return;

            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final boolean g = gkit != null, v = vkit != null, hasPerm = hasPermissionToObtain(player, kit);
            final YamlConfiguration yml = g ? gkits : v ? vkits : mkits;
            final String n = g ? gkit.getYamlName() : v ? vkit.getYamlName() : mkit.getYamlName();
            final long t = System.currentTimeMillis();
            final boolean cooldown = g && pdata.getKitCooldown(gkit)-t <= 0 || v && pdata.getKitCooldown(vkit)-t <= 0;
            final int slot = g ? gkit.getSlot() : v ? vkit.getSlot() : mkit.getSlot(), tier = g ? pdata.getKitLevel(gkit) : v ? pdata.getKitLevel(vkit) : pdata.getKitLevel(mkit);
            final ItemStack displayed = g ? gkit.getItem() : v ? vkit.getItem() : mkit.getItem();
            final HashMap<String, Long> cooldowns = g ? pdata.getKitCooldowns(KitType.GLOBAL) : v ? pdata.getKitCooldowns(KitType.EVOLUTION) : null;
            final String remainingTime = getRemainingTime(cooldowns.get(n)-t);
            item = (g ? gkitCooldown : v ? vkitCooldown : null).clone(); itemMeta = item.getItemMeta(); lore.clear();
            for(String s : itemMeta.getLore()) {
                if(s.equals("{LORE}"))
                    for(String q : displayed.getItemMeta().getLore())
                        lore.add(q.replace("{LEVEL}", Integer.toString(tier)));
                else
                    lore.add(s.replace("{LEVEL}", Integer.toString(tier)).replace("{TIME}", remainingTime));
            }
            if(hasPerm) {
                if(!cooldown)
                    for(String s : yml.getStringList("permissions.unlocked"))
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
            } else {
                for(String s : yml.getStringList("permissions.locked"))
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            for(String s : yml.getStringList("permissions.preview")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            player.getOpenInventory().getTopInventory().setItem(slot, item);
            player.updateInventory();
        }
    }
    /*
    public void edit(Player player, GlobalKit kit, boolean editOriginalItems) {
        final String k = kit.getYamlName();
        final List<KitItem> items = kit.getItems();
        player.openInventory(Bukkit.createInventory(player, 54, "Edit gkit: " + k));
        final Inventory top = player.getOpenInventory().getTopInventory();
        final YamlConfiguration yml = kit.getYaml();
        for(KitItem ki : items) {
            final String chances = ki.stringChances;
            if(chances == null) {
                top.setItem(top.firstEmpty(), d(yml, "items." + ki.path, kit.getMaxLevel()));
            }
        }
        final ItemStack I = kit.getItem();
        itemMeta = I.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Settings");
        lore.clear();
        lore.addAll(Arrays.asList(" ", ChatColor.GRAY + "Left click to edit", ChatColor.GRAY + "Right click to remove", " ", ChatColor.GRAY + "Click items in your inventory to add to gkit"));
        itemMeta.setLore(lore); lore.clear();
        I.setItemMeta(itemMeta);
        top.setItem(49, I);
        final EditedKit ek = EditedKit.editing.getOrDefault(player, null);
        if(editOriginalItems) {
            if(ek != null) ek.delete();
            final List<KitItem> i = kit.getItems();
            new EditedKit(player, i, new ArrayList<>(i));
        } else if(ek != null) {
            ek.selected = -1;
        }
    }
    public void editKitItem(Player player, EditedKit ek, int slot) {
        ek.selected = slot;
        final KitItem ki = ek.edited.get(slot);
        final GlobalKit gkit = (GlobalKit) ki.kit;
        final YamlConfiguration yml = gkit.getYaml();
        final ItemStack it = d(yml, "items." + ki.path, gkit.getMaxLevel());
        player.openInventory(Bukkit.createInventory(player, 9, "Editing kit item: " + ki.path));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setItem(0, it);

        for(int i = 0; i < top.getSize(); i++) {
            item = i == 2 ? UMaterial.GOLD_NUGGET.getItemStack() : i == 3 ? UMaterial.NAME_TAG.getItemStack() : i == 4 ? UMaterial.OAK_SIGN.getItemStack() : i == 5 ? UMaterial.GLOWSTONE_DUST.getItemStack() : i == 6 ? UMaterial.MAP.getItemStack() : i == 8 ? UMaterial.ARROW.getItemStack() : null;
            if(item != null) {
                itemMeta = item.getItemMeta();
                final String n = i == 2 ? ChatColor.GREEN + "Set new amount" : i == 3 ? ChatColor.AQUA + "Set new name" : i == 4 ? ChatColor.YELLOW + "Set new lore" : i == 5 ? ChatColor.GOLD + "Set new chance" : i == 6 ? ChatColor.GOLD + "Set Givedp Item" : i == 8 ? ChatColor.GRAY + "Back" : null;
                itemMeta.setDisplayName(n);
                lore.clear();
                if(i == 4) {
                    lore.add(ChatColor.GRAY + "Current:");
                    for(String s : ki.lore) {
                        lore.add(ChatColor.RESET + (s.length() >= 60 ? s.substring(0, 60) + "..." : s));
                    }
                } else {
                    lore.addAll(
                            i == 2 ? Arrays.asList(ChatColor.GRAY + "Current:", ChatColor.RESET + ki.amount)
                                    : i == 3 ? Arrays.asList(ChatColor.GRAY + "Current:", ChatColor.RESET + (ki.stringName != null ? ki.stringName : ChatColor.RESET + "N/A"))
                                    : i == 5 ? Arrays.asList(ChatColor.GRAY + "Current: " + ChatColor.RESET + ki.chance + "%")
                                    : i == 6 ? Arrays.asList(ChatColor.GRAY + "Set this item to a Givedp Item")
                                    : new ArrayList<>()
                    );
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                top.setItem(i, item);
            }
            player.updateInventory();
        }
    }
    public void editKitAmount(EditedKit ek) { enterEdit(ek, "AMOUNT"); }
    public void editKitName(EditedKit ek) { enterEdit(ek, "NAME"); }
    public void editKitLore(EditedKit ek) { enterEdit(ek, "LORE"); }
    public void editKitChance(EditedKit ek) { enterEdit(ek, "CHANCE"); }
    private void enterEdit(EditedKit ek, String type) {
        final Player player = ek.player;
        player.closeInventory();
        editing.put(ek, type);
    }

    @EventHandler
    private void playerChatEvent(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final EditedKit ek = EditedKit.editing.getOrDefault(player, null);
        if(ek != null) {
            event.setCancelled(true);
            final String msg = event.getMessage();
            final KitItem target = ek.edited.get(ek.selected);
            final String type = editing.get(ek);
            if(type.equals("AMOUNT")) {
                target.amount = msg;
            } else if(type.equals("NAME")) {
                target.stringName = msg;
            } else if(type.equals("LORE")) {

            } else if(type.equals("CHANCE")) {
                target.chance = Integer.parseInt(msg);
            } else return;
            editing.remove(ek);
            editKitItem(player, ek, ek.selected);
        }
    }*/



    public boolean hasPermissionToObtain(Player player, Object kit) {
        if(player != null && kit != null) {
            final GlobalKit gkit = kit instanceof GlobalKit ? (GlobalKit) kit : null;
            final EvolutionKit vkit = gkit == null && kit instanceof EvolutionKit ? (EvolutionKit) kit : null;
            final MasteryKit mkit = gkit == null && kit instanceof MasteryKit ? (MasteryKit) kit : null;
            if(gkit == null && vkit == null && mkit == null) return false;

            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final String n = gkit != null ? gkit.getYamlName() : vkit != null ? vkit.getYamlName() : mkit.getYamlName();
            return gkit != null && (pdata.getKitLevels(KitType.GLOBAL).containsKey(n) || player.hasPermission("RandomPackage.gkit." + n))
                    || vkit != null && (pdata.getKitLevels(KitType.EVOLUTION).containsKey(n) || player.hasPermission("RandomPackage.vkit." + n));
        } else {
            return false;
        }
    }
    public void resetAll(CommandSender sender, String target, KitType type) {
        final RPPlayer pdata = r(sender, target, type);
        if(pdata != null) {
            pdata.getKitCooldowns(type).clear();
        }
    }
    public void reset(CommandSender sender, String target, KitType type, String kitName) {
        final RPPlayer pdata = r(sender, target, type);
        if(pdata != null) pdata.getKitCooldowns(type).put(kitName, 0l);
    }
    private RPPlayer r(CommandSender sender, String target, KitType type) {
        final OfflinePlayer p = Bukkit.getOfflinePlayer(target);
        final RPPlayer pdata = RPPlayer.get(p.getUniqueId());
        final YamlConfiguration yml = type.equals(KitType.EVOLUTION) ? vkits : type.equals(KitType.GLOBAL) ? gkits : mkits;

        if(pdata == null) {
            sendStringListMessage(sender, yml.getStringList("messages.target doesnt exist"), null);
        } else {
            for(String s : yml.getStringList("messages.success")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", p.getName())));
            }
        }
        return pdata;
    }
    public void preview(Player player, GlobalKit kit, int tier) {
        if(player == null || kit == null) return;
        player.closeInventory();
        final List<ItemStack> rewards = new ArrayList<>();
        final String pn = player.getName(), t = Integer.toString(tier), mt = Integer.toString(kit.getMaxLevel());
        final YamlConfiguration yml = kit.getYaml();
        for(KitItem ki : kit.getItems()) {
            final ItemStack is = d(gkits, yml, "items." + ki.path, tier);
            if(is != null && is.hasItemMeta()) {
                itemMeta = is.getItemMeta();
                if(itemMeta.hasDisplayName()) {
                    itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PLAYER}", pn));
                }
                if(itemMeta.hasLore()) {
                    lore.clear();
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{TIER}", t).replace("{MAX_TIER}", mt));
                    }
                    itemMeta.setLore(lore);
                }
                is.setItemMeta(itemMeta);
            }
            rewards.add(is);
        }
        int s = rewards.size();
        s = s == 9 || s == 18 || s == 27 || s == 36 || s == 45 || s == 54 ? s : s > 54 ? 54 : ((s+9)/9)*9;
        player.openInventory(Bukkit.createInventory(player, s, gkitPreview.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack i : rewards)
            top.setItem(top.firstEmpty(), i);
        for(int i = 0; i < top.getSize(); i++) {
            item = top.getItem(i);
            if(item == null || item.getType().name().contains("AIR"))
                top.setItem(i, gkitPreviewBackground.clone());
        }
        player.updateInventory();
    }
    public void give(Player player, Object kit, int tier, boolean allItems, boolean addCooldown) {
        if(player != null && kit != null) {
            final GlobalKit gkit = kit instanceof GlobalKit ? (GlobalKit) kit : null;
            final EvolutionKit vkit = gkit == null && kit instanceof EvolutionKit ? (EvolutionKit) kit : null;
            final MasteryKit mkit = gkit == null && kit instanceof MasteryKit ? (MasteryKit) kit : null;
            if(gkit == null && vkit == null && mkit == null) return;

            final boolean g = gkit != null, v = vkit != null;
            final int max = g || v ? (g ? gkit : vkit).getMaxLevel() : 0;
            final YamlConfiguration yml = g ? gkit.getYaml() : v ? vkit.getYaml() : mkit.getYaml();
            final List<KitItem> kitItems = g ? gkit.getItems() : v ? vkit.getItems() : null;
            if(kitItems == null) return;
            final YamlConfiguration typeYML = g ? gkits : v ? vkits : mkits;
            final String pn = player.getName(), t = Integer.toString(tier), mt = Integer.toString(max);
            for(KitItem ki : kitItems) {
                if(allItems || ki.chance >= 100 || ki.chance <= random.nextInt(100)) {
                    final ItemStack is = d(typeYML, yml, "items." + ki.path, tier);
                    if(is != null && is.hasItemMeta()) {
                        itemMeta = is.getItemMeta();
                        if(itemMeta.hasDisplayName()) {
                            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{LEVEL}", t).replace("{PLAYER}", pn));
                        }
                        if(itemMeta.hasLore()) {
                            lore.clear();
                            for(String s : itemMeta.getLore()) {
                                lore.add(s.replace("{LEVEL}", t).replace("{TIER}", t).replace("{MAX_TIER}", mt));
                            }
                            itemMeta.setLore(lore); lore.clear();
                        }
                        is.setItemMeta(itemMeta);
                    }
                    giveItem(player, is);
                }
            }
            if(addCooldown) setCooldown(player.getUniqueId(), kit);
        }
    }
    public void give(Player player, GlobalKit kit, int tier, boolean addCooldown) { give(player, kit, tier, false, addCooldown); }
    public void give(Player player, EvolutionKit vkit, boolean preview) {
        if(vkit == null) return;
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        final HashMap<String, Integer> lvls = pdata.getKitLevels(KitType.EVOLUTION);
        final String n = vkit.getYamlName();
        final int vkitlvl = lvls.containsKey(n) ? lvls.get(n) : player.hasPermission("RandomPackage.vkit." + n) ? 1 : 0;
        final List<ItemStack> rewards = new ArrayList<>();
        final YamlConfiguration yml = vkit.getYaml();
        for(KitItem ki : vkit.getItems())
            if(preview || ki.reqLevel <= 0 || vkitlvl >= ki.reqLevel)
                rewards.add(d(vkits, yml, "items." + ki.path, vkitlvl));
        if(preview) {
            int s = rewards.size();
            s = s == 9 || s == 18 || s == 27 || s == 36 || s == 45 || s == 54 ? s : s > 54 ? 54 : ((s+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, s, vkitPreview.getTitle()));
        }
        for(ItemStack is : rewards) {
            if(is != null) {
                item = is.clone(); itemMeta = item.getItemMeta(); lore.clear();
                if(item.hasItemMeta()) {
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PLAYER}", player.getName()).replace("{LEVEL}", Integer.toString(vkitlvl)));
                    }
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            if(s.startsWith("{") && s.contains("reqlevel=")) {
                                final String[] a = s.split(":");
                                final int level = getRemainingInt(a[0]), reqlevel = getRemainingInt(s.split("reqlevel=")[1].split(":")[0]), chance = a.length == 3 ? getRemainingInt(a[2]) : 100;
                                final AbstractCustomEnchant enchant = AbstractCustomEnchant.valueOf(s.split("\\{")[1].split("}")[0].replace("" + level, ""));
                                if(random.nextInt(100) <= chance && enchant != null && vkitlvl >= reqlevel) {
                                    lore.add(EnchantRarity.valueOf(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != -1 ? level : 1+random.nextInt(enchant.getMaxLevel())));
                                }
                            } else if(s.startsWith("{") && s.contains(":") && s.endsWith("}")) {
                                final String r = s.split(":")[random.nextInt(s.split(":").length)];
                                int level = getRemainingInt(s.split("\\{")[1].split("}")[0]);
                                final AbstractCustomEnchant enchant = AbstractCustomEnchant.valueOf(r.split("\\{")[1].split("}")[0].replace("" + level, ""));

                                if(enchant != null) {
                                    if(level == -1) level = random.nextInt(enchant.getMaxLevel());
                                    lore.add(EnchantRarity.valueOf(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != 0 ? level : 1));
                                }
                            } else
                                lore.add(s.replace("{LEVEL}", Integer.toString(vkitlvl)));
                        }
                        itemMeta.setLore(lore); lore.clear();
                    }
                    item.setItemMeta(itemMeta);
                }
                if(preview) player.getOpenInventory().getTopInventory().addItem(item);
                else        giveItem(player, item);
            }
        }
        final int fe = player.getOpenInventory().getTopInventory().firstEmpty();
        if(preview && fe > -1)
            for(int i = fe; i < player.getOpenInventory().getTopInventory().getSize(); i++)
                player.getOpenInventory().getTopInventory().setItem(i, vkitPreviewBackground.clone());
        if(!preview)
            pdata.getKitCooldowns(KitType.EVOLUTION).put(n, System.currentTimeMillis()+(vkit.getCooldown()*1000));
        player.updateInventory();
        final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
        int upgradechance = vkit.getUpgradeChance(), a = fu.isEnabled() ? (int) (fu.getVkitLevelingChance(fapi.getFaction(player))*100) : 0;
        upgradechance += a;
        if(!preview && random.nextInt(100) <= upgradechance) {
            final int newlvl = vkitlvl+1;
            if(newlvl > vkit.getMaxLevel()) return;
            final String name = vkit.getItem().getItemMeta().getDisplayName();
            pdata.getKitLevels(KitType.EVOLUTION).put(n, newlvl);
            for(String s : vkits.getStringList("messages.upgrade"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{LEVEL}", Integer.toString(newlvl)).replace("{VKIT}", name)));
            for(String s : vkits.getStringList("messages.upgrade broadcast"))
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", player.getName()).replace("{VKIT}", name).replace("{LEVEL}", Integer.toString(newlvl))));
        }
    }
    private ItemStack d(YamlConfiguration category, FileConfiguration config, String path, int tier) {
        item = d(config, path, category.getDouble("gui.settings.tier custom enchant multiplier." + tier));
        itemMeta = item.getItemMeta();
        if(itemMeta != null && itemMeta.hasLore()) {
            final boolean levelzeroremoval = CustomEnchants.getCustomEnchants().levelZeroRemoval;
            lore.clear();
            for(String string : itemMeta.getLore()) {
                final String sl = string.toLowerCase();
                if(string.startsWith("{") && (!sl.contains("reqlevel=") && sl.contains("chance=") || sl.contains("reqlevel=") && tier >= Integer.parseInt(sl.split("reqlevel=")[1].split(":")[0]))) {
                    final AbstractCustomEnchant en = AbstractCustomEnchant.valueOf(string.split("\\{")[1].split("}")[0]);
                    final boolean c = string.contains("chance=");
                    if(en != null && !c || random.nextInt(100) <= Integer.parseInt(string.split("chance=")[1])) {
                        final int lvl = random.nextInt(en.getMaxLevel()+1);
                        if(lvl != 0 || !levelzeroremoval) {
                            lore.add(EnchantRarity.valueOf(en).getApplyColors() + en.getName() + " " + toRoman(lvl == 0 ? 1 : lvl));
                        }
                    }
                } else {
                    lore.add(string);
                }
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
        }
        return item;
    }
    private void tryIncreaseTier(Player player, ItemStack is, AbstractCustomKit kit) {
        final boolean gkit = kit instanceof GlobalKit;
        final String type = gkit ? "gkit" : "vkit";
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        final String n = kit.getYamlName();
        final HashMap<String, String> replacements = new HashMap<>();
        final HashMap<String, Integer> tiers = pdata.getKitLevels(gkit ? KitType.GLOBAL : KitType.EVOLUTION);
        if(!tiers.containsKey(n) && player.hasPermission("RandomPackage." + type + "." + n)) tiers.put(n, 0);
        if(tiers.containsKey(n)) {
            final int l = tiers.get(n);
            if(l != kit.getMaxLevel()) {
                tiers.put(n, l+1);
            } else {
                sendStringListMessage(player, (gkit ? gkits : vkits).getStringList("messages.already have max"), null);
                return;
            }
        } else {
            replacements.put("{NAME}", (gkit ? ((GlobalKit) kit).getFallenHeroName() : ((EvolutionKit) kit).getFallenHeroName()));
            tiers.put(n, 1);
            sendStringListMessage(player, (gkit ? gkits : vkits).getStringList("messages.redeem"), replacements);
        }
        removeItem(player, is, 1);
        player.updateInventory();
    }
    private void trySpawning(Player player, ItemStack is, AbstractCustomKit kit, Location l) {
        final boolean gkit = kit instanceof GlobalKit;
        final AbstractFallenHero h = (gkit ? (GlobalKit) kit : (EvolutionKit) kit).getFallenHero();
        if(h.canSpawnAt(l)) {
            removeItem(player, is, 1);
            h.spawn(player, new Location(l.getWorld(), l.getX(), l.getY()+1, l.getZ()), kit);
            final HashMap<String, String> r = new HashMap<>();
            r.put("{NAME}", (gkit ? ((GlobalKit) kit).getFallenHeroName() : ((EvolutionKit) kit).getFallenHeroName()));
            sendStringListMessage(player, h.getSummonMsg(), r);
        } else {
            sendStringListMessage(player, (gkit ? gkits : vkits).getStringList("messages.not in warzone"), null);
        }
    }

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        if(!(e instanceof Player)) {
            final HashMap<UUID, LivingFallenHero> L = LivingFallenHero.living;
            if(L != null) {
                final LivingFallenHero f = L.getOrDefault(e.getUniqueId(), null);
                if(f != null) {
                    f.killed(event);
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack is = event.getItem();
        final GlobalKit g = GlobalKit.valueOfFallenHeroSpawnItem(is), gem = g == null ? GlobalKit.valueOfFallenHeroGem(is) : null;
        final EvolutionKit v = gem == null ? EvolutionKit.valueOfFallenHeroSpawnItem(is) : null, vgem = v == null ? EvolutionKit.valueOfFallenHeroGem(is) : null;
        if((g != null || v != null) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            trySpawning(player, is, g != null ? g : v, event.getClickedBlock().getLocation());
        } else if(gem != null || vgem != null) {
            tryIncreaseTier(player, is, gem != null ? gem : vgem);
        }
    }

    private class gkitevents extends RandomPackageAPI implements Listener {

        @EventHandler
        private void inventoryClickEvent(InventoryClickEvent event) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory top = player.getOpenInventory().getTopInventory();
            if(!event.isCancelled() && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && top.getHolder() == player) {
                final String t = event.getView().getTitle();
                final int r = event.getRawSlot();
                if(t.equals(gkit.getTitle()) || t.equals(gkitPreview.getTitle())) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final GlobalKit gkit = GlobalKit.valueOf(r);
                    if(gkit == null || r < 0 || r >= top.getSize()) return;

                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    final int tier = pdata.getKitLevel(gkit);
                    if(t.equals(gkitPreview.getTitle())) {
                        player.closeInventory();
                        sendStringListMessage(player, gkits.getStringList("messages.cannot withdraw"), null);
                    } else if(event.getClick().name().contains("RIGHT")) {
                        preview(player, gkit, tier);
                    } else {
                        final String n = gkit.getYamlName();
                        final HashMap<String, Long> cooldowns = pdata.getKitCooldowns(KitType.GLOBAL);
                        final HashMap<String, Integer> tiers = pdata.getKitLevels(KitType.GLOBAL);
                        final boolean hasPerm = hasPermissionToObtain(player, gkit);
                        if(!hasPerm) {
                            sendStringListMessage(player, gkits.getStringList("messages.not unlocked kit"), null);
                        } else if(tiers.containsKey(n) && !cooldowns.containsKey(n)
                                || !tiers.containsKey(n) && player.hasPermission("RandomPackage.gkit." + n) && !cooldowns.containsKey(n)
                                || cooldowns.containsKey(n) && cooldowns.get(n) <= System.currentTimeMillis()) {
                            cooldowns.put(n, System.currentTimeMillis()+(gkit.getCooldown()*1000));
                            give(player, gkit, tier, false);
                            setCooldown(player, gkit);
                        }
                    }
                } else {
                    final EditedKit ek = EditedKit.editing.getOrDefault(player, null);
                    if(ek != null) {
                        event.setCancelled(true);
                        player.updateInventory();
                        /*
                        if(ek.selected == -1) editKitItem(player, ek, r);
                        else if(r == 2) editKitAmount(ek);
                        else if(r == 3) editKitName(ek);
                        else if(r == 4) editKitLore(ek);
                        else if(r == 5) editKitChance(ek);
                        else if(r == 8) {
                            final GlobalKit gkit = (GlobalKit) ek.original.get(0).kit;
                            ek.delete();
                            edit(player, gkit, false);
                        }*/
                    }
                }
            }
        }

        @EventHandler
        private void playerInteractEvent(PlayerInteractEvent event) {
            final Player player = event.getPlayer();
            final ItemStack is = event.getItem();
            if(is != null && is.hasItemMeta() && is.isSimilar(gkitFallenHeroBundle)) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final List<String> s = gkits.getStringList("items.fallen hero bundle.reveals");
                final HashMap<String, GlobalKit> g = GlobalKit.kits;
                final int size = g.size();
                for(int i = 1; i <= gkits.getInt("items.fallen hero bundle.reveal amount"); i++) {
                    giveItem(player, g.get(s.get(random.nextInt(size))).getFallenHeroSpawnItem());
                }
            }
        }
    }

    private class vkitevents extends RandomPackageAPI implements Listener {

        @EventHandler
        private void inventoryClickEvent(InventoryClickEvent event) {
            if(!event.isCancelled() && event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() == event.getWhoClicked()) {
                final String t = event.getView().getTitle();
                if(t.equals(vkit.getTitle()) || t.equals(vkitPreview.getTitle())) {
                    final Player player = (Player) event.getWhoClicked();
                    event.setCancelled(true);
                    player.updateInventory();
                    final int r = event.getRawSlot();
                    if(r >= player.getOpenInventory().getTopInventory().getSize()) return;

                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    if(t.equals(vkitPreview.getTitle())) {
                        player.closeInventory();
                        sendStringListMessage(player, vkits.getStringList("messages.cannot withdraw"), null);
                    } else if(event.getClick().name().contains("RIGHT")) {
                        player.closeInventory();
                        final EvolutionKit vkit = EvolutionKit.valueOf(r);
                        give(player, vkit, true);
                    } else {
                        final EvolutionKit vkit = EvolutionKit.valueOf(r);
                        if(vkit == null) return;
                        final String n = vkit.getYamlName();
                        final HashMap<String, Long> cooldowns = pdata.getKitCooldowns(KitType.EVOLUTION);
                        final HashMap<String, Integer> levels = pdata.getKitLevels(KitType.EVOLUTION);
                        final boolean hasPerm = hasPermissionToObtain(player, vkit);
                        final long time = System.currentTimeMillis();
                        if(!hasPerm) {
                            sendStringListMessage(player, vkits.getStringList("messages.not unlocked kit"), null);
                        } else if(!cooldowns.keySet().contains(n) && (levels.containsKey(n) || !levels.containsKey(n) && player.hasPermission("RandomPackage.vkit." + n))
                                || cooldowns.keySet().contains(n) && cooldowns.get(n) <= time) {
                            give(player, vkit, false);
                            cooldowns.put(n, time+(vkit.getCooldown()*1000));
                            setCooldown(player, vkit);
                        }
                    }
                }
            }
        }
        @EventHandler
        private void playerInteractEvent(PlayerInteractEvent event) {
            final Player player = event.getPlayer();
            final ItemStack is = event.getItem();
            if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
                final EvolutionKit e = EvolutionKit.valueOfUpgradeGem(is);
                if(e != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    final HashMap<String, Integer> kits = pdata.getKitLevels(KitType.EVOLUTION);
                    final String n = e.getYamlName();
                    if(!kits.containsKey(n)) {
                        sendStringListMessage(player, vkits.getStringList("messages.not unlocked kit"), null);
                    } else {
                        final int lvl = kits.get(n);
                        final String name = e.getItem().getItemMeta().getDisplayName(), newl = Integer.toString(lvl+1);
                        if(lvl < e.getMaxLevel()) {
                            kits.put(n, lvl+1);
                        }
                        removeItem(player, is, 1);
                        for(String s : vkits.getStringList("messages.upgrade"))
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{LEVEL}", newl).replace("{VKIT}", name)));
                        for(String s : vkits.getStringList("messages.upgrade broadcast"))
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", player.getName()).replace("{VKIT}", name).replace("{LEVEL}", newl)));
                    }
                    player.updateInventory();
                }
            }
        }
    }

    private class mkitevents extends RandomPackageAPI implements Listener {

        @EventHandler
        private void inventoryClickEvent(InventoryClickEvent event) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory top = player.getOpenInventory().getTopInventory();
            if(!event.isCancelled() && top.getHolder() == player) {
                final String t = event.getView().getTitle();
                if(t.equals(mkit.getTitle())) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final int r = event.getRawSlot();
                    final String cl = event.getClick().name();
                    final MasteryKit m = MasteryKit.valueOf(r);
                    if(r < 0 || r >= top.getSize() || !cl.contains("LEFT") && !cl.contains("RIGHT") || event.getCurrentItem() == null || m == null) return;
                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    if(cl.contains("RIGHT")) {

                    } else {
                        if(pdata.getKitLevels(KitType.MASTERY).containsKey(m.getYamlName())) {

                        } else {
                            sendStringListMessage(player, mkits.getStringList("messages.not unlocked"), null);
                        }
                    }
                }
            }
        }

        @EventHandler
        private void playerInteractEvent(PlayerInteractEvent event) {
            final ItemStack is = event.getItem();
            if(is != null) {
                final Player player = event.getPlayer();
                final MasteryKit mkit = MasteryKit.valueOfRedeem(is);
                if(mkit != null) {
                    event.setCancelled(true);
                    player.updateInventory();

                    final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                    final HashMap<Object, Integer> required = mkit.getRequiredKits();
                    final List<GlobalKit> gkits = new ArrayList<>();
                    final List<EvolutionKit> vkits = new ArrayList<>();
                    for(Object o : required.keySet()) {
                        if(o instanceof GlobalKit) {
                            gkits.add((GlobalKit) o);
                        } else if(o instanceof EvolutionKit) {
                            vkits.add((EvolutionKit) o);
                        }
                    }
                    GlobalKit missingG = null;
                    EvolutionKit missingV = null;
                    final HashMap<String, Integer> l = pdata.getKitLevels(KitType.GLOBAL);
                    final HashMap<String, Integer> m = pdata.getKitLevels(KitType.EVOLUTION);
                    if(!gkits.isEmpty()) {
                        for(GlobalKit g : gkits) {
                            final String n = g.getYamlName();
                            if(missingG == null && (!l.containsKey(n) || l.get(n) < required.get(g))) {
                                missingG = g;
                            }
                        }
                    }
                    if(!vkits.isEmpty()) {
                        for(EvolutionKit v : vkits) {
                            final String n = v.getYamlName();
                            if(missingV == null && (!m.containsKey(n) || m.get(n) < required.get(v))) {
                                missingV = v;
                            }
                        }
                    }
                    final HashMap<String, String> replacements = new HashMap<>();
                    if(missingG != null) {
                        replacements.put("{KIT}", missingG.getItem().getItemMeta().getDisplayName());
                        replacements.put("{TIER}", toRoman(required.get(missingG)));
                        sendStringListMessage(player, mkits.getStringList("messages.unlock missing required gkit"), replacements);
                    } else if(missingV != null) {
                        replacements.put("{KIT}", missingV.getItem().getItemMeta().getDisplayName());
                        replacements.put("{TIER}", toRoman(required.get(missingV)));
                        sendStringListMessage(player, mkits.getStringList("messages.unlock missing required vkit"), replacements);
                    } else {
                        if(!gkits.isEmpty()) {
                            for(String s : colorizeListString(mkits.getStringList("messages.unlocked lost gkits"))) {
                                if(s.contains("{KIT}")) {
                                    for(GlobalKit k : gkits) {
                                        player.sendMessage(s.replace("{KIT}", k.getItem().getItemMeta().getDisplayName()));
                                    }
                                } else {
                                    player.sendMessage(s);
                                }
                            }
                            for(GlobalKit g : gkits) {
                                l.remove(g.getYamlName());
                                pdata.setKitCooldown(g, 0);
                            }
                        }
                        if(!vkits.isEmpty()) {
                            for(String s : colorizeListString(mkits.getStringList("messages.unlocked lost vkits"))) {
                                if(s.contains("{KIT}")) {
                                    for(EvolutionKit k : vkits) {
                                        player.sendMessage(s.replace("{KIT}", k.getItem().getItemMeta().getDisplayName()));
                                    }
                                } else {
                                    player.sendMessage(s);
                                }
                            }
                            for(EvolutionKit v : vkits) {
                                m.remove(v.getYamlName());
                                pdata.setKitCooldown(v, 0);
                            }
                        }
                        removeItem(player, is, 1);
                        pdata.getKitLevels(KitType.MASTERY).put(mkit.getYamlName(), 1);
                        replacements.put("{KIT}", mkit.getName());
                        sendStringListMessage(player, mkits.getStringList("messages.unlocked"), replacements);
                        player.updateInventory();
                    }
                }
            }
        }
    }
}
