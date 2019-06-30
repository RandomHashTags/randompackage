package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractOutpost;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

public class Outpost extends AbstractOutpost {
    public static TreeMap<String, Outpost> outposts;
    public static HashMap<Integer, Outpost> slots;

    public Outpost(File f) {
        if(outposts == null) {
            outposts = new TreeMap<>();
            slots = new HashMap<>();
        }
        load(f);
        slots.put(getSlot(), this);
        outposts.put(getYamlName(), this);
    }

    public static void deleteAll() {
        outposts = null;
        slots = null;
    }
}
