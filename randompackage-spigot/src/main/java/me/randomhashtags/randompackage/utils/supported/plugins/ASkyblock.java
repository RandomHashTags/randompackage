package me.randomhashtags.randompackage.utils.supported.plugins;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Location;

import java.util.UUID;

public class ASkyblock {

    private ASkyBlockAPI api;
    private static ASkyblock instance;
    public static final ASkyblock getASkyblock() {
        if(instance == null) instance = new ASkyblock();
        return instance;
    }

    private boolean isEnabled = false;

    public void enable() {
        if(isEnabled) return;
        api = ASkyBlockAPI.getInstance();
        isEnabled = true;
    }
    public void disable() {
        if(!isEnabled) return;
        api = null;
        isEnabled = false;
    }


    public boolean areTeammates(UUID player1, UUID player2) {
        return api.inTeam(player1) && api.getTeamMembers(player1).contains(player2);
    }
    public String getIslandName(UUID player) {
        return api.getIslandName(player);
    }
    public boolean canModify(UUID player, Location location) {
        final Island i = api.getIslandAt(location);
        return i != null && (i.getMembers().contains(player) || areTeammates(i.getOwner(), player));
    }
    public boolean isNotWarZoneOrSafeZone(Location l) {
        final Island i = api.getIslandAt(l);
        return i == null || !i.isSpawn();
    }
}