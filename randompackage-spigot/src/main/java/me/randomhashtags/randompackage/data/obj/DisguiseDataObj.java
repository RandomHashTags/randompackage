package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.DisguiseData;
import me.randomhashtags.randompackage.addon.dev.Disguise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DisguiseDataObj implements DisguiseData {
    private String active;
    private List<String> owned;

    public DisguiseDataObj(@Nullable String active, @NotNull List<String> owned) {
        this.active = active;
        this.owned = owned;
    }

    @Override
    public @Nullable String getActive() {
        return active;
    }

    @Override
    public void setActive(Disguise disguise) {
        active = disguise != null ? disguise.getIdentifier() : "nil";
    }

    @Override
    public @NotNull List<String> getOwned() {
        return owned;
    }

    @Override
    public void setOwned(@NotNull List<String> owned) {
        this.owned = owned;
    }
}
