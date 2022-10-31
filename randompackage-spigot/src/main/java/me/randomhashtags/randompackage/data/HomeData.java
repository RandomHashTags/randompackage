package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.api.Homes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HomeData {
    int getAddedMaxHomes();
    void setAddedMaxHomes(int addedMaxHomes);
    @NotNull List<Home> getHomes();
    default int getMaxHomes(@NotNull Player player) {
        final int addedMaxHomes = getAddedMaxHomes();
        for(int i = 100; i >= 1; i--) {
            if(player.hasPermission("RandomPackage.sethome." + i)) {
                return addedMaxHomes+i;
            }
        }
        return Homes.INSTANCE.defaultMax + addedMaxHomes;
    }
    default Home getHome(@NotNull String identifier) {
        for(Home h : getHomes()) {
            if(h.getName().equals(identifier)) {
                return h;
            }
        }
        return null;
    }
    void addHome(@NotNull Home home);
    void deleteHome(@NotNull Home home);
}
