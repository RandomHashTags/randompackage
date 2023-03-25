package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.api.ArmorSockets;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.api.FatBuckets;
import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings({"unchecked"})
public interface RPValues extends UVersionableSpigot {
    @NotNull
    default LinkedHashMap<String, ?> getAllObj(Feature f) {
        return FEATURES.getOrDefault(f, new LinkedHashMap<>());
    }

    @NotNull
    default LinkedHashMap<String, ArmorSet> getAllArmorSets() {
        return (LinkedHashMap<String, ArmorSet>) getAllObj(Feature.ARMOR_SET);
    }
    @NotNull
    default LinkedHashMap<String, ArmorSocket> getAllArmorSockets() {
        return (LinkedHashMap<String, ArmorSocket>) getAllObj(Feature.ARMOR_SOCKET);
    }
    @NotNull
    default LinkedHashMap<String, BlackScroll> getAllBlackScrolls() {
        return (LinkedHashMap<String, BlackScroll>) getAllObj(Feature.SCROLL_BLACK);
    }
    @NotNull
    default LinkedHashMap<String, Booster> getAllBoosters() {
        return (LinkedHashMap<String, Booster>) getAllObj(Feature.BOOSTER);
    }
    @NotNull
    default LinkedHashMap<String, ConquestChest> getAllConquestChests() {
        return (LinkedHashMap<String, ConquestChest>) getAllObj(Feature.CONQUEST_CHEST);
    }
    @NotNull
    default LinkedHashMap<String, CustomBoss> getAllCustomBosses() {
        return (LinkedHashMap<String, CustomBoss>) getAllObj(Feature.CUSTOM_BOSS);
    }
    @NotNull
    default LinkedHashMap<String, EnchantRarity> getAllCustomEnchantRarities() {
        return (LinkedHashMap<String, EnchantRarity>) getAllObj(Feature.CUSTOM_ENCHANT_RARITY);
    }
    @NotNull
    default LinkedHashMap<String, CustomEnchantSpigot> getAllCustomEnchants(boolean enabled) {
        return (LinkedHashMap<String, CustomEnchantSpigot>) getAllObj(enabled ? Feature.CUSTOM_ENCHANT_ENABLED : Feature.CUSTOM_ENCHANT_DISABLED);
    }
    @NotNull
    default LinkedHashMap<String, CustomExplosion> getAllCustomExplosions() {
        return (LinkedHashMap<String, CustomExplosion>) getAllObj(Feature.CUSTOM_EXPLOSION);
    }
    @NotNull
    default LinkedHashMap<String, CustomKit> getAllCustomKits() {
        return (LinkedHashMap<String, CustomKit>) getAllObj(Feature.CUSTOM_KIT);
    }
    @NotNull
    default LinkedHashMap<String, Dungeon> getAllDungeons() {
        return (LinkedHashMap<String, Dungeon>) getAllObj(Feature.DUNGEON);
    }
    @NotNull
    default LinkedHashMap<String, EnchantmentOrb> getAllEnchantmentOrbs() {
        return (LinkedHashMap<String, EnchantmentOrb>) getAllObj(Feature.ENCHANTMENT_ORB);
    }
    @NotNull
    default LinkedHashMap<String, EnvoyCrate> getAllEnvoyCrates() {
        return (LinkedHashMap<String, EnvoyCrate>) getAllObj(Feature.ENVOY_CRATE);
    }
    @NotNull
    default LinkedHashMap<String, FactionUpgrade> getAllFactionUpgrades() {
        return (LinkedHashMap<String, FactionUpgrade>) getAllObj(Feature.FACTION_UPGRADE);
    }
    @NotNull
    default LinkedHashMap<String, GlobalChallenge> getAllGlobalChallenges() {
        return (LinkedHashMap<String, GlobalChallenge>) getAllObj(Feature.GLOBAL_CHALLENGE);
    }
    @NotNull
    default LinkedHashMap<String, GlobalChallengePrize> getAllGlobalChallengePrizes() {
        return (LinkedHashMap<String, GlobalChallengePrize>) getAllObj(Feature.GLOBAL_CHALLENGE_PRIZE);
    }
    @NotNull
    default LinkedHashMap<String, ItemSkin> getAllItemSkins() {
        return (LinkedHashMap<String, ItemSkin>) getAllObj(Feature.ITEM_SKIN);
    }
    @NotNull
    default LinkedHashMap<String, Lootbox> getAllLootboxes() {
        return (LinkedHashMap<String, Lootbox>) getAllObj(Feature.LOOTBOX);
    }
    @NotNull
    default LinkedHashMap<String, MagicDust> getAllMagicDust() {
        return (LinkedHashMap<String, MagicDust>) getAllObj(Feature.MAGIC_DUST);
    }
    @NotNull
    default LinkedHashMap<String, Mask> getAllMasks() {
        return (LinkedHashMap<String, Mask>) getAllObj(Feature.MASK);
    }
    @NotNull
    default LinkedHashMap<String, MonthlyCrate> getAllMonthlyCrates() {
        return (LinkedHashMap<String, MonthlyCrate>) getAllObj(Feature.MONTHLY_CRATE);
    }
    @NotNull
    default LinkedHashMap<String, Outpost> getAllOutposts() {
        return (LinkedHashMap<String, Outpost>) getAllObj(Feature.OUTPOST);
    }
    @NotNull
    default LinkedHashMap<String, PlayerQuest> getAllPlayerQuests() {
        return (LinkedHashMap<String, PlayerQuest>) getAllObj(Feature.PLAYER_QUEST);
    }
    @NotNull
    default LinkedHashMap<String, RandomizationScroll> getAllRandomizationScrolls() {
        return (LinkedHashMap<String, RandomizationScroll>) getAllObj(Feature.SCROLL_RANDOMIZATION);
    }
    @NotNull
    default LinkedHashMap<String, RarityFireball> getAllRarityFireballs() {
        return (LinkedHashMap<String, RarityFireball>) getAllObj(Feature.RARITY_FIREBALL);
    }
    @NotNull
    default LinkedHashMap<String, RarityGem> getAllRarityGems() {
        return (LinkedHashMap<String, RarityGem>) getAllObj(Feature.RARITY_GEM);
    }
    @NotNull
    default LinkedHashMap<String, ServerCrate> getAllServerCrates() {
        return (LinkedHashMap<String, ServerCrate>) getAllObj(Feature.SERVER_CRATE);
    }
    @NotNull
    default LinkedHashMap<String, SoulTracker> getAllSoulTrackers() {
        return (LinkedHashMap<String, SoulTracker>) getAllObj(Feature.SOUL_TRACKER);
    }
    @NotNull
    default LinkedHashMap<String, Stronghold> getAllStrongholds() {
        return (LinkedHashMap<String, Stronghold>) getAllObj(Feature.STRONGHOLD);
    }
    @NotNull
    default LinkedHashMap<String, Title> getAllTitles() {
        return (LinkedHashMap<String, Title>) getAllObj(Feature.TITLE);
    }
    @NotNull
    default LinkedHashMap<String, TransmogScroll> getAllTransmogScrolls() {
        return (LinkedHashMap<String, TransmogScroll>) getAllObj(Feature.SCROLL_TRANSMOG);
    }
    @NotNull
    default LinkedHashMap<String, WhiteScroll> getAllWhiteScrolls() {
        return (LinkedHashMap<String, WhiteScroll>) getAllObj(Feature.SCROLL_WHITE);
    }


    @NotNull
    default List<ItemStack> getFatBuckets(@NotNull Player player) {
        final FatBuckets buckets = FatBuckets.INSTANCE;
        final List<ItemStack> list = new ArrayList<>();
        if(buckets.isEnabled()) {
            for(ItemStack is : player.getInventory().getContents()) {
                if(buckets.isFatBucket(is)) {
                    list.add(is);
                }
            }
        }
        return list;
    }

    @Nullable
    default ArmorSet valueOfArmorSet(@NotNull Player player) {
        return valueOfArmorSet(player, false);
    }
    @Nullable
    default ArmorSet valueOfArmorSet(@NotNull Player player, boolean checkOmni) {
        final PlayerInventory inv = player.getInventory();
        final ItemStack helmet = inv.getHelmet(), chest = inv.getChestplate(), legs = inv.getLeggings(), boots = inv.getBoots();
        final List<String> helmetLore = helmet != null && helmet.hasItemMeta() && helmet.getItemMeta().hasLore() ? helmet.getItemMeta().getLore() : null;
        final List<String> chestLore = chest != null && chest.hasItemMeta() && chest.getItemMeta().hasLore() ? chest.getItemMeta().getLore() : null;
        final List<String> legLore = legs != null && legs.hasItemMeta() && legs.getItemMeta().hasLore() ? legs.getItemMeta().getLore() : null;
        final List<String> bootsLore = boots != null && boots.hasItemMeta() && boots.getItemMeta().hasLore() ? boots.getItemMeta().getLore() : null;
        final List<String> omniLore = CustomArmor.INSTANCE.omniAppliedLore;
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
        return null;
    }
    @Nullable
    default ArmorSet valueOfArmorSet(@NotNull ItemStack is) {
        if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(ArmorSet a : getAllArmorSets().values()) {
                if(l.containsAll(a.getArmorLore())) {
                    return a;
                }
            }
        }
        return null;
    }
    @Nullable
    default ArmorSet valueOfArmorCrystal(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomArmor armor = CustomArmor.INSTANCE;
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
    @Nullable
    default ArmorSet getArmorCrystalOnItem(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final CustomArmor armor = CustomArmor.INSTANCE;
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

    @Nullable
    default ArmorSocket valueOfArmorSocket(@NotNull ItemStack item) {
        final ArmorSockets sockets = ArmorSockets.INSTANCE;
        if(sockets.isEnabled()) {
            if(!item.getType().equals(Material.AIR)) {
                final ItemMeta meta = item.getItemMeta();
                if(meta.hasLore()) {
                    final List<String> lore = meta.getLore();
                    final int slot = sockets.getChanceSlot();
                    if(lore.size() > slot) {
                        final int chance = getRemainingInt(lore.get(slot));
                        for(ArmorSocket socket : getAllArmorSockets().values()) {
                            if(item.isSimilar(sockets.getArmorSocketItem(socket, chance))) {
                                return socket;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
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
    @Nullable
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
    @Nullable
    default EnchantmentOrb valueOfEnchantmentOrb(String appliedlore) {
        if(appliedlore != null) {
            for(EnchantmentOrb orb : getAllEnchantmentOrbs().values()) {
                if(orb.getApplied().equals(appliedlore))
                    return orb;
            }
        }
        return null;
    }
    @Nullable
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
    @Nullable
    default CustomBoss valueOfCustomBoss(@Nullable ItemStack spawn_item) {
        if(spawn_item != null && spawn_item.hasItemMeta()) {
            for(CustomBoss b : getAllCustomBosses().values()) {
                if(b.getSpawnItem().isSimilar(spawn_item)) {
                    return b;
                }
            }
        }
        return null;
    }
    @Nullable
    default EnchantRarity valueOfCustomEnchantRarity(@Nullable ItemStack is) {
        if(is != null) {
            for(EnchantRarity rarity : getAllCustomEnchantRarities().values()) {
                final ItemStack reveal_item = rarity.getRevealItem();
                if(reveal_item != null && reveal_item.isSimilar(is)) {
                    return rarity;
                }
            }
        }
        return null;
    }
    @Nullable
    default EnchantRarity valueOfCustomEnchantRarity(@NotNull CustomEnchantSpigot enchant) {
        for(EnchantRarity rarity : getAllCustomEnchantRarities().values()) {
            if(rarity.getEnchants().contains(enchant)) {
                return rarity;
            }
        }
        return null;
    }
    @Nullable
    default CustomEnchantSpigot valueOfCustomEnchant(@NotNull String string) {
        return valueOfCustomEnchant(string, false);
    }
    @Nullable
    default CustomEnchantSpigot valueOfCustomEnchant(@NotNull String string, boolean checkDisabledEnchants) {
        string = ChatColor.stripColor(string);
        for(CustomEnchantSpigot ce : getAllCustomEnchants(true).values()) {
            if(string.startsWith(ce.getIdentifier()) || string.startsWith(ChatColor.stripColor(ce.getName()))) {
                return ce;
            }
        }
        if(checkDisabledEnchants) {
            for(CustomEnchantSpigot ce : getAllCustomEnchants(false).values()) {
                if(string.startsWith(ce.getIdentifier()) || string.startsWith(ChatColor.stripColor(ce.getName()))) {
                    return ce;
                }
            }
        }
        return null;
    }
    @Nullable
    default CustomEnchantSpigot valueOfCustomEnchant(@Nullable ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomEnchantSpigot e = valueOfCustomEnchant(is.getItemMeta().getDisplayName());
            if(e != null) {
                final EnchantRarity r = valueOfCustomEnchantRarity(e);
                return UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
            }
        }
        return null;
    }
    @Nullable
    default CustomExplosion valueOfCustomExplosion(@NotNull ItemStack is) {
        for(CustomExplosion c : getAllCustomExplosions().values()) {
            if(c.getItem().isSimilar(is)) {
                return c;
            }
        }
        return null;
    }
    @Nullable
    default CustomKit valueOfCustomKit(int slot, @NotNull Class<?> type) {
        for(CustomKit kit : getAllCustomKits().values()) {
            if(kit.getSlot() == slot && type.isAssignableFrom(kit.getClass())) {
                return kit;
            }
        }
        return null;
    }
    @Nullable
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
    @Nullable
    default CustomKitEvolution valueOfCustomKitUpgradeGem(ItemStack is) {
        if(is != null) {
            final Class<?> clazz = CustomKitEvolution.class;
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
    @Nullable
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
    @Nullable
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

    @Nullable
    default FactionUpgrade valueOfFactionUpgrade(int slot) {
        for(FactionUpgrade f : getAllFactionUpgrades().values()) {
            if(f.getSlot() == slot) {
                return f;
            }
        }
        return null;
    }
    @Nullable
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
    @Nullable
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

    @Nullable
    default GlobalChallengePrize valueOfGlobalChallengePrize(int placement) {
        for(GlobalChallengePrize p : getAllGlobalChallengePrizes().values()) {
            if(p.getPlacement() == placement) {
                return p;
            }
        }
        return null;
    }
    @Nullable
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

    @Nullable
    default Lootbox valueOfLootbox(@Nullable ItemStack is) {
        if(is != null && is.hasItemMeta()) {
            for(Lootbox l : getAllLootboxes().values()) {
                if(l.getItem().isSimilar(is)) {
                    return l;
                }
            }
        }
        return null;
    }

    @Nullable
    default MagicDust valueOfMagicDust(@Nullable ItemStack is) {
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
    @Nullable
    default Mask valueOfMask(@Nullable ItemStack is) {
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
    @Nullable
    default Mask getMaskOnItem(@Nullable ItemStack is) {
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
    @Nullable
    default MonthlyCrate valueOfMonthlyCrate(String title) {
        for(MonthlyCrate m : getAllMonthlyCrates().values()) {
            if(m.getGuiTitle().equals(title)) {
                return m;
            }
        }
        return null;
    }
    @Nullable
    default MonthlyCrate valueOfMonthlyCrate(ItemStack item) {
        for(MonthlyCrate c : getAllMonthlyCrates().values()) {
            if(c.getItem().isSimilar(item)) {
                return c;
            }
        }
        return null;
    }
    @Nullable
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
    @Nullable
    default MonthlyCrate valueOfMonthlyCrate(int category, int slot) {
        for(MonthlyCrate c : getAllMonthlyCrates().values()) {
            if(category == c.getCategory() && slot == c.getCategorySlot()) {
                return c;
            }
        }
        return null;
    }
    @Nullable
    default Outpost valueOfOutpost(int slot) {
        for(Outpost o : getAllOutposts().values()) {
            if(o.getSlot() == slot) {
                return o;
            }
        }
        return null;
    }

    @Nullable
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
    @Nullable
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
    @Nullable
    default RarityFireball valueOfRarityFireball(List<EnchantRarity> exchangeablerarities) {
        for(RarityFireball f : getAllRarityFireballs().values()) {
            if(f.getExchangeableRarities().equals(exchangeablerarities)) {
                return f;
            }
        }
        return null;
    }
    @Nullable
    default ItemStack getRarityGem(@NotNull RarityGem gem, @NotNull Player player) {
        final PlayerInventory pi = player.getInventory();
        final List<String> l = gem.getItem().getItemMeta().getLore();
        for(int i = 0; i < pi.getSize(); i++) {
            final ItemStack is = pi.getItem(i);
            if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().equals(l)) {
                return is;
            }
        }
        return null;
    }
    @Nullable
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
    @Nullable
    default ServerCrate valueOfServerCrate(@NotNull ItemStack item) {
        for(ServerCrate crate : getAllServerCrates().values()) {
            if(crate.getItem().isSimilar(item)) {
                return crate;
            }
        }
        return null;
    }
    @Nullable
    default ServerCrate valueOfServerCrateFlare(@NotNull ItemStack flare) {
        for(ServerCrate crate : getAllServerCrates().values()) {
            final ServerCrateFlare f = crate.getFlare();
            if(f != null && flare.isSimilar(f.getItem())) {
                return crate;
            }
        }
        return null;
    }
    @Nullable
    default SoulTracker valueOfSoulTracker(@NotNull RarityGem gem) {
        for(SoulTracker st : getAllSoulTrackers().values()) {
            if(st.getConvertsTo().equals(gem)) {
                return st;
            }
        }
        return null;
    }
    @Nullable
    default SoulTracker valueOfSoulTracker(@Nullable ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(SoulTracker s : getAllSoulTrackers().values()) {
                if(s.getItem().getItemMeta().equals(m)) {
                    return s;
                }
            }
        }
        return null;
    }
    @Nullable
    default HashMap<Integer, SoulTracker> valueOfSoulTrackerApplied(ItemStack is) {
        if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> itemLore = is.getItemMeta().getLore();
            final Collection<SoulTracker> trackers = getAllSoulTrackers().values();
            int slot = 0;
            for(String s : itemLore) {
                for(SoulTracker tracker : trackers) {
                    final String applied = tracker.getApplied().replace("{SOULS}", "");
                    if(s.startsWith(applied)) {
                        final HashMap<Integer, SoulTracker> map = new HashMap<>();
                        map.put(slot, tracker);
                        return map;
                    }
                }
                slot++;
            }
        }
        return null;
    }
    @Nullable
    default SoulTracker valueOfSoulTrackerApplied(@NotNull String trackerAppliedString) {
        for(SoulTracker tracker : getAllSoulTrackers().values()) {
            if(trackerAppliedString.startsWith(tracker.getApplied().replace("{SOULS}", ""))) {
                return tracker;
            }
        }
        return null;
    }
    @Nullable
    default Title valueOfTitle(@Nullable ItemStack is) {
        if(is != null) {
            for(Title title : getAllTitles().values()) {
                if(title.getItem().isSimilar(is)) {
                    return title;
                }
            }
        }
        return null;
    }
    @Nullable
    default TransmogScroll valueOfTransmogScroll(@Nullable ItemStack is) {
        if(is != null) {
            for(TransmogScroll t : getAllTransmogScrolls().values()) {
                if(t.getItem().isSimilar(is)) {
                    return t;
                }
            }
        }
        return null;
    }
    @Nullable
    default TransmogScroll valueOfTransmogScrollApplied(@Nullable ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final CustomEnchants enchants = CustomEnchants.INSTANCE;
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
    @Nullable
    default WhiteScroll valueOfWhiteScroll(@Nullable ItemStack is) {
        if(is != null) {
            for(WhiteScroll w : getAllWhiteScrolls().values()) {
                if(is.isSimilar(w.getItem())) {
                    return w;
                }
            }
        }
        return null;
    }
    @Nullable
    default WhiteScroll valueOfWhiteScroll(@Nullable String apply) {
        if(apply != null && !apply.isEmpty()) {
            for(WhiteScroll w : getAllWhiteScrolls().values()) {
                if(w.getApplied().equals(apply)) {
                    return w;
                }
            }
        }
        return null;
    }
    @Nullable
    default List<WhiteScroll> valueOfWhiteScrollApplied(@Nullable ItemStack is) {
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
