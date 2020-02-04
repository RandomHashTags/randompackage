package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.RarityGem;
import org.bukkit.entity.Player;

public class DepleteRarityGemEvent extends RPEventCancellable {
    private RarityGem gem;
    private int gemAmount, depleteAmount;
    public DepleteRarityGemEvent(Player player, RarityGem gem, int gemAmount, int depleteAmount) {
        super(player);
        this.gem = gem;
        this.gemAmount = gemAmount;
        this.depleteAmount = depleteAmount;
    }
    public RarityGem getGem() {
        return gem;
    }
    public int getGemAmount() {
        return gemAmount;
    }
    public void setGemAmount(int gemAmount) {
        this.gemAmount = gemAmount;
    }
    public int getDepleteAmount() {
        return depleteAmount;
    }
    public void setDepleteAmount(int depleteAmount) {
        this.depleteAmount = depleteAmount;
    }
}
