package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.data.HomeData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HomeDataObj implements HomeData {
    private int addedMaxHomes;
    private final List<Home> homes;

    public HomeDataObj(int addedMaxHomes, @NotNull List<Home> homes) {
        this.addedMaxHomes = addedMaxHomes;
        this.homes = homes;
    }

    @Override
    public int getAddedMaxHomes() {
        return addedMaxHomes;
    }
    @Override
    public void setAddedMaxHomes(int addedMaxHomes) {
        this.addedMaxHomes = addedMaxHomes;
    }

    @Override
    public void addHome(@NotNull Home home) {
        homes.add(home);
    }

    @Override
    public void deleteHome(@NotNull Home home) {
        homes.remove(home);
    }
    @NotNull
    @Override
    public List<Home> getHomes() {
        return homes;
    }
}
