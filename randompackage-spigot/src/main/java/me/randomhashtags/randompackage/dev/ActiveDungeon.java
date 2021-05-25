package me.randomhashtags.randompackage.dev;

import org.bukkit.entity.Player;

import java.util.List;

public class ActiveDungeon {
    private final Dungeon type;
    private final long startedTime;
    private long objectiveStartTime;
    private final List<Player> players;
    private DungeonObjective objective;
    public ActiveDungeon(Dungeon type, long startedTime, List<Player> players, long objectiveStarTime, DungeonObjective objective) {
        this.type = type;
        this.startedTime = startedTime;
        this.players = players;
        this.objective = objective;
    }
    public Dungeon getType() {
        return type;
    }
    public long getStartedTime() {
        return startedTime;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public long getObjectiveStartTime() {
        return objectiveStartTime;
    }
    public void setObjectiveStartTime(long objectiveStartTime) {
        this.objectiveStartTime = objectiveStartTime;
    }
    public DungeonObjective getObjective() {
        return objective;
    }
    public void setObjective(DungeonObjective objective) {
        this.objective = objective;
    }
}
