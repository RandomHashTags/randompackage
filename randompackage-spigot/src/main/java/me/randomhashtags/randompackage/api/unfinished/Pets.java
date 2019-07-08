package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.addons.usingfile.FilePet;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

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
                new FilePet(f);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (pets != null ? pets.size() : 0) + " Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        config = null;
        deleteAll(Feature.PETS);
    }


}
