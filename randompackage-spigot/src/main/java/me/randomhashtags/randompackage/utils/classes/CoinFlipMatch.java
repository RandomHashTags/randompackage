package me.randomhashtags.randompackage.utils.classes;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class CoinFlipMatch {
    public static HashMap<Long, CoinFlipMatch> matches;
    public boolean isActive;
    private long created;
    private OfflinePlayer creator;
    public OfflinePlayer challenger;
    private CoinFlipOption option;
    public CoinFlipOption challengerOption;
    private long wager;
    public CoinFlipMatch(long created, OfflinePlayer creator, CoinFlipOption option, long wager) {
        if(matches == null) {
            matches = new HashMap<>();
        }
        this.created = created;
        this.creator = creator;
        this.option = option;
        this.wager = wager;
        isActive = false;
        matches.put(created, this);
    }
    public long created() { return created; }
    public OfflinePlayer creator() { return creator; }
    public CoinFlipOption option() { return option; }
    public long wager() { return wager; }

    public void delete() {
        matches.remove(created);
        this.created = 0;
        this.creator = null;
        this.option = null;
        this.challenger = null;
        this.challengerOption = null;
    }
    public static CoinFlipMatch valueOf(OfflinePlayer creator) {
        if(matches != null) {
            for(long l : matches.keySet()) {
                final CoinFlipMatch m = matches.get(l);
                if(m.creator.equals(creator)) {
                    return m;
                }
            }
        }
        return null;
    }
    public static CoinFlipMatch valueOF(OfflinePlayer challenger) {
        if(matches != null) {
            for(long l : matches.keySet()) {
                final CoinFlipMatch m = matches.get(l);
                if(m.challenger.equals(challenger)) {
                    return m;
                }
            }
        }
        return null;
    }
}
