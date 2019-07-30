package me.randomhashtags.randompackage.api.enchantAddons;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RandomizationScroll;
import me.randomhashtags.randompackage.addons.usingpath.PathRandomizationScroll;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.api.events.RandomizationScrollUseEvent;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RandomizationScrolls extends CustomEnchants {
    private static RandomizationScrolls instance;
    public static RandomizationScrolls getRandomizationScrolls() {
        if(instance == null) instance = new RandomizationScrolls();
        return instance;
    }

    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
        save("addons", "randomization scrolls.yml");
        final ConfigurationSection cs = getAddonConfig("randomization scrolls.yml").getConfigurationSection("randomization scrolls");
        if(cs != null) {
            final List<ItemStack> z = new ArrayList<>();
            for(String s : cs.getKeys(false)) {
                z.add(new PathRandomizationScroll(s).getItem());
            }
            addGivedpCategory(z, UMaterial.PAPER, "Randomization Scrolls", "Givedp: Randomization Scrolls");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (randomizationscrolls != null ? randomizationscrolls.size() : 0) + " Randomization Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        instance = null;
        deleteAll(Feature.RANDOMIZATION_SCROLLS);
        unloadUtils();
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final Player player = (Player) event.getWhoClicked();
            final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
            if(current != null && cursor != null && !current.getType().equals(Material.AIR) && !cursor.getType().equals(Material.AIR)) {
                final CustomEnchant enchant = CustomEnchant.valueOf(current);
                final RandomizationScroll randomizationscroll = valueOfRandomizationScroll(cursor);
                if(enchant != null && randomizationscroll != null) {
                    final EnchantRarity r = EnchantRarity.valueOf(enchant);
                    if(randomizationscroll.getAppliesToRarities().contains(r)) {
                        item = current; itemMeta = item.getItemMeta(); lore.clear();
                        final String s = r.getSuccess(), d = r.getDestroy();
                        int newSuccess = random.nextInt(101), newDestroy = random.nextInt(101);
                        final RandomizationScrollUseEvent e = new RandomizationScrollUseEvent(player, enchant, getEnchantmentLevel(itemMeta.getDisplayName()), randomizationscroll, newSuccess, newDestroy);
                        pluginmanager.callEvent(e);
                        newSuccess = e.getNewSuccess();
                        newDestroy = e.getNewDestroy();
                        for(String string : itemMeta.getLore()) {
                            if(string.equals(s.replace("{PERCENT}", "" + getRemainingInt(string))))        string = s.replace("{PERCENT}", "" + newSuccess);
                            else if(string.equals(d.replace("{PERCENT}", "" + getRemainingInt(string))))   string = d.replace("{PERCENT}", "" + newDestroy);
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                        itemMeta.setLore(lore); lore.clear();
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
    }

    public RandomizationScroll valueOfRandomizationScroll(ItemStack is) {
        if(randomizationscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(RandomizationScroll r : randomizationscrolls.values()) {
                if(r.getItem().getItemMeta().equals(m)) {
                    return r;
                }
            }
        }
        return null;
    }
}
