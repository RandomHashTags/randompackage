package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.ShowcaseData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ShowcaseDataObj implements ShowcaseData {
    private HashMap<Integer, Integer> sizes;
    private HashMap<Integer, ItemStack[]> showcases;
    public ShowcaseDataObj(HashMap<Integer, Integer> sizes, HashMap<Integer, ItemStack[]> showcases) {
        this.sizes = sizes;
        this.showcases = showcases;
    }
    @Override
    public HashMap<Integer, Integer> getSizes() {
        return sizes;
    }

    @Override
    public void setSizes(HashMap<Integer, Integer> sizes) {
        this.sizes = sizes;
    }

    @Override
    public HashMap<Integer, ItemStack[]> getShowcases() {
        return showcases;
    }
    @Override
    public void setShowcases(HashMap<Integer, ItemStack[]> showcases) {
        this.showcases = showcases;
    }
}
