package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;

public final class CoinFlipMatch {
    public static HashMap<Long, CoinFlipMatch> MATCHES;
    private boolean active;
    private final long created;
    private final OfflinePlayer creator;
    private OfflinePlayer challenger;
    private final CoinFlipOption option;
    private CoinFlipOption challengerOption;
    private final BigDecimal wager;
    public CoinFlipMatch(long created, OfflinePlayer creator, CoinFlipOption option, BigDecimal wager) {
        if(MATCHES == null) {
            MATCHES = new HashMap<>();
        }
        this.created = created;
        this.creator = creator;
        this.option = option;
        this.wager = wager;
        active = false;
        MATCHES.put(created, this);
    }
    public long getCreationTime() {
        return created;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public OfflinePlayer getCreator() {
        return creator;
    }
    public OfflinePlayer getChallenger() {
        return challenger;
    }
    public void setChallenger(OfflinePlayer player) {
        challenger = player;
    }

    public CoinFlipOption getCreatorOption() {
        return option;
    }
    public CoinFlipOption getChallengerOption() {
        return challengerOption;
    }
    public void setChallengerOption(CoinFlipOption option) {
        challengerOption = option;
    }
    public BigDecimal getWager() {
        return wager;
    }

    public void delete() {
        MATCHES.remove(created);
    }
    @Nullable
    public static CoinFlipMatch valueOfCreator(OfflinePlayer creator) {
        if(MATCHES != null) {
            for(CoinFlipMatch match : MATCHES.values()) {
                if(match.creator.equals(creator)) {
                    return match;
                }
            }
        }
        return null;
    }
    @Nullable
    public static CoinFlipMatch valueOfChallenger(OfflinePlayer challenger) {
        if(MATCHES != null) {
            for(CoinFlipMatch match : MATCHES.values()) {
                if(match.challenger.equals(challenger)) {
                    return match;
                }
            }
        }
        return null;
    }
}
