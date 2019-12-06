package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.enums.Feature;

import java.io.File;
import java.util.List;

public class FilePlayerQuest extends RPAddon implements PlayerQuest {
    public FilePlayerQuest(File f) {
        load(f);
        if(isEnabled()) {
            register(Feature.PLAYER_QUEST, this);
        }
    }
    public String getIdentifier() { return getYamlName(); }

    public boolean isEnabled() { return yml.getBoolean("settings.enabled"); }
    public String getName() { return colorize(yml.getString("settings.name")); }
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
    public List<String> getLore() { return colorizeListString(yml.getStringList("lore")); }
    public List<String> getRewards() { return yml.getStringList("rewards"); }
    public List<String> getTrigger() { return yml.getStringList("trigger"); }
}
