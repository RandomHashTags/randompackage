package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.data.HomeData;

import java.util.List;

public class HomeDataObj implements HomeData {
    private int addedMaxHomes;
    private List<Home> homes;
    public HomeDataObj(int addedMaxHomes, List<Home> homes) {
        this.addedMaxHomes = addedMaxHomes;
        this.homes = homes;
    }

    @Override
    public int getAddedMaxHomes() {
        return addedMaxHomes;
    }
    @Override
    public List<Home> getHomes() {
        return homes;
    }
}
