package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TitleData {
    @Nullable Title getActive();
    void setActive(@Nullable Title active);
    List<Title> getOwned();
    default void addTitle(@NotNull Title title) {
        final List<Title> owned = getOwned();
        if(owned != null) {
            owned.add(title);
        }
    }
}
