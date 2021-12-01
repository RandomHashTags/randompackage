package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.dev.Disguise;

import java.util.List;

public interface DisguiseData {
    String getActive();
    void setActive(Disguise disguise);
    List<String> getOwned();
    void setOwned(List<String> owned);
}
