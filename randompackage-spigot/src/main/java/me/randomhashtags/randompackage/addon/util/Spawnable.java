package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Spawnable extends Identifiable {
    boolean canSpawnAt(@Nullable Player player, @NotNull Location l);
    boolean canSpawnAt(@NotNull Location l);
    boolean canSpawnAtOwnedIsland();
    boolean canSpawnAtCoopIsland();
    boolean canSpawnAtVisitingIsland();
}
