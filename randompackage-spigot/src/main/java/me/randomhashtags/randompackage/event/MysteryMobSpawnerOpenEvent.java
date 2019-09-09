package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

public class MysteryMobSpawnerOpenEvent extends RPEventCancellable {
    public String entity;
    public MysteryMobSpawnerOpenEvent(Player player, String entity) {
        super(player);
        this.entity = entity;
    }
}
