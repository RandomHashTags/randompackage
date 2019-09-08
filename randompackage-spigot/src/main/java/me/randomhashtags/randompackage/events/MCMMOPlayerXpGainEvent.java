package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

public class MCMMOPlayerXpGainEvent extends AbstractCancellable {
    public final Player player;
    public final String skill;
    public float xp;
    public MCMMOPlayerXpGainEvent(Player player, String skill, float xp) {
        this.player = player;
        this.skill = skill;
        this.xp = xp;
    }
}
