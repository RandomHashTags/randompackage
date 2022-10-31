package me.randomhashtags.randompackage.addon.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Spawnable extends Identifiable {
    boolean canSpawnAt(@Nullable Player player, @NotNull Location l);
    boolean canSpawnAt(@NotNull Location l);
    boolean canSpawnAtOwnedIsland();
    boolean canSpawnAtCoopIsland();
    boolean canSpawnAtVisitingIsland();
}
