package me.randomhashtags.randompackage.addons.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PvPCountdownMatch {
    public static List<PvPCountdownMatch> countdowns;
    private Player creator, challenger;
    public PvPCountdownMatch(Player creator, Player challenger) {
        if(countdowns == null) {
            countdowns = new ArrayList<>();
        }
        this.creator = creator;
        this.challenger = challenger;
        countdowns.add(this);
    }
    public Player getCreator() { return creator; }
    public Player getChallenger() { return challenger; }

    public void delete() {
        creator = null;
        challenger = null;
        countdowns.remove(this);
    }


    public static PvPCountdownMatch valueOf(Player player) {
        if(countdowns != null) {
            for(PvPCountdownMatch p : countdowns) {
                if(p.getChallenger().equals(player) || p.getCreator().equals(player)) {
                    return p;
                }
            }
        }
        return null;
    }
}
