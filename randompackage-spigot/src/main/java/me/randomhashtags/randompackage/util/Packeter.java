package me.randomhashtags.randompackage.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface Packeter extends Versionable {
    default void sendItemCooldownPacket(Player player, Material material, int ticks) {
        if(EIGHT) {
        } else if(NINE) {
        } else if(TEN) {
        } else if(ELEVEN) {
        } else if(TWELVE) {
            ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().getCooldownTracker().a(org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers.getItem(material), ticks);
        } else if(THIRTEEN) {
            ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().getCooldownTracker().a(org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers.getItem(material), ticks);
        } else if(FOURTEEN) {
            ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer) player).getHandle().getCooldownTracker().setCooldown(org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers.getItem(material), ticks);
        }
    }
}
