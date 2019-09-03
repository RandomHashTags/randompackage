package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

public class MysteryMobSpawnerOpenEvent extends AbstractCancellable {
    public final Player player;
    public String entity;
    public MysteryMobSpawnerOpenEvent(Player player, String entity) {
        this.player = player;
        this.entity = entity;
    }
}
