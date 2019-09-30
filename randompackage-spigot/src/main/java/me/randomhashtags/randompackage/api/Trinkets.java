package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Trinket;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPItemStack;
import me.randomhashtags.randompackage.util.addon.FileTrinket;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trinkets extends EventAttributes implements RPItemStack {
    private static Trinkets instance;
    public static Trinkets getTrinkets() {
        if(instance == null) instance = new Trinkets();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "TRINKETS"; }
    protected RPFeature getFeature() { return getTrinkets(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "trinkets.yml");
        if(!otherdata.getBoolean("saved default trinkets")) {
            final String[] a = new String[] {
                    "BATTLESTAFF_OF_YIJKI",
                    "EMP_PULSE",
                    "FACTION_BANNER",
                    "PHOENIX_FEATHER",
                    "SOUL_ANVIL",
                    "SOUL_PEARL",
                    "SPEED"
            };
            for(String s : a) save("trinkets", s + ".yml");
            otherdata.set("saved default trinkets", true);
            saveOtherData();
        }

        final List<ItemStack> t = new ArrayList<>();
        final File folder = new File(rpd + separator + "trinkets");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final Trinket trinket = new FileTrinket(f);
                if(trinket.isEnabled()) {
                    t.add(trinket.getItem(0));
                }
            }
        }
        addGivedpCategory(t, UMaterial.NETHER_STAR, "Trinkets", "Givedp: Trinkets");

        config = YamlConfiguration.loadConfiguration(new File(rpd, "trinkets.yml"));

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (trinkets != null ? trinkets.size() : 0) + " Trinkets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        trinkets = null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final Player player = event.getPlayer();
            final String id = getRPItemStackValue(is, "TrinketInfo");
            if(id != null) {
                event.setCancelled(true);
                player.updateInventory();

                final String[] info = id.split(":");
                final String identifier = info[0];
                final Trinket trinket = getTrinket(identifier);
                final long expiration = Long.parseLong(info[1]), time = System.currentTimeMillis(), remainingtime = expiration-time;

                if(remainingtime <= 0) {
                    if(trigger(event, trinket.getAttributes())) {
                        trinket.didUse(is, identifier);
                    }
                } else {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{TIME}", getRemainingTime(remainingtime));
                    sendStringListMessage(player, config.getStringList("messages.on cooldown"), replacements);
                }
            }
        }
    }
}
