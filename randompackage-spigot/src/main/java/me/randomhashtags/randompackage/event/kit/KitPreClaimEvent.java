package me.randomhashtags.randompackage.event.kit;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;

public class KitPreClaimEvent extends RPEventCancellable {
    private RPPlayer pdata;
    private CustomKit kit;
    private int level, chance, levelupChance;
    public KitPreClaimEvent(RPPlayer pdata, Player player, CustomKit kit, int level) {
        this(pdata, player, kit, level, -1);
    }
    public KitPreClaimEvent(RPPlayer pdata, Player player, CustomKit kit, int level, int levelupChance) {
        this(pdata, player, kit, level, 100, levelupChance);
    }
    public KitPreClaimEvent(RPPlayer pdata, Player player, CustomKit kit, int level, int chance, int levelupChance) {
        super(player);
        this.pdata = pdata;
        this.kit = kit;
        this.level = level;
        this.chance = chance;
        this.levelupChance = levelupChance;
    }
    public RPPlayer getRPPlayer() {
        return pdata;
    }
    public CustomKit getKit() {
        return kit;
    }
    public int getLevel() {
        return level;
    }
    public int getChance() {
        return chance;
    }
    public void setChance(int chance) {
        this.chance = chance;
    }
    public int getLevelupChance() {
        return levelupChance;
    }
    public void setLevelupChance(int levelupChance) {
        this.levelupChance = levelupChance;
    }
}
