package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileTrinket;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trinkets extends RPFeature {
    private static Trinkets instance;
    public static Trinkets getTrinkets() {
        if(instance == null) instance = new Trinkets();
        return instance;
    }

    public void load() {
        final long started = System.currentTimeMillis();

        if(!otherdata.getBoolean("saved default trinkets")) {
            final String[] a = new String[]{"BATTLESTAFF_OF_YIJKI", "EMP_PULSE", "FACTION_BANNER", "PHOENIX_FEATHER", "SPEED"};
            for(String s : a) save("trinkets", s + ".yml");
            otherdata.set("saved default trinkets", true);
            saveOtherData();
        }

        final List<ItemStack> trinkets = new ArrayList<>();
        final File folder = new File(rpd + separator + "trinkets");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                trinkets.add(new FileTrinket(f).getItem());
            }
        }
        final HashMap<String, FileTrinket> t = FileTrinket.trinkets;
        if(t != null) addGivedpCategory(trinkets, UMaterial.NETHER_STAR, "Trinkets", "Givedp: Trinkets");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (t != null ? t.size() : 0) + " Trinkets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        FileTrinket.deleteAll();
    }
}
