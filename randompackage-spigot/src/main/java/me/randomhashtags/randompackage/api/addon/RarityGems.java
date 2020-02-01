package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.addon.file.FileRarityGem;
import org.bukkit.Material;
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

    public String getIdentifier() {
        return "RARITY_GEMS";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save("rarity gems", "_settings.yml");
        final YamlConfiguration config = getRPConfig("rarity gems", "_settings.yml");

        FileRarityGem.defaultColors = new HashMap<>();
        final HashMap<Integer, String> defaultColors = FileRarityGem.defaultColors;
        defaultColors.put(-1, colorize(config.getString("default colors.else")));
        defaultColors.put(0, colorize(config.getString("default colors.less than 100")));
        for(String s : getConfigurationSectionKeys(config, "default colors", false)) {
            if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
                defaultColors.put(Integer.parseInt(s.split("s")[0]), colorize(config.getString("default colors." + s)));
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

        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "rarity gems")) {
            if(!f.getName().equals("_settings.yml")) {
                new FileRarityGem(f);
            }
        }
        sendConsoleDidLoadFeature(getAll(Feature.RARITY_GEM).size() + " Rarity Gems", started);
    }
    public void unload() {
        unregister(Feature.RARITY_GEM);
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
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(current != null && cursor != null && current.hasItemMeta() && cursor.hasItemMeta()) {
            final RarityGem cursorGem = valueOfRarityGem(cursor), currentGem = cursorGem != null ? valueOfRarityGem(current) : null;
            if(cursorGem == null || currentGem == null) {
                return;
            }
            if(cursorGem.equals(currentGem)) {
                final Player player = (Player) event.getWhoClicked();
                final int combinedTotal = getRemainingInt(cursor.getItemMeta().getDisplayName()) + getRemainingInt(current.getItemMeta().getDisplayName());
                event.setCancelled(true);
                item = cursorGem.getItem();
                itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SOULS}", colorize(cursorGem.getColors(combinedTotal)) + combinedTotal));
                item.setItemMeta(itemMeta);
                event.setCurrentItem(item);
                final int amount = cursor.getAmount();
                if(amount == 1) {
                    event.setCursor(new ItemStack(Material.AIR));
                } else {
                    cursor.setAmount(amount-1);
                    event.setCursor(cursor);
                }
                player.updateInventory();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        final Player player = event.getPlayer();
        final RarityGem gem = valueOfRarityGem(is);
        if(gem != null) {
            event.setCancelled(true);
            player.updateInventory();
            final int souls = getRemainingInt(is.getItemMeta().getDisplayName());
            if(souls > 0) {
                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                pdata.toggleRarityGem(gem, pdata.hasActiveRarityGem(gem) ? gem.getToggleOffInteractMsg() : gem.getToggleOnMsg());
            }
        }
    }
    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
        final UUID uuid = event.getEntity().getUniqueId();
        final RPPlayer pdata = RPPlayer.get(uuid);
        for(RarityGem gem : pdata.getRarityGems().keySet()) {
            if(pdata.hasActiveRarityGem(gem)) {
                pdata.toggleRarityGem(gem, gem.getToggleOffDroppedMsg());
            }
        }
    }
}