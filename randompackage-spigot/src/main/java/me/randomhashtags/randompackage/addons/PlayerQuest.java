package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.utils.RPAddon;

import java.util.List;

public abstract class PlayerQuest extends RPAddon {
    public abstract boolean isEnabled();
    public abstract String getName();
    public abstract long getExpiration();
    public abstract String getCompletion();
    public abstract boolean isTimeBased();
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
    public abstract List<String> getLore();
    public abstract List<String> getRewards();
    public abstract List<String> getTrigger();
}
