package me.randomhashtags.randompackage.data;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.Title;

import java.util.List;

public interface TitleData {
    Title getActiveTitle();
    List<String> getOwnedTitles();
    void addTitle(@NotNull Title title);
}
