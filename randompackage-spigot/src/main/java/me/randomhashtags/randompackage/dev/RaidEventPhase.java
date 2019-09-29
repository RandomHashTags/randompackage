package me.randomhashtags.randompackage.dev;

public interface RaidEventPhase {
    String getString();
    RaidEventPhaseTrigger getAdvanceToNextPhaseWhen();
    long getSelfDestructCountdown();
    boolean isWarpable();
}
