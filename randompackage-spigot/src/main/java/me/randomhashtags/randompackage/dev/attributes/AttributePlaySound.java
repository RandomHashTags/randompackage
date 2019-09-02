package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AttributePlaySound extends AbstractEventAttribute {
    public String getIdentifier() { return "PLAYSOUND"; }
    @Override
    public void execute(Player player, String value) {
        if(player != null && value != null) {
            final String[] a = value.split(":");
            final int count = a.length, plays = count >= 4 ? Integer.parseInt(a[3]) : 1;
            final boolean world = count >= 5 && Boolean.parseBoolean(a[4]);
            final Location l = player.getLocation();
            final World w = player.getWorld();
            final Sound s = Sound.valueOf(a[0].toUpperCase());
            final float f1 = Float.parseFloat(a[1]), f2 = Float.parseFloat(a[2]);
            for(int i = 1; i <= plays; i++) {
                if(world) {
                    w.playSound(l, s, f1, f2);
                } else {
                    player.playSound(l, s, f1, f2);
                }
            }
        }
    }
    @Override
    public void executeAt(HashMap<Location, String> locations) {
    }
}
