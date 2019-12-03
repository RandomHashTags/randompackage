package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public interface RPStorage {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();

    default void register(Feature f, Identifiable obj) {
        if(!FEATURES.containsKey(f)) {
            FEATURES.put(f, new LinkedHashMap<>());
        }
        FEATURES.get(f).put(obj.getIdentifier(), obj);
    }

    default Identifiable get(Feature f, @NotNull String identifier) {
        return FEATURES.containsKey(f) ? FEATURES.get(f).getOrDefault(identifier, null) : null;
    }
    default LinkedHashMap<String, Identifiable> getAll(Feature f) {
        return FEATURES.getOrDefault(f, new LinkedHashMap<>());
    }
    default void unregister(Feature...features) {
        for(Feature f : features) {
            FEATURES.remove(f);
        }
    }

    default Set<ArmorSet> getAllArmorSets() {
        final Set<Identifiable> i = new HashSet<>(getAll(Feature.ARMOR_SET).values());
        final Set<ArmorSet> sets = new HashSet<>();
        for(Identifiable id : i) {
            sets.add((ArmorSet) id);
        }
        return sets;
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
        final Identifiable o = get(Feature.CUSTOM_ENCHANT, identifier);
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
            final List<Identifiable> armorsets = new ArrayList<>(getAll(Feature.ARMOR_SET).values());
            final PlayerInventory pi = player.getInventory();
            final ItemStack h = pi.getHelmet(), c = pi.getChestplate(), l = pi.getLeggings(), b = pi.getBoots();
            for(Identifiable s : armorsets) {
                final ArmorSet set = (ArmorSet) s;
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
    default ArmorSet valueOfArmorCrystal(ItemStack is) {
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

    default EnchantRarity valueOfCustomEnchantRarity(ItemStack is) {
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
    default EnchantRarity valueOfCustomEnchantRarity(CustomEnchant enchant) {
        if(rarities != null) {
            for(EnchantRarity e : rarities.values()) {
                if(e.getEnchants().contains(enchant)) {
                    return e;
                }
            }
        }
        return null;
    }
    default CustomEnchant valueOfCustomEnchant(String string) { return valueOfCustomEnchant(string, false); }
    default CustomEnchant valueOfCustomEnchant(String string, boolean checkDisabledEnchants) {
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
    default CustomEnchant valueOfCustomEnchant(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomEnchant e = valueOfCustomEnchant(is.getItemMeta().getDisplayName());
            final EnchantRarity r = CustomEnchants.getCustomEnchants().valueOfCustomEnchantRarity(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
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

}
