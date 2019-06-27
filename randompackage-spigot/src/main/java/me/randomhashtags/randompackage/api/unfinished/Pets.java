package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.classes.Pet;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashMap;

public class Pets extends RPFeature implements Listener {
    private static Pets instance;
    public static Pets getPets() {
        if(instance == null) instance = new Pets();
        return instance;
    }
    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();

        if(!otherdata.getBoolean("saved default pets")) {
            final String[] p = new String[] {"ANTI_TELEBLOCK", "BANNER", "LAVA_ELEMENTAL", "WATER_ELEMENTAL"};
            for(String s : p) save("pets", s + ".yml");
            otherdata.set("saved default pets", true);
            saveOtherData();
        }
        final File folder = new File(rpd + separator + "pets");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                new Pet(f);
            }
        }
        final HashMap<String, Pet> p = Pet.pets;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (p != null ? p.size() : 0) + " Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        config = null;
        Pet.deleteAll();
    }


}
