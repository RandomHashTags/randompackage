package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.HashMap;

public class CoinFlipMatch {
    public static HashMap<Long, CoinFlipMatch> matches;
    private boolean active;
    private long created;
    private OfflinePlayer creator;
    private OfflinePlayer challenger;
    private CoinFlipOption option;
    private CoinFlipOption challengerOption;
    private BigDecimal wager;
    public CoinFlipMatch(long created, OfflinePlayer creator, CoinFlipOption option, BigDecimal wager) {
        if(matches == null) {
            matches = new HashMap<>();
        }
        this.created = created;
        this.creator = creator;
        this.option = option;
        this.wager = wager;
        active = false;
        matches.put(created, this);
    }
    public long getCreationTime() { return created; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public OfflinePlayer getCreator() { return creator; }
    public OfflinePlayer getChallenger() { return challenger; }
    public void setChallenger(OfflinePlayer player) { challenger = player; }

    public CoinFlipOption getCreatorOption() { return option; }
    public CoinFlipOption getChallengerOption() { return challengerOption; }
    public void setChallengerOption(CoinFlipOption option) { challengerOption = option; }
    public BigDecimal getWager() { return wager; }

    public void delete() {
        matches.remove(created);
    }
    public static CoinFlipMatch valueOfCreator(OfflinePlayer creator) {
        if(matches != null) {
            for(CoinFlipMatch match : matches.values()) {
                if(match.creator.equals(creator)) {
                    return match;
                }
            }
        }
        return null;
    }
    public static CoinFlipMatch valueOfChallenger(OfflinePlayer challenger) {
        if(matches != null) {
            for(CoinFlipMatch match : matches.values()) {
                if(match.challenger.equals(challenger)) {
                    return match;
                }
            }
        }
        return null;
    }
}
