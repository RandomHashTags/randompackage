package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.addons.EventCondition;
import org.bukkit.entity.Player;

public abstract class AbstractEventCondition implements EventCondition {
    public String getIdentifier() {
        final String[] n = getClass().getName().split("\\.");
        return n[n.length-1].toUpperCase();
    }
    public void load() {
        //addEventCondition(this);
    }

    public boolean check(Player player) { return false; }
}
