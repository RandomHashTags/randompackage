package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.legacy.ShopCategory;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.addons.EventAttribute;
import me.randomhashtags.randompackage.dev.*;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.utils.RPFeature.rpd;
import static me.randomhashtags.randompackage.utils.RPFeature.separator;

public abstract class RPStorage extends RegionalAPI {
    protected static final String version = Bukkit.getVersion();
    protected static final Random random = new Random();
    protected static final boolean isLegacy = version.contains("1.8") || version.contains("1.9") || version.contains("1.10") || version.contains("1.11") || version.contains("1.12");

    private static HashMap<String, YamlConfiguration> cached = new HashMap<>();

    private static LinkedHashMap<UUID, RPPlayer> players;
    protected static LinkedHashMap<String, ArmorSet> armorsets;
    protected static LinkedHashMap<String, BlackScroll> blackscrolls;
    protected static LinkedHashMap<String, Booster> boosters;
    protected static LinkedHashMap<String, CustomBoss> bosses;
    protected static LinkedHashMap<String, ConquestChest> conquestchests;
    protected static TreeMap<String, CustomEnchant> enabled, disabled;

    protected static LinkedHashMap<String, EventAttribute> eventattributes;
    protected static LinkedHashMap<String, EventCondition> eventconditions;

    protected static LinkedHashMap<String, DuelArena> duelArenas;
    protected static LinkedHashMap<String, Dungeon> dungeons;
    protected static LinkedHashMap<String, MagicDust> dusts;
    protected static LinkedHashMap<String, EnchantRarity> rarities;
    protected static LinkedHashMap<String, EnchantmentOrb> enchantmentorbs;
    protected static LinkedHashMap<String, EnvoyCrate> envoycrates;
    protected static LinkedHashMap<String, CustomExplosion> explosions;
    protected static LinkedHashMap<String, FactionUpgrade> factionupgrades;
    protected static LinkedHashMap<String, FactionUpgradeType> factionupgradetypes;
    protected static LinkedHashMap<String, FallenHero> fallenheroes;
    protected static LinkedHashMap<String, FilterCategory> filtercategories;
    protected static LinkedHashMap<String, RarityFireball> fireballs;
    protected static LinkedHashMap<String, GlobalChallenge> globalchallenges;
    protected static LinkedHashMap<String, GlobalChallengePrize> globalchallengeprizes;
    protected static LinkedHashMap<String, CustomKit> kits;
    protected static LinkedHashMap<String, Lootbox> lootboxes;
    protected static LinkedHashMap<String, Mask> masks;
    protected static LinkedHashMap<String, MonthlyCrate> monthlycrates;
    protected static LinkedHashMap<String, Outpost> outposts;
    protected static LinkedHashMap<String, InventoryPet> inventorypets;
    protected static LinkedHashMap<String, PlayerQuest> playerquests;
    protected static LinkedHashMap<String, RandomizationScroll> randomizationscrolls;
    protected static LinkedHashMap<String, RarityGem> raritygems;
    protected static LinkedHashMap<String, ServerCrate> servercrates;
    protected static LinkedHashMap<String, ShopCategory> shopcategories;
    protected static LinkedHashMap<String, SoulTracker> soultrackers;
    protected static LinkedHashMap<String, Stronghold> strongholds;
    protected static LinkedHashMap<String, Title> titles;
    protected static LinkedHashMap<String, TransmogScroll> transmogscrolls;
    protected static LinkedHashMap<String, Trinket> trinkets;
    protected static LinkedHashMap<String, WhiteScroll> whitescrolls;

    private File exists(UUID uuid) {
        final File f = new File(getPlugin.getDataFolder() + File.separator + "_Data", uuid.toString() + ".yml");
        return f.exists() ? f : null;
    }
    private void check(Map<String, ?> map, String identifier, String subject) {
        if(map.containsKey(identifier)) System.out.println("[RandomPackage] Already contains " + subject + " \"" + identifier + "\"");
    }
    public RPPlayer getPlayer(UUID uuid) {
        if(players == null) players = new LinkedHashMap<>();
        if(players.containsKey(uuid)) return players.get(uuid);
        final File f = exists(uuid);
        return f != null ? null : null;
    }
    public void unloadPlayer(UUID player, boolean async) {
        if(players.containsKey(player)) {
            players.get(player).unload(async);
            players.remove(player);
        }
    }


    private YamlConfiguration get(String folder, String fileName) {
        if(cached.containsKey(fileName)) return cached.get(fileName);
        final File f = new File(rpd + separator + (folder != null ? folder : ""), fileName);
        final YamlConfiguration c = f.exists() ? YamlConfiguration.loadConfiguration(f) : null;
        cached.put(fileName, c);
        return c;
    }
    public YamlConfiguration getAddonConfig(String fileName) { return get("addons", fileName); }
    public YamlConfiguration getRPConfig(String folder, String fileName) { return get(folder, fileName); }


    public ArmorSet getArmorSet(String identifier) {
        return armorsets != null ? armorsets.getOrDefault(identifier, null) : null;
    }
    public void addArmorSet(ArmorSet a) {
        if(armorsets == null) armorsets = new LinkedHashMap<>();
        final String identifier = a.getIdentifier();
        check(armorsets, identifier, "armor set");
        armorsets.put(identifier, a);
    }

    public BlackScroll getBlackScroll(String identifier) {
        return blackscrolls != null ? blackscrolls.getOrDefault(identifier, null) : null;
    }
    public void addBlackScroll(BlackScroll a) {
        if(blackscrolls == null) blackscrolls = new LinkedHashMap<>();
        final String identifier = a.getIdentifier();
        check(blackscrolls, identifier, "black scroll");
        blackscrolls.put(identifier, a);
    }

    public Booster getBooster(String identifier) {
        return boosters != null ? boosters.getOrDefault(identifier, null) : null;
    }
    public void addBooster(Booster b) {
        if(boosters == null) boosters = new LinkedHashMap<>();
        final String identifier = b.getIdentifier();
        check(boosters, identifier, "booster");
        boosters.put(identifier, b);
    }

    public CustomBoss getBoss(String identifier) {
        return bosses != null ? bosses.getOrDefault(identifier, null) : null;
    }
    public void addBoss(CustomBoss b) {
        if(bosses == null) bosses = new LinkedHashMap<>();
        final String identifier = b.getIdentifier();
        check(bosses, identifier, "custom boss");
        bosses.put(identifier, b);
    }

    public ConquestChest getConquestChest(String identifier) {
        return conquestchests != null ? conquestchests.getOrDefault(identifier, null) : null;
    }
    public void addConquestChest(ConquestChest b) {
        if(conquestchests == null) conquestchests = new LinkedHashMap<>();
        final String identifier = b.getIdentifier();
        check(conquestchests, identifier, "conquest chest");
        conquestchests.put(identifier, b);
    }

    public DuelArena getDuelArena(String identifier) {
        return duelArenas != null ? duelArenas.getOrDefault(identifier, null) : null;
    }
    public void addDuelArena(DuelArena d) {
        if(duelArenas == null) duelArenas = new LinkedHashMap<>();
        final String identifier = d.getIdentifier();
        check(duelArenas, identifier, "duel arena");
        duelArenas.put(identifier, d);
    }

    public Dungeon getDungeon(String identifier) {
        return dungeons != null ? dungeons.getOrDefault(identifier, null) : null;
    }
    public void addDungeon(Dungeon d) {
        if(dungeons == null) dungeons = new LinkedHashMap<>();
        final String identifier = d.getIdentifier();
        check(dungeons, identifier, "dungeon");
        dungeons.put(identifier, d);
    }

    public MagicDust getDust(String identifier) {
        return dusts != null ? dusts.getOrDefault(identifier, null) : null;
    }
    public void addDust(MagicDust m) {
        if(dusts == null) dusts = new LinkedHashMap<>();
        final String identifier = m.getIdentifier();
        check(dusts, identifier, "rarity dust");
        dusts.put(identifier, m);
    }

    public CustomEnchant getEnchant(String identifier) {
        return enabled != null && enabled.containsKey(identifier) ? enabled.get(identifier) : disabled != null && disabled.containsKey(identifier) ? disabled.get(identifier) : null;
    }
    public void addEnchant(CustomEnchant enchant) {
        final boolean e = enchant.isEnabled();
        if(e && enabled == null) enabled = new TreeMap<>();
        else if(!e && disabled == null) disabled = new TreeMap<>();
        final String identifier = enchant.getIdentifier();
        check(e ? enabled : disabled, identifier, "custom enchant");
        (e ? enabled : disabled).put(identifier, enchant);
    }

    public EnchantmentOrb getEnchantmentOrb(String identifier) {
        return enchantmentorbs != null ? enchantmentorbs.get(identifier) : null;
    }
    public void addEnchantmentOrb(EnchantmentOrb enchantmentorb) {
        if(enchantmentorbs == null) enchantmentorbs = new LinkedHashMap<>();
        final String identifier = enchantmentorb.getIdentifier();
        check(enchantmentorbs, identifier, "enchantment orb");
        enchantmentorbs.put(identifier, enchantmentorb);
    }

    public EnchantRarity getEnchantRarity(String identifier) {
        return rarities != null ? rarities.getOrDefault(identifier, null) : null;
    }
    public void addEnchantRarity(EnchantRarity rarity) {
        if(rarities == null) rarities = new LinkedHashMap<>();
        final String identifier = rarity.getIdentifier();
        check(rarities, identifier, "enchant rarity");
        rarities.put(identifier, rarity);
    }

    public EnvoyCrate getEnvoyCrate(String identifier) {
        return envoycrates != null ? envoycrates.getOrDefault(identifier, null) : null;
    }
    public void addEnvoyCrate(EnvoyCrate e) {
        if(envoycrates == null) envoycrates = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(envoycrates, identifier, "envoy crate");
        envoycrates.put(identifier, e);
    }

    public EventAttribute getEventAttribute(String identifier) {
        return eventattributes != null ? eventattributes.getOrDefault(identifier, null) : null;
    }
    public void addEventAttribute(EventAttribute e) {
        if(eventattributes == null) eventattributes = new LinkedHashMap<>();
        final String i = e.getIdentifier();
        check(eventattributes, i, "event attribute");
        eventattributes.put(i, e);
    }
    public EventCondition getEventCondition(String identifier) {
        return eventconditions != null ? eventconditions.getOrDefault(identifier, null) : null;
    }
    public void addEventCondition(EventCondition e) {
        if(eventconditions == null) eventconditions = new LinkedHashMap<>();
        final String i = e.getIdentifier();
        check(eventconditions, i, "event condition");
        eventconditions.put(i, e);
    }

    public CustomExplosion getExplosion(String identifier) {
        return explosions != null ? explosions.getOrDefault(identifier, null) : null;
    }
    public void addExplosion(CustomExplosion e) {
        if(explosions == null) explosions = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(explosions, identifier, "custom explosion");
        explosions.put(identifier, e);
    }

    public FactionUpgrade getFactionUpgrade(String identifier) {
        return factionupgrades != null ? factionupgrades.getOrDefault(identifier, null) : null;
    }
    public void addFactionUpgrade(FactionUpgrade e) {
        if(factionupgrades == null) factionupgrades = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(factionupgrades, identifier, "faction upgrade");
        factionupgrades.put(identifier, e);
    }

    public FactionUpgradeType getFactionUpgradeType(String identifier) {
        return factionupgradetypes != null ? factionupgradetypes.getOrDefault(identifier, null) : null;
    }
    public void addFactionUpgradeType(FactionUpgradeType e) {
        if(factionupgradetypes == null) factionupgradetypes = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(factionupgradetypes, identifier, "faction upgrade type");
        factionupgradetypes.put(identifier, e);
    }

    public FallenHero getFallenHero(String identifier) {
        return fallenheroes != null ? fallenheroes.getOrDefault(identifier, null) : null;
    }
    public void addFallenHero(FallenHero rarity) {
        if(fallenheroes == null) fallenheroes = new LinkedHashMap<>();
        final String identifier = rarity.getIdentifier();
        check(fallenheroes, identifier, "fallen hero");
        fallenheroes.put(identifier, rarity);
    }

    public FilterCategory getFilterCategory(String identifier) {
        return filtercategories != null ? filtercategories.getOrDefault(identifier, null) : null;
    }
    public void addFilterCategory(FilterCategory e) {
        if(filtercategories == null) filtercategories = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(filtercategories, identifier, "filter category");
        filtercategories.put(identifier, e);
    }

    public RarityFireball getFireball(String identifier) {
        return fireballs != null ? fireballs.getOrDefault(identifier, null) : null;
    }
    public void addFireball(RarityFireball e) {
        if(fireballs == null) fireballs = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(fireballs, identifier, "rarity fireball");
        fireballs.put(identifier, e);
    }

    public GlobalChallenge getGlobalChallenge(String identifier) {
        return globalchallenges != null ? globalchallenges.getOrDefault(identifier, null) : null;
    }
    public void addGlobalChallenge(GlobalChallenge e) {
        if(globalchallenges == null) globalchallenges = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(globalchallenges, identifier, "global challenge");
        globalchallenges.put(identifier, e);
    }

    public GlobalChallengePrize getGlobalChallengePrize(String identifier) {
        return globalchallengeprizes != null ? globalchallengeprizes.getOrDefault(identifier, null) : null;
    }
    public void addGlobalChallengePrize(GlobalChallengePrize e) {
        if(globalchallengeprizes == null) globalchallengeprizes = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(globalchallengeprizes, identifier, "global challenge prize");
        globalchallengeprizes.put(identifier, e);
    }

    public CustomKit getKit(String identifier) {
        return kits != null ? kits.getOrDefault(identifier, null) : null;
    }
    public void addKit(CustomKit e) {
        if(kits == null) kits = new LinkedHashMap<>();
        final String identifier = e.getIdentifier();
        check(kits, identifier, "custom kit");
        kits.put(identifier, e);
    }

    public Lootbox getLootbox(String identifier) {
        return lootboxes != null ? lootboxes.getOrDefault(identifier, null) : null;
    }
    public void addLootbox(Lootbox l) {
        if(lootboxes == null) lootboxes = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(lootboxes, identifier, "lootbox");
        lootboxes.put(identifier, l);
    }

    public Mask getMask(String identifier) {
        return masks != null ? masks.getOrDefault(identifier, null) : null;
    }
    public void addMask(Mask l) {
        if(masks == null) masks = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(masks, identifier, "mask");
        masks.put(identifier, l);
    }

    public MonthlyCrate getMonthlyCrate(String identifier) {
        return monthlycrates != null ? monthlycrates.getOrDefault(identifier, null) : null;
    }
    public void addMonthlyCrate(MonthlyCrate l) {
        if(monthlycrates == null) monthlycrates = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(monthlycrates, identifier, "monthly crate");
        monthlycrates.put(identifier, l);
    }

    public Outpost getOutpost(String identifier) {
        return outposts != null ? outposts.getOrDefault(identifier, null) : null;
    }
    public void addOutpost(Outpost l) {
        if(outposts == null) outposts = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(outposts, identifier, "outpost");
        outposts.put(identifier, l);
    }

    public InventoryPet getPet(String identifier) {
        return inventorypets != null ? inventorypets.getOrDefault(identifier, null) : null;
    }
    public void addPet(InventoryPet l) {
        if(inventorypets == null) inventorypets = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(inventorypets, identifier, "pet");
        inventorypets.put(identifier, l);
    }

    public PlayerQuest getPlayerQuest(String identifier) {
        return playerquests != null ? playerquests.getOrDefault(identifier, null) : null;
    }
    public void addPlayerQuest(PlayerQuest l) {
        if(playerquests == null) playerquests = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(playerquests, identifier, "player quest");
        playerquests.put(identifier, l);
    }

    public RandomizationScroll getRandomizationScroll(String identifier) {
        return randomizationscrolls != null ? randomizationscrolls.getOrDefault(identifier, null) : null;
    }
    public void addRandomizationScroll(RandomizationScroll l) {
        if(randomizationscrolls == null) randomizationscrolls = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(randomizationscrolls, identifier, "randomization scroll");
        randomizationscrolls.put(identifier, l);
    }

    public RarityGem getRarityGem(String identifier) {
        return raritygems != null ? raritygems.getOrDefault(identifier, null) : null;
    }
    public ItemStack getRarityGem(RarityGem gem, Player player) {
        final PlayerInventory pi = player.getInventory();
        final List<String> l = gem.getItem().getItemMeta().getLore();
        for(int i = 0; i < pi.getSize(); i++) {
            final ItemStack a = pi.getItem(i);
            if(a != null && a.hasItemMeta() && a.getItemMeta().hasLore() && a.getItemMeta().getLore().equals(l)) {
                return a;
            }
        }
        return null;
    }
    public void addRarityGem(RarityGem l) {
        if(raritygems == null) raritygems = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(raritygems, identifier, "rarity gem");
        raritygems.put(identifier, l);
    }

    public ServerCrate getServerCrate(String identifier) {
        return servercrates != null ? servercrates.getOrDefault(identifier, null) : null;
    }
    public void addServerCrate(ServerCrate l) {
        if(servercrates == null) servercrates = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(servercrates, identifier, "server crate");
        servercrates.put(identifier, l);
    }

    public ShopCategory getShopCategory(String identifier) {
        return shopcategories != null ? shopcategories.getOrDefault(identifier, null) : null;
    }
    public void addShopCategory(ShopCategory l) {
        if(shopcategories == null) shopcategories = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(shopcategories, identifier, "shop category");
        shopcategories.put(identifier, l);
    }

    public SoulTracker getSoulTracker(String identifier) {
        return soultrackers != null ? soultrackers.getOrDefault(identifier, null) : null;
    }
    public void addSoulTracker(SoulTracker l) {
        if(soultrackers == null) soultrackers = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(soultrackers, identifier, "soul tracker");
        soultrackers.put(identifier, l);
    }

    public Title getTitle(String identifier) {
        return titles != null ? titles.getOrDefault(identifier, null) : null;
    }
    public void addTitle(Title l) {
        if(titles == null) titles = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(titles, identifier, "title");
        titles.put(identifier, l);
    }

    public TransmogScroll getTransmogScroll(String identifier) {
        return transmogscrolls != null ? transmogscrolls.getOrDefault(identifier, null) : null;
    }
    public void addTransmogScroll(TransmogScroll t) {
        if(transmogscrolls == null) transmogscrolls = new LinkedHashMap<>();
        final String identifier = t.getIdentifier();
        check(transmogscrolls, identifier, "transmog scroll");
        transmogscrolls.put(identifier, t);
    }

    public Trinket getTrinket(String identifier) {
        return trinkets != null ? trinkets.getOrDefault(identifier, null) : null;
    }
    public void addTrinket(Trinket l) {
        if(trinkets == null) trinkets = new LinkedHashMap<>();
        final String identifier = l.getIdentifier();
        check(trinkets, identifier, "trinket");
        trinkets.put(identifier, l);
    }

    public WhiteScroll getWhiteScroll(String identifier) {
        return whitescrolls != null ? whitescrolls.getOrDefault(identifier, null) : null;
    }
    public void addWhiteScroll(WhiteScroll w) {
        if(whitescrolls == null) whitescrolls = new LinkedHashMap<>();
        final String identifier = w.getIdentifier();
        check(whitescrolls, identifier, "white scroll");
        whitescrolls.put(identifier, w);
    }

    /*
        Value Of
     */
    public ArmorSet valueOfArmorSet(Player player) {
        if(armorsets != null && player != null) {
            final PlayerInventory pi = player.getInventory();
            final ItemStack h = pi.getHelmet(), c = pi.getChestplate(), l = pi.getLeggings(), b = pi.getBoots();
            for(ArmorSet set : armorsets.values()) {
                final List<String> a = set.getArmorLore();
                if(a != null &&
                        (h != null && h.hasItemMeta() && h.getItemMeta().hasLore() && h.getItemMeta().getLore().containsAll(a)
                                && c != null && c.hasItemMeta() && c.getItemMeta().hasLore() && c.getItemMeta().getLore().containsAll(a)
                                && l != null && l.hasItemMeta() && l.getItemMeta().hasLore() && l.getItemMeta().getLore().containsAll(a)
                                && b != null && b.hasItemMeta() && b.getItemMeta().hasLore() && b.getItemMeta().getLore().containsAll(a))) {
                    return set;
                }
            }
        }
        return null;
    }
    public ArmorSet valueOfArmorSet(ItemStack is) {
        if(armorsets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(ArmorSet a : armorsets.values()) {
                if(l.containsAll(a.getArmorLore())) {
                    return a;
                }
            }
        }
        return null;
    }
    public ArmorSet valueOfArmorCrystal(ItemStack is) {
        if(armorsets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomArmor armor = CustomArmor.getCustomArmor();
            final int percent = getRemainingInt(is.getItemMeta().getLore().get(armor.percentSlot));
            for(ArmorSet a : armorsets.values()) {
                if(is.isSimilar(armor.getCrystal(a, percent))) {
                    return a;
                }
            }
        }
        return null;
    }
    public ArmorSet getArmorCrystalOnItem(ItemStack is) {
        if(armorsets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final String added = CustomArmor.getCustomArmor().crystalAddedLore;
            final List<String> l = is.getItemMeta().getLore();
            for(ArmorSet a : armorsets.values()) {
                if(l.contains(added.replace("{NAME}", a.getCrystalName()))) {
                    return a;
                }
            }
        }
        return null;
    }
    public BlackScroll valueOfBlackScroll(ItemStack is) {
        if(blackscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Material m = is.getType();
            final String d = is.getItemMeta().getDisplayName();
            for(BlackScroll b : blackscrolls.values()) {
                final ItemStack i = b.getItem();
                if(m.equals(i.getType()) && is.getData().getData() == i.getData().getData() && d.equals(i.getItemMeta().getDisplayName())) {
                    return b;
                }
            }
        }
        return null;
    }
    public CustomBoss valueOfCustomBoss(ItemStack spawnitem) {
        if(bosses != null && spawnitem != null && spawnitem.hasItemMeta()) {
            for(CustomBoss b : bosses.values()) {
                if(b.getSpawnItem().isSimilar(spawnitem)) {
                    return b;
                }
            }
        }
        return null;
    }
    public CustomExplosion valueOfCustomExplosion(ItemStack is) {
        if(explosions != null) {
            for(CustomExplosion c : explosions.values()) {
                if(c.getItem().isSimilar(is)) {
                    return c;
                }
            }
        }
        return null;
    }
    public EnchantmentOrb valueOfEnchantmentOrb(ItemStack is) {
        if(enchantmentorbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemStack item = is.clone();
            final ItemMeta M = item.getItemMeta();
            final List<String> l = M.getLore();
            final int S = l.size();
            for(EnchantmentOrb orb : enchantmentorbs.values()) {
                final ItemStack its = orb.getItem();
                final ItemMeta m = its.getItemMeta();
                final List<String> L = m.getLore();
                if(L.size() == S) {
                    final int slot = orb.getPercentLoreSlot();
                    L.set(slot, l.get(slot));
                    M.setLore(L);
                    its.setItemMeta(M);
                    if(is.isSimilar(its))
                        return orb;
                }
            }
        }
        return null;
    }
    public EnchantmentOrb valueOfEnchantmentOrb(String appliedlore) {
        if(enchantmentorbs != null && appliedlore != null) {
            for(EnchantmentOrb orb : enchantmentorbs.values())
                if(orb.getApplied().equals(appliedlore))
                    return orb;
        }
        return null;
    }
    public boolean hasEnchantmentOrb(ItemStack is) {
        if(enchantmentorbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(EnchantmentOrb orb : enchantmentorbs.values())
                if(l.contains(orb.getApplied()))
                    return true;
        }
        return false;
    }
    public EnchantmentOrb getEnchantmentOrb(ItemStack is) {
        if(enchantmentorbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(EnchantmentOrb e : enchantmentorbs.values())
                if(l.contains(e.getApplied()))
                    return e;
        }
        return null;
    }
    public EnchantRarity valueOfEnchantRarity(ItemStack is) {
        if(is != null && rarities != null) {
            for(EnchantRarity r : rarities.values()) {
                final ItemStack re = r.getRevealItem();
                if(re != null && re.isSimilar(is)) {
                    return r;
                }
            }
        }
        return null;
    }
    public EnchantRarity valueOfEnchantRarity(CustomEnchant enchant) {
        if(rarities != null) {
            for(EnchantRarity e : rarities.values()) {
                if(e.getEnchants().contains(enchant)) {
                    return e;
                }
            }
        }
        return null;
    }
    public FactionUpgrade valueOfFactionUpgrade(int slot) {
        if(factionupgrades != null) {
            for(FactionUpgrade f : factionupgrades.values()) {
                if(f.getSlot() == slot) {
                    return f;
                }
            }
        }
        return null;
    }
    public CustomKit valueOfFallenHeroSpawnItem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                final ItemStack f = k.getFallenHeroItem(k, true);
                if(f != null && (type == null || k.getClass().isInstance(type)) && f.isSimilar(is)) {
                    return k;
                }
            }
        }
        return null;
    }
    public CustomKit valueOfFallenHeroGem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                final ItemStack f = k.getFallenHeroItem(k, false);
                if(f != null && (type == null || k.getClass().isInstance(type)) && f.isSimilar(is)) {
                    return k;
                }
            }
        }
        return null;
    }
    public CustomEnchant valueOfCustomEnchant(String string) { return valueOfCustomEnchant(string, false); }
    public CustomEnchant valueOfCustomEnchant(String string, boolean checkDisabledEnchants) {
        if(string != null) {
            final String s = ChatColor.stripColor(string);
            if(enabled != null) {
                for(CustomEnchant ce : enabled.values()) {
                    if(s.startsWith(ce.getIdentifier()) || s.startsWith(ChatColor.stripColor(ce.getName())))
                        return ce;
                }
            }
            if(checkDisabledEnchants && disabled != null) {
                for(CustomEnchant ce : disabled.values()) {
                    if(s.startsWith(ce.getIdentifier()) || s.startsWith(ChatColor.stripColor(ce.getName())))
                        return ce;
                }
            }
        }
        return null;
    }
    public CustomEnchant valueOfCustomEnchant(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomEnchant e = valueOfCustomEnchant(is.getItemMeta().getDisplayName());
            final EnchantRarity r = CustomEnchants.getCustomEnchants().valueOfEnchantRarity(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
        }
        return null;
    }
    public CustomKit valueOfCustomKit(int slot, Class<?> type) {
        if(kits != null && type != null) {
            for(CustomKit k : kits.values()) {
                if(k.getSlot() == slot) {
                    final boolean isKit = type.isAssignableFrom(k.getClass());
                    if(isKit) {
                        return k;
                    }
                }
            }
        }
        return null;
    }
    public CustomKitEvolution valueOfCustomKitUpgradeGem(ItemStack is) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                if(k instanceof CustomKitEvolution) {
                    final CustomKitEvolution e = (CustomKitEvolution) k;
                    final ItemStack i = e.getUpgradeGem();
                    if(i != null && i.isSimilar(is)) {
                        return e;
                    }
                }
            }
        }
        return null;
    }
    public CustomKitMastery valueOfCustomKitRedeem(ItemStack is) {
        if(kits != null && is != null) {
            final Class a = CustomKitMastery.class;
            for(CustomKit k : kits.values()) {
                if(k.getClass().isInstance(a)) {
                    final CustomKitMastery m = (CustomKitMastery) k;
                    final ItemStack r = m.getRedeem();
                    if(r != null && r.isSimilar(is)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
    public Dungeon valueOfDungeonFromKey(ItemStack is) {
        if(dungeons != null && is != null) {
            for(Dungeon d : dungeons.values()) {
                if(d.getKey().isSimilar(is)) {
                    return d;
                }
            }
        }
        return null;
    }
    public Dungeon valueOfDungeonFromPortal(ItemStack is) {
        if(dungeons != null && is != null) {
            for(Dungeon d : dungeons.values()) {
                if(d.getPortal().isSimilar(is)) {
                    return d;
                }
            }
        }
        return null;
    }
    public RarityGem valueOfRarityGem(ItemStack item) {
        if(raritygems != null && item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final List<String> l = item.getItemMeta().getLore();
            for(RarityGem g : raritygems.values())
                if(g.getItem().getItemMeta().getLore().equals(l))
                    return g;
        }
        return null;
    }
    public RarityFireball valueOfFireball(ItemStack is) {
        if(fireballs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            for(RarityFireball f : fireballs.values())
                if(is.isSimilar(f.getItem()))
                    return f;
        }
        return null;
    }
    public RarityFireball valueOfFireball(List<EnchantRarity> exchangeablerarities) {
        if(fireballs != null) {
            for(RarityFireball f : fireballs.values()) {
                if(f.getExchangeableRarities().equals(exchangeablerarities)) {
                    return f;
                }
            }
        }
        return null;
    }
    public GlobalChallengePrize valueOfGlobalChallengePrize(int placement) {
        if(globalchallengeprizes != null) {
            for(GlobalChallengePrize p : globalchallengeprizes.values())
                if(p.getPlacement() == placement)
                    return p;
        }
        return null;
    }
    public GlobalChallengePrize valueOfGlobalChallengePrize(ItemStack display) {
        if(globalchallengeprizes != null && display != null && display.hasItemMeta())
            for(GlobalChallengePrize p : globalchallengeprizes.values()) {
                final ItemStack d = p.getItem();
                if(d.isSimilar(display))
                    return p;
            }
        return null;
    }
    public Mask valueOfMask(ItemStack is) {
        if(masks != null && is != null && is.hasItemMeta()) {
            for(Mask m : masks.values()) {
                final ItemStack i = m.getItem();
                if(i.isSimilar(is))
                    return m;
            }
        }
        return null;
    }
    public Mask getMaskOnItem(ItemStack is) {
        if(masks != null) {
            final ItemMeta im = is != null ? is.getItemMeta() : null;
            if(im != null && im.hasLore()) {
                final List<String> l = im.getLore();
                for(Mask m : masks.values())
                    if(l.contains(m.getApplied()))
                        return m;
            }
        }
        return null;
    }
    public MagicDust valueOfMagicDust(ItemStack is) {
        if(dusts != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Material m = is.getType();
            final String d = is.getItemMeta().getDisplayName();
            for(MagicDust dust : dusts.values()) {
                final ItemStack i = dust.getItem();
                if(i.getType().equals(m) && i.getItemMeta().getDisplayName().equals(d)) return dust;
            }
        }
        return null;
    }
    public MonthlyCrate valueOfMonthlyCrate(String title) {
        if(monthlycrates != null) {
            for(MonthlyCrate m : monthlycrates.values()) {
                if(m.getGuiTitle().equals(title)) {
                    return m;
                }
            }
        }
        return null;
    }
    public MonthlyCrate valueOfMonthlyCrate(ItemStack item) {
        if(monthlycrates != null) {
            for(MonthlyCrate c : monthlycrates.values()) {
                if(c.getItem().isSimilar(item)) {
                    return c;
                }
            }
        }
        return null;
    }
    public MonthlyCrate valueOfMonthlyCrate(Player player, ItemStack item) {
        if(monthlycrates != null && player != null && item != null) {
            final String p = player.getName();
            for(MonthlyCrate c : monthlycrates.values()) {
                final ItemStack is = c.getItem(), IS = is.clone();
                final ItemMeta m = is.getItemMeta();
                final List<String> s = new ArrayList<>();
                if(m.hasLore()) {
                    for(String l : m.getLore()) {
                        s.add(l.replace("{UNLOCKED_BY}", p));
                    }
                    m.setLore(s);
                }
                is.setItemMeta(m);
                if(item.isSimilar(is) || item.isSimilar(IS)) {
                    return c;
                }
            }
        }
        return null;
    }
    public MonthlyCrate valueOfMonthlyCrate(int category, int slot) {
        if(monthlycrates != null) {
            for(MonthlyCrate c : monthlycrates.values()) {
                if(category == c.getCategory() && slot == c.getCategorySlot()) {
                    return c;
                }
            }
        }
        return null;
    }
    public Outpost valueOfOutpost(int slot) {
        if(outposts != null) {
            for(Outpost o : outposts.values()) {
                if(o.getSlot() == slot) {
                    return o;
                }
            }
        }
        return null;
    }
    public InventoryPet valueOfInventoryPet(ItemStack is) {
        if(inventorypets != null && is != null) {
            for(InventoryPet p : inventorypets.values()) {
                final ItemStack i = p.getItem();
            }
        }
        return null;
    }
    public TransmogScroll valueOfTransmogScroll(ItemStack is) {
        if(transmogscrolls != null && is != null) {
            for(TransmogScroll t : transmogscrolls.values()) {
                if(t.getItem().isSimilar(is)) {
                    return t;
                }
            }
        }
        return null;
    }
}