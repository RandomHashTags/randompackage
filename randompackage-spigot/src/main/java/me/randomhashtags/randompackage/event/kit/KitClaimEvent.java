package me.randomhashtags.randompackage.event.kit;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.event.AbstractCancellable;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.entity.Player;

public class KitClaimEvent extends AbstractCancellable {
    private RPPlayer pdata;
    private CustomKit kit;
    private int level, chance;
    private Player player;
    public KitClaimEvent(RPPlayer pdata, CustomKit kit, int level, Player player) {
        this(pdata, kit, level, player, -1);
    }
    public KitClaimEvent(RPPlayer pdata, CustomKit kit, int level, Player player, int chance) {
        this.pdata = pdata;
        this.kit = kit;
        this.level = level;
        this.player = player;
        this.chance = chance;
    }
    public RPPlayer getRPPlayer() { return pdata; }
    public CustomKit getKit() { return kit; }
    public int getLevel() { return level; }
    public Player getPlayer() { return player; }
    public int getLevelupChance() { return chance; }
    public void setLevelupChance(int chance) { this.chance = chance; }
}
