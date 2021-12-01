package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.DisguiseData;
import me.randomhashtags.randompackage.addon.dev.Disguise;

import java.util.List;

public final class DisguiseDataObj implements DisguiseData {
    private String active;
    private List<String> owned;

    public DisguiseDataObj(String active, List<String> owned) {
        this.active = active;
        this.owned = owned;
    }

    @Override
    public String getActive() {
        return active;
    }

    @Override
    public void setActive(Disguise disguise) {
        active = disguise != null ? disguise.getIdentifier() : "nil";
    }

    @Override
    public List<String> getOwned() {
        return owned;
    }

    @Override
    public void setOwned(List<String> owned) {
        this.owned = owned;
    }
}
