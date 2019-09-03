package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.addons.objects.CustomEnchantEntity;
import me.randomhashtags.randompackage.api.addons.TransmogScrolls;
import me.randomhashtags.randompackage.events.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.events.MobStackDepleteEvent;
import me.randomhashtags.randompackage.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.events.customenchant.*;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.addons.FileCustomEnchant;
import me.randomhashtags.randompackage.utils.addons.FileEnchantRarity;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static me.randomhashtags.randompackage.utils.listeners.GivedpItem.givedpitem;

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
    private ItemStack tinkereraccept, alchemistpreview, alchemistexchange, alchemistaccept;
    private HashMap<Integer, Long> enchantercost;
    private HashMap<Integer, ItemStack> enchanterpurchase;
    private List<Player> invAccepting;
    private List<String> noMoreEnchantsAllowed;

    public String getIdentifier() { return "CUSTOM_ENCHANTS"; }
    protected RPFeature getFeature() { return getCustomEnchants(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String n = cmd.getName();
        if(n.equals("disabledenchants") && hasPermission(player, "RandomPackage.disabledenchants", true))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', disabled.keySet().toString()));
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
        }
        return true;
    }
    @Override
    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
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

        enchantercost = new HashMap<>();
        enchanterpurchase = new HashMap<>();
        invAccepting = new ArrayList<>();

        alchemist = new UInventory(null, 27, ChatColor.translateAlternateColorCodes('&', config.getString("alchemist.title")));
        enchanter = new UInventory(null, config.getInt("enchanter.size"), ChatColor.translateAlternateColorCodes('&', config.getString("enchanter.title")));
        tinkerer = new UInventory(null, config.getInt("tinkerer.size"), ChatColor.translateAlternateColorCodes('&', config.getString("tinkerer.title")));
        setupInventory(alchemist);
        setupInventory(tinkerer);

        final YamlConfiguration a = otherdata;
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
                    "DEEP BLEED", "DEMONIC_LIFESTEAL", "DIVINE ENLIGHTED",
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
                        FileEnchantRarity rarity = null;
                        final List<File> F = Arrays.asList(files);
                        for(File k : F) {
                            if(k.getName().contains("_settings")) {
                                rarity = new FileEnchantRarity(f, k);
                                raritybooks.add(rarity.getRevealItem());
                            }
                        }
                        if(rarity != null) {
                            for(File ff : files) {
                                if(!ff.getName().startsWith("_settings")) {
                                    final FileCustomEnchant e = new FileCustomEnchant(ff);
                                    rarity.getEnchants().add(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded [&f" + enabled.size() + "e, &c" + disabled.size() + "d&a] Custom Enchants &e(took " + (System.currentTimeMillis()-started) + "ms)");
        addGivedpCategory(raritybooks, UMaterial.BOOK, "Rarity Books", "Givedp: Rarity Books");

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
    @Override
    public void unload() {
        unloadUtils();
        givedpitem.items.remove("transmogscroll");
        givedpitem.items.remove("whitescroll");
        CustomEnchantEntity.deleteAll();
        enabled = null;
        disabled = null;
        rarities = null;
    }


    public void viewEnchants(CommandSender sender, int page) {
        final ChatEvents cea = ChatEvents.getChatEvents();
        final String format = randompackage.getConfig().getString("enchants.format");
        final List<String> L = colorizeListString(randompackage.getConfig().getStringList("enchants.hover"));
        final int size = enabled.size(), maxpage = size/10;
        page = page > maxpage ? maxpage : page;
        final int starting = page*10;
        for(String s : randompackage.getConfig().getStringList("enchants.msg")) {
            if(s.equals("{ENCHANTS}")) {
                for(int i = starting; i <= starting+10; i++) {
                    if(size > i) {
                        final CustomEnchant ce = (CustomEnchant) enabled.values().toArray()[i];
                        final EnchantRarity rarity = valueOfEnchantRarity(ce);
                        final HashMap<String, List<String>> replacements = new HashMap<>();
                        replacements.put("{TIER}", Arrays.asList(rarity.getApplyColors() + rarity.getIdentifier()));
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
    private void playerArmorEvent(PlayerArmorEvent event) {
        if(!event.isCancelled()) {
            final ItemStack itemstack = event.getItem();
            if(itemstack != null && itemstack.hasItemMeta() && itemstack.getItemMeta().hasLore()) {
                for(String s : itemstack.getItemMeta().getLore()) {
                    final CustomEnchant enchant = valueOfCustomEnchant(s);
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
        final EnchantRarity rarity = valueOfEnchantRarity(enchant);
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
        final EnchantRarity rar = rarities.get(rarity.getRevealedEnchantRarities()[random.nextInt(l)]);
        final List<CustomEnchant> enchants = rar.getEnchants();
        item = new ItemStack(Material.BOOK);
        for(int i = 1; i <= 100; i++) {
            final CustomEnchant enchant = enchants.get(random.nextInt(enchants.size()));
            if(enchant.isEnabled()) {
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
                break;
            }
        }
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
        EnchantRarity rarity = valueOfEnchantRarity(I);
        if(rarity != null) {
            final ItemStack r = getRandomEnabledEnchant(rarity);
            final String displayname = r.getItemMeta().getDisplayName();
            final CustomEnchant enchant = valueOfCustomEnchant(r);
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
        } else if(I != null && I.hasItemMeta() && I.getItemMeta().hasDisplayName() && I.getItemMeta().hasLore()) {
            final CustomEnchant enchant = valueOfCustomEnchant(I);
            if(enchant != null)
                for(String s : config.getStringList("rarities.apply info")) player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
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
        if(!(e instanceof Player) && k != null) {
            procPlayerArmor(event, k);
            procPlayerItem(event, k, null);
        }
        final HashMap<UUID, LivingCustomEnchantEntity> L = LivingCustomEnchantEntity.living;
        if(L != null) {
            final LivingCustomEnchantEntity entity = L.get(u);
            if(entity != null) {
                if(!entity.getType().dropsItemsUponDeath()) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
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

                final CustomEnchant e = cur.hasItemMeta() && cur.getItemMeta().hasDisplayName() ? valueOfCustomEnchant(cur.getItemMeta().getDisplayName()) : null;
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
                    final EnchantRarity R = valueOfEnchantRarity(e);
                    final RarityFireball f = valueOfFireball(Arrays.asList(R));
                    if(f != null) {
                        final ItemStack itemstack = f.getItem();
                        if(itemstack == null) return;
                        item = itemstack.clone();
                    } else {
                        return;
                    }
                } else if(cur.getItemMeta().hasEnchants() && (cu.endsWith("HELMET") || cu.endsWith("CHESTPLATE") || cu.endsWith("LEGGINGS") || cu.endsWith("BOOTS") || cu.endsWith("SWORD") || cu.endsWith("AXE") || cu.endsWith("SPADE") || cu.endsWith("SHOVEL") || cu.endsWith("HOE") || cu.endsWith("BOW"))) {
                    final BigDecimal zero = BigDecimal.ZERO;
                    BigDecimal xp = BigDecimal.ZERO;
                    for(Enchantment enchant : cur.getEnchantments().keySet())
                        xp = xp.add(BigDecimal.valueOf(Integer.parseInt(config.getString("tinkerer.enchant values." + enchant.getName().toLowerCase()))));
                    if(cur.hasItemMeta() && cur.getItemMeta().hasLore()) {
                        final HashMap<CustomEnchant, Integer> enchants = getEnchants(cur);
                        for(CustomEnchant enchant : enchants.keySet()) xp = xp.add(enchant.getTinkererValue(enchants.get(enchant)));
                    }
                    if(!xp.equals(zero)) item = givedpitem.getXPBottle(xp, "Tinkerer").clone();
                } else {
                    sendStringListMessage(player, config.getStringList("tinkerer.messages.doesnt want item"), null);
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
                    final CustomEnchant enchant = valueOfCustomEnchant(cm.getDisplayName());
                    final MagicDust dust = enchant == null ? valueOfMagicDust(event.getCurrentItem()) : null;
                    final String suCCess = enchant != null ? "enchant" : dust != null ? "dust" : null, d = cm.getDisplayName();
                    final int F = top.firstEmpty();
                    if(suCCess != null) {
                        boolean upgrade = false;
                        final BigDecimal zero = BigDecimal.ZERO;
                        BigDecimal cost = zero;
                        if(F == 5 && !top.getItem(3).getItemMeta().getDisplayName().equals(d)
                                || F == 3 && top.getItem(5) != null && !top.getItem(5).getItemMeta().getDisplayName().equals(d)
                                || F < 0
                        ) {
                            return;
                        } else if(F == 3 && top.getItem(5) == null
                                || F == 5 && top.getItem(3) == null) {
                            // This is meant to be here :)
                            if(dust != null && dust.getUpgradeCost().equals(zero)) return;
                        } else {
                            final int slot = F == 3 ? 5 : 3;
                            if(suCCess.equals("dust")) {
                                final MagicDust u = dust.getUpgradesTo();
                                if(u != null) {
                                    item = top.getItem(slot).clone(); itemMeta = item.getItemMeta(); lore.clear();
                                    cost = dust.getUpgradeCost();
                                    boolean did = false;
                                    if(cost.equals(zero)) return;
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
                                final EnchantRarity rar = valueOfEnchantRarity(enchant);
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
                                if(string.contains("{COST}")) string = string.replace("{COST}", formatBigDecimal(cost));
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

                final CustomEnchant enchant = valueOfCustomEnchant(d);
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
                    String result = null;
                    if(enchant != null) {
                        final EnchantRarity rar = valueOfEnchantRarity(enchant);
                        final List<String> cml = cm.getLore();
                        final int success = getRemainingInt(cml.get(rar.getSuccessSlot())), destroy = getRemainingInt(cml.get(rar.getDestroySlot()));
                        int prevlevel = -1, prevslot = -1, haspermfor = 0, eoIncrement = 0;
                        for(int i = 0; i <= 100; i++)
                            if(player.hasPermission("RandomPackage.levelcap." + i))
                                haspermfor = i;
                        for(int z = 0; z < lore.size(); z++) {
                            final CustomEnchant e = valueOfCustomEnchant(lore.get(z));
                            if(e != null) {
                                enchantsize += 1;
                                if(e.equals(enchant)) {
                                    prevslot = z;
                                    prevlevel = getEnchantmentLevel(lore.get(z));
                                    if(prevlevel == e.getMaxLevel()) return;
                                }
                            } else {
                                final EnchantmentOrb eo = valueOfEnchantmentOrb(lore.get(z));
                                if(eo != null) eoIncrement = eo.getIncrement();
                            }
                        }
                        if(haspermfor+eoIncrement <= enchantsize) {
                            ev.setCancelled(true);
                            return;
                        } else {
                            final String requires = enchant.getRequiredEnchant();
                            final CustomEnchant replaces = requires != null ? valueOfCustomEnchant(requires.split(";")[0]) : null;
                            final int requiredLvl = replaces != null ? Integer.parseInt(requires.split(";")[1]) : -1;
                            final HashMap<CustomEnchant, Integer> enchants = replaces != null ? getEnchants(current) : null;
                            if(enchants != null && (!enchants.containsKey(replaces) || enchants.get(replaces) < requiredLvl)) return;
                            //
                            if(random.nextInt(100) <= success) {
                                final String a = rar.getApplyColors(), en = enchant.getName(), e = a + en + " " + toRoman(level);
                                if(lore.isEmpty()) {
                                    lore.add(e);
                                } else if(prevlevel == -1 && prevslot == -1) {
                                    String replacedEnchant = null;
                                    final ArrayList<String> newlore = new ArrayList<>();
                                    for(String s : lore) {
                                        final CustomEnchant ce = valueOfCustomEnchant(s);
                                        if(ce != null) {
                                            if(ce.equals(replaces)) {
                                                newlore.add(e);
                                                replacedEnchant = s;
                                            } else {
                                                newlore.add(s);
                                            }
                                        }
                                    }
                                    if(!newlore.contains(e)) newlore.add(e);
                                    for(String s : lore) if(!newlore.contains(s) && (replacedEnchant == null || !s.equals(replacedEnchant))) newlore.add(s);
                                    lore = newlore;
                                } else {
                                    lore.set(prevslot, a + en + " " + toRoman(level > prevlevel ? level : prevlevel + 1));
                                }
                                result = lore.isEmpty() || prevlevel == -1 && prevslot == -1 ? "SUCCESS_APPLY" : "SUCCESS_UPGRADE";
                            } else if(random.nextInt(100) <= destroy) {
                                final WhiteScroll w = getWhiteScroll("REGULAR");
                                final String applied = w != null ? w.getApplied() : null;
                                result = applied != null && lore.contains(applied) ? "DESTROY_WHITE_SCROLL" : "DESTROY";
                                if(lore.contains(applied)) lore.remove(applied);
                                else                       item = new ItemStack(Material.AIR);
                            }
                            apply = true;
                            final CustomEnchantApplyEvent ce = new CustomEnchantApplyEvent(player, enchant, level, result);
                            pluginmanager.callEvent(ce);
                        }
                    }
                    if(apply) {
                        if(!item.getType().equals(Material.AIR)) {
                            if(itemMeta.hasDisplayName()) {
                                final TransmogScrolls t = TransmogScrolls.getTransmogScrolls();
                                if(t.isEnabled()) {
                                    final TransmogScroll ts = t.getApplied(item);
                                    if(ts != null) {
                                        t.update(item, enchantsize, enchantsize+1);
                                    }
                                }
                            }
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void anvilClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inv = player.getInventory();
        final ItemStack current = event.getCurrentItem();
        if(player.getOpenInventory().getTopInventory().getType().equals(InventoryType.ANVIL)) {
            if(current != null && current.hasItemMeta() && current.getItemMeta().hasDisplayName() || event.getClick().equals(ClickType.NUMBER_KEY)) {
                CustomEnchant enchant = null;
                final int hb = event.getHotbarButton();
                item = event.getClick().equals(ClickType.NUMBER_KEY) && inv.getItem(hb) != null ? inv.getItem(hb) : current;
                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName())
                    enchant = valueOfCustomEnchant(item.getItemMeta().getDisplayName());
                if(enchant != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.closeInventory();
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
                    item = inv.getItem(i);
                    if(item != null && (contains && (i >= 5 && i <= 7 || i >= 14 && i <= 17 || i >= 23 && i <= 26 || i >= 32 && i <= 35 || i >= 41 && i <= 44 || i >= 50 && i <= 53) || !contains && (i >= 1 && i <= 3 || i >= 9 && i <= 12 || i >= 18 && i <= 21 || i >= 27 && i <= 30 || i >= 36 && i <= 39 || i >= 45 && i <= 48))) {
                        giveItem(player, item);
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

                    if(!entity.getType().canTargetSummoner() && pdata.getCustomEnchantEntities().contains(u) || hookedFactionsUUID() && (!factions.isEnemy(player, S) || factions.isNeutral(player, S))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
