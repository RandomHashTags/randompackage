package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.attribute.EventAttribute;
import me.randomhashtags.randompackage.attribute.EventCondition;
import me.randomhashtags.randompackage.addon.dev.Disguise;
import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if(obj instanceof GivedpItemableSpigot) {
            final GivedpItemableSpigot item = (GivedpItemableSpigot) obj;
            for(String identifier : item.getGivedpItemIdentifiers()) {
                GivedpItemableSpigot.GIVEDP_ITEMS.put(identifier, item);
            }
        }
    }

    @Nullable
    default Identifiable get(Feature feature, @NotNull String identifier) {
        return FEATURES.containsKey(feature) ? FEATURES.get(feature).getOrDefault(identifier, null) : null;
    }
    @NotNull
    default LinkedHashMap<String, Identifiable> getAll(@NotNull Feature feature) {
        return FEATURES.getOrDefault(feature, new LinkedHashMap<>());
    }

    static void unregisterAll(@NotNull Feature...features) {
        for(Feature f : features) {
            FEATURES.remove(f);
        }
    }
    default void unregister(@NotNull Feature...features) {
        for(Feature f : features) {
            FEATURES.remove(f);
        }
    }

    @Nullable
    default ArmorSet getArmorSet(@NotNull String identifier) {
        return (ArmorSet) get(Feature.ARMOR_SET, identifier);
    }
    @Nullable
    default ArmorSocket getArmorSocket(@NotNull String identifier) {
        return (ArmorSocket) get(Feature.ARMOR_SOCKET, identifier);
    }
    @Nullable
    default BlackScroll getBlackScroll(@NotNull String identifier) {
        return (BlackScroll) get(Feature.SCROLL_BLACK, identifier);
    }
    @Nullable
    default Booster getBooster(@NotNull String identifier) {
        return (Booster) get(Feature.BOOSTER, identifier);
    }
    @Nullable
    default ConquestChest getConquestChest(@NotNull String identifier) {
        return (ConquestChest) get(Feature.CONQUEST_CHEST, identifier);
    }
    @Nullable
    default CustomBoss getCustomBoss(@NotNull String identifier) {
        return (CustomBoss) get(Feature.CUSTOM_BOSS, identifier);
    }
    @Nullable
    default CustomEnchantSpigot getCustomEnchant(@NotNull String identifier) {
        return (CustomEnchantSpigot) get(Feature.CUSTOM_ENCHANT_ENABLED, identifier);
    }
    @Nullable
    default EnchantRarity getCustomEnchantRarity(@NotNull String identifier) {
        return (EnchantRarity) get(Feature.CUSTOM_ENCHANT_RARITY, identifier);
    }
    @Nullable
    default CustomExplosion getCustomExplosion(@NotNull String identifier) {
        return (CustomExplosion) get(Feature.CUSTOM_EXPLOSION, identifier);
    }
    @Nullable
    default CustomKit getCustomKit(@NotNull String identifier) {
        return (CustomKit) get(Feature.CUSTOM_KIT, identifier);
    }
    @Nullable
    default Disguise getDisguise(@NotNull String identifier) {
        return (Disguise) get(Feature.DISGUISE, identifier);
    }
    @Nullable
    default DuelArena getDuelArena(@NotNull String identifier) {
        return (DuelArena) get(Feature.DUEL_ARENA, identifier);
    }
    @Nullable
    default Dungeon getDungeon(@NotNull String identifier) {
        return (Dungeon) get(Feature.DUNGEON, identifier);
    }
    @Nullable
    default EnchantmentOrb getEnchantmentOrb(@NotNull String identifier) {
        return (EnchantmentOrb) get(Feature.ENCHANTMENT_ORB, identifier);
    }
    @Nullable
    default EnvoyCrate getEnvoyCrate(@NotNull String identifier) {
        return (EnvoyCrate) get(Feature.ENVOY_CRATE, identifier);
    }
    @Nullable
    default EventAttribute getEventAttribute(@NotNull String identifier) {
        return (EventAttribute) get(Feature.EVENT_ATTRIBUTE, identifier);
    }
    @Nullable
    default EventCondition getEventCondition(@NotNull String identifier) {
        return (EventCondition) get(Feature.EVENT_CONDITION, identifier);
    }
    @Nullable
    default FactionUpgrade getFactionUpgrade(@NotNull String identifier) {
        return (FactionUpgrade) get(Feature.FACTION_UPGRADE, identifier);
    }
    @Nullable
    default FactionUpgradeType getFactionUpgradeType(@NotNull String identifier) {
        return (FactionUpgradeType) get(Feature.FACTION_UPGRADE_TYPE, identifier);
    }
    @Nullable
    default FallenHero getFallenHero(@NotNull String identifier) {
        return (FallenHero) get(Feature.FALLEN_HERO, identifier);
    }
    @Nullable
    default FatBucket getFatBucket(@NotNull String identifier) {
        return (FatBucket) get(Feature.FAT_BUCKET, identifier);
    }
    @Nullable
    default FilterCategory getFilterCategory(@NotNull String identifier) {
        return (FilterCategory) get(Feature.FILTER_CATEGORY, identifier);
    }
    @Nullable
    default GlobalChallenge getGlobalChallenge(@NotNull String identifier) {
        return (GlobalChallenge) get(Feature.GLOBAL_CHALLENGE, identifier);
    }
    @Nullable
    default InventoryPet getInventoryPet(@NotNull String identifier) {
        return (InventoryPet) get(Feature.INVENTORY_PET, identifier);
    }
    @Nullable
    default ItemSkin getItemSkin(@NotNull String identifier) {
        return (ItemSkin) get(Feature.ITEM_SKIN, identifier);
    }
    @Nullable
    default KOTH getKingOfTheHill(@NotNull String identifier) {
        return (KOTH) get(Feature.KING_OF_THE_HILL, identifier);
    }
    @Nullable
    default Lootbag getLootbag(@NotNull String identifier) {
        return (Lootbag) get(Feature.LOOTBAG, identifier);
    }
    @Nullable
    default Lootbox getLootbox(@NotNull String identifier) {
        return (Lootbox) get(Feature.LOOTBOX, identifier);
    }
    @Nullable
    default MagicDust getMagicDust(@NotNull String identifier) {
        return (MagicDust) get(Feature.MAGIC_DUST, identifier);
    }
    @Nullable
    default Mask getMask(@NotNull String identifier) {
        return (Mask) get(Feature.MASK, identifier);
    }
    @Nullable
    default MonthlyCrate getMonthlyCrate(@NotNull String identifier) {
        return  (MonthlyCrate) get(Feature.MONTHLY_CRATE, identifier);
    }
    @Nullable
    default Outpost getOutpost(@NotNull String identifier) {
        return (Outpost) get(Feature.OUTPOST, identifier);
    }
    @Nullable
    default PlayerQuest getPlayerQuest(@NotNull String identifier) {
        return (PlayerQuest) get(Feature.PLAYER_QUEST, identifier);
    }
    @Nullable
    default RandomizationScroll getRandomizationScroll(@NotNull String identifier) {
        return (RandomizationScroll) get(Feature.SCROLL_RANDOMIZATION, identifier);
    }
    @Nullable
    default RarityFireball getRarityFireball(@NotNull String identifier) {
        return (RarityFireball) get(Feature.RARITY_FIREBALL, identifier);
    }
    @Nullable
    default RarityGem getRarityGem(@NotNull String identifier) {
        return (RarityGem) get(Feature.RARITY_GEM, identifier);
    }
    @Nullable
    default ServerCrate getServerCrate(@NotNull String identifier) {
        return (ServerCrate) get(Feature.SERVER_CRATE, identifier);
    }
    @Nullable
    default ShopCategory getShopCategory(@NotNull String identifier) {
        return (ShopCategory) get(Feature.SHOP_CATEGORY, identifier);
    }
    @Nullable
    default SoulTracker getSoulTracker(@NotNull String identifier) {
        return (SoulTracker) get(Feature.SOUL_TRACKER, identifier);
    }
    @Nullable
    default TitanAttribute getTitanAttribute(@NotNull String identifier) {
        return (TitanAttribute) get(Feature.TITAN_ATTRIBUTE, identifier);
    }
    @Nullable
    default Title getTitle(@NotNull String identifier) {
        return (Title) get(Feature.TITLE, identifier);
    }
    @Nullable
    default TransmogScroll getTransmogScroll(@NotNull String identifier) {
        return (TransmogScroll) get(Feature.SCROLL_TRANSMOG, identifier);
    }
    @Nullable
    default Trinket getTrinket(@NotNull String identifier) {
        return (Trinket) get(Feature.TRINKET, identifier);
    }
    @Nullable
    default WhiteScroll getWhiteScroll(@NotNull String identifier) {
        return (WhiteScroll) get(Feature.SCROLL_WHITE, identifier);
    }
}
