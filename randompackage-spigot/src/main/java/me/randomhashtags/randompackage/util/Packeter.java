package me.randomhashtags.randompackage.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface Packeter extends Versionable {
    default void sendItemCooldownPacket(Player player, Material material, int ticks) {
        if(EIGHT) {
        } else if(NINE) {
        } else if(TEN) {
        } else if(ELEVEN) {
        } else {
            player.setCooldown(material, ticks);
        }
    }
}
