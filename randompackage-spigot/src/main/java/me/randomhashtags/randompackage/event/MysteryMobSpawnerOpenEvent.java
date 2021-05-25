package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

public final class MysteryMobSpawnerOpenEvent extends RPEventCancellable {
    public final String entity;
    public MysteryMobSpawnerOpenEvent(Player player, String entity) {
        super(player);
        this.entity = entity;
    }
}
