package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.attribute.EventAttribute;
import me.randomhashtags.randompackage.attribute.EventCondition;
import me.randomhashtags.randompackage.dev.Disguise;
import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface RPStorage extends RPValues {
    HashMap<String, YamlConfiguration> CACHED_YAMLS = new HashMap<>();

    default YamlConfiguration getYaml(String folder, String fileName) {
        if(CACHED_YAMLS.containsKey(folder + fileName)) {
            return CACHED_YAMLS.get(folder + fileName);
        }
        final File file = new File(DATA_FOLDER + SEPARATOR + (folder != null ? folder : ""), fileName);
        final YamlConfiguration c = file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
        CACHED_YAMLS.put(folder+fileName, c);
        return c;
    }
    default YamlConfiguration getAddonConfig(String fileName) {
        return getYaml("addons", fileName);
    }
    default YamlConfiguration getRPConfig(String folder, String fileName) {
        return getYaml(folder, fileName);
    }

    default void register(Feature f, Identifiable obj) {
        if(!FEATURES.containsKey(f)) {
            FEATURES.put(f, new LinkedHashMap<>());
        }
        FEATURES.get(f).put(obj.getIdentifier(), obj);
        if(obj instanceof GivedpItemable) {
            final GivedpItemable item = (GivedpItemable) obj;
            for(String identifier : item.getGivedpItemIdentifiers()) {
                GivedpItemable.GIVEDP_ITEMS.put(identifier, item);
            }
        }
    }

    default Identifiable get(Feature f, @NotNull String identifier) {
        return FEATURES.containsKey(f) ? FEATURES.get(f).getOrDefault(identifier, null) : null;
    }
    default LinkedHashMap<String, Identifiable> getAll(Feature f) { return FEATURES.getOrDefault(f, new LinkedHashMap<>()); }

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
    default ArmorSocket getArmorSocket(@NotNull String identifier) {
        final Identifiable o = get(Feature.ARMOR_SOCKET, identifier);
        return o != null ? (ArmorSocket) o : null;
    }
    default BlackScroll getBlackScroll(@NotNull String identifier) {
        final Identifiable o = get(Feature.SCROLL_BLACK, identifier);
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
    default ItemSkin getItemSkin(@NotNull String identifier) {
        final Identifiable o = get(Feature.ITEM_SKIN, identifier);
        return o != null ? (ItemSkin) o : null;
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
        final Identifiable o = get(Feature.SCROLL_RANDOMIZATION, identifier);
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
        final Identifiable o = get(Feature.SCROLL_TRANSMOG, identifier);
        return o != null ? (TransmogScroll) o : null;
    }
    default Trinket getTrinket(@NotNull String identifier) {
        final Identifiable o = get(Feature.TRINKET, identifier);
        return o != null ? (Trinket) o : null;
    }
    default WhiteScroll getWhiteScroll(@NotNull String identifier) {
        final Identifiable o = get(Feature.SCROLL_WHITE, identifier);
        return o != null ? (WhiteScroll) o : null;
    }
}
