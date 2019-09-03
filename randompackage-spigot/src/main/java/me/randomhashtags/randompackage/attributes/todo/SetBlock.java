package me.randomhashtags.randompackage.attributes.todo;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import org.bukkit.Location;

import java.util.HashMap;

public class SetBlock extends AbstractEventAttribute {
    // TODO: finish this attribute
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
        }
    }
}
