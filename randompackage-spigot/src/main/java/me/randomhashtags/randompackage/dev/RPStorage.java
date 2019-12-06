package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static me.randomhashtags.randompackage.api.CustomArmor.getCustomArmor;

public interface RPStorage extends UVersionable {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();
    HashMap<String, YamlConfiguration> CACHED_YAMLS = new HashMap<>();

    default YamlConfiguration getYaml(String folder, String fileName) {
        if(CACHED_YAMLS.containsKey(folder + fileName)) return CACHED_YAMLS.get(folder + fileName);
        final File f = new File(DATA_FOLDER + SEPARATOR + (folder != null ? folder : ""), fileName);
        final YamlConfiguration c = f.exists() ? YamlConfiguration.loadConfiguration(f) : null;
        CACHED_YAMLS.put(folder+fileName, c);
        return c;
    }
    default YamlConfiguration getAddonConfig(String fileName) { return getYaml("addons", fileName); }
    default YamlConfiguration getRPConfig(String folder, String fileName) { return getYaml(folder, fileName); }

    default void register(Feature f, Identifiable obj) {
        if(!FEATURES.containsKey(f)) {
            FEATURES.put(f, new LinkedHashMap<>());
        }
        FEATURES.get(f).put(obj.getIdentifier(), obj);
    }

    default Identifiable get(Feature f, @NotNull String identifier) {
        return FEATURES.containsKey(f) ? FEATURES.get(f).getOrDefault(identifier, null) : null;
    }
    default LinkedHashMap<String, Identifiable> getAll(Feature f) { return FEATURES.getOrDefault(f, new LinkedHashMap<>()); }
    default LinkedHashMap<String, ? extends Object> getAllObj(Feature f) { return FEATURES.getOrDefault(f, new LinkedHashMap<>()); }

    default LinkedHashMap<String, ArmorSet> getAllArmorSets() { return (LinkedHashMap<String, ArmorSet>) getAllObj(Feature.ARMOR_SET); }
    default LinkedHashMap<String, BlackScroll> getAllBlackScrolls() { return (LinkedHashMap<String, BlackScroll>) getAllObj(Feature.BLACK_SCROLL); }
    default LinkedHashMap<String, Booster> getAllBoosters() { return (LinkedHashMap<String, Booster>) getAllObj(Feature.BOOSTER); }
    default LinkedHashMap<String, ConquestChest> getAllConquestChests() { return (LinkedHashMap<String, ConquestChest>) getAllObj(Feature.CONQUEST_CHEST); }
    default LinkedHashMap<String, CustomEnchant> getAllCustomEnchants(boolean enabled) { return (LinkedHashMap<String, CustomEnchant>) getAllObj(enabled ? Feature.CUSTOM_ENCHANT_ENABLED : Feature.CUSTOM_ENCHANT_DISABLED); }
    default LinkedHashMap<String, CustomKit> getAllCustomKits() { return (LinkedHashMap<String, CustomKit>) getAllObj(Feature.CUSTOM_KIT); }
    default LinkedHashMap<String, Dungeon> getAllDungeons() { return (LinkedHashMap<String, Dungeon>) getAllObj(Feature.DUNGEON); }
    default LinkedHashMap<String, EnchantmentOrb> getAllEnchantmentOrbs() { return (LinkedHashMap<String, EnchantmentOrb>) getAllObj(Feature.ENCHANTMENT_ORB); }
    default LinkedHashMap<String, EnvoyCrate> getAllEnvoyCrates() { return (LinkedHashMap<String, EnvoyCrate>) getAllObj(Feature.ENVOY_CRATE); }
    default LinkedHashMap<String, GlobalChallenge> getAllGlobalChallenges() { return (LinkedHashMap<String, GlobalChallenge>) getAllObj(Feature.GLOBAL_CHALLENGE); }
    default LinkedHashMap<String, GlobalChallengePrize> getAllGlobalChallengePrizes() { return (LinkedHashMap<String, GlobalChallengePrize>) getAllObj(Feature.GLOBAL_CHALLENGE_PRIZE); }
    default LinkedHashMap<String, Lootbox> getAllLootboxes() { return (LinkedHashMap<String, Lootbox>) getAllObj(Feature.LOOTBOX); }
    default LinkedHashMap<String, MagicDust> getAllMagicDust() { return (LinkedHashMap<String, MagicDust>) getAllObj(Feature.MAGIC_DUST); }
    default LinkedHashMap<String, Mask> getAllMasks() { return (LinkedHashMap<String, Mask>) getAllObj(Feature.MASK); }
    default LinkedHashMap<String, MonthlyCrate> getAllMonthlyCrates() { return (LinkedHashMap<String, MonthlyCrate>) getAllObj(Feature.MONTHLY_CRATE); }
    default LinkedHashMap<String, Outpost> getAllOutposts() { return (LinkedHashMap<String, Outpost>) getAllObj(Feature.OUTPOST); }
    default LinkedHashMap<String, PlayerQuest> getAllPlayerQuests() { return (LinkedHashMap<String, PlayerQuest>) getAllObj(Feature.PLAYER_QUEST); }
    default LinkedHashMap<String, RandomizationScroll> getAllRandomizationScrolls() { return (LinkedHashMap<String, RandomizationScroll>) getAllObj(Feature.RANDOMIZATION_SCROLL); }
    default LinkedHashMap<String, RarityFireball> getAllRarityFireballs() { return (LinkedHashMap<String, RarityFireball>) getAllObj(Feature.RARITY_FIREBALL); }
    default LinkedHashMap<String, RarityGem> getAllRarityGems() { return (LinkedHashMap<String, RarityGem>) getAllObj(Feature.RARITY_GEM); }
    default LinkedHashMap<String, ServerCrate> getAllServerCrates() { return (LinkedHashMap<String, ServerCrate>) getAllObj(Feature.SERVER_CRATE); }
    default LinkedHashMap<String, SoulTracker> getAllSoulTrackers() { return (LinkedHashMap<String, SoulTracker>) getAllObj(Feature.SOUL_TRACKER); }
    default LinkedHashMap<String, Stronghold> getAllStrongholds() { return (LinkedHashMap<String, Stronghold>) getAllObj(Feature.STRONGHOLD); }
    default LinkedHashMap<String, Title> getAllTitles() { return (LinkedHashMap<String, Title>) getAllObj(Feature.TITLE); }
    default LinkedHashMap<String, TransmogScroll> getAllTransmogScrolls() { return (LinkedHashMap<String, TransmogScroll>) getAllObj(Feature.TRANSMOG_SCROLL); }
    default LinkedHashMap<String, WhiteScroll> getAllWhiteScrolls() { return (LinkedHashMap<String, WhiteScroll>) getAllObj(Feature.WHITE_SCROLL); }

    static void unregisterAll(Feature...features) {
        for(Feature f : features) {
            FEATURES.remove(f);
        }
    }
    default void unregister(Feature...features) {
        for(Feature f : features) {
            FEATURES.remove(f);
        }
    }

    default ArmorSet getArmorSet(@NotNull String identifier) {
        final Identifiable o = get(Feature.ARMOR_SET, identifier);
        return o != null ? (ArmorSet) o : null;
    }
    default BlackScroll getBlackScroll(@NotNull String identifier) {
        final Identifiable o = get(Feature.BLACK_SCROLL, identifier);
        return o != null ? (BlackScroll) o : null;
    }
    default Booster getBooster(@NotNull String identifier) {
        final Identifiable o = get(Feature.BOOSTER, identifier);
        return o != null ? (Booster) o : null;
    }
    default ConquestChest getConquestChest(@NotNull String identifier) {
        final Identifiable o = get(Feature.CONQUEST_CHEST, identifier);
        return o != null ? (ConquestChest) o : null;
    }
    default CustomBoss getCustomBoss(@NotNull String identifier) {
        final Identifiable o = get(Feature.CUSTOM_BOSS, identifier);
        return o != null ? (CustomBoss) o : null;
    }
    default CustomEnchant getCustomEnchant(@NotNull String identifier) {
        final Identifiable o = get(Feature.CUSTOM_ENCHANT_ENABLED, identifier);
        return o != null ? (CustomEnchant) o : null;
    }
    default EnchantRarity getCustomEnchantRarity(@NotNull String identifier) {
        final Identifiable o = get(Feature.CUSTOM_ENCHANT_RARITY, identifier);
        return o != null ? (EnchantRarity) o : null;
    }
    default CustomExplosion getCustomExplosion(@NotNull String identifier) {
        final Identifiable o = get(Feature.CUSTOM_EXPLOSION, identifier);
        return o != null ? (CustomExplosion) o : null;
    }
    default CustomKit getCustomKit(@NotNull String identifier) {
        final Identifiable o = get(Feature.CUSTOM_KIT, identifier);
        return o != null ? (CustomKit) o : null;
    }
    default Disguise getDisguise(@NotNull String identifier) {
        final Identifiable o = get(Feature.DISGUISE, identifier);
        return o != null ? (Disguise) o : null;
    }
    default DuelArena getDuelArena(@NotNull String identifier) {
        final Identifiable o = get(Feature.DUEL_ARENA, identifier);
        return o != null ? (DuelArena) o : null;
    }
    default Dungeon getDungeon(@NotNull String identifier) {
        final Identifiable o = get(Feature.DUNGEON, identifier);
        return o != null ? (Dungeon) o : null;
    }
    default EnchantmentOrb getEnchantmentOrb(@NotNull String identifier) {
        final Identifiable o = get(Feature.ENCHANTMENT_ORB, identifier);
        return o != null ? (EnchantmentOrb) o : null;
    }
    default EnvoyCrate getEnvoyCrate(@NotNull String identifier) {
        final Identifiable o = get(Feature.ENVOY_CRATE, identifier);
        return o != null ? (EnvoyCrate) o : null;
    }
    default EventAttribute getEventAttribute(@NotNull String identifier) {
        final Identifiable o = get(Feature.EVENT_ATTRIBUTE, identifier);
        return o != null ? (EventAttribute) o : null;
    }
    default EventCondition getEventCondition(@NotNull String identifier) {
        final Identifiable o = get(Feature.EVENT_CONDITION, identifier);
        return o != null ? (EventCondition) o : null;
    }
    default FactionUpgrade getFactionUpgrade(@NotNull String identifier) {
        final Identifiable o = get(Feature.FACTION_UPGRADE, identifier);
        return o != null ? (FactionUpgrade) o : null;
    }
    default FactionUpgradeType getFactionUpgradeType(@NotNull String identifier) {
        final Identifiable o = get(Feature.FACTION_UPGRADE_TYPE, identifier);
        return o != null ? (FactionUpgradeType) o : null;
    }
    default FallenHero getFallenHero(@NotNull String identifier) {
        final Identifiable o = get(Feature.FALLEN_HERO, identifier);
        return o != null ? (FallenHero) o : null;
    }
    default FatBucket getFatBucket(@NotNull String identifier) {
        final Identifiable o = get(Feature.FAT_BUCKET, identifier);
        return o != null ? (FatBucket) o : null;
    }
    default FilterCategory getFilterCategory(@NotNull String identifier) {
        final Identifiable o = get(Feature.FILTER_CATEGORY, identifier);
        return o != null ? (FilterCategory) o : null;
    }
    default GlobalChallenge getGlobalChallenge(@NotNull String identifier) {
        final Identifiable o = get(Feature.GLOBAL_CHALLENGE, identifier);
        return o != null ? (GlobalChallenge) o : null;
    }
    default InventoryPet getInventoryPet(@NotNull String identifier) {
        final Identifiable o = get(Feature.INVENTORY_PET, identifier);
        return o != null ? (InventoryPet) o : null;
    }
    default KOTH getKingOfTheHill(@NotNull String identifier) {
        final Identifiable o = get(Feature.KING_OF_THE_HILL, identifier);
        return o != null ? (KOTH) o : null;
    }
    default Lootbag getLootbag(@NotNull String identifier) {
        final Identifiable o = get(Feature.LOOTBAG, identifier);
        return o != null ? (Lootbag) o : null;
    }
    default Lootbox getLootbox(@NotNull String identifier) {
        final Identifiable o = get(Feature.LOOTBOX, identifier);
        return o != null ? (Lootbox) o : null;
    }
    default MagicDust getMagicDust(@NotNull String identifier) {
        final Identifiable o = get(Feature.MAGIC_DUST, identifier);
        return o != null ? (MagicDust) o : null;
    }
    default Mask getMask(@NotNull String identifier) {
        final Identifiable o = get(Feature.MASK, identifier);
        return o != null ? (Mask) o : null;
    }
    default MonthlyCrate getMonthlyCrate(@NotNull String identifier) {
        final Identifiable o = get(Feature.MONTHLY_CRATE, identifier);
        return o != null ? (MonthlyCrate) o : null;
    }
    default Outpost getOutpost(@NotNull String identifier) {
        final Identifiable o = get(Feature.OUTPOST, identifier);
        return o != null ? (Outpost) o : null;
    }
    default PlayerQuest getPlayerQuest(@NotNull String identifier) {
        final Identifiable o = get(Feature.PLAYER_QUEST, identifier);
        return o != null ? (PlayerQuest) o : null;
    }
    default RandomizationScroll getRandomizationScroll(@NotNull String identifier) {
        final Identifiable o = get(Feature.RANDOMIZATION_SCROLL, identifier);
        return o != null ? (RandomizationScroll) o : null;
    }
    default RarityFireball getRarityFireball(@NotNull String identifier) {
        final Identifiable o = get(Feature.RARITY_FIREBALL, identifier);
        return o != null ? (RarityFireball) o : null;
    }
    default RarityGem getRarityGem(@NotNull String identifier) {
        final Identifiable o = get(Feature.RARITY_GEM, identifier);
        return o != null ? (RarityGem) o : null;
    }
    default ServerCrate getServerCrate(@NotNull String identifier) {
        final Identifiable o = get(Feature.SERVER_CRATE, identifier);
        return o != null ? (ServerCrate) o : null;
    }
    default ShopCategory getShopCategory(@NotNull String identifier) {
        final Identifiable o = get(Feature.SHOP_CATEGORY, identifier);
        return o != null ? (ShopCategory) o : null;
    }
    default SoulTracker getSoulTracker(@NotNull String identifier) {
        final Identifiable o = get(Feature.SOUL_TRACKER, identifier);
        return o != null ? (SoulTracker) o : null;
    }
    default TitanAttribute getTitanAttribute(@NotNull String identifier) {
        final Identifiable o = get(Feature.TITAN_ATTRIBUTE, identifier);
        return o != null ? (TitanAttribute) o : null;
    }
    default Title getTitle(@NotNull String identifier) {
        final Identifiable o = get(Feature.TITLE, identifier);
        return o != null ? (Title) o : null;
    }
    default TransmogScroll getTransmogScroll(@NotNull String identifier) {
        final Identifiable o = get(Feature.TRANSMOG_SCROLL, identifier);
        return o != null ? (TransmogScroll) o : null;
    }
    default Trinket getTrinket(@NotNull String identifier) {
        final Identifiable o = get(Feature.TRINKET, identifier);
        return o != null ? (Trinket) o : null;
    }
    default WhiteScroll getWhiteScroll(@NotNull String identifier) {
        final Identifiable o = get(Feature.WHITE_SCROLL, identifier);
        return o != null ? (WhiteScroll) o : null;
    }

    // valueOf
    default ArmorSet valueOfArmorSet(@NotNull Player player) {
        if(player != null) {
            final PlayerInventory pi = player.getInventory();
            final ItemStack h = pi.getHelmet(), c = pi.getChestplate(), l = pi.getLeggings(), b = pi.getBoots();
            for(ArmorSet set : getAllArmorSets().values()) {
                final List<String> lore = set.getArmorLore();
                if(lore != null &&
                        (h != null && h.hasItemMeta() && h.getItemMeta().hasLore() && h.getItemMeta().getLore().containsAll(lore)
                                && c != null && c.hasItemMeta() && c.getItemMeta().hasLore() && c.getItemMeta().getLore().containsAll(lore)
                                && l != null && l.hasItemMeta() && l.getItemMeta().hasLore() && l.getItemMeta().getLore().containsAll(lore)
                                && b != null && b.hasItemMeta() && b.getItemMeta().hasLore() && b.getItemMeta().getLore().containsAll(lore))) {
                    return set;
                }
            }
        }
        return null;
    }
    default ArmorSet valueOfArmorSet(@NotNull ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(ArmorSet a : getAllArmorSets().values()) {
                if(l.containsAll(a.getArmorLore())) {
                    return a;
                }
            }
        }
        return null;
    }
    default ArmorSet valueOfArmorCrystal(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomArmor armor = getCustomArmor();
            if(armor.isEnabled()) {
                final int percent = getRemainingInt(is.getItemMeta().getLore().get(armor.percentSlot));
                for(ArmorSet a : getAllArmorSets().values()) {
                    if(is.isSimilar(armor.getCrystal(a, percent))) {
                        return a;
                    }
                }
            }
        }
        return null;
    }
    default ArmorSet getArmorCrystalOnItem(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final CustomArmor armor = getCustomArmor();
            if(armor.isEnabled()) {
                final String added = armor.crystalAddedLore;
                final List<String> l = is.getItemMeta().getLore();
                for(ArmorSet a : getAllArmorSets().values()) {
                    if(l.contains(added.replace("{NAME}", a.getName()))) {
                        return a;
                    }
                }
            }
        }
        return null;
    }
    default BlackScroll valueOfBlackScroll(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Material m = is.getType();
            final String d = is.getItemMeta().getDisplayName();
            for(BlackScroll b : getAllBlackScrolls().values()) {
                final ItemStack i = b.getItem();
                if(m.equals(i.getType()) && (!LEGACY || is.getData().getData() == i.getData().getData()) && d.equals(i.getItemMeta().getDisplayName())) {
                    return b;
                }
            }
        }
        return null;
    }
    default EnchantmentOrb valueOfEnchantmentOrb(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemStack item = is.clone();
            final ItemMeta M = item.getItemMeta();
            final List<String> l = M.getLore();
            final int S = l.size();
            for(EnchantmentOrb orb : getAllEnchantmentOrbs().values()) {
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
    default EnchantmentOrb valueOfEnchantmentOrb(String appliedlore) {
        if(appliedlore != null) {
            for(EnchantmentOrb orb : getAllEnchantmentOrbs().values()) {
                if(orb.getApplied().equals(appliedlore))
                    return orb;
            }
        }
        return null;
    }
    default EnchantmentOrb getEnchantmentOrb(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(EnchantmentOrb e : getAllEnchantmentOrbs().values()) {
                if(l.contains(e.getApplied())) {
                    return e;
                }
            }
        }
        return null;
    }
    default CustomBoss valueOfCustomBoss(ItemStack spawnitem) {
        if(spawnitem != null && spawnitem.hasItemMeta()) {
            final HashMap<String, Identifiable> bosses = getAll(Feature.CUSTOM_BOSS);
            for(Identifiable id : bosses.values()) {
                final CustomBoss b = (CustomBoss) id;
                if(b.getSpawnItem().isSimilar(spawnitem)) {
                    return b;
                }
            }
        }
        return null;
    }
    default EnchantRarity valueOfCustomEnchantRarity(ItemStack is) {
        if(is != null) {
            final HashMap<String, Identifiable> rarities = getAll(Feature.CUSTOM_ENCHANT_RARITY);
            for(Identifiable id : rarities.values()) {
                final EnchantRarity r = (EnchantRarity) id;
                final ItemStack re = r.getRevealItem();
                if(re != null && re.isSimilar(is)) {
                    return r;
                }
            }
        }
        return null;
    }
    default EnchantRarity valueOfCustomEnchantRarity(@NotNull CustomEnchant enchant) {
        final HashMap<String, Identifiable> rarities = getAll(Feature.CUSTOM_ENCHANT_RARITY);
        for(Identifiable id : rarities.values()) {
            final EnchantRarity e = (EnchantRarity) id;
            if(e.getEnchants().contains(enchant)) {
                return e;
            }
        }
        return null;
    }
    default CustomEnchant valueOfCustomEnchant(String string) { return valueOfCustomEnchant(string, false); }
    default CustomEnchant valueOfCustomEnchant(String string, boolean checkDisabledEnchants) {
        if(string != null) {
            final String s = ChatColor.stripColor(string);
            for(CustomEnchant ce : getAllCustomEnchants(true).values()) {
                if(s.startsWith(ce.getIdentifier()) || s.startsWith(ChatColor.stripColor(ce.getName()))) {
                    return ce;
                }
            }
            if(checkDisabledEnchants) {
                for(CustomEnchant ce : getAllCustomEnchants(false).values()) {
                    if(s.startsWith(ce.getIdentifier()) || s.startsWith(ChatColor.stripColor(ce.getName()))) {
                        return ce;
                    }
                }
            }
        }
        return null;
    }
    default CustomEnchant valueOfCustomEnchant(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomEnchant e = valueOfCustomEnchant(is.getItemMeta().getDisplayName());
            final EnchantRarity r = CustomEnchants.getCustomEnchants().valueOfCustomEnchantRarity(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
        }
        return null;
    }
    default CustomExplosion valueOfCustomExplosion(ItemStack is) {
        final HashMap<String, Identifiable> explosions = getAll(Feature.CUSTOM_EXPLOSION);
        for(Identifiable id : explosions.values()) {
            final CustomExplosion c = (CustomExplosion) id;
            if(c.getItem().isSimilar(is)) {
                return c;
            }
        }
        return null;
    }
    default CustomKit valueOfCustomKit(int slot, Class<?> type) {
        if(type != null) {
            for(CustomKit k : getAllCustomKits().values()) {
                if(k.getSlot() == slot && type.isAssignableFrom(k.getClass())) {
                    return k;
                }
            }
        }
        return null;
    }
    default CustomKitMastery valueOfCustomKitRedeem(ItemStack is) {
        if(is != null) {
            for(CustomKit k : getAllCustomKits().values()) {
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
    default CustomKitEvolution valueOfCustomKitUpgradeGem(ItemStack is) {
        if(is != null) {
            final Class clazz = CustomKitEvolution.class;
            for(CustomKit k : getAllCustomKits().values()) {
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
    default Dungeon valueOfDungeonFromKey(ItemStack is) {
        if(is != null) {
            for(Dungeon d : getAllDungeons().values()) {
                if(d.getKey().isSimilar(is)) {
                    return d;
                }
            }
        }
        return null;
    }
    default Dungeon valueOfDungeonFromPortal(ItemStack is) {
        if(is != null) {
            for(Dungeon d : getAllDungeons().values()) {
                if(d.getPortal().isSimilar(is)) {
                    return d;
                }
            }
        }
        return null;
    }

    default FactionUpgrade valueOfFactionUpgrade(int slot) {
        final HashMap<String, Identifiable> upgrades = getAll(Feature.FACTION_UPGRADE);
        for(Identifiable id : upgrades.values()) {
            final FactionUpgrade f = (FactionUpgrade) id;
            if(f.getSlot() == slot) {
                return f;
            }
        }
        return null;
    }

    default GlobalChallengePrize valueOfGlobalChallengePrize(int placement) {
        for(GlobalChallengePrize p : getAllGlobalChallengePrizes().values()) {
            if(p.getPlacement() == placement) {
                return p;
            }
        }
        return null;
    }
    default GlobalChallengePrize valueOfGlobalChallengePrize(ItemStack display) {
        if(display != null && display.hasItemMeta()) {
            for(GlobalChallengePrize p : getAllGlobalChallengePrizes().values()) {
                final ItemStack d = p.getItem();
                if(d.isSimilar(display)) {
                    return p;
                }
            }
        }
        return null;
    }

    default MagicDust valueOfMagicDust(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Material m = is.getType();
            final String d = is.getItemMeta().getDisplayName();
            for(MagicDust dust : getAllMagicDust().values()) {
                final ItemStack i = dust.getItem();
                if(i.getType().equals(m) && i.getItemMeta().getDisplayName().equals(d)) {
                    return dust;
                }
            }
        }
        return null;
    }
    default Mask valueOfMask(ItemStack is) {
        if(is != null && is.hasItemMeta()) {
            for(Mask m : getAllMasks().values()) {
                final ItemStack i = m.getItem();
                if(i.isSimilar(is))
                    return m;
            }
        }
        return null;
    }
    default Mask getMaskOnItem(ItemStack is) {
        if(is != null && is.hasItemMeta()) {
            final ItemMeta im = is.getItemMeta();
            if(im.hasLore()) {
                final List<String> l = im.getLore();
                for(Mask m : getAllMasks().values()) {
                    if(l.contains(m.getApplied())) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
    default MonthlyCrate valueOfMonthlyCrate(String title) {
        for(MonthlyCrate m : getAllMonthlyCrates().values()) {
            if(m.getGuiTitle().equals(title)) {
                return m;
            }
        }
        return null;
    }
    default MonthlyCrate valueOfMonthlyCrate(ItemStack item) {
        for(MonthlyCrate c : getAllMonthlyCrates().values()) {
            if(c.getItem().isSimilar(item)) {
                return c;
            }
        }
        return null;
    }
    default MonthlyCrate valueOfMonthlyCrate(Player player, ItemStack item) {
        if(player != null && item != null) {
            final String p = player.getName();
            for(MonthlyCrate c : getAllMonthlyCrates().values()) {
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
    default MonthlyCrate valueOfMonthlyCrate(int category, int slot) {
        for(MonthlyCrate c : getAllMonthlyCrates().values()) {
            if(category == c.getCategory() && slot == c.getCategorySlot()) {
                return c;
            }
        }
        return null;
    }
    default Outpost valueOfOutpost(int slot) {
        for(Outpost o : getAllOutposts().values()) {
            if(o.getSlot() == slot) {
                return o;
            }
        }
        return null;
    }

    default RandomizationScroll valueOfRandomizationScroll(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(RandomizationScroll r : getAllRandomizationScrolls().values()) {
                if(r.getItem().getItemMeta().equals(m)) {
                    return r;
                }
            }
        }
        return null;
    }
    default RarityFireball valueOfRarityFireball(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            for(RarityFireball f : getAllRarityFireballs().values()) {
                if(is.isSimilar(f.getItem())) {
                    return f;
                }
            }
        }
        return null;
    }
    default RarityFireball valueOfRarityFireball(List<EnchantRarity> exchangeablerarities) {
        for(RarityFireball f : getAllRarityFireballs().values()) {
            if(f.getExchangeableRarities().equals(exchangeablerarities)) {
                return f;
            }
        }
        return null;
    }
    default ItemStack getRarityGem(@NotNull RarityGem gem, @NotNull Player player) {
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
    default RarityGem valueOfRarityGem(ItemStack item) {
        if(item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final List<String> l = item.getItemMeta().getLore();
            for(RarityGem g : getAllRarityGems().values()) {
                if(g.getItem().getItemMeta().getLore().equals(l)) {
                    return g;
                }
            }
        }
        return null;
    }
    default TransmogScroll valueOfTransmogScroll(ItemStack is) {
        if(is != null) {
            for(TransmogScroll t : getAllTransmogScrolls().values()) {
                if(t.getItem().isSimilar(is)) {
                    return t;
                }
            }
        }
        return null;
    }
}
