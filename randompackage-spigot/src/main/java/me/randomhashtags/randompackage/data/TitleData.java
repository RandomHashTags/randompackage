package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.addon.Title;

import java.util.List;

public interface TitleData {
    Title getActive();
    void setActive(@Nullable Title active);
    List<Title> getOwned();
    default void addTitle(@NotNull Title title) {
        final List<Title> owned = getOwned();
        if(owned != null) {
            owned.add(title);
        }
    }
}
