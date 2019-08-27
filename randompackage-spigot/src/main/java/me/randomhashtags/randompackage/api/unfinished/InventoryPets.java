package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.addons.InventoryPet;
import me.randomhashtags.randompackage.utils.addons.FileInventoryPet;
import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class InventoryPets extends RPFeature implements Listener {
    private static InventoryPets instance;
    public static InventoryPets getInventoryPets() {
        if(instance == null) instance = new InventoryPets();
        return instance;
    }
    public YamlConfiguration config;

    public String getIdentifier() { return "PETS"; }

    public void load() {
        final long started = System.currentTimeMillis();
        if(!otherdata.getBoolean("saved default pets")) {
            final String[] p = new String[] {
                    "ALCHEMIST", "ANTI_TELEBLOCK",
                    "BANNER", "BLACKSCROLL",
                    "ENCHANTER",
                    "FEIGN_DEATH",
                    "GAIA",
                    "LAVA_ELEMENTAL",
                    "RAID_CREEPER",
                    "SMITE",
                    "STRONGHOLD_SELL",
                    "TESLA",
                    "VILE_CREEPER",
                    "WATER_ELEMENTAL",
                    "XP_BOOSTER",
            };
            for(String s : p) save("pets", s + ".yml");
            otherdata.set("saved default pets", true);
            saveOtherData();
        }
        final File folder = new File(rpd + separator + "pets");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                new FileInventoryPet(f);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (inventorypets != null ? inventorypets.size() : 0) + " Inventory Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        inventorypets = null;
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final InventoryPet p = valueOfInventoryPet(is);
            if(p != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
}
