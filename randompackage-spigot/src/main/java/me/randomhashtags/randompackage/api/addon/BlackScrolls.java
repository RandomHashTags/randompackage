package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.BlackScroll;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.addon.file.PathBlackScroll;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BlackScrolls extends CustomEnchants {
    private static BlackScrolls instance;
    public static BlackScrolls getBlackScrolls() {
        if(instance == null) instance = new BlackScrolls();
        return instance;
    }

    @Override public String getIdentifier() { return "BLACK_SCROLLS"; }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "black scrolls.yml");
        final ConfigurationSection cs = getAddonConfig("black scrolls.yml").getConfigurationSection("black scrolls");
        if(cs != null) {
            for(String s : cs.getKeys(false))
                new PathBlackScroll(s);
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.BLACK_SCROLL).size() + " Black Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        unregister(Feature.BLACK_SCROLL);
    }

    public ItemStack applyBlackScroll(ItemStack is, ItemStack blackscroll, BlackScroll bs) {
        item = null;
        final HashMap<CustomEnchant, Integer> enchants = getEnchantsOnItem(is);
        if(is != null && enchants.size() > 0) {
            final Set<CustomEnchant> key = enchants.keySet();
            CustomEnchant enchant = (CustomEnchant) key.toArray()[RANDOM.nextInt(key.size())];
            final List<EnchantRarity> a = bs.getAppliesToRarities();
            int successP = -1;
            for(String string : blackscroll.getItemMeta().getLore()) if(getRemainingInt(string) != -1) successP = getRemainingInt(string);
            for(int f = 1; f <= 5; f++) {
                final EnchantRarity r = valueOfCustomEnchantRarity(enchant);
                if(a.contains(r)) {
                    int enchantlevel = enchants.get(enchant);
                    itemMeta = is.getItemMeta(); lore.clear(); lore.addAll(itemMeta.getLore());
                    int enchantslot = -1;
                    final String ap = r.getApplyColors();
                    for(int i = 0; i < lore.size(); i++) if(lore.get(i).equals(ap + enchant.getName() + " " + toRoman(enchantlevel))) enchantslot = i;
                    if(enchantslot == -1) return null;
                    lore.remove(enchantslot);
                    itemMeta.setLore(lore); lore.clear();
                    is.setItemMeta(itemMeta);
                    return CustomEnchants.getCustomEnchants().getRevealedItem(enchant, enchantlevel, successP, 100, true, true).clone();
                } else enchant = (CustomEnchant) key.toArray()[RANDOM.nextInt(key.size())];
            }
        }
        return item;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR) && cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            final Player player = (Player) event.getWhoClicked();
            item = current; itemMeta = current.getItemMeta(); lore.clear();
            HashMap<CustomEnchant, Integer> enchantmentsonitem = null;
            if(current.hasItemMeta() && current.getItemMeta().hasLore()) enchantmentsonitem = getEnchantsOnItem(current);

            final BlackScroll bs = valueOfBlackScroll(cursor);
            if(bs != null && item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && !enchantmentsonitem.isEmpty()) {
                giveItem(player, applyBlackScroll(current, cursor, bs));
                item = current; itemMeta = item.getItemMeta();
                if(itemMeta.hasDisplayName()) {
                    final TransmogScrolls t = TransmogScrolls.getTransmogScrolls();
                    if(t.isEnabled()) {
                        final int enchantcount = enchantmentsonitem.size();
                        t.update(item, enchantcount, enchantcount-1);
                    }
                }
                //playSuccess((Player) event.getWhoClicked());
                item.setItemMeta(itemMeta);
                event.setCancelled(true);
                event.setCurrentItem(item);
                final int a = cursor.getAmount();
                if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                else       cursor.setAmount(a-1);
                player.updateInventory();
            }
        }
    }
}
