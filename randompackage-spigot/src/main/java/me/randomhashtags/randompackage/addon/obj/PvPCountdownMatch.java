package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PvPCountdownMatch {
    public static List<PvPCountdownMatch> COUNTDOWNS;
    private Player creator, challenger;
    public PvPCountdownMatch(Player creator, Player challenger) {
        if(COUNTDOWNS == null) {
            COUNTDOWNS = new ArrayList<>();
        }
        this.creator = creator;
        this.challenger = challenger;
        COUNTDOWNS.add(this);
    }
    public Player getCreator() {
        return creator;
    }
    public Player getChallenger() {
        return challenger;
    }

    public void delete() {
        creator = null;
        challenger = null;
        COUNTDOWNS.remove(this);
    }


    public static PvPCountdownMatch valueOf(Player player) {
        if(COUNTDOWNS != null) {
            for(PvPCountdownMatch p : COUNTDOWNS) {
                if(p.getChallenger().equals(player) || p.getCreator().equals(player)) {
                    return p;
                }
            }
        }
        return null;
    }
}
