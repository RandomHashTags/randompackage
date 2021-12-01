package me.randomhashtags.randompackage.addon.dev;

import me.randomhashtags.randompackage.addon.dev.enums.RaidEventPhaseTrigger;

public interface RaidEventPhase {
    String getString();
    RaidEventPhaseTrigger getAdvanceToNextPhaseWhen();
    long getSelfDestructCountdown();
    boolean isWarpable();
}
