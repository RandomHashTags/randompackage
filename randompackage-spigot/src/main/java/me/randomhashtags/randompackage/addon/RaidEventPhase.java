package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.dev.RaidEventPhaseTrigger;

public interface RaidEventPhase {
    String getString();
    RaidEventPhaseTrigger getAdvanceToNextPhaseWhen();
    long getSelfDestructCountdown();
    boolean isWarpable();
}
