package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.addons.Pet;
import me.randomhashtags.randompackage.utils.addons.FilePet;
import me.randomhashtags.randompackage.utils.objects.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Pets extends RPFeature implements Listener {
    private static Pets instance;
    public static Pets getPets() {
        if(instance == null) instance = new Pets();
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
                new FilePet(f);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (pets != null ? pets.size() : 0) + " Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        pets = null;
        instance = null;
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Pet p = valueOf(is);
            if(p != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    public Pet valueOf(ItemStack is) {
        if(pets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            for(Pet p : pets.values()) {
                final ItemStack i = p.getItem();
            }
        }
        return null;
    }
}
