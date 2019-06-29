package me.randomhashtags.randompackage.utils.supported.plugins;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandRole;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SuperiorSkyblock {

    private static SuperiorSkyblock instance;
    public static SuperiorSkyblock getSuperiorSkylock() {
        if(instance == null) instance = new SuperiorSkyblock();
        return instance;
    }

    public String getRole(Player player) {
        final IslandRole r = SuperiorSkyblockAPI.getPlayer(player).getIslandRole();
        return r != null ? r.name() : "";
    }
    public boolean canModify(UUID player, Location location) {
        final Island i = getIslandAt(location);
        return i == null || i.getAllMembers().contains(player);
    }
    public Island getIslandAt(Location l) {
        return SuperiorSkyblockAPI.getIslandAt(l);
    }
}
