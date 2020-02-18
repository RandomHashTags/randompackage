package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.SlotBotData;

import java.math.BigDecimal;

public class SlotBotDataObj implements SlotBotData {
    private BigDecimal credits;

    public SlotBotDataObj(BigDecimal credits) {
        this.credits = credits;
    }

    @Override
    public BigDecimal getCredits() {
        return credits;
    }
}
