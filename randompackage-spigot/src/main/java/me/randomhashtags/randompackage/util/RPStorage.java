package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.addon.DuelArena;
import me.randomhashtags.randompackage.addon.Dungeon;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public abstract class RPStorage extends RegionalAPI {
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

    protected static LinkedHashMap<String, Disguise> disguises;
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
    protected static LinkedHashMap<String, FatBucket> fatbuckets;
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
        if(cached.containsKey(folder + fileName)) return cached.get(folder + fileName);
        final File f = new File(dataFolder + separator + (folder != null ? folder : ""), fileName);
        final YamlConfiguration c = f.exists() ? YamlConfiguration.loadConfiguration(f) : null;
        cached.put(folder+fileName, c);
        return c;
    }
    public YamlConfiguration getAddonConfig(String fileName) { return get("addons", fileName); }
    public YamlConfiguration getRPConfig(String folder, String fileName) { return get(folder, fileName); }

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

    /*
        Value Of
     */

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

    public ArmorSet getArmorCrystalOnItem(ItemStack is) {
        if(armorsets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final String added = CustomArmor.getCustomArmor().crystalAddedLore;
            final List<String> l = is.getItemMeta().getLore();
            for(ArmorSet a : armorsets.values()) {
                if(l.contains(added.replace("{NAME}", a.getName()))) {
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
    public static EnchantRarity valueOfEnchantRarity(CustomEnchant enchant) {
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
    public static CustomEnchant valueOfCustomEnchant(String string) { return valueOfCustomEnchant(string, false); }
    public static CustomEnchant valueOfCustomEnchant(String string, boolean checkDisabledEnchants) {
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
    public static CustomEnchant valueOfCustomEnchant(ItemStack is) {
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
                if(k.getSlot() == slot && type.isAssignableFrom(k.getClass())) {
                    return k;
                }
            }
        }
        return null;
    }
    public CustomKitEvolution valueOfCustomKitUpgradeGem(ItemStack is) {
        if(is != null && kits != null) {
            final Class clazz = CustomKitEvolution.class;
            for(CustomKit k : kits.values()) {
                if(k.getClass().isAssignableFrom(clazz)) {
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
            for(CustomKit k : kits.values()) {
                final boolean istype = k instanceof CustomKitMastery;
                if(istype) {
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
    public static Mask valueOfMask(ItemStack is) {
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
                for(Mask m : masks.values()) {
                    if(l.contains(m.getApplied())) {
                        return m;
                    }
                }
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