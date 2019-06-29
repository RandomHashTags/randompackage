package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.api.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.api.events.customboss.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.api.events.customenchant.*;
import me.randomhashtags.randompackage.api.events.mobstacker.MobStackDepleteEvent;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.customenchants.*;
import me.randomhashtags.randompackage.utils.classes.customenchants.Fireball;
import me.randomhashtags.randompackage.utils.classes.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.utils.GivedpItem.givedpitem;

public class CustomEnchants extends CustomEnchantUtils implements CommandExecutor {
    private static CustomEnchants instance;
    public static CustomEnchants getCustomEnchants() {
        if(instance == null) instance = new CustomEnchants();
        return instance;
    }

    public boolean levelZeroRemoval;
    private String alchemistcurrency, enchantercurrency;
    private int alchemistCostSlot;
    private UInventory alchemist, enchanter, tinkerer;
    public ItemStack mysterydust, transmogscroll, whitescroll;
    private ItemStack tinkereraccept, alchemistpreview, alchemistexchange, alchemistaccept;
    private HashMap<Integer, Long> enchantercost;
    private HashMap<Integer, ItemStack> enchanterpurchase;
    private List<Player> invAccepting;
    private List<String> noMoreEnchantsAllowed;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String n = cmd.getName();
        if(n.equals("disabledenchants") && hasPermission(player, "RandomPackage.disabledenchants", true))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CustomEnchant.disabled.keySet().toString()));
        else if(player != null && n.equals("alchemist") && hasPermission(player, "RandomPackage.alchemist", true))viewAlchemist(player);
        else if(player != null && n.equals("enchanter") && hasPermission(player, "RandomPackage.enchanter", true))viewEnchanter(player);
        else if(player != null && n.equals("tinkerer") && hasPermission(player, "RandomPackage.tinkerer", true))  viewTinkerer(player);
        else if(n.equals("enchants") && hasPermission(sender, "RandomPackage.enchants", true)) {
            if(args.length == 0)
                viewEnchants(sender, 1);
            else {
                final int page = getRemainingInt(args[0]);
                viewEnchants(sender, page > 0 ? page : 1);
            }
        } else if(player != null && n.equals("splitsouls") && hasPermission(sender, "RandomPackage.splitsouls", true)) {
            splitsouls(player, args.length == 0 ? -1 : getRemainingInt(args[0]));
        }
        return true;
    }
    public void load() {
        loadUtils();
        long started = System.currentTimeMillis();
        levelZeroRemoval = config.getBoolean("settings.level zero removal");
        alchemistcurrency = config.getString("alchemist.currency").toUpperCase();
        enchantercurrency = config.getString("enchanter.currency").toUpperCase();
        alchemistpreview = d(config, "alchemist.preview");
        alchemistexchange = d(config, "alchemist.exchange");
        alchemistaccept = d(config, "alchemist.accept");
        tinkereraccept = d(config, "tinkerer.accept");
        noMoreEnchantsAllowed = colorizeListString(config.getStringList("settings.no more enchants"));

        int X = 0;
        for(String s : alchemistaccept.getItemMeta().getLore()) {
            if(s.contains("{COST}")) alchemistCostSlot = X;
            X++;
        }
        transmogscroll = d(config, "items.transmog scroll");
        whitescroll = d(config, "items.white scroll");

        givedpitem.items.put("transmogscroll", transmogscroll);
        givedpitem.items.put("whitescroll", whitescroll);

        enchantercost = new HashMap<>();
        enchanterpurchase = new HashMap<>();
        invAccepting = new ArrayList<>();

        alchemist = new UInventory(null, 27, ChatColor.translateAlternateColorCodes('&', config.getString("alchemist.title")));
        enchanter = new UInventory(null, config.getInt("enchanter.size"), ChatColor.translateAlternateColorCodes('&', config.getString("enchanter.title")));
        tinkerer = new UInventory(null, config.getInt("tinkerer.size"), ChatColor.translateAlternateColorCodes('&', config.getString("tinkerer.title")));
        setupInventory(alchemist);
        setupInventory(tinkerer);
        mysterydust = d(config, "items.mystery dust");

        final YamlConfiguration a = otherdata;
        final FileConfiguration RP = randompackage.getConfig();
        if(!a.getBoolean("saved default custom enchants")) {
            final String[] mas = new String[] {
                    "_settings",
                    "AUTO SELL",
                    "CHAIN LIFESTEAL",
                    "DEATH PACT",
                    "EXPLOSIVES EXPERT",
                    "FEIGN DEATH",
                    "HORRIFY",
                    "MARK OF THE BEAST",
                    "PERMAFROST", "POLTERGEIST",
                    "TOMBSTONE",
            };
            final String[] her = new String[] {
                    "_settings",
                    "ALIEN IMPLANTS", "ATOMIC DETONATE",
                    "BIDIRECTIONAL TELEPORTATION",
                    "DEEP BLEED", "DIVINE ENLIGHTED",
                    "ETHEREAL DODGE",
                    "GHOSTLY GHOST", "GODLY OVERLOAD", "GUIDED ROCKET ESCAPE",
                    "INFINITE LUCK",
                    "LETHAL SNIPER",
                    "MASTER BLACKSMITH", "MASTER INQUISITIVE", "MIGHTY CACTUS", "MIGHTY CLEAVE",
                    "PLANETARY DEATHBRINGER", "POLYMORPHIC METAPHYSICAL",
                    "REFLECTIVE BLOCK",
                    "SHADOW ASSASSIN",
                    "TITAN TRAP",
                    "VENGEFUL DIMINISH",
            };
            final String[] sou = new String[] {
                    "_settings",
                    "DIVINE IMMOLATION",
                    "HERO KILLER",
                    "IMMORTAL",
                    "NATURES WRATH",
                    "PARADOX", "PHOENIX",
                    "SOUL TRAP",
                    "TELEBLOCK",
            };
            final String[] leg = new String[] {
                    "_settings",
                    "AEGIS", "ANTI GANK", "ARMORED",
                    "BARBARIAN", "BLACKSMITH", "BLOOD LINK", "BLOOD LUST", "BOSS SLAYER",
                    "CLARITY",
                    "DEATH GOD", "DEATHBRINGER", "DESTRUCTION", "DEVOUR", "DIMINISH", "DISARMOR", "DOUBLE STRIKE", "DRUNK",
                    "ENCHANT REFLECT", "ENLIGHTED",
                    "GEARS",
                    "HEX",
                    "INQUISITIVE", "INSANITY", "INVERSION",
                    "KILL AURA",
                    "LEADERSHIP", "LIFESTEAL",
                    "OVERLOAD",
                    "PROTECTION",
                    "RAGE",
                    "SILENCE", "SNIPER",
            };
            final String[] ult = new String[] {
                    "_settings",
                    "ANGELIC", "ARROW BREAK", "ARROW DEFLECT", "ARROW LIFESTEAL", "ASSASSIN", "AVENGING ANGEL",
                    "BLEED", "BLESSED", "BLOCK",
                    "CLEAVE", "CORRUPT", "CREEPER ARMOR",
                    "DETONATE", "DIMENSION RIFT", "DISINTEGRATE", "DODGE", "DOMINATE",
                    "EAGLE EYE", "ENDER WALKER", "ENRAGE",
                    "FUSE",
                    "GHOST", "GUARDIANS",
                    "HEAVY", "HELLFIRE",
                    "ICEASPECT", "IMPLANTS",
                    "OBSIDIANSHIELD",
                    "LONGBOW", "LUCKY",
                    "MARKSMAN", "METAPHYSICAL",
                    "PACIFY", "PIERCING",
                    "SPIRITS", "STICKY",
                    "TANK",
                    "UNFOCUS",
                    "VALOR",
            };
            final String[] eli = new String[] {
                    "_settings",
                    "ANTI GRAVITY",
                    "BLIND",
                    "CACTUS",
                    "DEMONFORGED",
                    "EXECUTE",
                    "FARCAST", "FROZEN",
                    "GREATSWORD",
                    "HARDENED", "HIJACK",
                    "ICE FREEZE", "INFERNAL",
                    "PARALYZE", "POISON", "POISONED", "PUMMEL",
                    "REFORGED", "REPAIR GUARD", "RESILIENCE", "ROCKET ESCAPE",
                    "SHACKLE", "SHOCKWAVE", "SMOKE BOMB", "SNARE", "SOLITUDE", "SPIRIT LINK", "SPRINGS", "STORMCALLER",
                    "TELEPORTATION", "TRAP", "TRICKSTER",
                    "UNDEAD RUSE",
                    "VAMPIRE", "VENOM", "VOODOO",
                    "WITHER",
            };
            final String[] uni = new String[] {
                    "_settings",
                    "BERSERK",
                    "COMMANDER", "COWIFICATION", "CURSE",
                    "DEEP WOUNDS",
                    "ENDER SHIFT", "EXPLOSIVE",
                    "FEATHERWEIGHT",
                    "LIFEBLOOM",
                    "MOLTEN",
                    "NIMBLE", "NUTRITION",
                    "OBSIDIAN DESTROYER",
                    "PLAGUE CARRIER",
                    "RAGDOLL", "RAVENOUS",
                    "SELF DESTRUCT", "SKILL SWIPE", "SKILLING",
                    "TELEPATHY", "TRAINING",
                    "VIRUS",
            };
            final String[] sim = new String[] {
                    "_settings",
                    "AQUATIC", "AUTO SMELT",
                    "CONFUSION",
                    "DECAPITATION",
                    "EPICNESS", "EXPERIENCE",
                    "OXYGENATE",
                    "GLOWING",
                    "HASTE", "HEADLESS", "HEALING",
                    "INSOMNIA",
                    "LIGHTNING",
                    "OBLITERATE",
                    "TARGET TRACKING", "THUNDERING BLOW",
            };
            for(String s : mas) save("custom enchants" + separator + "MASTERY", s + ".yml");
            for(String s : her) save("custom enchants" + separator + "HEROIC", s + ".yml");
            for(String s : sou) save("custom enchants" + separator + "SOUL", s + ".yml");
            for(String s : leg) save("custom enchants" + separator + "LEGENDARY", s + ".yml");
            for(String s : ult) save("custom enchants" + separator + "ULTIMATE", s + ".yml");
            for(String s : eli) save("custom enchants" + separator + "ELITE", s + ".yml");
            for(String s : uni) save("custom enchants" + separator + "UNIQUE", s + ".yml");
            for(String s : sim) save("custom enchants" + separator + "SIMPLE", s + ".yml");

            a.set("saved default custom enchants", true);
            saveOtherData();
        }
        final String p = rpd + separator + "custom enchants";
        final List<ItemStack> raritybooks = new ArrayList<>();
        final File folder = new File(p);
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                if(f.isDirectory()) {
                    final File[] files = new File(p + separator + f.getName()).listFiles();
                    if(files != null) {
                        EnchantRarity rarity = null;
                        final List<File> F = Arrays.asList(files);
                        for(File k : F) {
                            if(k.getName().contains("_settings")) {
                                rarity = new EnchantRarity(f, k);
                                raritybooks.add(rarity.getRevealItem());
                            }
                        }
                        if(rarity != null) {
                            for(File ff : files) {
                                if(!ff.getName().startsWith("_settings")) {
                                    final CustomEnchant e = new CustomEnchant(ff);
                                    if(e.isEnabled()) rarity.getEnchants().add(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        long Q = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded [&f" + CustomEnchant.enabled.size() + "e, &c" + CustomEnchant.disabled.size() + "d&a] Custom Enchants &e(took " + (Q-started) + "ms)");
        addGivedpCategory(raritybooks, UMaterial.BOOK, "Rarity Books", "Givedp: Rarity Books'");
        started = Q;

        if(RP.getBoolean("custom enchants.black scrolls")) {
            Q = System.currentTimeMillis();
            save("custom enchants", "black scrolls.yml");
            final YamlConfiguration r = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "black scrolls.yml"));
            final ConfigurationSection cs = r.getConfigurationSection("scrolls");
            if(cs != null) {
                for(String s : cs.getKeys(false)) {
                    new BlackScroll(s);
                }
                final HashMap<String, BlackScroll> L = BlackScroll.scrolls;
                sendConsoleMessage("&6[RandomPackage] &aLoaded " + (L != null ? L.size() : 0) + " Black Scrolls &e(took " + (Q-started) + "ms)");
                started = Q;
            }
        }
        if(RP.getBoolean("custom enchants.dusts")) {
            Q = System.currentTimeMillis();
            save("custom enchants", "dusts.yml");
            final YamlConfiguration r = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "dusts.yml"));
            final ConfigurationSection cs = r.getConfigurationSection("dusts");
            if(cs != null) {
                for(String s : cs.getKeys(false)) {
                    new MagicDust(s);
                }
                final HashMap<String, MagicDust> L = MagicDust.dust;
                sendConsoleMessage("&6[RandomPackage] &aLoaded " + (L != null ? L.size() : 0) + " Magic Dust &e(took " + (Q-started) + "ms)");
                started = Q;
            }
        }
        if(RP.getBoolean("custom enchants.enchantment orbs")) {
            Q = System.currentTimeMillis();
            save("custom enchants", "enchantment orbs.yml");
            final YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "enchantment orbs.yml"));
            final List<ItemStack> orbs = new ArrayList<>();
            for(String A : yml.getConfigurationSection("orbs").getKeys(false)) {
                final ItemStack iii = d(yml, "orbs." + A);
                final List<String> appliesto = new ArrayList<>();
                for(String s : yml.getString("orbs." + A + ".applies to").split(";")) appliesto.add(s.toUpperCase());
                final int starting = yml.getInt("orbs." + A + ".starting max slots");
                final int increment = yml.getInt("orbs." + A + ".upgrade increment");
                int increm = increment;
                for(int k = starting; k <= yml.getInt("orbs." + A + ".final max slots"); k += increment) {
                    if(k != starting) increm += increment;
                    final String slots = Integer.toString(k), increments = Integer.toString(increm), appliedlore = ChatColor.translateAlternateColorCodes('&', yml.getString("orbs." + A + ".apply").replace("{SLOTS}", slots).replace("{ADD_SLOTS}", increments));
                    item = iii.clone(); itemMeta = item.getItemMeta(); lore.clear();
                    itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOTS}", slots));
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{SLOTS}", slots).replace("{INCREMENT}", increments));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    new EnchantmentOrb(A, item, appliedlore, appliesto, k, increm);
                    item = item.clone(); itemMeta = item.getItemMeta(); lore.clear();
                    for(String s: itemMeta.getLore()) lore.add(s.replace("{PERCENT}", "100"));
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    orbs.add(item);
                }
            }
            addGivedpCategory(orbs, UMaterial.ENDER_EYE, "Enchantment Orbs", "Givedp: Enchantment Orbs");
            final HashMap<String, EnchantmentOrb> E = EnchantmentOrb.orbs;
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + (E != null ? E.size() : 0) + " Enchantment Orbs &e(took " + (Q-started) + "ms)");
            started = Q;
        }
        if(RP.getBoolean("custom enchants.fireballs")) {
            Q = System.currentTimeMillis();
            givedpitem.items.put("mysterydust", d(config, "items.mystery dust"));
            save("custom enchants", "fireballs.yml");
            final YamlConfiguration r = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "fireballs.yml"));
            final ConfigurationSection cs = r.getConfigurationSection("fireballs");
            if(cs != null) {
                final List<ItemStack> fireballs = new ArrayList<>();
                for(String s : cs.getKeys(false)) {
                    final Fireball f = new Fireball(s);
                    fireballs.add(f.getItem());
                }
                addGivedpCategory(fireballs, UMaterial.FIRE_CHARGE, "Rarity Fireballs", "Givedp: Rarity Fireballs");
                final HashMap<String, Fireball> f = Fireball.fireballs;
                sendConsoleMessage("&6[RandomPackage] &aLoaded " + (f != null ? f.size() : 0) + " Fireballs &e(took " + (Q-started) + "ms)");
                started = Q;
            }
        }
        if(RP.getBoolean("custom enchants.rarity gems")) {
            Q = System.currentTimeMillis();
            save("custom enchants", "rarity gems.yml");
            final YamlConfiguration r = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "rarity gems.yml"));
            final ConfigurationSection cs = r.getConfigurationSection("gems");
            if(cs != null) {
                RarityGem.defaultColors = new HashMap<>();
                final HashMap<Integer, String> d = RarityGem.defaultColors;
                final ConfigurationSection C = r.getConfigurationSection("default settings.colors");
                d.put(-1, ChatColor.translateAlternateColorCodes('&', r.getString("default settings.colors.else")));
                d.put(0, ChatColor.translateAlternateColorCodes('&', r.getString("default settings.colors.less than 100")));
                for(String s : C.getKeys(false)) {
                    if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
                        d.put(Integer.parseInt(s.split("s")[0]), ChatColor.translateAlternateColorCodes('&', r.getString("default settings.colors." + s)));
                    }
                }
                for(String s : cs.getKeys(false)) {
                    new RarityGem(r, s);
                }
                final HashMap<String, RarityGem> g = RarityGem.gems;
                sendConsoleMessage("&6[RandomPackage] &aLoaded " + (g != null ? g.size() : 0) + " Rarity Gems &e(took " + (Q-started) + "ms)");
                started = Q;
            }
        }
        if(RP.getBoolean("custom enchants.soul trackers")) {
            Q = System.currentTimeMillis();
            save("custom enchants", "soul trackers.yml");
            final YamlConfiguration r = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "soul trackers.yml"));
            final ConfigurationSection cs = r.getConfigurationSection("trackers");
            if(cs != null) {
                final List<ItemStack> z = new ArrayList<>();
                for(String s : cs.getKeys(false)) {
                    z.add(new SoulTracker(s).getItem());
                }
                addGivedpCategory(z, UMaterial.PAPER, "Soul Trackers", "Givedp: Soul Trackers");
                final HashMap<String, SoulTracker> s = SoulTracker.trackers;
                sendConsoleMessage("&6[RandomPackage] &aLoaded " + (s != null ? s.size() : 0) + " Soul Trackers &e(took " + (Q-started) + "ms)");
                started = Q;
            }
        }
        if(RP.getBoolean("custom enchants.randomization scrolls")) {
            Q = System.currentTimeMillis();
            save("custom enchants", "randomization scrolls.yml");
            final YamlConfiguration r = YamlConfiguration.loadConfiguration(new File(rpd + separator + "custom enchants", "randomization scrolls.yml"));
            final ConfigurationSection cs = r.getConfigurationSection("scrolls");
            if(cs != null) {
                final List<ItemStack> z = new ArrayList<>();
                for(String s : cs.getKeys(false)) {
                    z.add(new RandomizationScroll(s).getItem());
                }
                addGivedpCategory(z, UMaterial.PAPER, "Randomization Scrolls", "Givedp: Randomization Scrolls");
                final HashMap<String, RandomizationScroll> s = RandomizationScroll.scrolls;
                sendConsoleMessage("&6[RandomPackage] &aLoaded " + (s != null ? s.size() : 0) + " Randomization Scrolls &e(took " + (Q-started) + "ms)");
                started = Q;
            }
        }

        boolean dropsItemsUponDeath = config.getBoolean("entities.settings.default drops items upon death"), canTargetSummoner = config.getBoolean("entities.settings.default can target summoner");
        final HashMap<String, CustomEnchantEntity> entities = CustomEnchantEntity.paths;
        for(String s : config.getConfigurationSection("entities").getKeys(false)) {
            if(!s.startsWith("settings")) {
                final String path = s.split("\\.")[0];
                if(entities == null || !entities.containsKey(path)) {
                    canTargetSummoner = config.get("entities." + path + ".can target summoner") != null ? config.getBoolean("entities." + path + ".can target summoner") : canTargetSummoner;
                    dropsItemsUponDeath = config.get("entities." + path + ".drops items upon death") != null ? config.getBoolean("entities." + path + ".drops items upon death") : dropsItemsUponDeath;
                    new CustomEnchantEntity(EntityType.valueOf(config.getString("entities." + path + ".type").toUpperCase()), path, config.getString("entities." + path + ".name"), config.getStringList("entities." + path + ".attributes"), canTargetSummoner, dropsItemsUponDeath);
                }
            }
        }

        final Inventory ei = enchanter.getInventory();
        for(int i = 0; i < enchanter.getSize(); i++) {
            if(config.get("enchanter." + i) != null) {
                final long cost = config.getLong("enchanter." + i + ".cost");
                enchantercost.put(i, cost);
                enchanterpurchase.put(i, d(null, config.getString("enchanter." + i + ".purchase")));
                item = d(config, "enchanter." + i); itemMeta = item.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{COST}")) string = string.replace("{COST}", formatLong(cost));
                        lore.add(string);
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
                ei.setItem(i, item);
            }
        }
    }
    public void unload() {
        unloadUtils();
        alchemist = null;
        enchanter = null;
        tinkerer = null;
        mysterydust = null;
        enchantercost = null;
        enchanterpurchase = null;
        alchemistpreview = null;
        alchemistexchange = null;
        alchemistaccept = null;
        noMoreEnchantsAllowed = null;
        whitescroll = null;
        transmogscroll = null;
        givedpitem.items.remove("transmogscroll");
        givedpitem.items.remove("whitescroll");
        CustomEnchantEntity.deleteAll();
        CustomEnchant.deleteAll();
        EnchantRarity.deleteAll();
        RarityGem.deleteAll();
        SoulTracker.deleteAll();
        RandomizationScroll.deleteAll();
        BlackScroll.deleteAll();
        Fireball.deleteAll();
        MagicDust.deleteAll();
    }


    public void viewEnchants(CommandSender sender, int page) {
        final ChatEvents cea = ChatEvents.getChatEvents();
        final String format = randompackage.getConfig().getString("enchants.format");
        final List<String> L = colorizeListString(randompackage.getConfig().getStringList("enchants.hover"));
        final int size = CustomEnchant.enabled.size(), maxpage = size/10;
        page = page > maxpage ? maxpage : page;
        final int starting = page*10;
        for(String s : randompackage.getConfig().getStringList("enchants.msg")) {
            if(s.equals("{ENCHANTS}")) {
                for(int i = starting; i <= starting+10; i++) {
                    if(size > i) {
                        final CustomEnchant ce = (CustomEnchant) CustomEnchant.enabled.values().toArray()[i];
                        final EnchantRarity rarity = EnchantRarity.valueOf(ce);
                        final HashMap<String, List<String>> replacements = new HashMap<>();
                        replacements.put("{TIER}", Arrays.asList(rarity.getApplyColors() + rarity.getName()));
                        replacements.put("{DESC}", ce.getLore());
                        final String msg = ChatColor.translateAlternateColorCodes('&', format.replace("{MAX}", Integer.toString(ce.getMaxLevel())).replace("{ENCHANT}", rarity.getApplyColors() + ChatColor.BOLD + ce.getName()));
                        if(sender instanceof Player) {
                            lore.clear();
                            lore.addAll(L);
                            cea.sendHoverMessage((Player) sender, msg, lore, replacements);
                            lore.clear();
                        } else {
                            sender.sendMessage(msg);
                        }

                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{MAX_PAGE}", Integer.toString(maxpage)).replace("{PAGE}", Integer.toString(page))));
            }
        }
    }
    public void viewAlchemist(Player player) { openInventory(player, alchemist); }
    public void viewEnchanter(Player player) { openInventory(player, enchanter); }
    public void viewTinkerer(Player player) { openInventory(player, tinkerer); }
    private void openInventory(Player player, UInventory inv) {
        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(inv.getInventory().getContents());
        player.updateInventory();
    }
    private void setupInventory(UInventory inv) {
        final Inventory in = inv.getInventory();
        ItemStack i1 = inv.equals(tinkerer) ? d(config, "tinkerer.divider") : d(config, "alchemist.exchange"),
                i2 = inv.equals(alchemist) ? d(config, "alchemist.preview") : null,
                i3 = inv.equals(alchemist) ? d(config, "alchemist.other") : null;
        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.equals(alchemist)) {
                if(i == 3 || i == 5) {}
                else if(i == 13) in.setItem(i, i2);
                else if(i == 22) in.setItem(i, i1);
                else in.setItem(i, i3);
            } else if(inv.equals(tinkerer)) {
                if(i == 4 || i == 13 || i == 22 || i == 31 || i == 40 || i == 49) in.setItem(i, i1);
                else if(i == 0) in.setItem(i, d(config, "tinkerer.accept"));
                else if(i == 8) in.setItem(i, d(config, "tinkerer.accept dupe"));
            }

        }
    }

    public void stopTimerEnchant(CustomEnchant enchant) {
        sendConsoleMessage("&6[RandomPackage.CustomEnchants] &3Stopped Timer for enchant &7" + enchant.getName());
        scheduler.cancelTask(timerenchants.get(enchant));
        timerenchants.remove(enchant);
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void playerDropItemEvent(PlayerDropItemEvent event) {
        if(!event.isCancelled()) {
            final RarityGem gem = RarityGem.valueOf(event.getItemDrop().getItemStack());
            if(gem != null) {
                final RPPlayer pdata = RPPlayer.get(event.getPlayer().getUniqueId());
                if(pdata.hasActiveRarityGem(gem)) pdata.toggleRarityGem(event, gem);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void playerArmorEvent(PlayerArmorEvent event) {
        if(!event.isCancelled()) {
            final ItemStack itemstack = event.getItem();
            if(itemstack != null && itemstack.hasItemMeta() && itemstack.getItemMeta().hasLore()) {
                for(String s : itemstack.getItemMeta().getLore()) {
                    final CustomEnchant enchant = CustomEnchant.valueOf(s);
                    if(enchant != null)
                        procEnchant(event, enchant, getEnchantmentLevel(s), itemstack, null);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) {

        } else if(event.getEntity() instanceof LivingEntity && canProcOn(event.getEntity())) {
            final Entity entity = event.getEntity();
            Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;
            if(damager != null) {
                final PvAnyEvent e = new PvAnyEvent(damager, (LivingEntity) entity, event.getDamage());
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    event.setDamage(e.damage);
                    procPlayerArmor(e, damager);
                    procPlayerItem(e, damager, getItemInHand(damager));
                }
            }
            final UUID u = event.getDamager().getUniqueId();
            if(damager == null && event.getDamager() instanceof Arrow && shotbows.keySet().contains(u)) {
                damager = shotbows.get(u);
                final PvAnyEvent e = new PvAnyEvent(damager, (LivingEntity) entity, event.getDamage(), (Projectile) event.getDamager());
                pluginmanager.callEvent(e);
                if(!event.isCancelled()) {
                    procPlayerArmor(e, damager);
                    procPlayerItem(e, damager, shotBows.get(u));
                    shotBows.remove(u);
                    shotbows.remove(u);
                    event.setDamage(e.damage);
                }
            }
            if(entity instanceof Player && event.getDamager() instanceof LivingEntity && !(event.getDamager() instanceof TNTPrimed) && !(event.getDamager() instanceof Creeper)) {
                final Player victim = (Player) entity;
                final LivingEntity d = (LivingEntity) event.getDamager();
                final isDamagedEvent e = new isDamagedEvent(victim, d, event.getDamage());
                pluginmanager.callEvent(e);
                if(!e.isCancelled()) {
                    event.setDamage(e.damage);
                    procPlayerArmor(e, victim);
                    procPlayerItem(e, victim, getItemInHand(victim));
                }
            }
            if(canProcOn(entity)) {
                final HashMap<UUID, LivingCustomEnchantEntity> L = LivingCustomEnchantEntity.living;
                if(L != null) {
                    final LivingCustomEnchantEntity cee = L.getOrDefault(entity.getUniqueId(), null);
                    if(cee != null) {
                        final CustomEnchantEntityDamageByEntityEvent e = new CustomEnchantEntityDamageByEntityEvent(cee, event.getDamager(), event.getFinalDamage(), event.getDamage());
                        pluginmanager.callEvent(e);
                        if(!e.isCancelled()) {
                            final LivingEntity le = cee.getSummoner();
                            final Player player = le instanceof Player ? (Player) le : null;
                            procPlayerArmor(e, player);
                            procPlayerItem(e, player, getItemInHand(player));
                        }
                    }
                }
            }
            if(damager != null && combos.containsKey(damager)) {
                final double d = event.getDamage();
                for(CustomEnchant enchant : combos.get(damager).keySet()) {
                    event.setDamage(d*combos.get(damager).get(enchant));
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
        if(!event.isCancelled()) {
            final Entity d = event.damager;
            if(d instanceof Player) {
                final Player damager = (Player) d;
                procPlayerArmor(event, damager);
                procPlayerItem(event, damager, getItemInHand(damager));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void mobStackDepleteEvent(MobStackDepleteEvent event) {
        if(!event.isCancelled()) {
            final Player killer = event.killer instanceof Player ? (Player) event.killer : null;
            if(killer != null) {
                procPlayerItem(event, killer, getItemInHand(killer));
                procPlayerArmor(event, killer);
            }
        }
    }

    public ItemStack getRevealedItem(CustomEnchant enchant, int level, int success, int destroy, boolean showEnchantType, boolean showOtherLore) {
        final EnchantRarity rarity = EnchantRarity.valueOf(enchant);
        item = rarity.getRevealedItem().clone(); itemMeta = item.getItemMeta(); lore.clear();
        itemMeta.setDisplayName(rarity.getNameColors() + enchant.getName() + " " + toRoman(level));
        final String S = rarity.getSuccess(), D = rarity.getDestroy();
        final List<String> l = enchant.getLore();
        for(String r : rarity.getLoreFormat()) {
            if(r.equals("{SUCCESS}")) {
                if(success != -1) lore.add(ChatColor.translateAlternateColorCodes('&', S.replace("{PERCENT}", Integer.toString(success))));
            } else if(r.equals("{DESTROY}")) {
                if(destroy != -1) lore.add(ChatColor.translateAlternateColorCodes('&', D.replace("{PERCENT}", Integer.toString(destroy))));
            } else if(r.equals("{ENCHANT_LORE}")) {
                lore.addAll(l);
            } else if(r.equals("{ENCHANT_TYPE}") && showEnchantType) {
                final String path = enchant.getAppliesTo().toString().toLowerCase().replace(",", ";").replace("[", "").replace("]", "").replaceAll("\\p{Z}", "");
                lore.add(ChatColor.translateAlternateColorCodes('&', config.getString("enchant types." + path)));
            } else if(showOtherLore)
                lore.add(ChatColor.translateAlternateColorCodes('&', r));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack getRandomEnabledEnchant(EnchantRarity rarity) {
        final String[] r = rarity.getRevealedEnchantRarities();
        final int l = r.length;
        final EnchantRarity rar = EnchantRarity.rarities.get(rarity.getRevealedEnchantRarities()[random.nextInt(l)]);
        final List<CustomEnchant> enchants = rar.getEnchants();
        final CustomEnchant enchant = enchants.get(random.nextInt(enchants.size()));
        final int level = random.nextInt(enchant.getMaxLevel()+1);
        item = rarity.getRevealedItem().clone(); itemMeta = item.getItemMeta(); lore.clear();
        itemMeta.setDisplayName(rarity.getNameColors() + enchant.getName() + " " + toRoman(level == 0 ? 1 : level));
        final String appliesto = enchant.getAppliesTo().toString().replace(" ", "").replace(",", ";");
        final int sp = random.nextInt(101), dp = rarity.percentsAddUpto100() ? 100-sp : random.nextInt(101);
        for(String s : rarity.getLoreFormat()) {
            if(s.equals("{SUCCESS}")) s = rarity.getSuccess().replace("{PERCENT}", Integer.toString(sp));
            if(s.equals("{DESTROY}")) s = rarity.getDestroy().replace("{PERCENT}", Integer.toString(dp));
            if(s.equals("{ENCHANT_LORE}")) lore.addAll(enchant.getLore());
            if(s.equals("{ENCHANT_TYPE}")) s = config.getString("enchant types." + appliesto.substring(1, appliesto.length()-1));
            if(s != null && !s.equals("{ENCHANT_LORE}")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void entityDamageEvent(EntityDamageEvent event) {
        final EntityDamageEvent.DamageCause c = event.getCause();
        final Entity E = event.getEntity();
        if(!event.isCancelled() &&E instanceof Player && !c.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            final Player victim = (Player) E;
            final isDamagedEvent e = new isDamagedEvent(victim, c);
            pluginmanager.callEvent(e);
            if(!e.isCancelled()) {
                procPlayerArmor(e, victim);
                procPlayerItem(e, victim, null);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreakEvent(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            procPlayerArmor(event, player);
            procPlayerItem(event, player, null);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            procPlayerArmor(event, player);
            procPlayerItem(event, player, null);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void playerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        procPlayerArmor(event, player);
        procPlayerItem(event, player, null);
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack I = event.getItem();
        final Player player = event.getPlayer();
        if(!event.isCancelled()) {
            item = getItemInHand(player);
            procPlayerArmor(event, player);
            procPlayerItem(event, player, item);
        }
        EnchantRarity rarity = EnchantRarity.valueOf(I);
        final Fireball fireball = rarity == null ? Fireball.valueOf(I) : null;
        final RarityGem gem = fireball == null ? RarityGem.valueOf(I) : null;
        final EnchantmentOrb eo = gem == null ? EnchantmentOrb.valueOf(I) : null;
        if(rarity != null) {
            final ItemStack r = getRandomEnabledEnchant(rarity);
            final String displayname = r.getItemMeta().getDisplayName();
            final CustomEnchant enchant = CustomEnchant.valueOf(r);
            final PlayerRevealCustomEnchantEvent e = new PlayerRevealCustomEnchantEvent(player, I, enchant, getEnchantmentLevel(displayname));
            pluginmanager.callEvent(e);
            if(!e.isCancelled()) {
                event.setCancelled(true);
                removeItem(player, I, 1);
                giveItem(player, r);
                spawnFirework(rarity.getFirework(), player.getLocation());
                player.updateInventory();
                for(String s : rarity.getRevealedEnchantMsg()) player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{ENCHANT}", displayname)));
            }
        } else if(fireball != null) {
            event.setCancelled(true);
            removeItem(player, I, 1);
            ItemStack reward = fireball.getRevealedItem(true);
            if(reward == null) {
                reward = mysterydust.clone();
                playParticle(config, "dust.particles.default.mystery", player.getEyeLocation(), 15);
                playSound(config, "dust.sounds.reveal dust", player, player.getLocation(), false);
            }
            giveItem(player, reward);
        } else if(gem != null) {
            event.setCancelled(true);
            player.updateInventory();
            RPPlayer.get(player.getUniqueId()).toggleRarityGem(event, gem);
        } else if(eo != null) {
            event.setCancelled(true);
            player.updateInventory();
        } else if(I != null && I.hasItemMeta() && I.getItemMeta().hasDisplayName() && I.getItemMeta().hasLore()) {
            if(I.getItemMeta().equals(whitescroll.getItemMeta()) || I.getItemMeta().equals(transmogscroll.getItemMeta())) {
                event.setCancelled(true);
                player.updateInventory();
            } else {
                final CustomEnchant enchant = CustomEnchant.valueOf(I);
                if(enchant != null)
                    for(String s : config.getStringList("rarities.apply info")) player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        if(!event.isCancelled() && event.getEntity() instanceof Player) {
            final Player p = (Player) event.getEntity();
            procPlayerArmor(event, p);
            procPlayerItem(event, p, null);
            final UUID u = event.getProjectile().getUniqueId();
            shotBows.put(u, getItemInHand(p));
            shotbows.put(u, p);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void projectileHitEvent(ProjectileHitEvent event) {
        final Projectile e = event.getEntity();
        final UUID u = e.getUniqueId();
        final ProjectileSource shooter = e.getShooter();
        if(shooter instanceof Player && shotBows.keySet().contains(u)) {
            scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                final ItemStack is = shotBows.get(u);
                procPlayerItem(event, (Player) shooter, is);
                shotBows.remove(u);
                shotbows.remove(u);
            }, 0);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void playerDeathEvent(PlayerDeathEvent event) {
        final Player p = event.getEntity(), k = p.getKiller();
        procPlayerArmor(event, p);
        procPlayerItem(event, p, null);
        procPlayerArmor(event, k);
        procPlayerItem(event, k, null);
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void playerItemDamageEvent(PlayerItemDamageEvent event) {
        if(!event.isCancelled()) {
            procPlayerItem(event, event.getPlayer(), event.getItem());
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        final Player k = e.getKiller();
        final UUID u = e.getUniqueId();
        spawnedFromSpawner.remove(u);
        if(e instanceof Player) {
            final RPPlayer pdata = RPPlayer.get(u);
            for(RarityGem g : pdata.getRarityGems().keySet())
                pdata.toggleRarityGem(event, g);
        }
        if(!(e instanceof Player) && k != null) {
            procPlayerArmor(event, k);
            procPlayerItem(event, k, null);
        }
        final HashMap<UUID, LivingCustomEnchantEntity> L = LivingCustomEnchantEntity.living;
        if(L != null) {
            final LivingCustomEnchantEntity entity = L.getOrDefault(u, null);
            if(entity != null) {
                if(entity.getType().dropsItemsUponDeath()) {
                    event.getDrops().clear(); event.setDroppedExp(0);
                }
                final LivingEntity s = entity.getSummoner();
                if(s instanceof Player) {
                    final RPPlayer pdata = RPPlayer.get(s.getUniqueId());
                    pdata.removeCustomEnchantEntity(u);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        final Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if(!event.isCancelled() && player != null) {
            procPlayerArmor(event, player);
            procPlayerItem(event, player, null);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(!event.isCancelled() && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            spawnedFromSpawner.add(event.getEntity().getUniqueId());
        }
    }
    @EventHandler
    private void tinkererClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(!event.isCancelled() && top.getHolder() == player) {
            final String t = event.getView().getTitle();
            if(t.equals(tinkerer.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot(), size = top.getSize();
                int SLOT = top.firstEmpty();
                final ItemStack cur = event.getCurrentItem();
                final String c = event.getClick().name(), cu = cur != null ? cur.getType().name() : null;
                if(r < 0 || !c.contains("LEFT") && !c.contains("RIGHT") || cu == null || cu.equals("AIR")) return;

                final CustomEnchant e = cur.hasItemMeta() && cur.getItemMeta().hasDisplayName() ? CustomEnchant.valueOf(cur.getItemMeta().getDisplayName()) : null;
                if(r >= 4 && r <= 8
                        || r >= 13 && r <= 17
                        || r >= 22 && r <= 26
                        || r >= 31 && r <= 35
                        || r >= 40 && r <= 44
                        || r >= 49 && r <= 53) {
                    return;
                } else if(cur.equals(tinkereraccept)) {
                    invAccepting.add(player);
                    player.closeInventory();
                    return;
                } else if(r < size) {
                    giveItem(player, cur);
                    item = new ItemStack(Material.AIR);
                    SLOT = r;
                } else if(top.firstEmpty() < 0) {
                    return;
                } else if(e != null) {
                    final EnchantRarity R = EnchantRarity.valueOf(e);
                    final Fireball f = Fireball.valueOf(Arrays.asList(R));
                    if(f != null) {
                        final ItemStack itemstack = f.getItem();
                        if(itemstack == null) return;
                        item = itemstack.clone();
                    } else {
                        return;
                    }
                } else if(cur.getItemMeta().hasEnchants() && (cu.endsWith("HELMET") || cu.endsWith("CHESTPLATE") || cu.endsWith("LEGGINGS") || cu.endsWith("BOOTS") || cu.endsWith("SWORD") || cu.endsWith("AXE") || cu.endsWith("SPADE") || cu.endsWith("SHOVEL") || cu.endsWith("HOE") || cu.endsWith("BOW"))) {
                    int xp = 0;
                    for(Enchantment enchant : cur.getEnchantments().keySet())
                        xp += Integer.parseInt(config.getString("tinkerer.enchant values." + enchant.getName().toLowerCase()));
                    if(cur.hasItemMeta() && cur.getItemMeta().hasLore()) {
                        final HashMap<CustomEnchant, Integer> enchants = getEnchants(cur);
                        for(CustomEnchant enchant : enchants.keySet()) xp += enchant.getTinkererValue(enchants.get(enchant));
                    }
                    if(xp != 0) item = givedpitem.getXPBottle(xp, "Tinkerer").clone();
                } else {
                    sendStringListMessage(player, config.getStringList("tinkerer.messages.doesnt want that item"), null);
                    return;
                }
                final int first = top.firstEmpty();
                int slot = first <= 3 || r <= 3 ? 4 : 5;
                top.setItem(SLOT + slot, item);
                if(r >= size) top.setItem(first, cur);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String title = event.getView().getTitle();
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        if(!event.isCancelled()
                && cursor != null && current != null
                && cursor.hasItemMeta()
                && cursor.getItemMeta().hasDisplayName()
                && cursor.getItemMeta().hasLore()
                || title.equals(alchemist.getTitle())
                || title.equals(enchanter.getTitle())
        ) {
            final int r = event.getRawSlot(), size = top.getSize();
            if(title.equals(tinkerer.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                if(event.getClick().equals(ClickType.NUMBER_KEY)) return;
            } else if(title.equals(alchemist.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                if(r < size) {
                    if(r == 3 || r == 5) {
                        giveItem(player, current);
                        top.setItem(r, new ItemStack(Material.AIR));

                        item = alchemistpreview.clone();
                        if(!top.getItem(13).equals(item)) top.setItem(13, item);
                        item = alchemistexchange.clone();
                        if(!top.getItem(22).equals(item)) top.setItem(22, item);

                    } else if(r == 22 && top.getItem(3) != null && top.getItem(5) != null && !top.getItem(13).equals(alchemistpreview)) {
                        final int cost = getRemainingInt(top.getItem(22).getItemMeta().getLore().get(alchemistCostSlot));
                        final AlchemistExchangeEvent e = new AlchemistExchangeEvent(player, top.getItem(3), top.getItem(5), alchemistcurrency, cost,top.getItem(13));
                        pluginmanager.callEvent(e);
                        if(!e.isCancelled()) {
                            final Location l = player.getLocation();
                            if(!player.getGameMode().equals(GameMode.CREATIVE)) {
                                boolean notenough = false;
                                if(alchemistcurrency.equals("EXP")) {
                                    final int totalxp = getTotalExperience(player);
                                    if(totalxp < cost) {
                                        notenough = true;
                                        sendStringListMessage(player, config.getStringList("alchemist.messages.not enough xp"), null);
                                    } else {
                                        setTotalExperience(player, totalxp - cost);
                                    }
                                    playSound(config, "alchemist." + (notenough ? "need more xp" : "upgrade via xp"), player, l, false);
                                } else if(eco != null) {
                                    if(!eco.withdrawPlayer(player, cost).transactionSuccess()) {
                                        notenough = true;
                                        sendStringListMessage(player, config.getStringList("alchemist.messages.not enough cash"), null);
                                    }
                                    playSound(config, "alchemist." + (notenough ? "need more cash" : "upgrade via cash"), player, l, false);
                                }
                                else return;
                                if(notenough) {
                                    player.closeInventory();
                                    player.updateInventory();
                                    return;
                                }
                            } else playSound(config, "alchemist.upgrade creative", player, l, false);
                            item = top.getItem(13).clone(); itemMeta = item.getItemMeta();
                            itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE); itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                            item.setItemMeta(itemMeta);
                            giveItem(player, item);
                            invAccepting.add(player);
                            player.closeInventory();
                        }
                    }
                } else if(current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().hasLore()) {
                    final ItemMeta cm = current.getItemMeta();
                    final CustomEnchant enchant = CustomEnchant.valueOf(cm.getDisplayName());
                    final MagicDust dust = enchant == null ? MagicDust.valueOf(event.getCurrentItem()) : null;
                    final String suCCess = enchant != null ? "enchant" : dust != null ? "dust" : null, d = cm.getDisplayName();
                    final int F = top.firstEmpty();
                    if(suCCess != null) {
                        boolean upgrade = false;
                        int cost = -1;
                        if(F == 5 && !top.getItem(3).getItemMeta().getDisplayName().equals(d)
                                || F == 3 && top.getItem(5) != null && !top.getItem(5).getItemMeta().getDisplayName().equals(d)
                                || F < 0
                        ) {
                            return;
                        } else if(F == 3 && top.getItem(5) == null
                                || F == 5 && top.getItem(3) == null) {
                            // This is meant to be here :)
                            if(dust != null && dust.getUpgradeCost() == 0) return;
                        } else {
                            final int slot = F == 3 ? 5 : 3;
                            if(suCCess.equals("dust")) {
                                final MagicDust u = dust.getUpgradesTo();
                                if(u != null) {
                                    item = top.getItem(slot).clone(); itemMeta = item.getItemMeta(); lore.clear();
                                    cost = dust.getUpgradeCost();
                                    boolean did = false;
                                    if(cost == -1) return;
                                    for(int i = 0; i < itemMeta.getLore().size(); i++) {
                                        if(getRemainingInt(itemMeta.getLore().get(i)) != -1 && !did) {
                                            did = true;
                                            int percent = ((getRemainingInt(itemMeta.getLore().get(i)) + getRemainingInt(cm.getLore().get(i))) / 2);
                                            item = u.getItem();
                                            if(item == null) {
                                                return;
                                            }
                                            item = item.clone(); itemMeta = item.getItemMeta();
                                            for(String s : itemMeta.getLore()) {
                                                if(s.contains("{PERCENT}")) s = s.replace("{PERCENT}", "" + percent);
                                                lore.add(s);
                                            }
                                            itemMeta.setLore(lore); lore.clear();
                                            item.setItemMeta(itemMeta);
                                        }
                                    }
                                }
                            } else {
                                final EnchantRarity rar = EnchantRarity.valueOf(enchant);
                                final String SUCCESS = rar.getSuccess(), DESTROY = rar.getDestroy();
                                final int level = getEnchantmentLevel(cm.getDisplayName());
                                if(level >= enchant.getMaxLevel()) return;
                                else                               cost = enchant.getAlchemistUpgradeCost(level);
                                final ItemStack is = top.getItem(slot);
                                item = UMaterial.match(is).getItemStack();
                                itemMeta = item.getItemMeta();
                                itemMeta.setDisplayName("randomhashtags was here");
                                int success = 0, destroy = 0, higherDestroy = -1;
                                final List<String> l = is.getItemMeta().getLore(), cml = cm.getLore();
                                for(int i = 0; i <= 100; i++) {
                                    if(l.contains(SUCCESS.replace("{PERCENT}", "" + i))
                                            || cml.contains(SUCCESS.replace("{PERCENT}", "" + i)))
                                        success = success + (i/4);
                                    if(l.contains(DESTROY.replace("{PERCENT}", "" + i))
                                            || cml.contains(DESTROY.replace("{PERCENT}", "" + i))) {
                                        if(i > higherDestroy) higherDestroy = i;
                                        destroy = destroy + i;
                                    }
                                }
                                destroy = higherDestroy + (destroy / 4);
                                if(destroy > 100) destroy = 100;
                                item = getRevealedItem(enchant, level + 1, success, destroy, true, true).clone(); itemMeta = item.getItemMeta();
                            }
                            upgrade = true;
                        }
                        if(upgrade) {
                            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            item.setItemMeta(itemMeta); item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                            top.setItem(13, item);
                            item = alchemistaccept.clone(); itemMeta = item.getItemMeta(); lore.clear();
                            for(String string : itemMeta.getLore()) {
                                if(string.contains("{COST}")) string = string.replace("{COST}", formatInt(cost));
                                lore.add(string);
                            }
                            itemMeta.setLore(lore); lore.clear();
                            item.setItemMeta(itemMeta);
                            top.setItem(22, item);
                        }
                        top.setItem(top.firstEmpty(), event.getCurrentItem());
                        event.setCurrentItem(new ItemStack(Material.AIR));
                    }
                }
            } else if(title.equals(enchanter.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                if(enchantercost.containsKey(r)) {
                    long cost = enchantercost.get(r);
                    item = enchanterpurchase.get(r).clone();
                    List<String> me = null;
                    final int totalxp = getTotalExperience(player);
                    final double bal = eco != null ? eco.getBalance(player) : 0.00;
                    final boolean give, isCreative = player.getGameMode().equals(GameMode.CREATIVE), exp = enchantercurrency.equals("EXP");
                    give = isCreative || exp && totalxp >= cost || bal >= cost;

                    if(give) {
                        final EnchanterPurchaseEvent e = new EnchanterPurchaseEvent(player, item, enchantercurrency, cost);
                        pluginmanager.callEvent(e);
                        if(e.isCancelled()) return;
                        boolean bought = true;
                        cost = e.cost;
                        if(!isCreative) {
                            if(exp) {
                                if(totalxp >= cost) setTotalExperience(player, (int) (totalxp-cost));
                                else bought = false;
                                me = config.getStringList("enchanter.messages." + (bought ? "xp purchase" : "need more xp"));
                            } else {
                                if(bal >= cost) eco.withdrawPlayer(player, cost);
                                else bought = false;
                                me = config.getStringList("enchanter.messages." + (bought ? "cash purchase" : "need more cash"));
                            }
                        }
                        if(bought) giveItem(player, item);
                    } else {
                        me = config.getStringList("enchanter.messages.need more " + (exp ? "xp" : "cash"));
                    }
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{AMOUNT}", formatLong(cost));
                    sendStringListMessage(player, me, replacements);
                    player.updateInventory();
                }
            } else if(r >= 0) {
                /*
                 * Apply enchants
                 */
                final ItemMeta cm = cursor.getItemMeta();
                final String d = cm.getDisplayName();

                final CustomEnchant enchant = CustomEnchant.valueOf(d);
                final int level = getEnchantmentLevel(d);
                int enchantsize = 0;
                final PlayerPreApplyCustomEnchantEvent ev = new PlayerPreApplyCustomEnchantEvent(player, enchant, getEnchantmentLevel(d), current);
                pluginmanager.callEvent(ev);
                if(!ev.isCancelled() && isOnCorrectItem(enchant, current)) {
                    boolean apply = false;
                    item = current.clone(); itemMeta = item.getItemMeta(); lore.clear();
                    if(item.hasItemMeta() && itemMeta.hasLore()) {
                        if(itemMeta.getLore().containsAll(noMoreEnchantsAllowed)) {
                            ev.setCancelled(true);
                            return;
                        }
                        lore.addAll(itemMeta.getLore());
                    }
                    CustomEnchantApplyResult result = null;
                    if(enchant != null) {
                        final EnchantRarity rar = EnchantRarity.valueOf(enchant);
                        final List<String> cml = cm.getLore();
                        final int success = getRemainingInt(cml.get(rar.getSuccessSlot())), destroy = getRemainingInt(cml.get(rar.getDestroySlot()));
                        int prevlevel = -1, prevslot = -1, haspermfor = 0, eoIncrement = 0;
                        for(int i = 0; i <= 100; i++)
                            if(player.hasPermission("RandomPackage.levelcap." + i))
                                haspermfor = i;
                        for(int z = 0; z < lore.size(); z++) {
                            final CustomEnchant e = CustomEnchant.valueOf(lore.get(z));
                            if(e != null) {
                                enchantsize += 1;
                                if(e.equals(enchant)) {
                                    prevslot = z;
                                    prevlevel = getEnchantmentLevel(lore.get(z));
                                    if(prevlevel == e.getMaxLevel()) return;
                                }
                            } else {
                                final EnchantmentOrb eo = EnchantmentOrb.valueOf(lore.get(z));
                                if(eo != null) eoIncrement = eo.getIncrement();
                            }
                        }
                        if(haspermfor+eoIncrement <= enchantsize) {
                            ev.setCancelled(true);
                            return;
                        } else {
                            final String requires = enchant.getRequiredEnchant();
                            final CustomEnchant replaces = requires != null ? CustomEnchant.valueOf(requires.split(";")[0]) : null;
                            final int requiredLvl = replaces != null ? Integer.parseInt(requires.split(";")[1]) : -1;
                            final HashMap<CustomEnchant, Integer> enchants = replaces != null ? getEnchants(current) : null;
                            if(enchants != null && (!enchants.containsKey(replaces) || enchants.get(replaces) < requiredLvl)) return;
                            //
                            if(random.nextInt(100) <= success) {
                                final String a = rar.getApplyColors(), en = enchant.getName(), e = a + en + " " + toRoman(level);
                                if(lore.isEmpty()) {
                                    lore.add(e);
                                } else if(prevlevel == -1 && prevslot == -1) {
                                    int R = -1;
                                    boolean did = false;
                                    final ArrayList<String> newlore = new ArrayList<>();
                                    for(String s : lore) {
                                        if(!did) R += 1;
                                        final CustomEnchant ce = CustomEnchant.valueOf(s);
                                        if(ce != null) {
                                            if(ce.equals(replaces)) did = true;
                                            newlore.add(s);
                                        }
                                    }
                                    newlore.add(e);
                                    for(String s : lore) if(!newlore.contains(s)) newlore.add(s);
                                    if(replaces != null) newlore.remove(R);
                                    lore = newlore;
                                } else {
                                    lore.set(prevslot, a + en + " " + toRoman(level > prevlevel ? level : prevlevel + 1));
                                }
                                result = lore.isEmpty() || prevlevel == -1 && prevslot == -1 ? CustomEnchantApplyResult.SUCCESS_APPLY : CustomEnchantApplyResult.SUCCESS_UPGRADE;
                            } else if(random.nextInt(100) <= destroy) {
                                result = lore.contains(WHITE_SCROLL) ? CustomEnchantApplyResult.DESTROY_WHITE_SCROLL : CustomEnchantApplyResult.DESTROY;
                                if(lore.contains(WHITE_SCROLL)) lore.remove(WHITE_SCROLL);
                                else                            item = new ItemStack(Material.AIR);
                            }
                            apply = true;
                            final PlayerApplyCustomEnchantEvent ce = new PlayerApplyCustomEnchantEvent(player, enchant, level, result);
                            pluginmanager.callEvent(ce);
                        }
                    } else {
                        if(whitescroll != null && cm.equals(whitescroll.getItemMeta()) && !current.getType().equals(Material.AIR)) {
                            final ItemMeta c = current.getItemMeta();
                            if(!current.hasItemMeta() || current.hasItemMeta() && (!c.hasLore() || c.hasLore() && !c.getLore().contains(WHITE_SCROLL))) {
                                apply = true;
                                lore.add(WHITE_SCROLL);
                            }
                        }
                    }
                    if(apply) {
                        if(!item.getType().equals(Material.AIR)) {
                            if(itemMeta.hasDisplayName()) {
                                final String D = itemMeta.getDisplayName(), l = TRANSMOG.replace("{LORE_COUNT}", Integer.toString(enchantsize));
                                if(D.contains(l)) {
                                    itemMeta.setDisplayName(D.replace(l, TRANSMOG.replace("{LORE_COUNT}", Integer.toString(enchantsize+1))));
                                }
                            }
                            if(lore.contains(WHITE_SCROLL)) { lore.remove(WHITE_SCROLL); lore.add(WHITE_SCROLL); }
                            itemMeta.setLore(lore); lore.clear();
                            item.setItemMeta(itemMeta);
                        }
                        event.setCancelled(true);
                        event.setCurrentItem(item);
                        final int a = cursor.getAmount();
                        if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                        else cursor.setAmount(a-1);
                        player.updateInventory();
                    }
                } else {
                    ev.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void raritygemClickEvent(InventoryClickEvent event) {
        final ItemStack curs = event.getCursor(), curr = event.getCurrentItem();
        if(!event.isCancelled() && curr != null && curs != null && curr.hasItemMeta() && curs.hasItemMeta()) {
            final int cursorAmount = curs.getAmount();
            final RarityGem cursor = RarityGem.valueOf(curs), current = cursor != null ? RarityGem.valueOf(curr) : null;
            if(cursor == null || current == null) return;
            if(cursor.equals(current)) {
                final Player player = (Player) event.getWhoClicked();
                final int combinedTotal = getRemainingInt(curs.getItemMeta().getDisplayName()) + getRemainingInt(curr.getItemMeta().getDisplayName());
                event.setCancelled(true);
                item = cursor.getItem();
                itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SOULS}", ChatColor.translateAlternateColorCodes('&', cursor.getColors(combinedTotal)) + combinedTotal));
                item.setItemMeta(itemMeta);
                event.setCurrentItem(item);
                if(cursorAmount == 1) event.setCursor(new ItemStack(Material.AIR));
                else {
                    curs.setAmount(cursorAmount-1);
                    event.setCursor(curs);
                }
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void givedpClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getInventory(), inv = player.getInventory(), top = player.getOpenInventory().getTopInventory();
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(event.isCancelled()) return;
        else if(top.getType().equals(InventoryType.ANVIL)) {
            if(current != null && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().hasLore() || event.getClick().equals(ClickType.NUMBER_KEY)) {
                CustomEnchant enchant = null;
                final int hb = event.getHotbarButton();
                item = event.getClick().equals(ClickType.NUMBER_KEY) && inv.getItem(hb) != null ? inv.getItem(hb) : current;
                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName())
                    enchant = CustomEnchant.valueOf(item.getItemMeta().getDisplayName());
                if(enchant != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.closeInventory();
                }
            }
            return;
        } else if(current == null || current.getType().equals(Material.AIR)) { return;
        } else if(inventory.getType().equals(InventoryType.CRAFTING) || inventory.getType().equals(InventoryType.PLAYER)) {
            boolean success = false;
            int enchantcount = -1;
            if(cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
                item = current; itemMeta = current.getItemMeta(); lore.clear();
                final CustomEnchant enchant = CustomEnchant.valueOf(current);
                final RandomizationScroll randomizationscroll = RandomizationScroll.valueOf(cursor);
                if(enchant != null && randomizationscroll != null) {
                    final EnchantRarity r = EnchantRarity.valueOf(enchant);
                    if(randomizationscroll.getAppliesToRarities().contains(r)) {
                        final String s = r.getSuccess(), d = r.getDestroy();
                        int newSuccess = random.nextInt(101), newDestroy = random.nextInt(101);
                        final RandomizationScrollUseEvent e = new RandomizationScrollUseEvent(player, enchant, getEnchantmentLevel(itemMeta.getDisplayName()), randomizationscroll, newSuccess, newDestroy);
                        pluginmanager.callEvent(e);
                        newSuccess = e.getNewSuccess();
                        newDestroy = e.getNewDestroy();
                        for(String string : itemMeta.getLore()) {
                            if(string.equals(s.replace("{PERCENT}", "" + getRemainingInt(string))))        string = s.replace("{PERCENT}", "" + newSuccess);
                            else if(string.equals(d.replace("{PERCENT}", "" + getRemainingInt(string))))   string = d.replace("{PERCENT}", "" + newDestroy);
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        success = true;
                    }
                }
                HashMap<CustomEnchant, Integer> enchantmentsonitem = null;
                if(current.hasItemMeta() && current.getItemMeta().hasLore()) enchantmentsonitem = getEnchants(current);
                if(cursor.getItemMeta().equals(whitescroll.getItemMeta())) {
                    final String t = item.getType().name();
                    if(t.endsWith("AXE") || t.endsWith("SWORD") || t.endsWith("SPADE") || t.equals("BOW") || t.equals("FISHING_ROD") || t.endsWith("HELMET") || t.endsWith("CHESTPLATE") || t.endsWith("LEGGINGS") || t.endsWith("BOOTS")) {
                        if(!item.hasItemMeta() || item.hasItemMeta() && (!itemMeta.hasLore() || !itemMeta.getLore().contains(WHITE_SCROLL))) {
                            if(item.hasItemMeta() && itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                            lore.add(WHITE_SCROLL);
                            itemMeta.setLore(lore); lore.clear();
                            success = true;
                        }
                    }
                } else if(cursor.getItemMeta().equals(transmogscroll.getItemMeta())) {
                    applyTransmogScroll(current);
                    //playSuccess((Player) event.getWhoClicked());
                    event.setCancelled(true);
                    event.setCurrentItem(current);
                    final int a = cursor.getAmount();
                    if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                    else       cursor.setAmount(a-1);
                    player.updateInventory();
                    return;
                } else {
                    final BlackScroll bs = BlackScroll.valueOf(cursor);
                    if(bs != null && item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && !enchantmentsonitem.isEmpty()) {
                        enchantcount = enchantmentsonitem.size();
                        giveItem(player, applyBlackScroll(current, cursor, bs));
                        item = current; itemMeta = item.getItemMeta();
                        if(itemMeta.hasDisplayName()) {
                            final String d = itemMeta.getDisplayName(), l = TRANSMOG.replace("{LORE_COUNT}", Integer.toString(enchantcount));
                            if(d.contains(l)) {
                                itemMeta.setDisplayName(d.replace(l, TRANSMOG.replace("{LORE_COUNT}", Integer.toString(enchantcount-1))));
                            }
                        }
                        success = true;
                    }
                    final MagicDust dust = bs == null ? MagicDust.valueOf(cursor) : null;
                    if(dust != null && enchant != null) {
                        final EnchantRarity ra = EnchantRarity.valueOf(enchant);
                        if(dust.getAppliesTo().contains(ra)) {
                            final String SUCCESS = ra.getSuccess();
                            int percent = -1;
                            final List<String> l = dust.getPlainItem().getItemMeta().getLore();
                            for(int z = 0; z < l.size(); z++)
                                if(l.get(z).contains("{PERCENT}"))
                                    percent = getRemainingInt(cursor.getItemMeta().getLore().get(z));
                            if(percent == -1) return;
                            for(String string : itemMeta.getLore()) {
                                int r = getRemainingInt(string);
                                if(string.equals(SUCCESS.replace("{PERCENT}", "" + r))) {
                                    if(r >= 100) return;
                                    else if(r + percent > 100) { r = 50; percent = 50; }
                                    string = SUCCESS.replace("{PERCENT}", "" + (r + percent));
                                }
                                lore.add(ChatColor.translateAlternateColorCodes('&', string));
                            }
                            itemMeta.setLore(lore); lore.clear();
                            success = true;
                        }
                    }
                    final EnchantmentOrb orb = dust == null ? EnchantmentOrb.valueOf(cursor) : null;
                    if(orb != null) {
                        boolean did = false;
                        final EnchantmentOrb o = EnchantmentOrb.getOrb(current);
                        for(String s : orb.getAppliesTo()) {
                            if((o == null || !o.equals(orb) && o.getIncrement() < orb.getIncrement()) && current.getType().name().toLowerCase().endsWith(s.toLowerCase())) {
                                did = true;
                                applyEnchantmentOrb(player, current, cursor, orb);
                            }
                        }
                        if(!did) return;
                        success = true;
                    }
                    final SoulTracker soultracker = orb == null ? SoulTracker.valueOf(cursor) : null;
                    if(soultracker != null) {
                        applySoulTracker(player, current, soultracker);
                        success = true;
                    }
                }
                if(success) {
                    //playSuccess((Player) event.getWhoClicked());
                    item.setItemMeta(itemMeta);
                    if(itemMeta.hasDisplayName() && itemMeta.getDisplayName().contains(TRANSMOG.replace("{LORE_COUNT}", Integer.toString(enchantcount))))
                        applyTransmogScroll(item);
                    event.setCancelled(true);
                    event.setCurrentItem(item);
                    final int a = cursor.getAmount();
                    if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                    else       cursor.setAmount(a-1);
                    player.updateInventory();
                }
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Inventory inv = event.getInventory();
        if(inv.getHolder() == player) {
            final String title = event.getView().getTitle();
            final boolean contains = invAccepting.contains(player);
            invAccepting.remove(player);
            if(title.equals(alchemist.getTitle())) {
                if(contains) {
                    sendStringListMessage(player, config.getStringList("alchemist.messages.exchange"), null);
                } else {
                    giveItem(player, inv.getItem(3));
                    giveItem(player, inv.getItem(5));
                }
            } else if(title.equals(tinkerer.getTitle())) {
                sendStringListMessage(player, config.getStringList("tinkerer.messages." + (contains ? "accept" : "cancel") + " trade"), null);
                for(int i = 0; i < inv.getSize(); i++) {
                    if(inv.getItem(i) != null && (contains && (i >= 5 && i <= 7 || i >= 14 && i <= 17 || i >= 23 && i <= 26 || i >= 32 && i <= 35 || i >= 41 && i <= 44 || i >= 50 && i <= 53) || !contains && (i >= 1 && i <= 3 || i >= 9 && i <= 12 || i >= 18 && i <= 21 || i >= 27 && i <= 30 || i >= 36 && i <= 39 || i >= 45 && i <= 48))) {
                        giveItem(player, inv.getItem(i));
                    }
                }
            } else { return; }
            if(player.isOnline()) player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void customEnchantProcEvent(CustomEnchantProcEvent event) {
        final Event e = event.event;
        if(e instanceof CustomEnchantProcEvent && !event.isCancelled() && !((CustomEnchantProcEvent) e).isCancelled()) {
            final Player p = event.player;
            procPlayerArmor(e, p);
            procPlayerItem(e, p, null);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void customEnchantProcEventCancel(CustomEnchantProcEvent event) {
        final Player player = event.player;
        if(stoppedAllEnchants.contains(player) || stoppedEnchants.containsKey(player) && stoppedEnchants.get(player).containsKey(event.enchant)) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void entityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        final Entity E = event.getEntity();
        final LivingEntity EE = event.getTarget();
        if(!event.isCancelled() && E instanceof LivingEntity && EE instanceof Player) {
            final UUID u = E.getUniqueId();
            final HashMap<UUID, LivingCustomEnchantEntity> L = LivingCustomEnchantEntity.living;
            if(L != null) {
                final LivingCustomEnchantEntity entity = L.getOrDefault(u, null);
                if(entity != null) {
                    final Player player = (Player) EE, S = (Player) entity.getSummoner();
                    final RPPlayer pdata = RPPlayer.get(EE.getUniqueId());

                    if(!entity.getType().canTargetSummoner() && pdata.getCustomEnchantEntities().contains(u) || fapi != null && (!fapi.relationIsEnemyOrNull(player, S) || fapi.relationIsNeutral(player, S))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    public ItemStack applyBlackScroll(ItemStack is, ItemStack blackscroll, BlackScroll bs) {
        item = null;
        final HashMap<CustomEnchant, Integer> enchants = getEnchants(is);
        if(is != null && enchants.size() > 0) {
            final Set<CustomEnchant> key = enchants.keySet();
            CustomEnchant enchant = (CustomEnchant) key.toArray()[random.nextInt(key.size())];
            final List<EnchantRarity> a = bs.getAppliesTo();
            int successP = -1;
            for(String string : blackscroll.getItemMeta().getLore()) if(getRemainingInt(string) != -1) successP = getRemainingInt(string);
            for(int f = 1; f <= 5; f++) {
                final EnchantRarity r = EnchantRarity.valueOf(enchant);
                if(a.contains(r)) {
                    int enchantlevel = enchants.get(enchant);
                    itemMeta = is.getItemMeta(); lore.clear(); lore.addAll(itemMeta.getLore());
                    int enchantslot = -1;
                    final String ap = r.getApplyColors();
                    for(int i = 0; i < lore.size(); i++) if(lore.get(i).equals(ap + enchant.getName() + " " + toRoman(enchantlevel))) enchantslot = i;
                    if(enchantslot == -1) return null;
                    lore.remove(enchantslot);
                    itemMeta.setLore(lore); lore.clear();
                    is.setItemMeta(itemMeta);
                    return getRevealedItem(enchant, enchantlevel, successP, 100, true, true).clone();
                } else enchant = (CustomEnchant) key.toArray()[random.nextInt(key.size())];
            }
        }
        return item;
    }
    public void applySoulTracker(Player player, ItemStack is, SoulTracker soultracker) {
        if(is != null && SoulTracker.trackers != null) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            boolean did = false;
            final String a = soultracker.getAppliedLore(), ist = is.getType().name();
            for(String s : soultracker.getAppliesTo()) {
                if(!did && ist.toLowerCase().endsWith(s.toLowerCase())) {
                    if(!lore.isEmpty()) {
                        for(int i = 0; i < lore.size(); i++) {
                            final String targetLore = lore.get(i);
                            for(SoulTracker st : SoulTracker.trackers.values()) {
                                if(!did && targetLore.startsWith(st.getAppliedLore().replace("{SOULS}", ""))) {
                                    did = true;
                                    lore.set(i, a.replace("{SOULS}", "0"));
                                }
                            }
                        }
                        if(!did) {
                            did = true;
                            lore.add(a.replace("{SOULS}", "0"));
                        }
                    } else {
                        lore.add(a.replace("{SOULS}", "0"));
                        did = true;
                    }
                }
            }
            if(did) {
                for(String string : soultracker.getApplyMessage()) {
                    if(string.contains("{ITEM}")) string = string.replace("{ITEM}", is.getType().name());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                }
                itemMeta.setLore(lore); lore.clear();
                is.setItemMeta(itemMeta);
            }
        }
    }
    public boolean applyTransmogScroll(ItemStack is) {
        final String currentitem = is != null ? is.getType().name() : null;
        if(currentitem != null && (currentitem.endsWith("AXE") || currentitem.endsWith("SWORD") || currentitem.endsWith("SPADE") || currentitem.equals("BOW") || currentitem.endsWith("HELMET") || currentitem.endsWith("CHESTPLATE") || currentitem.endsWith("LEGGINGS") || currentitem.endsWith("BOOTS"))) {
            final HashMap<CustomEnchant, Integer> enchants = getEnchants(is);
            final int size = enchants.keySet().size(), prevsize = enchants.keySet().size();
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) {
                final List<String> l = itemMeta.getLore();
                for(String ss : transmog_organization) {
                    final EnchantRarity r = EnchantRarity.rarities.get(ss);
                    for(String s : l) {
                        final CustomEnchant enchant = CustomEnchant.valueOf(s);
                        if(enchant != null && EnchantRarity.valueOf(enchant).equals(r)) lore.add(s);
                    }
                }
                String soultracker = null;
                for(String s : itemMeta.getLore()) {
                    final HashMap<String, SoulTracker> t = SoulTracker.trackers;
                    if(t != null) {
                        for(SoulTracker st : t.values()) {
                            if(s.startsWith(st.getAppliedLore().replace("{SOULS}", ""))) {
                                soultracker = s;
                            }
                        }
                    }
                }
                if(lore.contains(WHITE_SCROLL)) { lore.remove(WHITE_SCROLL); lore.add(WHITE_SCROLL); }
                if(soultracker != null) { lore.remove(soultracker); lore.add(soultracker); }
                for(String s : itemMeta.getLore())
                    if(!lore.contains(s)) lore.add(s);
            }
            itemMeta.setLore(lore); lore.clear();
            //
            String name;
            if(itemMeta.hasDisplayName()) {
                name = itemMeta.getDisplayName();
                if(name.contains(TRANSMOG.replace("{LORE_COUNT}", "" + prevsize))) name = name.replace(TRANSMOG.replace("{LORE_COUNT}", "" + prevsize), TRANSMOG.replace("{LORE_COUNT}", "" + size));
            } else name = is.getType().name();
            if(name.equals(is.getType().name())) name = toMaterial(is.getType().name(), false);
            name = name.replace("{ENCHANT_SIZE}", TRANSMOG.replace("{LORE_COUNT}", "" + size));
            if(!name.contains(TRANSMOG.replace("{LORE_COUNT}", Integer.toString(size)))) name = name + " " + TRANSMOG.replace("{LORE_COUNT}", Integer.toString(size));
            ChatColor color = ChatColor.RESET;
            if(itemMeta.hasEnchants()) color = ChatColor.AQUA;
            itemMeta.setDisplayName(color + name);
            is.setItemMeta(itemMeta);
            return true;
        }
        return false;
    }
    private void applyEnchantmentOrb(Player player, ItemStack is, ItemStack enchantmentorb, EnchantmentOrb orb) {
        EnchantmentOrb prevOrb = null;
        final int percent = getRemainingInt(enchantmentorb.getItemMeta().getLore().get(orb.getPercentLoreSlot()));
        item = is; itemMeta = item.getItemMeta(); lore.clear();
        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
        for(String s : lore) {
            EnchantmentOrb q = EnchantmentOrb.valueOf(s);
            if(q != null) prevOrb = q;
        }
        if(random.nextInt(100) <= percent) {
            final String a = orb.getApplyLore();
            if(prevOrb == null) {
                lore.add(a);
            } else {
                final String prev = prevOrb.getApplyLore();
                boolean did = false;
                for(int i = 0; i < lore.size(); i++) {
                    if(!did && lore.get(i).equals(prev)) {
                        did = true;
                        lore.set(i, a);
                    }
                }
                if(!did) return;
            }
            //playSuccess(player);
        } else {
            //playDestroy(player);
            return;
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        player.updateInventory();
    }
    public void applyWhiteScroll(Player player, ItemStack is) {
        if(is != null) {
            if(is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains(WHITE_SCROLL)) return;
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            lore.add(WHITE_SCROLL);
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
            player.updateInventory();
        }
    }
    public void splitsouls(Player player, int amount) {
        final HashMap<String, SoulTracker> ST = SoulTracker.trackers;
        if(ST != null) {
            item = getItemInHand(player);
            RarityGem g = RarityGem.valueOf(item);
            int collectedsouls = 0, gems = 0;
            List<String> split = null;
            SoulTracker appliedst = null;
            if(g != null) {
                split = g.getSplitMessage();
                collectedsouls = getRemainingInt(item.getItemMeta().getDisplayName());
            } else if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                sendStringListMessage(player, config.getStringList("messages.need item with soul tracker"), null);
            } else {
                itemMeta = item.getItemMeta();
                lore.clear();
                if(itemMeta.hasLore())
                    lore.addAll(itemMeta.getLore());
                boolean did = false;
                int applied = -1, totalsouls = -1;
                for(SoulTracker st : ST.values()) {
                    int i = -1;
                    if(!did) {
                        for(String s : lore) {
                            i += 1;
                            final String a = st.getAppliedLore();
                            if(s.startsWith(a.replace("{SOULS}", ""))) {
                                appliedst = st;
                                applied = i;
                                split = st.getSplitMessage();
                                collectedsouls = getRemainingInt(s);
                                totalsouls = collectedsouls;
                                if(amount == -1) {
                                    amount = collectedsouls;
                                } else if(collectedsouls <= 0) {
                                    sendStringListMessage(player, config.getStringList("messages.need to collect souls"), null);
                                    return;
                                } else {
                                    collectedsouls = amount;
                                }
                                if(amount == 0)  {
                                    sendStringListMessage(player, config.getStringList("messages.need to collect souls"), null);
                                    return;
                                }
                                gems = (int) (collectedsouls * st.getSoulsPerKill());
                                did = true;
                            }
                        }
                    }
                }
                if(did) {
                    lore.set(applied, appliedst.getAppliedLore().replace("{SOULS}", Integer.toString(totalsouls - amount)));
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    player.updateInventory();
                }
            }
            if(split != null) {
                if(g == null) {
                    g = appliedst.getConvertsTo();
                }
                item = g.getItem().clone(); itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(item.getItemMeta().getDisplayName().replace("{SOULS}", ChatColor.translateAlternateColorCodes('&', g.getColors(gems)) + gems));
                if(gems != 0) item.setAmount(1);
                item.setItemMeta(itemMeta);
                giveItem(player, item);
                for(String string : split) {
                    if(string.contains("{SOULS}")) string = string.replace("{SOULS}", Integer.toString(collectedsouls));
                    if(string.contains("{GEMS}")) string = string.replace("{GEMS}", Integer.toString(gems));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                }
                player.updateInventory();
            }
        }
    }
}
