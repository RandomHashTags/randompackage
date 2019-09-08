package me.randomhashtags.randompackage.dev.nearFinished;

import me.randomhashtags.randompackage.dev.InventoryPet;
import me.randomhashtags.randompackage.utils.EventAttributes;
import me.randomhashtags.randompackage.utils.addons.FileInventoryPet;
import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class InventoryPets extends EventAttributes implements Listener {
    private static InventoryPets instance;
    public static InventoryPets getInventoryPets() {
        if(instance == null) instance = new InventoryPets();
        return instance;
    }
    public YamlConfiguration config;

    public ItemStack leash, rarecandy;
    private String leashedLore;

    public String getIdentifier() { return "PETS"; }
    protected RPFeature getFeature() { return getInventoryPets(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "inventory pets.yml");
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
        final File folder = new File(rpd + separator + "inventory pets");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                new FileInventoryPet(f);
            }
        }

        config = YamlConfiguration.loadConfiguration(new File(rpd, "inventory pets.yml"));
        leash = d(config, "items.leash");
        leashedLore = ChatColor.translateAlternateColorCodes('&', config.getString("items.leash.added lore"));
        rarecandy = d(config, "items.rare candy");

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (inventorypets != null ? inventorypets.size() : 0) + " Inventory Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        inventorypets = null;
    }

    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
    }
    @EventHandler
    private void playerRespawnEvent(PlayerRespawnEvent event) {
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final Player player = event.getPlayer();
            final InventoryPet p = valueOfInventoryPet(is);
            if(p != null) {
            } else if(is.isSimilar(leash) || is.isSimilar(rarecandy)) {
            } else return;
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
