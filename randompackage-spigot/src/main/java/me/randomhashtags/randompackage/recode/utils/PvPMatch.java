package me.randomhashtags.randompackage.recode.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class PvPMatch {
    public static HashMap<Player, PvPMatch> matches;

    public int slot;
    private Player creator;
    private Inventory inventory;
    private Chunk chunk;
    public PvPMatch(Player creator, Inventory inventory, Chunk chunk) {
        if(matches == null) {
            matches = new HashMap<>();
        }
        this.creator = creator;
        this.inventory = inventory;
        this.chunk = chunk;
        matches.put(creator, this);
    }
    public Player getCreator() { return creator; }
    public Inventory getInventory() { return inventory; }
    public Chunk getChunk() { return chunk; }
    public void delete() {
        matches.remove(creator);
        creator = null;
        chunk = null;
    }

    public static PvPMatch valueOf(Player player) {
        return matches != null ? matches.getOrDefault(player, null) : null;
    }
    public static PvPMatch valueOf(int slot) {
        if(matches != null) {
            for(PvPMatch m : matches.values()) {
                if(m.slot == slot) {
                    return m;
                }
            }
        }
        return null;
    }
}
