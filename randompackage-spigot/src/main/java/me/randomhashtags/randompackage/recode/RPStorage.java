package me.randomhashtags.randompackage.recode;

import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.utils.enums.Feature;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public abstract class RPStorage {
    protected static final String version = Bukkit.getVersion();
    protected static final Random random = new Random();

    private static TreeMap<UUID, RPPlayer> players;
    protected static TreeMap<String, ArmorSet> armorsets;
    protected static TreeMap<String, Booster> boosters;
    protected static TreeMap<String, CustomBoss> bosses;
    protected static TreeMap<String, CustomEnchant> enabled, disabled;
    protected static TreeMap<String, DuelArena> duelArenas;
    protected static TreeMap<String, Dungeon> dungeons;
    protected static TreeMap<String, EnchantRarity> rarities;
    protected static TreeMap<String, EnvoyCrate> envoycrates;
    protected static TreeMap<String, CustomExplosion> explosions;
    protected static TreeMap<String, FactionUpgrade> factionupgrades;
    protected static TreeMap<String, FactionUpgradeType> factionupgradetypes;
    protected static TreeMap<String, FilterCategory> filtercategories;
    protected static TreeMap<String, GlobalChallenge> globalchallenges;
    protected static TreeMap<String, GlobalChallengePrize> globalchallengeprizes;
    protected static TreeMap<String, CustomKit> kits;
    protected static TreeMap<String, Lootbox> lootboxes;
    protected static TreeMap<String, Mask> masks;
    protected static TreeMap<String, MonthlyCrate> monthlycrates;
    protected static TreeMap<String, Outpost> outposts;
    protected static TreeMap<String, Pet> pets;
    protected static TreeMap<String, PlayerQuest> playerquests;
    protected static TreeMap<String, ServerCrate> servercrates;
    protected static TreeMap<String, ShopCategory> shopcategories;
    protected static TreeMap<String, Title> titles;
    protected static TreeMap<String, Trinket> trinkets;

    private File exists(UUID uuid) {
        final File f = new File(getPlugin.getDataFolder() + File.separator + "_Data", uuid.toString() + ".yml");
        return f.exists() ? f : null;
    }
    public RPPlayer getPlayer(UUID uuid) {
        if(players == null) players = new TreeMap<>();
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

    public ArmorSet getArmorSet(String identifier) {
        return armorsets != null ? armorsets.getOrDefault(identifier, null) : null;
    }
    public void addArmorSet(String identifier, ArmorSet a) {
        if(armorsets == null) armorsets = new TreeMap<>();
        armorsets.put(identifier, a);
    }
    public Booster getBooster(String identifier) {
        return boosters != null ? boosters.getOrDefault(identifier, null) : null;
    }
    public void addBooster(String identifier, Booster b) {
        if(boosters == null) boosters = new TreeMap<>();
        boosters.put(identifier, b);
    }

    public CustomBoss getBoss(String identifier) {
        return bosses != null ? bosses.getOrDefault(identifier, null) : null;
    }
    public void addBoss(String identifier, CustomBoss b) {
        if(bosses == null) bosses = new TreeMap<>();
        bosses.put(identifier, b);
    }

    public CustomEnchant getEnchant(String identifier) {
        return enabled != null && enabled.containsKey(identifier) ? enabled.get(identifier) : disabled != null && disabled.containsKey(identifier) ? disabled.get(identifier) : null;
    }
    public void addEnchant(String identifier, CustomEnchant enchant) {
        final boolean e = enchant.isEnabled();
        if(e && enabled == null) enabled = new TreeMap<>();
        else if(!e && disabled == null) disabled = new TreeMap<>();
        (e ? enabled : disabled).put(identifier, enchant);
    }

    public EnchantRarity getEnchantRarity(String identifier) {
        return rarities != null ? rarities.getOrDefault(identifier, null) : null;
    }
    public void addEnchantRarity(String identifier, EnchantRarity rarity) {
        if(rarities == null) rarities = new TreeMap<>();
        rarities.put(identifier, rarity);
    }

    public DuelArena getDuelArena(String identifier) {
        return duelArenas != null ? duelArenas.getOrDefault(identifier, null) : null;
    }
    public void addDuelArena(String identifier, DuelArena e) {
        if(duelArenas == null) duelArenas = new TreeMap<>();
        duelArenas.put(identifier, e);
    }

    public Dungeon getDungeon(String identifier) {
        return dungeons != null ? dungeons.getOrDefault(identifier, null) : null;
    }
    public void addDungeon(String identifier, Dungeon e) {
        if(dungeons == null) dungeons = new TreeMap<>();
        dungeons.put(identifier, e);
    }

    public EnvoyCrate getEnvoyCrate(String identifier) {
        return envoycrates != null ? envoycrates.getOrDefault(identifier, null) : null;
    }
    public void addEnvoyCrate(String identifier, EnvoyCrate e) {
        if(envoycrates == null) envoycrates = new TreeMap<>();
        envoycrates.put(identifier, e);
    }

    public CustomExplosion getExplosion(String identifier) {
        return explosions != null ? explosions.getOrDefault(identifier, null) : null;
    }
    public void addExplosion(String identifier, CustomExplosion e) {
        if(explosions == null) explosions = new TreeMap<>();
        explosions.put(identifier, e);
    }

    public FactionUpgrade getFactionUpgrade(String identifier) {
        return factionupgrades != null ? factionupgrades.getOrDefault(identifier, null) : null;
    }
    public void addFactionUpgrade(String identifier, FactionUpgrade e) {
        if(factionupgrades == null) factionupgrades = new TreeMap<>();
        factionupgrades.put(identifier, e);
    }

    public FactionUpgradeType getFactionUpgradeType(String identifier) {
        return factionupgradetypes != null ? factionupgradetypes.getOrDefault(identifier, null) : null;
    }
    public void addFactionUpgradeType(String identifier, FactionUpgradeType e) {
        if(factionupgradetypes == null) factionupgradetypes = new TreeMap<>();
        factionupgradetypes.put(identifier, e);
    }

    public FilterCategory getFilterCategory(String identifier) {
        return filtercategories != null ? filtercategories.getOrDefault(identifier, null) : null;
    }
    public void addFilterCategory(String identifier, FilterCategory e) {
        if(filtercategories == null) filtercategories = new TreeMap<>();
        filtercategories.put(identifier, e);
    }

    public GlobalChallenge getGlobalChallenge(String identifier) {
        return globalchallenges != null ? globalchallenges.getOrDefault(identifier, null) : null;
    }
    public void addGlobalChallenge(String identifier, GlobalChallenge e) {
        if(globalchallenges == null) globalchallenges = new TreeMap<>();
        globalchallenges.put(identifier, e);
    }

    public GlobalChallengePrize getGlobalChallengePrize(String identifier) {
        return globalchallengeprizes != null ? globalchallengeprizes.getOrDefault(identifier, null) : null;
    }
    public void addGlobalChallengePrize(String identifier, GlobalChallengePrize e) {
        if(globalchallengeprizes == null) globalchallengeprizes = new TreeMap<>();
        globalchallengeprizes.put(identifier, e);
    }

    public CustomKit getKit(String identifier) {
        return kits != null ? kits.getOrDefault(identifier, null) : null;
    }
    public void addKit(String identifier, CustomKit e) {
        if(kits == null) kits = new TreeMap<>();
        kits.put(identifier, e);
    }

    public Lootbox getLootbox(String identifier) {
        return lootboxes != null ? lootboxes.getOrDefault(identifier, null) : null;
    }
    public void addLootbox(String identifier, Lootbox l) {
        if(lootboxes == null) lootboxes = new TreeMap<>();
        lootboxes.put(identifier, l);
    }

    public Mask getMask(String identifier) {
        return masks != null ? masks.getOrDefault(identifier, null) : null;
    }
    public void addMask(String identifier, Mask l) {
        if(masks == null) masks = new TreeMap<>();
        masks.put(identifier, l);
    }

    public MonthlyCrate getMonthlyCrate(String identifier) {
        return monthlycrates != null ? monthlycrates.getOrDefault(identifier, null) : null;
    }
    public void addMonthlyCrate(String identifier, MonthlyCrate l) {
        if(monthlycrates == null) monthlycrates = new TreeMap<>();
        monthlycrates.put(identifier, l);
    }

    public Outpost getOutpost(String identifier) {
        return outposts != null ? outposts.getOrDefault(identifier, null) : null;
    }
    public void addOutpost(String identifier, Outpost l) {
        if(outposts == null) outposts = new TreeMap<>();
        outposts.put(identifier, l);
    }

    public Pet getPet(String identifier) {
        return pets != null ? pets.getOrDefault(identifier, null) : null;
    }
    public void addPet(String identifier, Pet l) {
        if(pets == null) pets = new TreeMap<>();
        pets.put(identifier, l);
    }

    public PlayerQuest getPlayerQuest(String identifier) {
        return playerquests != null ? playerquests.getOrDefault(identifier, null) : null;
    }
    public void addPlayerQuest(String identifier, PlayerQuest l) {
        if(playerquests == null) playerquests = new TreeMap<>();
        playerquests.put(identifier, l);
    }

    public ServerCrate getServerCrate(String identifier) {
        return servercrates != null ? servercrates.getOrDefault(identifier, null) : null;
    }
    public void addServerCrate(String identifier, ServerCrate l) {
        if(servercrates == null) servercrates = new TreeMap<>();
        servercrates.put(identifier, l);
    }

    public ShopCategory getShopCategory(String identifier) {
        return shopcategories != null ? shopcategories.getOrDefault(identifier, null) : null;
    }
    public void addShopCategory(String identifier, ShopCategory l) {
        if(shopcategories == null) shopcategories = new TreeMap<>();
        shopcategories.put(identifier, l);
    }

    public Title getTitle(String identifier) {
        return titles != null ? titles.getOrDefault(identifier, null) : null;
    }
    public void addTitle(String identifier, Title l) {
        if(titles == null) titles = new TreeMap<>();
        titles.put(identifier, l);
    }

    public Trinket getTrinket(String identifier) {
        return trinkets != null ? trinkets.getOrDefault(identifier, null) : null;
    }
    public void addTrinket(String identifier, Trinket l) {
        if(trinkets == null) trinkets = new TreeMap<>();
        trinkets.put(identifier, l);
    }

    public void deleteAll(Feature f) {
        if(f.equals(Feature.BOOSTERS)) boosters = null;
        else if(f.equals(Feature.CUSTOM_ARMOR)) armorsets = null;
        else if(f.equals(Feature.CUSTOM_BOSSES)) bosses = null;
        else if(f.equals(Feature.CUSTOM_ENCHANTS)) {
            enabled = null;
            disabled = null;
            rarities = null;
        } else if(f.equals(Feature.DUELS)) duelArenas = null;
        else if(f.equals(Feature.DUNGEONS)) dungeons = null;
        else if(f.equals(Feature.ENVOY)) envoycrates = null;
        else if(f.equals(Feature.FACTION_UPGRADES)) {
            factionupgrades = null;
            factionupgradetypes = null;
        } else if(f.equals(Feature.ITEM_FILTER)) filtercategories = null;
        else if(f.equals(Feature.MASKS)) masks = null;
        else if(f.equals(Feature.LOOTBOXES)) lootboxes = null;
        else if(f.equals(Feature.MONTHLY_CRATES)) monthlycrates = null;
        else if(f.equals(Feature.OUTPOSTS)) outposts = null;
        else if(f.equals(Feature.PLAYER_QUESTS)) playerquests = null;
        else if(f.equals(Feature.SERVER_CRATES)) servercrates = null;
        else if(f.equals(Feature.SHOP)) shopcategories = null;
        else if(f.equals(Feature.TITLES)) titles = null;
        else if(f.equals(Feature.TRINKETS)) trinkets = null;
    }
    public static void deleteAll() {

    }
}