package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractDungeon;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.TreeMap;

public class Dungeon extends AbstractDungeon {
    public static TreeMap<String, Dungeon> dungeons;
    public Dungeon(File f) {
        if(dungeons == null) dungeons = new TreeMap<>();
        load(f);
        dungeons.put(getYamlName(), this);
    }
    public static Dungeon valueOf(ItemStack key) {
        if(dungeons != null && key != null) {
            for(Dungeon d : dungeons.values()) {
                if(d.getKey().isSimilar(key)) {
                    return d;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        dungeons = null;
    }
}
