package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.SlotBotData;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public final class SlotBotDataObj implements SlotBotData {
    private final BigDecimal credits;

    public SlotBotDataObj(@NotNull BigDecimal credits) {
        this.credits = credits;
    }

    @Override
    public @NotNull BigDecimal getCredits() {
        return credits;
    }
}
