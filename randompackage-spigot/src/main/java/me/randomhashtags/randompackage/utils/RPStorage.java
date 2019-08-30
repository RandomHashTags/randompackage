package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.legacy.ShopCategory;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.dev.EventAttribute;
import me.randomhashtags.randompackage.utils.supported.RegionalAPI;
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

    public MagicDust getDust(String identifier) {
        return dusts != null ? dusts.getOrDefault(identifier, null) : null;
    }
    public void addDust(String identifier, MagicDust e) {
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
    public void addEnchantmentOrb(String identifier, EnchantmentOrb enchantmentorb) {
        if(enchantmentorbs == null) enchantmentorbs = new LinkedHashMap<>();
        //check(enchantmentorbs, identifier, "enchantment orb"); Allows the same identifier
        enchantmentorbs.put(identifier, enchantmentorb);
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

    public RarityFireball getFireball(String identifier) {
        return fireballs != null ? fireballs.getOrDefault(identifier, null) : null;
    }
    public void addFireball(String identifier, RarityFireball e) {
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

    public InventoryPet getPet(String identifier) {
        return inventorypets != null ? inventorypets.getOrDefault(identifier, null) : null;
    }
    public void addPet(String identifier, InventoryPet l) {
        if(inventorypets == null) inventorypets = new LinkedHashMap<>();
        check(inventorypets, identifier, "pet");
        inventorypets.put(identifier, l);
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