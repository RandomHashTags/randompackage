package me.randomhashtags.randompackage.util;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static me.randomhashtags.randompackage.api.CustomArmor.getCustomArmor;

@SuppressWarnings({"unchecked"})
public interface RPValues extends UVersionable {
    default LinkedHashMap<String, ?> getAllObj(Feature f) {
        return FEATURES.getOrDefault(f, new LinkedHashMap<>());
    }

    default LinkedHashMap<String, ArmorSet> getAllArmorSets() {
        return (LinkedHashMap<String, ArmorSet>) getAllObj(Feature.ARMOR_SET);
    }
    default LinkedHashMap<String, BlackScroll> getAllBlackScrolls() {
        return (LinkedHashMap<String, BlackScroll>) getAllObj(Feature.SCROLL_BLACK);
    }
    default LinkedHashMap<String, Booster> getAllBoosters() {
        return (LinkedHashMap<String, Booster>) getAllObj(Feature.BOOSTER);
    }
    default LinkedHashMap<String, ConquestChest> getAllConquestChests() {
        return (LinkedHashMap<String, ConquestChest>) getAllObj(Feature.CONQUEST_CHEST);
    }
    default LinkedHashMap<String, CustomBoss> getAllCustomBosses() {
        return (LinkedHashMap<String, CustomBoss>) getAllObj(Feature.CUSTOM_BOSS);
    }
    default LinkedHashMap<String, EnchantRarity> getAllCustomEnchantRarities() {
        return (LinkedHashMap<String, EnchantRarity>) getAllObj(Feature.CUSTOM_ENCHANT_RARITY);
    }
    default LinkedHashMap<String, CustomEnchant> getAllCustomEnchants(boolean enabled) {
        return (LinkedHashMap<String, CustomEnchant>) getAllObj(enabled ? Feature.CUSTOM_ENCHANT_ENABLED : Feature.CUSTOM_ENCHANT_DISABLED);
    }
    default LinkedHashMap<String, CustomExplosion> getAllCustomExplosions() {
        return (LinkedHashMap<String, CustomExplosion>) getAllObj(Feature.CUSTOM_EXPLOSION);
    }
    default LinkedHashMap<String, CustomKit> getAllCustomKits() {
        return (LinkedHashMap<String, CustomKit>) getAllObj(Feature.CUSTOM_KIT);
    }
    default LinkedHashMap<String, Dungeon> getAllDungeons() {
        return (LinkedHashMap<String, Dungeon>) getAllObj(Feature.DUNGEON);
    }
    default LinkedHashMap<String, EnchantmentOrb> getAllEnchantmentOrbs() {
        return (LinkedHashMap<String, EnchantmentOrb>) getAllObj(Feature.ENCHANTMENT_ORB);
    }
    default LinkedHashMap<String, EnvoyCrate> getAllEnvoyCrates() {
        return (LinkedHashMap<String, EnvoyCrate>) getAllObj(Feature.ENVOY_CRATE);
    }
    default LinkedHashMap<String, FactionUpgrade> getAllFactionUpgrades() {
        return (LinkedHashMap<String, FactionUpgrade>) getAllObj(Feature.FACTION_UPGRADE);
    }
    default LinkedHashMap<String, GlobalChallenge> getAllGlobalChallenges() {
        return (LinkedHashMap<String, GlobalChallenge>) getAllObj(Feature.GLOBAL_CHALLENGE);
    }
    default LinkedHashMap<String, GlobalChallengePrize> getAllGlobalChallengePrizes() {
        return (LinkedHashMap<String, GlobalChallengePrize>) getAllObj(Feature.GLOBAL_CHALLENGE_PRIZE);
    }
    default LinkedHashMap<String, ItemSkin> getAllItemSkins() {
        return (LinkedHashMap<String, ItemSkin>) getAllObj(Feature.ITEM_SKIN);
    }
    default LinkedHashMap<String, Lootbox> getAllLootboxes() {
        return (LinkedHashMap<String, Lootbox>) getAllObj(Feature.LOOTBOX);
    }
    default LinkedHashMap<String, MagicDust> getAllMagicDust() {
        return (LinkedHashMap<String, MagicDust>) getAllObj(Feature.MAGIC_DUST);
    }
    default LinkedHashMap<String, Mask> getAllMasks() {
        return (LinkedHashMap<String, Mask>) getAllObj(Feature.MASK);
    }
    default LinkedHashMap<String, MonthlyCrate> getAllMonthlyCrates() {
        return (LinkedHashMap<String, MonthlyCrate>) getAllObj(Feature.MONTHLY_CRATE);
    }
    default LinkedHashMap<String, Outpost> getAllOutposts() {
        return (LinkedHashMap<String, Outpost>) getAllObj(Feature.OUTPOST);
    }
    default LinkedHashMap<String, PlayerQuest> getAllPlayerQuests() {
        return (LinkedHashMap<String, PlayerQuest>) getAllObj(Feature.PLAYER_QUEST);
    }
    default LinkedHashMap<String, RandomizationScroll> getAllRandomizationScrolls() {
        return (LinkedHashMap<String, RandomizationScroll>) getAllObj(Feature.SCROLL_RANDOMIZATION);
    }
    default LinkedHashMap<String, RarityFireball> getAllRarityFireballs() {
        return (LinkedHashMap<String, RarityFireball>) getAllObj(Feature.RARITY_FIREBALL);
    }
    default LinkedHashMap<String, RarityGem> getAllRarityGems() {
        return (LinkedHashMap<String, RarityGem>) getAllObj(Feature.RARITY_GEM);
    }
    default LinkedHashMap<String, ServerCrate> getAllServerCrates() {
        return (LinkedHashMap<String, ServerCrate>) getAllObj(Feature.SERVER_CRATE);
    }
    default LinkedHashMap<String, SoulTracker> getAllSoulTrackers() {
        return (LinkedHashMap<String, SoulTracker>) getAllObj(Feature.SOUL_TRACKER);
    }
    default LinkedHashMap<String, Stronghold> getAllStrongholds() {
        return (LinkedHashMap<String, Stronghold>) getAllObj(Feature.STRONGHOLD);
    }
    default LinkedHashMap<String, Title> getAllTitles() {
        return (LinkedHashMap<String, Title>) getAllObj(Feature.TITLE);
    }
    default LinkedHashMap<String, TransmogScroll> getAllTransmogScrolls() {
        return (LinkedHashMap<String, TransmogScroll>) getAllObj(Feature.SCROLL_TRANSMOG);
    }
    default LinkedHashMap<String, WhiteScroll> getAllWhiteScrolls() {
        return (LinkedHashMap<String, WhiteScroll>) getAllObj(Feature.SCROLL_WHITE);
    }


    default ArmorSet valueOfArmorSet(@NotNull Player player) {
        return valueOfArmorSet(player, false);
    }
    default ArmorSet valueOfArmorSet(@NotNull Player player, boolean checkOmni) {
        if(player != null) {
            final PlayerInventory inv = player.getInventory();
            final ItemStack helmet = inv.getHelmet(), chest = inv.getChestplate(), legs = inv.getLeggings(), boots = inv.getBoots();
            final List<String> helmetLore = helmet != null && helmet.hasItemMeta() && helmet.getItemMeta().hasLore() ? helmet.getItemMeta().getLore() : null;
            final List<String> chestLore = chest != null && chest.hasItemMeta() && chest.getItemMeta().hasLore() ? chest.getItemMeta().getLore() : null;
            final List<String> legLore = legs != null && legs.hasItemMeta() && legs.getItemMeta().hasLore() ? legs.getItemMeta().getLore() : null;
            final List<String> bootsLore = boots != null && helmet.hasItemMeta() && boots.getItemMeta().hasLore() ? boots.getItemMeta().getLore() : null;
            final List<String> omniLore = getCustomArmor().omniAppliedLore;
            for(ArmorSet set : getAllArmorSets().values()) {
                final List<String> lore = set.getArmorLore();
                if(lore != null
                        && (
                                helmetLore != null && (helmetLore.containsAll(lore) || checkOmni && helmetLore.containsAll(omniLore))
                                && chestLore != null && (chestLore.containsAll(lore) || checkOmni && chestLore.containsAll(omniLore))
                                && legLore != null && (legLore.containsAll(lore) || checkOmni && legLore.containsAll(omniLore))
                                && bootsLore != null && (bootsLore.containsAll(lore) || checkOmni && bootsLore.containsAll(omniLore))
                        )
                ) {
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
            for(CustomBoss b : getAllCustomBosses().values()) {
                if(b.getSpawnItem().isSimilar(spawnitem)) {
                    return b;
                }
            }
        }
        return null;
    }
    default EnchantRarity valueOfCustomEnchantRarity(ItemStack is) {
        if(is != null) {
            for(EnchantRarity r : getAllCustomEnchantRarities().values()) {
                final ItemStack re = r.getRevealItem();
                if(re != null && re.isSimilar(is)) {
                    return r;
                }
            }
        }
        return null;
    }
    default EnchantRarity valueOfCustomEnchantRarity(@NotNull CustomEnchant enchant) {
        for(EnchantRarity e : getAllCustomEnchantRarities().values()) {
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
            final EnchantRarity r = valueOfCustomEnchantRarity(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
        }
        return null;
    }
    default CustomExplosion valueOfCustomExplosion(ItemStack is) {
        for(CustomExplosion c : getAllCustomExplosions().values()) {
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
        for(FactionUpgrade f : getAllFactionUpgrades().values()) {
            if(f.getSlot() == slot) {
                return f;
            }
        }
        return null;
    }
    default CustomKit valueOfFallenHeroSpawnItem(ItemStack is, Class type) {
        if(is != null) {
            for(CustomKit k : getAllCustomKits().values()) {
                final ItemStack f = k.getFallenHeroItem(k, true);
                if(f != null && (type == null || k.getClass().isInstance(type)) && f.isSimilar(is)) {
                    return k;
                }
            }
        }
        return null;
    }
    default CustomKit valueOfFallenHeroGem(ItemStack is, Class type) {
        if(is != null) {
            for(CustomKit k : getAllCustomKits().values()) {
                final ItemStack f = k.getFallenHeroItem(k, false);
                if(f != null && (type == null || k.getClass().isInstance(type)) && f.isSimilar(is)) {
                    return k;
                }
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

    default Lootbox valueOfLootbox(@NotNull ItemStack is) {
        if(is != null && is.hasItemMeta()) {
            for(Lootbox l : getAllLootboxes().values()) {
                if(l.getItem().isSimilar(is)) {
                    return l;
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
                if(is.isSimilar(i)) {
                    return m;
                }
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
    default Title valueOfTitle(ItemStack is) {
        if(is != null) {
            for(Title title : getAllTitles().values()) {
                if(title.getItem().isSimilar(is)) {
                    return title;
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
    default TransmogScroll valueOfTransmogScrollApplied(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final CustomEnchants enchants = CustomEnchants.getCustomEnchants();
            if(enchants.isEnabled()) {
                final String size = Integer.toString(enchants.getEnchantsOnItem(is).size()), d = is.getItemMeta().getDisplayName();
                for(TransmogScroll t : getAllTransmogScrolls().values()) {
                    if(d.endsWith(t.getApplied().replace("{LORE_COUNT}", size).replace("{ENCHANT_SIZE}", size))) {
                        return t;
                    }
                }
            }
        }
        return null;
    }
    default WhiteScroll valueOfWhiteScroll(@NotNull ItemStack is) {
        if(is != null) {
            for(WhiteScroll w : getAllWhiteScrolls().values()) {
                if(is.isSimilar(w.getItem())) {
                    return w;
                }
            }
        }
        return null;
    }
    default WhiteScroll valueOfWhiteScroll(@NotNull String apply) {
        if(apply != null && !apply.isEmpty()) {
            for(WhiteScroll w : getAllWhiteScrolls().values()) {
                if(w.getApplied().equals(apply)) {
                    return w;
                }
            }
        }
        return null;
    }
    default List<WhiteScroll> valueOfWhiteScrollApplied(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<WhiteScroll> l = new ArrayList<>();
            for(String s : is.getItemMeta().getLore()) {
                final WhiteScroll w = valueOfWhiteScroll(s);
                if(w != null) l.add(w);
            }
            return l;
        }
        return null;
    }
}
