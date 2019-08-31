package me.randomhashtags.randompackage.api.addons;

import me.randomhashtags.randompackage.addons.WhiteScroll;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.addons.PathWhiteScroll;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WhiteScrolls extends CustomEnchantUtils {
    private static WhiteScrolls instance;
    public static WhiteScrolls getWhiteScrolls() {
        if(instance == null) instance = new WhiteScrolls();
        return instance;
    }

    public String getIdentifier() { return "WHITE_SCROLLS"; }
    protected RPFeature getFeature() { return getWhiteScrolls(); }
    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
        save("addons", "white scrolls.yml");
        final ConfigurationSection c = getAddonConfig("white scrolls.yml").getConfigurationSection("white scrolls");
        if(c != null) {
            final List<ItemStack> a = new ArrayList<>();
            for(String s : c.getKeys(false)) {
                a.add(new PathWhiteScroll(s).getItem());
            }
            addGivedpCategory(a, UMaterial.MAP, "White Scrolls", "Givedp: White Scrolls");
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (whitescrolls != null ? whitescrolls.size() : 0) + " White Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unloadUtils();
        whitescrolls = null;
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore() && current != null) {
            final WhiteScroll w = valueOf(cursor);
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
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final WhiteScroll w = valueOf(event.getItem());
        if(w != null) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }


    public boolean applyWhiteScroll(Player player, ItemStack is, WhiteScroll ws) {
        final boolean did = player != null && is != null && ws != null && ws.canBeApplied(is);
        if(did) {
            final String r = ws.getRequiredWhiteScroll();
            final WhiteScroll required = r != null ? getWhiteScroll(r) : null;
            itemMeta = is.getItemMeta(); lore.clear();
            if(is.hasItemMeta() && itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            lore.add(ws.getApplied());
            if(required != null && ws.removesRequiredAfterApplication()) lore.remove(required.getApplied());
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
            player.updateInventory();
        }
        return did;
    }

    public WhiteScroll valueOf(String apply) {
        if(whitescrolls != null && apply != null && !apply.isEmpty()) {
            for(WhiteScroll w : whitescrolls.values()) {
                if(w.getApplied().equals(apply)) {
                    return w;
                }
            }
        }
        return null;
    }
    public WhiteScroll valueOf(ItemStack is) {
        if(whitescrolls != null && is != null) {
            for(WhiteScroll w : whitescrolls.values()) {
                if(is.isSimilar(w.getItem())) {
                    return w;
                }
            }
        }
        return null;
    }
    public List<WhiteScroll> valueOfApplied(ItemStack is) {
        if(whitescrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<WhiteScroll> l = new ArrayList<>();
            for(String s : is.getItemMeta().getLore()) {
                final WhiteScroll w = valueOf(s);
                if(w != null) l.add(w);
            }
            return l;
        }
        return null;
    }
}
