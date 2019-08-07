package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.dev.eventattributes.EventAttribute;
import me.randomhashtags.randompackage.addons.objects.EnchantmentOrb;
import me.randomhashtags.randompackage.addons.usingpath.PathFireball;
import me.randomhashtags.randompackage.addons.usingpath.PathMagicDust;
import me.randomhashtags.randompackage.api.Boosters;
import me.randomhashtags.randompackage.api.nearFinished.Outposts;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.utils.RPFeature.rpd;
import static me.randomhashtags.randompackage.utils.RPFeature.separator;

public abstract class RPStorage extends UVersion {
    protected static final String version = Bukkit.getVersion();
    protected static final Random random = new Random();

    private static HashMap<String, YamlConfiguration> cached = new HashMap<>();

    private static LinkedHashMap<UUID, RPPlayer> players;
    protected static LinkedHashMap<String, ArmorSet> armorsets;
    protected static LinkedHashMap<String, BlackScroll> blackscrolls;
    protected static LinkedHashMap<String, Booster> boosters;
    protected static LinkedHashMap<String, CustomBoss> bosses;
    protected static LinkedHashMap<String, ConquestChest> conquestchests;
    protected static TreeMap<String, CustomEnchant> enabled, disabled;
    protected static LinkedHashMap<String, EventAttribute> eventattributes;
    protected static LinkedHashMap<String, DuelArena> duelArenas;
    protected static LinkedHashMap<String, Dungeon> dungeons;
    protected static LinkedHashMap<String, PathMagicDust> dusts;
    protected static LinkedHashMap<String, EnchantRarity> rarities;
    protected static LinkedHashMap<String, EnchantmentOrb> enchantmentorbs;
    protected static LinkedHashMap<String, EnvoyCrate> envoycrates;
    protected static LinkedHashMap<String, CustomExplosion> explosions;
    protected static LinkedHashMap<String, FactionUpgrade> factionupgrades;
    protected static LinkedHashMap<String, FactionUpgradeType> factionupgradetypes;
    protected static LinkedHashMap<String, FallenHero> fallenheroes;
    protected static LinkedHashMap<String, FilterCategory> filtercategories;
    protected static LinkedHashMap<String, PathFireball> fireballs;
    protected static LinkedHashMap<String, GlobalChallenge> globalchallenges;
    protected static LinkedHashMap<String, GlobalChallengePrize> globalchallengeprizes;
    protected static LinkedHashMap<String, CustomKit> kits;
    protected static LinkedHashMap<String, Lootbox> lootboxes;
    protected static LinkedHashMap<String, Mask> masks;
    protected static LinkedHashMap<String, MonthlyCrate> monthlycrates;
    protected static LinkedHashMap<String, Outpost> outposts;
    protected static LinkedHashMap<String, Pet> pets;
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


    private YamlConfiguration get(String folder, String yml) {
        if(cached.containsKey(yml)) return cached.get(yml);
        final File f = new File(rpd + separator + (folder != null ? folder : ""), yml);
        final YamlConfiguration c = f.exists() ? YamlConfiguration.loadConfiguration(f) : null;
        cached.put(yml ,c);
        return c;
    }
    public YamlConfiguration getAddonConfig(String yml) { return get("addons", yml); }
    public YamlConfiguration getRPConfig(String folder, String yml) { return get(folder, yml); }


    public ArmorSet getArmorSet(String identifier) {
        return armorsets != null ? armorsets.getOrDefault(identifier, null) : null;
    }
    public void addArmorSet(String identifier, ArmorSet a) {
        if(armorsets == null) armorsets = new LinkedHashMap<>();
        check(armorsets, identifier, "armor set");
        armorsets.put(identifier, a);
    }

    public BlackScroll getBlackScroll(String identifier) {
        return blackscrolls != null ? blackscrolls.getOrDefault(identifier, null) : null;
    }
    public void addBlackScroll(String identifier, BlackScroll a) {
        if(blackscrolls == null) blackscrolls = new LinkedHashMap<>();
        check(blackscrolls, identifier, "black scroll");
        blackscrolls.put(identifier, a);
    }

    public Booster getBooster(String identifier) {
        return boosters != null ? boosters.getOrDefault(identifier, null) : null;
    }
    public void addBooster(String identifier, Booster b) {
        if(boosters == null) boosters = new LinkedHashMap<>();
        check(boosters, identifier, "booster");
        boosters.put(identifier, b);
    }

    public CustomBoss getBoss(String identifier) {
        return bosses != null ? bosses.getOrDefault(identifier, null) : null;
    }
    public void addBoss(String identifier, CustomBoss b) {
        if(bosses == null) bosses = new LinkedHashMap<>();
        check(bosses, identifier, "custom boss");
        bosses.put(identifier, b);
    }

    public ConquestChest getConquestChest(String identifier) {
        return conquestchests != null ? conquestchests.getOrDefault(identifier, null) : null;
    }
    public void addConquestChest(String identifier, ConquestChest b) {
        if(conquestchests == null) conquestchests = new LinkedHashMap<>();
        check(conquestchests, identifier, "conquest chest");
        conquestchests.put(identifier, b);
    }

    public DuelArena getDuelArena(String identifier) {
        return duelArenas != null ? duelArenas.getOrDefault(identifier, null) : null;
    }
    public void addDuelArena(String identifier, DuelArena e) {
        if(duelArenas == null) duelArenas = new LinkedHashMap<>();
        check(duelArenas, identifier, "duel arena");
        duelArenas.put(identifier, e);
    }

    public Dungeon getDungeon(String identifier) {
        return dungeons != null ? dungeons.getOrDefault(identifier, null) : null;
    }
    public void addDungeon(String identifier, Dungeon e) {
        if(dungeons == null) dungeons = new LinkedHashMap<>();
        check(dungeons, identifier, "dungeon");
        dungeons.put(identifier, e);
    }

    public PathMagicDust getDust(String identifier) {
        return dusts != null ? dusts.getOrDefault(identifier, null) : null;
    }
    public void addDust(String identifier, PathMagicDust e) {
        if(dusts == null) dusts = new LinkedHashMap<>();
        check(dusts, identifier, "rarity dust");
        dusts.put(identifier, e);
    }

    public CustomEnchant getEnchant(String identifier) {
        return enabled != null && enabled.containsKey(identifier) ? enabled.get(identifier) : disabled != null && disabled.containsKey(identifier) ? disabled.get(identifier) : null;
    }
    public void addEnchant(String identifier, CustomEnchant enchant) {
        final boolean e = enchant.isEnabled();
        if(e && enabled == null) enabled = new TreeMap<>();
        else if(!e && disabled == null) disabled = new TreeMap<>();
        check(e ? enabled : disabled, identifier, "custom enchant");
        (e ? enabled : disabled).put(identifier, enchant);
    }

    public EnchantmentOrb getEnchantmentOrb(String identifier) {
        return enchantmentorbs != null ? enchantmentorbs.get(identifier) : null;
    }
    public void addEnchantmentOrb(String identifier, EnchantmentOrb enchant) {
        if(enchantmentorbs == null) enchantmentorbs = new LinkedHashMap<>();
        //check(enchantmentorbs, identifier, "enchantment orb"); Allows the same identifier
        enchantmentorbs.put(identifier, enchant);
    }

    public EnchantRarity getEnchantRarity(String identifier) {
        return rarities != null ? rarities.getOrDefault(identifier, null) : null;
    }
    public void addEnchantRarity(String identifier, EnchantRarity rarity) {
        if(rarities == null) rarities = new LinkedHashMap<>();
        check(rarities, identifier, "enchant rarity");
        rarities.put(identifier, rarity);
    }

    public EnvoyCrate getEnvoyCrate(String identifier) {
        return envoycrates != null ? envoycrates.getOrDefault(identifier, null) : null;
    }
    public void addEnvoyCrate(String identifier, EnvoyCrate e) {
        if(envoycrates == null) envoycrates = new LinkedHashMap<>();
        check(envoycrates, identifier, "envoy crate");
        envoycrates.put(identifier, e);
    }

    public EventAttribute getEventAttribute(String identifier) {
        return eventattributes != null ? eventattributes.getOrDefault(identifier, null) : null;
    }
    public void addEventAttribute(String identifier, EventAttribute e) {
        if(eventattributes == null) eventattributes = new LinkedHashMap<>();
        check(eventattributes, identifier, "event attribute");
        eventattributes.put(identifier, e);
    }

    public CustomExplosion getExplosion(String identifier) {
        return explosions != null ? explosions.getOrDefault(identifier, null) : null;
    }
    public void addExplosion(String identifier, CustomExplosion e) {
        if(explosions == null) explosions = new LinkedHashMap<>();
        check(explosions, identifier, "custom explosion");
        explosions.put(identifier, e);
    }

    public FactionUpgrade getFactionUpgrade(String identifier) {
        return factionupgrades != null ? factionupgrades.getOrDefault(identifier, null) : null;
    }
    public void addFactionUpgrade(String identifier, FactionUpgrade e) {
        if(factionupgrades == null) factionupgrades = new LinkedHashMap<>();
        check(factionupgrades, identifier, "faction upgrade");
        factionupgrades.put(identifier, e);
    }

    public FactionUpgradeType getFactionUpgradeType(String identifier) {
        return factionupgradetypes != null ? factionupgradetypes.getOrDefault(identifier, null) : null;
    }
    public void addFactionUpgradeType(String identifier, FactionUpgradeType e) {
        if(factionupgradetypes == null) factionupgradetypes = new LinkedHashMap<>();
        check(factionupgradetypes, identifier, "faction upgrade type");
        factionupgradetypes.put(identifier, e);
    }

    public FallenHero getFallenHero(String identifier) {
        return fallenheroes != null ? fallenheroes.getOrDefault(identifier, null) : null;
    }
    public void addFallenHero(String identifier, FallenHero rarity) {
        if(fallenheroes == null) fallenheroes = new LinkedHashMap<>();
        check(fallenheroes, identifier, "fallen hero");
        fallenheroes.put(identifier, rarity);
    }

    public FilterCategory getFilterCategory(String identifier) {
        return filtercategories != null ? filtercategories.getOrDefault(identifier, null) : null;
    }
    public void addFilterCategory(String identifier, FilterCategory e) {
        if(filtercategories == null) filtercategories = new LinkedHashMap<>();
        check(filtercategories, identifier, "filter category");
        filtercategories.put(identifier, e);
    }

    public PathFireball getFireball(String identifier) {
        return fireballs != null ? fireballs.getOrDefault(identifier, null) : null;
    }
    public void addFireball(String identifier, PathFireball e) {
        if(fireballs == null) fireballs = new LinkedHashMap<>();
        check(fireballs, identifier, "rarity fireball");
        fireballs.put(identifier, e);
    }

    public GlobalChallenge getGlobalChallenge(String identifier) {
        return globalchallenges != null ? globalchallenges.getOrDefault(identifier, null) : null;
    }
    public void addGlobalChallenge(String identifier, GlobalChallenge e) {
        if(globalchallenges == null) globalchallenges = new LinkedHashMap<>();
        check(globalchallenges, identifier, "global challenge");
        globalchallenges.put(identifier, e);
    }

    public GlobalChallengePrize getGlobalChallengePrize(String identifier) {
        return globalchallengeprizes != null ? globalchallengeprizes.getOrDefault(identifier, null) : null;
    }
    public void addGlobalChallengePrize(String identifier, GlobalChallengePrize e) {
        if(globalchallengeprizes == null) globalchallengeprizes = new LinkedHashMap<>();
        check(globalchallengeprizes, identifier, "global challenge prize");
        globalchallengeprizes.put(identifier, e);
    }

    public CustomKit getKit(String identifier) {
        return kits != null ? kits.getOrDefault(identifier, null) : null;
    }
    public void addKit(String identifier, CustomKit e) {
        if(kits == null) kits = new LinkedHashMap<>();
        check(kits, identifier, "custom kit");
        kits.put(identifier, e);
    }

    public Lootbox getLootbox(String identifier) {
        return lootboxes != null ? lootboxes.getOrDefault(identifier, null) : null;
    }
    public void addLootbox(String identifier, Lootbox l) {
        if(lootboxes == null) lootboxes = new LinkedHashMap<>();
        check(lootboxes, identifier, "lootbox");
        lootboxes.put(identifier, l);
    }

    public Mask getMask(String identifier) {
        return masks != null ? masks.getOrDefault(identifier, null) : null;
    }
    public void addMask(String identifier, Mask l) {
        if(masks == null) masks = new LinkedHashMap<>();
        check(masks, identifier, "mask");
        masks.put(identifier, l);
    }

    public MonthlyCrate getMonthlyCrate(String identifier) {
        return monthlycrates != null ? monthlycrates.getOrDefault(identifier, null) : null;
    }
    public void addMonthlyCrate(String identifier, MonthlyCrate l) {
        if(monthlycrates == null) monthlycrates = new LinkedHashMap<>();
        check(monthlycrates, identifier, "monthly crate");
        monthlycrates.put(identifier, l);
    }

    public Outpost getOutpost(String identifier) {
        return outposts != null ? outposts.getOrDefault(identifier, null) : null;
    }
    public void addOutpost(String identifier, Outpost l) {
        if(outposts == null) outposts = new LinkedHashMap<>();
        check(outposts, identifier, "outpost");
        outposts.put(identifier, l);
    }

    public Pet getPet(String identifier) {
        return pets != null ? pets.getOrDefault(identifier, null) : null;
    }
    public void addPet(String identifier, Pet l) {
        if(pets == null) pets = new LinkedHashMap<>();
        check(pets, identifier, "pet");
        pets.put(identifier, l);
    }

    public PlayerQuest getPlayerQuest(String identifier) {
        return playerquests != null ? playerquests.getOrDefault(identifier, null) : null;
    }
    public void addPlayerQuest(String identifier, PlayerQuest l) {
        if(playerquests == null) playerquests = new LinkedHashMap<>();
        check(playerquests, identifier, "player quest");
        playerquests.put(identifier, l);
    }

    public RandomizationScroll getRandomizationScroll(String identifier) {
        return randomizationscrolls != null ? randomizationscrolls.getOrDefault(identifier, null) : null;
    }
    public void addRandomizationScroll(String identifier, RandomizationScroll l) {
        if(randomizationscrolls == null) randomizationscrolls = new LinkedHashMap<>();
        check(randomizationscrolls, identifier, "randomization scroll");
        randomizationscrolls.put(identifier, l);
    }

    public RarityGem getRarityGem(String identifier) {
        return raritygems != null ? raritygems.getOrDefault(identifier, null) : null;
    }
    public void addRarityGem(String identifier, RarityGem l) {
        if(raritygems == null) raritygems = new LinkedHashMap<>();
        check(raritygems, identifier, "rarity gem");
        raritygems.put(identifier, l);
    }

    public ServerCrate getServerCrate(String identifier) {
        return servercrates != null ? servercrates.getOrDefault(identifier, null) : null;
    }
    public void addServerCrate(String identifier, ServerCrate l) {
        if(servercrates == null) servercrates = new LinkedHashMap<>();
        check(servercrates, identifier, "server crate");
        servercrates.put(identifier, l);
    }

    public ShopCategory getShopCategory(String identifier) {
        return shopcategories != null ? shopcategories.getOrDefault(identifier, null) : null;
    }
    public void addShopCategory(String identifier, ShopCategory l) {
        if(shopcategories == null) shopcategories = new LinkedHashMap<>();
        check(shopcategories, identifier, "shop category");
        shopcategories.put(identifier, l);
    }

    public SoulTracker getSoulTracker(String identifier) {
        return soultrackers != null ? soultrackers.getOrDefault(identifier, null) : null;
    }
    public void addSoulTracker(String identifier, SoulTracker l) {
        if(soultrackers == null) soultrackers = new LinkedHashMap<>();
        check(soultrackers, identifier, "soul tracker");
        soultrackers.put(identifier, l);
    }

    public Title getTitle(String identifier) {
        return titles != null ? titles.getOrDefault(identifier, null) : null;
    }
    public void addTitle(String identifier, Title l) {
        if(titles == null) titles = new LinkedHashMap<>();
        check(titles, identifier, "title");
        titles.put(identifier, l);
    }

    public TransmogScroll getTransmogScroll(String identifier) {
        return transmogscrolls != null ? transmogscrolls.getOrDefault(identifier, null) : null;
    }
    public void addTransmogScroll(String identifier, TransmogScroll t) {
        if(transmogscrolls == null) transmogscrolls = new LinkedHashMap<>();
        check(transmogscrolls, identifier, "transmog scroll");
        transmogscrolls.put(identifier, t);
    }

    public Trinket getTrinket(String identifier) {
        return trinkets != null ? trinkets.getOrDefault(identifier, null) : null;
    }
    public void addTrinket(String identifier, Trinket l) {
        if(trinkets == null) trinkets = new LinkedHashMap<>();
        check(trinkets, identifier, "trinket");
        trinkets.put(identifier, l);
    }

    public WhiteScroll getWhiteScroll(String identifier) {
        return whitescrolls != null ? whitescrolls.getOrDefault(identifier, null) : null;
    }
    public void addWhiteScroll(String identifier, WhiteScroll w) {
        if(whitescrolls == null) whitescrolls = new LinkedHashMap<>();
        check(whitescrolls, identifier, "white scroll");
        whitescrolls.put(identifier, w);
    }

    public void deleteAll(Feature f) {
        if(f.equals(Feature.BOOSTERS)) {
            boosters = null;
            Boosters.activeFactionBoosters = null;
        } else if(f.equals(Feature.BLACK_SCROLLS)) blackscrolls = null;
        else if(f.equals(Feature.CONQUEST)) conquestchests = null;
        else if(f.equals(Feature.CUSTOM_ARMOR)) armorsets = null;
        else if(f.equals(Feature.CUSTOM_BOSSES)) bosses = null;
        else if(f.equals(Feature.CUSTOM_ENCHANTS)) {
            enabled = null;
            disabled = null;
            rarities = null;
        } else if(f.equals(Feature.DUELS)) duelArenas = null;
        else if(f.equals(Feature.DUNGEONS)) dungeons = null;
        else if(f.equals(Feature.ENCHANTMENT_ORBS)) enchantmentorbs = null;
        else if(f.equals(Feature.ENVOY)) envoycrates = null;
        else if(f.equals(Feature.FACTION_UPGRADES)) {
            factionupgrades = null;
            factionupgradetypes = null;
        } else if(f.equals(Feature.FIREBALLS_AND_DUST)) {
            dusts = null;
            fireballs = null;
        } else if(f.equals(Feature.ITEM_FILTER)) filtercategories = null;
        else if(f.equals(Feature.MASKS)) masks = null;
        else if(f.equals(Feature.LOOTBOXES)) lootboxes = null;
        else if(f.equals(Feature.MONTHLY_CRATES)) monthlycrates = null;
        else if(f.equals(Feature.OUTPOSTS)) {
            outposts = null;
            Outposts.statuses = null;
        } else if(f.equals(Feature.PLAYER_QUESTS)) playerquests = null;
        else if(f.equals(Feature.RANDOMIZATION_SCROLLS)) randomizationscrolls = null;
        else if(f.equals(Feature.RARITY_GEMS)) raritygems = null;
        else if(f.equals(Feature.SERVER_CRATES)) servercrates = null;
        else if(f.equals(Feature.SOUL_TRACKERS)) soultrackers = null;
        else if(f.equals(Feature.SHOP)) shopcategories = null;
        else if(f.equals(Feature.TITLES)) titles = null;
        else if(f.equals(Feature.TRINKETS)) trinkets = null;
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
}