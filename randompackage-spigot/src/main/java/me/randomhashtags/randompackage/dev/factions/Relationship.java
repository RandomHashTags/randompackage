package me.randomhashtags.randompackage.dev.factions;

public final class Relationship {
    private final long startTime;
    private final Relation relation;
    public Relationship(long startTime, Relation relation) {
        this.startTime = startTime;
        this.relation = relation;
    }
    public long getStartTime() { return startTime; }
    public Relation getRelation() { return relation; }
}
