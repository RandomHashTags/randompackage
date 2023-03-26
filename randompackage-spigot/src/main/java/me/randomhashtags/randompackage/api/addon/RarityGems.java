package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.addon.file.FileRarityGem;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.RarityGemData;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public enum RarityGems implements RPFeatureSpigot {
    INSTANCE;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.RARITY_GEM;
    }

    @Override
    public void load() {
        save("rarity gems", "_settings.yml");
        final YamlConfiguration config = getRPConfig("rarity gems", "_settings.yml");

        FileRarityGem.DEFAULT_COLORS = new HashMap<>();
        final HashMap<Integer, String> defaultColors = FileRarityGem.DEFAULT_COLORS;
        defaultColors.put(-1, colorize(config.getString("default colors.else")));
        defaultColors.put(0, colorize(config.getString("default colors.less than 100")));
        for(String s : getConfigurationSectionKeys(config, "default colors", false)) {
            if(!s.equals("less than 100") && !s.equals("else") && s.endsWith("s")) {
                defaultColors.put(Integer.parseInt(s.split("s")[0]), colorize(config.getString("default colors." + s)));
            }
        }

        if(!OTHER_YML.getBoolean("saved default rarity gems")) {
            final String[] g = new String[]{ "SOUL" };
            for(String s : g) {
                save("rarity gems", s + ".yml");
            }
            OTHER_YML.set("saved default rarity gems", true);
            saveOtherData();
        }

        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "rarity gems")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileRarityGem(f);
            }
        }
    }
    @Override
    public void unload() {
        FileRarityGem.DEFAULT_COLORS = null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerDropItemEvent(PlayerDropItemEvent event) {
        final RarityGem gem = valueOfRarityGem(event.getItemDrop().getItemStack());
        if(gem != null) {
            final FileRPPlayer pdata = FileRPPlayer.get(event.getPlayer().getUniqueId());
            final RarityGemData data = pdata.getRarityGemData();
            if(data.isActive(gem)) {
                data.toggle(gem, gem.getToggleOffDroppedMsg());
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
                final ItemStack item = cursorGem.getItem();
                final ItemMeta itemMeta = item.getItemMeta();
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
                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                final RarityGemData data = pdata.getRarityGemData();
                data.toggle(gem, data.isActive(gem) ? gem.getToggleOffInteractMsg() : gem.getToggleOnMsg());
            }
        }
    }
    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
        final UUID uuid = event.getEntity().getUniqueId();
        final FileRPPlayer pdata = FileRPPlayer.get(uuid);
        final RarityGemData data = pdata.getRarityGemData();
        for(RarityGem gem : data.getRarityGems().keySet()) {
            if(data.isActive(gem)) {
                data.toggle(gem, gem.getToggleOffDroppedMsg());
            }
        }
    }
}