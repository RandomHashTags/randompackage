package me.randomhashtags.randompackage.dev.dungeons;

import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;

public class DungeonPortalOpenEvent extends RPEventCancellable {
    private Dungeon type;
    private int ticksOpen;
    public DungeonPortalOpenEvent(Player player, Dungeon type, int ticksOpen) {
        super(player);
        this.type = type;
        this.ticksOpen = ticksOpen;
    }
    public Dungeon getType() { return type; }
    public int getTicksOpen() { return ticksOpen; }
    public void setTicksOpen(int ticksOpen) { this.ticksOpen =  ticksOpen; }
}
