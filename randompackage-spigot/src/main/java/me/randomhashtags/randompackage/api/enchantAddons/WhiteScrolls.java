package me.randomhashtags.randompackage.api.enchantAddons;

import me.randomhashtags.randompackage.addons.WhiteScroll;
import me.randomhashtags.randompackage.addons.usingpath.PathWhiteScroll;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WhiteScrolls extends CustomEnchantUtils {
    private static WhiteScrolls instance;
    public static WhiteScrolls getWhiteScrolls() {
        if(instance == null) instance = new WhiteScrolls();
        return instance;
    }

    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
        final ConfigurationSection c = addons.getConfigurationSection("white scrolls");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                new PathWhiteScroll(s);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (whitescrolls != null ? whitescrolls.size() : 0) + " White Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        instance = null;
        whitescrolls = null;
        unloadUtils();
    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
            if(cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore() && current != null) {
                final WhiteScroll w = WhiteScroll.valueOf(cursor);
                final Player player = (Player) event.getWhoClicked();
                if(applyWhiteScroll(player, current, w)) {
                    event.setCancelled(true);
                    event.setCurrentItem(current);
                    final int a = cursor.getAmount();
                    if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                    else       cursor.setAmount(a-1);
                    player.updateInventory();
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        if(!event.isCancelled()) {
            final WhiteScroll w = WhiteScroll.valueOf(event.getItem());
            if(w != null) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }
        }
    }


    public boolean applyWhiteScroll(Player player, ItemStack is, WhiteScroll ws) {
        final boolean did = player != null && is != null && ws != null && ws.canBeApplied(is);
        if(did) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(is.hasItemMeta() && itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            lore.add(ws.getApplied());
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
            player.updateInventory();
        }
        return did;
    }
}
