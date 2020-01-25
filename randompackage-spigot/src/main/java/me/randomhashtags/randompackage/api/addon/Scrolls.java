package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.file.PathBlackScroll;
import me.randomhashtags.randompackage.addon.file.PathRandomizationScroll;
import me.randomhashtags.randompackage.addon.file.PathTransmogScroll;
import me.randomhashtags.randompackage.addon.file.PathWhiteScroll;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.RandomizationScrollUseEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class Scrolls extends CustomEnchants {
    private static Scrolls instance;
    public static Scrolls getScrolls() {
        if(instance == null) instance = new Scrolls();
        return instance;
    }

    private String folder;
    public YamlConfiguration config;

    private Set<Feature> enabled;

    @Override
    public String getIdentifier() {
        return "SCROLLS";
    }
    @Override
    public void load() {
        save("addons", "scrolls.yml");
        folder = DATA_FOLDER + SEPARATOR + "addons";
        enabled = new HashSet<>();
        config = YamlConfiguration.loadConfiguration(new File(folder, "scrolls.yml"));
        final Feature[] list = new Feature[] {
                Feature.SCROLL_BLACK,
                Feature.SCROLL_TRANSMOG,
                Feature.SCROLL_WHITE,
                Feature.SCROLL_HOLY,
                Feature.SCROLL_RANDOMIZATION
        };
        for(Feature scroll : list) {
            tryLoadingScroll(scroll);
        }
    }
    @Override
    public void unload() {
        for(Feature scroll : new HashSet<>(enabled)) {
            tryUnloadingScroll(scroll);
        }
    }

    public boolean isEnabled(Feature scroll) {
        return enabled.contains(scroll);
    }
    public void tryLoadingScroll(Feature scroll) {
        if(!isEnabled(scroll)) {
            final String[] values = scroll.name().toLowerCase().split("_");
            if(RP_CONFIG.getBoolean("custom enchants." + values[1] + " " + values[0] + "s")) {
                switch (scroll) {
                    case SCROLL_BLACK:
                        loadBlackScrolls();
                        break;
                    case SCROLL_TRANSMOG:
                        loadTransmogScrolls();
                        break;
                    case SCROLL_WHITE:
                        loadWhiteScrolls();
                        break;
                    case SCROLL_HOLY:
                        loadHolyScrolls();
                        break;
                    case SCROLL_RANDOMIZATION:
                        loadRandomizationScrolls();
                        break;
                }
            }
        }
    }
    public void tryUnloadingScroll(Feature scroll) {
        if(isEnabled(scroll)) {
            switch (scroll) {
                case SCROLL_BLACK:
                    unloadBlackScrolls();
                    break;
                case SCROLL_TRANSMOG:
                    unloadTransmogScrolls();
                    break;
                case SCROLL_WHITE:
                    unloadWhiteScrolls();
                    break;
                case SCROLL_HOLY:
                    unloadHolyScrolls();
                    break;
                case SCROLL_RANDOMIZATION:
                    unloadRandomizationScrolls();
                    break;
            }
        }
    }

    private void loadBlackScrolls() {
        final long started = System.currentTimeMillis();
        enabled.add(Feature.SCROLL_BLACK);
        for(String key : getConfigurationSectionKeys(config, "black scrolls", false, "sounds", "particles")) {
            new PathBlackScroll(key);
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.SCROLL_BLACK).size() + " Black Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private void unloadBlackScrolls() {
        enabled.remove(Feature.SCROLL_BLACK);
        unregister(Feature.SCROLL_BLACK);
    }

    private void loadTransmogScrolls() {
        final long started = System.currentTimeMillis();
        enabled.add(Feature.SCROLL_TRANSMOG);
        final List<ItemStack> list = new ArrayList<>();
        for(String s : getConfigurationSectionKeys(config, "transmog scrolls", false, "sounds", "particles")) {
            list.add(new PathTransmogScroll(s).getItem());
        }
        addGivedpCategory(list, UMaterial.PAPER, "Transmog Scrolls", "Givedp: Transmog Scrolls");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.SCROLL_TRANSMOG).size() + " Transmog Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private void unloadTransmogScrolls() {
        enabled.remove(Feature.SCROLL_TRANSMOG);
        unregister(Feature.SCROLL_TRANSMOG);
    }

    private void loadWhiteScrolls() {
        final long started = System.currentTimeMillis();
        enabled.add(Feature.SCROLL_WHITE);
        final List<ItemStack> list = new ArrayList<>();
        for(String s : getConfigurationSectionKeys(config, "white scrolls", false, "sounds", "particles")) {
            list.add(new PathWhiteScroll(s).getItem());
        }
        addGivedpCategory(list, UMaterial.MAP, "White Scrolls", "Givedp: White Scrolls");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.SCROLL_WHITE) .size()+ " White Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private void unloadWhiteScrolls() {
        enabled.remove(Feature.SCROLL_WHITE);
        unregister(Feature.SCROLL_WHITE);
    }

    private void loadHolyScrolls() {
        final long started = System.currentTimeMillis();
        enabled.add(Feature.SCROLL_HOLY);
        for(String s : getConfigurationSectionKeys(config, "holy scrolls", false, "sounds", "particles")) {
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.SCROLL_HOLY).size() + " Holy Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private void unloadHolyScrolls() {
        enabled.remove(Feature.SCROLL_HOLY);
        unregister(Feature.SCROLL_HOLY);
    }

    private void loadRandomizationScrolls() {
        final long started = System.currentTimeMillis();
        enabled.add(Feature.SCROLL_RANDOMIZATION);
        final List<ItemStack> list = new ArrayList<>();
        for(String s : getConfigurationSectionKeys(config, "randomization scrolls", false, "sounds", "particles")) {
            list.add(new PathRandomizationScroll(s).getItem());
        }
        addGivedpCategory(list, UMaterial.PAPER, "Randomization Scrolls", "Givedp: Randomization Scrolls");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.SCROLL_RANDOMIZATION).size() + " Randomization Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private void unloadRandomizationScrolls() {
        enabled.remove(Feature.SCROLL_RANDOMIZATION);
        unregister(Feature.SCROLL_RANDOMIZATION);
    }


    public ItemStack applyBlackScroll(ItemStack is, ItemStack blackscroll, BlackScroll bs) {
        final HashMap<CustomEnchant, Integer> enchants = getEnchantsOnItem(is);
        if(is != null && enchants.size() > 0) {
            final Set<CustomEnchant> key = enchants.keySet();
            CustomEnchant enchant = (CustomEnchant) key.toArray()[RANDOM.nextInt(key.size())];
            final List<EnchantRarity> rarityList = bs.getAppliesToRarities();
            int percentSuccess = -1;
            for(String string : blackscroll.getItemMeta().getLore()) {
                if(getRemainingInt(string) != -1) {
                    percentSuccess = getRemainingInt(string);
                }
            }
            for(int f = 1; f <= 5; f++) {
                final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
                if(rarityList.contains(rarity)) {
                    int enchantlevel = enchants.get(enchant);
                    final ItemMeta itemMeta = is.getItemMeta();
                    final List<String> lore = new ArrayList<>(itemMeta.getLore());
                    int enchantslot = -1;
                    final String target = rarity.getApplyColors() + enchant.getName() + " " + toRoman(enchantlevel);
                    for(int i = 0; i < lore.size(); i++) {
                        if(target.equals(lore.get(i))) {
                            enchantslot = i;
                        }
                    }
                    if(enchantslot == -1) return null;
                    lore.remove(enchantslot);
                    itemMeta.setLore(lore); lore.clear();
                    is.setItemMeta(itemMeta);
                    return getRevealedItem(enchant, enchantlevel, percentSuccess, 100, true, true).clone();
                } else {
                    enchant = (CustomEnchant) key.toArray()[RANDOM.nextInt(key.size())];
                }
            }
        }
        return item;
    }
    public boolean applyRandomizationScroll(Player player, ItemStack item, RandomizationScroll scroll) {
        final CustomEnchant enchant = valueOfCustomEnchant(item);
        final EnchantRarity rarity = enchant != null ? valueOfCustomEnchantRarity(enchant) : null;
        final ItemMeta meta = item.getItemMeta();
        if(meta != null && rarity != null && scroll.getAppliesToRarities().contains(rarity)) {
            final String success = rarity.getSuccess(), destroy = rarity.getDestroy();
            int newSuccess = RANDOM.nextInt(101), newDestroy = RANDOM.nextInt(101);
            final RandomizationScrollUseEvent useEvent = new RandomizationScrollUseEvent(player, enchant, getEnchantmentLevel(meta.getDisplayName()), scroll, newSuccess, newDestroy);
            PLUGIN_MANAGER.callEvent(useEvent);
            newSuccess = useEvent.getNewSuccess();
            newDestroy = useEvent.getNewDestroy();
            final List<String> lore = new ArrayList<>();
            for(String string : meta.getLore()) {
                final String remainingInt = "" + getRemainingInt(string);
                if(string.equals(success.replace("{PERCENT}", remainingInt))) {
                    string = success.replace("{PERCENT}", "" + newSuccess);
                } else if(string.equals(destroy.replace("{PERCENT}", remainingInt))) {
                    string = destroy.replace("{PERCENT}", "" + newDestroy);
                }
                lore.add(string);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }
    public boolean applyTransmogScroll(ItemStack is, TransmogScroll scroll) {
        boolean did = true;
        if(is != null && scroll != null) {
            did = scroll.canBeApplied(is);
            if(did) {
                final HashMap<CustomEnchant, Integer> enchants = getEnchantsOnItem(is);
                final int size = enchants.size();
                final ItemMeta itemMeta = is.getItemMeta();
                final List<String> lore = new ArrayList<>();
                final String apply = scroll.getApplied(), previous = apply.replace("{LORE_COUNT}", Integer.toString(size));
                int newsize = 0;
                if(itemMeta.hasLore()) {
                    final List<String> itemLore = itemMeta.getLore();
                    for(String ss : scroll.getRarityOrganization()) {
                        final EnchantRarity r = getCustomEnchantRarity(ss);
                        for(String s : itemLore) {
                            final CustomEnchant enchant = valueOfCustomEnchant(s);
                            if(enchant != null && valueOfCustomEnchantRarity(enchant) == r) {
                                lore.add(s);
                            }
                        }
                        newsize = lore.size();
                    }
                    for(String s : itemLore) {
                        if(!lore.contains(s)) {
                            lore.add(s);
                        }
                    }
                }
                final String current = apply.replace("{LORE_COUNT}", Integer.toString(newsize)), material = is.getType().name();
                itemMeta.setLore(lore);

                String name;
                if(itemMeta.hasDisplayName()) {
                    name = itemMeta.getDisplayName();
                    if(name.contains(previous)) {
                        name = name.replace(previous, current);
                    }
                } else {
                    name = is.getType().name();
                }
                if(name.equals(material)) {
                    name = toMaterial(material, false);
                }
                name = name.replace("{ENCHANT_SIZE}", current);
                if(!name.contains(previous)) {
                    name = name.concat(" " + current);
                }
                ChatColor color = ChatColor.RESET;
                if(itemMeta.hasEnchants()) {
                    color = ChatColor.AQUA;
                }
                itemMeta.setDisplayName(color + name);
                is.setItemMeta(itemMeta);
            }
        }
        return did;
    }
    public void updateTransmogScroll(ItemStack is, int prevSize, int newSize) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final String size = Integer.toString(prevSize), newsize = Integer.toString(newSize), name = is.getItemMeta().getDisplayName();
            for(TransmogScroll scroll : getAllTransmogScrolls().values()) {
                final String applied = scroll.getApplied(), actual = applied.replace("{LORE_COUNT}", size).replace("{ENCHANT_SIZE}", size);
                if(name.endsWith(actual)) {
                    itemMeta = is.getItemMeta();
                    itemMeta.setDisplayName(itemMeta.getDisplayName().replace(actual, applied.replace("{LORE_COUNT}", newsize).replace("{ENCHANT_SIZE}", newsize)));
                    is.setItemMeta(itemMeta);
                    return;
                }
            }
        }
    }
    public boolean applyWhiteScroll(Player player, ItemStack is, WhiteScroll scroll) {
        final boolean did = player != null && is != null && scroll != null && scroll.canBeApplied(is);
        if(did) {
            final String requiredString = scroll.getRequiredWhiteScroll();
            final WhiteScroll required = requiredString != null ? getWhiteScroll(requiredString) : null;
            final ItemMeta itemMeta = is.getItemMeta();
            final List<String> lore = new ArrayList<>();
            if(is.hasItemMeta() && itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            lore.add(scroll.getApplied());
            if(required != null && scroll.removesRequiredAfterApplication()) {
                lore.remove(required.getApplied());
            }
            itemMeta.setLore(lore);
            is.setItemMeta(itemMeta);
            player.updateInventory();
        }
        return did;
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(valueOfBlackScroll(is) != null || valueOfWhiteScroll(is) != null || valueOfRandomizationScroll(is) != null || valueOfTransmogScroll(is) != null) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR) && cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            final Player player = (Player) event.getWhoClicked();
            item = current;
            itemMeta = current.getItemMeta();

            final RandomizationScroll randomizationscroll = valueOfRandomizationScroll(cursor);
            final BlackScroll blackscroll = randomizationscroll == null ? valueOfBlackScroll(cursor) : null;
            final TransmogScroll transmogscroll = blackscroll == null ? valueOfTransmogScroll(cursor) : null;
            final WhiteScroll whitescroll = valueOfWhiteScroll(cursor);
            if(blackscroll != null) {
                final boolean hasMeta = item.hasItemMeta() && itemMeta.hasLore();
                final HashMap<CustomEnchant, Integer> enchantsOnItem = hasMeta ? getEnchantsOnItem(current) : null;
                if(enchantsOnItem != null && !enchantsOnItem.isEmpty()) {
                    giveItem(player, applyBlackScroll(current, cursor, blackscroll));
                    if(itemMeta.hasDisplayName() && isEnabled(Feature.SCROLL_TRANSMOG)) {
                        final int size = enchantsOnItem.size();
                        updateTransmogScroll(current, size, size-1);
                    }
                } else {
                    return;
                }
            } else if(randomizationscroll != null) {
                if(!applyRandomizationScroll(player, current, randomizationscroll)) {
                    return;
                }
            } else if(transmogscroll != null) {
                if(!applyTransmogScroll(current, transmogscroll)) {
                    return;
                }
            } else if(whitescroll != null) {
                if(!applyWhiteScroll(player, current, whitescroll)) {
                    return;
                }
            } else {
                return;
            }

            event.setCancelled(true);
            event.setCurrentItem(current);
            final int a = cursor.getAmount();
            if(a == 1) {
                event.setCursor(new ItemStack(Material.AIR));
            } else {
                cursor.setAmount(a-1);
            }
            player.updateInventory();
        }
    }
}
