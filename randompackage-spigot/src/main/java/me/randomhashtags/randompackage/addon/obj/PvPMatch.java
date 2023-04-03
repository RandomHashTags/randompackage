package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class PvPMatch {
    public static HashMap<Player, PvPMatch> MATCHES;

    public int slot;
    private Player creator;
    private final Inventory inventory;
    private Chunk chunk;
    public PvPMatch(Player creator, Inventory inventory, Chunk chunk) {
        if(MATCHES == null) {
            MATCHES = new HashMap<>();
        }
        this.creator = creator;
        this.inventory = inventory;
        this.chunk = chunk;
        MATCHES.put(creator, this);
    }
    public Player getCreator() {
        return creator;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public Chunk getChunk() {
        return chunk;
    }
    public void delete() {
        MATCHES.remove(creator);
        creator = null;
        chunk = null;
    }

    @Nullable
    public static PvPMatch valueOf(Player player) {
        return MATCHES != null ? MATCHES.getOrDefault(player, null) : null;
    }
    @Nullable
    public static PvPMatch valueOf(int slot) {
        if(MATCHES != null) {
            for(PvPMatch m : MATCHES.values()) {
                if(m.slot == slot) {
                    return m;
                }
            }
        }
        return null;
    }
}
