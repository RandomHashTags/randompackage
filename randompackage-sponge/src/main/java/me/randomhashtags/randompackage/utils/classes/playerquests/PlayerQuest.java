package me.randomhashtags.randompackage.utils.classes.playerquests;

import me.randomhashtags.randompackage.api.nearFinished.PlayerQuests;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

public class PlayerQuest {
    public static TreeMap<String, PlayerQuest> quests;
    private static PlayerQuests api;
    private File f;
    private YamlConfiguration yml;
    public PlayerQuest(File f) {
        if(quests == null) {
            quests = new TreeMap<>();
            api = PlayerQuests.getPlayerQuests();
        }
        this.f = f;
        yml = YamlConfiguration.loadConfiguration(f);
        quests.put(f.getName().split("\\.yml")[0], this);
    }
    public File getFile() { return f; }
    public YamlConfiguration getYaml() { return yml; }
    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.name")); }
    public long getExpiration() { return yml.getLong("settings.expiration"); }
    public String getCompletion() { return yml.getString("settings.completion"); }
    public boolean isTimeBased() {
        final String c = getCompletion().toLowerCase();
        return c.contains("d") || c.contains("h") || c.contains("m") || c.contains("s");
    }
    public double getTimedCompletion() {
        String s = getCompletion().toLowerCase().replaceAll("\\p{Z}", "").replaceAll("\\p{S}", "").replaceAll("\\p{Nl}", "").replaceAll("\\p{No}", "").replaceAll("\\p{M}", "");
        double c = 0.00;
        if(s.contains("d")) {
            c += Double.parseDouble(s.split("d")[0])*24*60*60*1000;
            s = s.split("d")[1];
        }
        if(s.contains("h")) {
            c += Double.parseDouble(s.split("h")[0])*60*60*1000;
            s = s.split("h")[1];
        }
        if(s.contains("m")) {
            c += Double.parseDouble(s.split("m")[0])*60*1000;
            s = s.split("m")[1];
        }
        if(s.contains("s")) {
            c += Double.parseDouble(s.split("s")[0])*1000;
            //s = s.split("s")[1];
        }
        return c;
    }
    public List<String> getLore() { return api.colorizeListString(yml.getStringList("lore")); }
    public List<String> getRewards(){ return yml.getStringList("rewards"); }
    public List<String> getTrigger() { return yml.getStringList("trigger"); }

    public static void deleteAll() {
        quests = null;
        api = null;
    }
}
