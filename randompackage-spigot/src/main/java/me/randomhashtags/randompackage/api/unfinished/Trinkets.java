package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.classes.Trinket;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trinkets extends RandomPackageAPI implements Listener {

    private static Trinkets instance;
    public static final Trinkets getTrinkets() {
        if(instance == null) instance = new Trinkets();
        return instance;
    }

    public boolean isEnabled = false;

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;

        if(!otherdata.getBoolean("saved default trinkets")) {
            final String[] a = new String[]{"BATTLESTAFF_OF_YIJKI", "EMP_PULSE", "FACTION_BANNER", "PHOENIX_FEATHER", "SPEED"};
            for(String s : a) save("trinkets", s + ".yml");
            otherdata.set("saved default trinkets", true);
            saveOtherData();
        }

        final List<ItemStack> trinkets = new ArrayList<>();
        for(File f : new File(rpd + separator + "trinkets").listFiles()) {
            trinkets.add(new Trinket(f).getItem());
        }
        final HashMap<String, Trinket> t = Trinket.trinkets;
        if(t != null) addGivedpCategory(trinkets, UMaterial.NETHER_STAR, "Trinkets", "Givedp: Trinkets");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (t != null ? t.size() : 0) + " Trinkets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        Trinket.deleteAll();
        HandlerList.unregisterAll(this);
    }
}
