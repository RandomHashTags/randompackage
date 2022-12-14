package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.SlotBotData;

import java.math.BigDecimal;

public final class SlotBotDataObj implements SlotBotData {
    private final BigDecimal credits;

    public SlotBotDataObj(BigDecimal credits) {
        this.credits = credits;
    }

    @Override
    public BigDecimal getCredits() {
        return credits;
    }
}
