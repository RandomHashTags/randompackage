package me.randomhashtags.randompackage.api.nearFinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class FactionPoints extends RPFeature {
    private static FactionPoints instance;
    public static FactionPoints getFactionPoints() {
        if(instance == null) instance = new FactionPoints();
        return instance;
    }

    private File dataF;
    private YamlConfiguration config, data;
    private HashMap<String, Integer> points;

    public String getIdentifier() { return "FACTION_POINTS"; }
    protected RPFeature getFeature() { return getFactionPoints(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "faction points.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "faction points.yml"));
        points = new HashMap<>();
        loadBackup();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Points &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        backup();
    }

    public void loadBackup() {
        points.clear();
        dataF = new File(rpd + separator + "_Data", "faction points.yml");
        data = YamlConfiguration.loadConfiguration(dataF);
        final ConfigurationSection c = data.getConfigurationSection("points");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                points.put(s, data.getInt("points" + s));
            }
        }
    }
    public void backup() {
        for(String s : points.keySet()) {
            data.set("points." + s, points.get(s));
        }
        save();
    }
    private void save() {
        try {
            data.save(dataF);
            dataF = new File(rpd + separator + "_Data", "faction points.yml");
            data = YamlConfiguration.loadConfiguration(dataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPoints(String identifier) { return points.getOrDefault(identifier, 0); }
    public void addPoints(String identifier, int points) { setPoints(identifier, getPoints(identifier)+points); }
    public void setPoints(String identifier, int points) { this.points.put(identifier, points); }
}
