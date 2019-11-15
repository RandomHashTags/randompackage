package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.addon.FileRarityGem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class RarityGems extends RPFeature {
    private static RarityGems instance;
    public static RarityGems getRarityGems() {
        if(instance == null) instance = new RarityGems();
        return instance;
    }

    public String getIdentifier() { return "RARITY_GEMS"; }
    public void load() {
        final long started = System.currentTimeMillis();
        save("rarity gems", "_settings.yml");
        final YamlConfiguration config = getRPConfig("rarity gems", "_settings.yml");

        FileRarityGem.defaultColors = new HashMap<>();
        final HashMap<Integer, String> d = FileRarityGem.defaultColors;
        final ConfigurationSection C = config.getConfigurationSection("default colors");
        d.put(-1, ChatColor.translateAlternateColorCodes('&', config.getString("default colors.else")));
        d.put(0, ChatColor.translateAlternateColorCodes('&', config.getString("default colors.less than 100")));
        for(String s : C.getKeys(false)) {
            if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
                d.put(Integer.parseInt(s.split("s")[0]), ChatColor.translateAlternateColorCodes('&', config.getString("default colors." + s)));
            }
        }

        if(!otherdata.getBoolean("saved default rarity gems")) {
            final String[] g = new String[]{ "SOUL" };
            for(String s : g) {
                save("rarity gems", s + ".yml");
            }
            otherdata.set("saved default rarity gems", true);
            saveOtherData();
        }

        for(File f : new File(rpd + separator + "rarity gems").listFiles()) {
            if(!f.getName().equals("_settings.yml")) {
                new FileRarityGem(f);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (raritygems != null ? raritygems.size() : 0) + " Rarity Gems &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        raritygems = null;
        FileRarityGem.defaultColors = null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerDropItemEvent(PlayerDropItemEvent event) {
        final RarityGem gem = valueOfRarityGem(event.getItemDrop().getItemStack());
        if(gem != null) {
            final RPPlayer pdata = RPPlayer.get(event.getPlayer().getUniqueId());
            if(pdata.hasActiveRarityGem(gem)) {
                pdata.toggleRarityGem(gem, gem.getToggleOffDroppedMsg());
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack curs = event.getCursor(), curr = event.getCurrentItem();
        if(curr != null && curs != null && curr.hasItemMeta() && curs.hasItemMeta()) {
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
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack I = event.getItem();
        final Player player = event.getPlayer();
        final RarityGem gem = valueOfRarityGem(I);
        if(gem != null) {
            event.setCancelled(true);
            player.updateInventory();
            final int souls = getRemainingInt(I.getItemMeta().getDisplayName());
            if(souls > 0) {
                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                pdata.toggleRarityGem(gem, pdata.hasActiveRarityGem(gem) ? gem.getToggleOffInteractMsg() : gem.getToggleOnMsg());
            }
        }
    }
    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
        final UUID u = event.getEntity().getUniqueId();
        final RPPlayer pdata = RPPlayer.get(u);
        for(RarityGem g : pdata.getRarityGems().keySet()) {
            if(pdata.hasActiveRarityGem(g)) {
                pdata.toggleRarityGem(g, g.getToggleOffDroppedMsg());
            }
        }
    }
}