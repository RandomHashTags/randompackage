package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.dev.Disguise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DisguiseData {
    @Nullable String getActive();
    void setActive(Disguise disguise);
    @NotNull List<String> getOwned();
    void setOwned(@NotNull List<String> owned);
}
