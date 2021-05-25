package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

public final class BattleRoyaleTeam {
    private final int id;
    private final LinkedHashMap<Player, Boolean> players; // [Player, isAlive]
    private final HashMap<Player, Location> locations;
    private final HashMap<Player, ItemStack[]>  inventory;
    public BattleRoyaleTeam(int id) {
        this(id, new LinkedHashMap<>(), new HashMap<>(), new HashMap<>());
    }
    public BattleRoyaleTeam(int id, LinkedHashMap<Player, Boolean> players, HashMap<Player, Location> locations, HashMap<Player, ItemStack[]> inventory) {
        this.id = id;
        this.players = players;
        this.locations = locations;
        this.inventory = inventory;
    }
    public int getID() {
        return id;
    }
    public LinkedHashMap<Player, Boolean> getPlayers() {
        return players;
    }
    public HashMap<Player, Location> getPreviousLocations() {
        return locations;
    }
    public HashMap<Player, ItemStack[]> getPreviousInventories() {
        return inventory;
    }
    public void join(Player player) {
        players.put(player, true);
        locations.put(player, player.getLocation());
        inventory.put(player, player.getInventory().getContents());
    }
    public void quit(Player player) {
        players.remove(player);
        locations.remove(player);
        inventory.remove(player);
    }
}
