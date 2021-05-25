package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.data.TitleData;

import java.util.List;

public final class TitleDataObj implements TitleData {
    private Title active;
    private final List<Title> owned;

    public TitleDataObj(Title active, List<Title> owned) {
        this.active = active;
        this.owned = owned;
    }

    @Override
    public Title getActive() {
        return active;
    }
    @Override
    public void setActive(Title active) {
        this.active = active;
    }

    @Override
    public List<Title> getOwned() {
        return owned;
    }
}
