package me.randomhashtags.randompackage.api.enchantAddons;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.usingpath.PathRarityGem;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RarityGems extends CustomEnchantUtils {
    private static RarityGems instance;
    public static RarityGems getRarityGems() {
        if(instance == null) instance = new RarityGems();
        return instance;
    }

    public void load() {
        loadUtils();
        final long started = System.currentTimeMillis();
        save("addons", "rarity gems.yml");
        final YamlConfiguration config = getAddonConfig("rarity gems.yml");
        final ConfigurationSection cs = config.getConfigurationSection("rarity gems");
        if(cs != null) {
            PathRarityGem.defaultColors = new HashMap<>();
            final HashMap<Integer, String> d = PathRarityGem.defaultColors;
            final ConfigurationSection C = config.getConfigurationSection("default settings.colors");
            d.put(-1, ChatColor.translateAlternateColorCodes('&', config.getString("default settings.colors.else")));
            d.put(0, ChatColor.translateAlternateColorCodes('&', config.getString("default settings.colors.less than 100")));
            for(String s : C.getKeys(false)) {
                if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
                    d.put(Integer.parseInt(s.split("s")[0]), ChatColor.translateAlternateColorCodes('&', config.getString("default settings.colors." + s)));
                }
            }
            for(String s : cs.getKeys(false)) {
                new PathRarityGem(s);
            }
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + (raritygems != null ? raritygems.size() : 0) + " Rarity Gems &e(took " + (System.currentTimeMillis()-started) + "ms)");
        }
    }
    public void unload() {
        instance = null;
        deleteAll(Feature.RARITY_GEMS);
        unloadUtils();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void playerDropItemEvent(PlayerDropItemEvent event) {
        if(!event.isCancelled()) {
            final RarityGem gem = valueOfRarityGem(event.getItemDrop().getItemStack());
            if(gem != null) {
                final RPPlayer pdata = RPPlayer.get(event.getPlayer().getUniqueId());
                if(pdata.hasActiveRarityGem(gem)) pdata.toggleRarityGem(event, gem);
            }
        }
    }
    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack curs = event.getCursor(), curr = event.getCurrentItem();
        if(!event.isCancelled() && curr != null && curs != null && curr.hasItemMeta() && curs.hasItemMeta()) {
            final int cursorAmount = curs.getAmount();
            final RarityGem cursor = valueOfRarityGem(curs), current = cursor != null ? valueOfRarityGem(curr) : null;
            if(cursor == null || current == null) return;
            if(cursor.equals(current)) {
                final Player player = (Player) event.getWhoClicked();
                final int combinedTotal = getRemainingInt(curs.getItemMeta().getDisplayName()) + getRemainingInt(curr.getItemMeta().getDisplayName());
                event.setCancelled(true);
                item = cursor.getItem();
                itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SOULS}", ChatColor.translateAlternateColorCodes('&', cursor.getColors(combinedTotal)) + combinedTotal));
                item.setItemMeta(itemMeta);
                event.setCurrentItem(item);
                if(cursorAmount == 1) event.setCursor(new ItemStack(Material.AIR));
                else {
                    curs.setAmount(cursorAmount-1);
                    event.setCursor(curs);
                }
                player.updateInventory();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack I = event.getItem();
        final Player player = event.getPlayer();
        final RarityGem gem = valueOfRarityGem(I);
        if(gem != null) {
            event.setCancelled(true);
            player.updateInventory();
            final int souls = getRemainingInt(I.getItemMeta().getDisplayName());
            if(souls > 0) {
                RPPlayer.get(player.getUniqueId()).toggleRarityGem(event, gem);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        final UUID u = e.getUniqueId();
        if(e instanceof Player) {
            final RPPlayer pdata = RPPlayer.get(u);
            for(RarityGem g : pdata.getRarityGems().keySet())
                pdata.toggleRarityGem(event, g);
        }
    }
}